/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package de.gerdiproject.harvest.harvester;

import de.gerdiproject.harvest.utils.FaoStatDownloader;
import de.gerdiproject.harvest.utils.FaoStatConstants;
import de.gerdiproject.harvest.utils.FaoStatDomainParser;
import de.gerdiproject.json.GsonUtils;
import de.gerdiproject.json.IJsonObject;
import de.gerdiproject.json.datacite.DataCiteJson;
import de.gerdiproject.json.datacite.Subject;
import de.gerdiproject.json.fao.FaoBulkDownloads;
import de.gerdiproject.json.fao.FaoDimensions;
import de.gerdiproject.json.fao.FaoDocuments;
import de.gerdiproject.json.fao.FaoDomains;
import de.gerdiproject.json.fao.FaoDomains.Domain;
import de.gerdiproject.json.fao.FaoFilters;
import de.gerdiproject.json.fao.FaoMetadata;
import de.gerdiproject.json.impl.GsonObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonObject;

/**
 * A harvester for FAOSTAT (http://www.fao.org/faostat/en/#data).
 *
 * @author row
 */
public class FaoStatHarvester extends AbstractListHarvester<Domain>
{
    private final static String PROPERTY_VERSION = "version";
    private final static String PROPERTY_LANGUAGE = "language";
    private final static String DEFAULT_VERSION = "v1";
    private final static String DEFAULT_LANGUAGE = "en";
    private final static List<String> VALID_PARAMS = Arrays.asList(PROPERTY_VERSION, PROPERTY_LANGUAGE);

    private final FaoStatDownloader downloader;


    /**
     * Default Constructor. Sets language to "en" and version to "v1". Version
     * and language are essential parts of the URL.
     */
    public FaoStatHarvester()
    {
        // only one document is created per harvested entry
        super(1);
        super.setProperty(PROPERTY_VERSION, DEFAULT_VERSION);
        super.setProperty(PROPERTY_LANGUAGE, DEFAULT_LANGUAGE);

        downloader = new FaoStatDownloader(DEFAULT_VERSION, DEFAULT_LANGUAGE);
    }


    @Override
    public List<String> getValidProperties()
    {
        return VALID_PARAMS;
    }


    @Override
    protected Collection<Domain> loadEntries()
    {
        FaoDomains domainsObj = downloader.getDomains();
        return domainsObj.getData();
    }


    @Override
    protected List<IJsonObject> harvestEntry(Domain domain)
    {
        String language = getProperty(PROPERTY_LANGUAGE);
        String version = getProperty(PROPERTY_VERSION);

        // get the domainCode, an identifier that is used FAOStat-internally
        String domainCode = domain.getDomain_code();

        // create the document
        DataCiteJson document = new DataCiteJson();

        document.setVersion(version);
        document.setLanguage(language);
        document.setPublicationYear(FaoStatConstants.EARLIEST_PUBLICATION_YEAR);
        document.setResourceType(FaoStatConstants.RESOURCE_TYPE);

        // get source
        document.setSources(FaoStatDomainParser.parseSource(domainCode));
        document.setPublisher(document.getSources().getProvider());

        // get a readable name of the domain
        document.setTitles(FaoStatDomainParser.parseTitles(domain, language));

        // get bulk-download URL
        FaoBulkDownloads bulkDownloads = downloader.getBulkDownloads(domainCode);
        document.setFiles(FaoStatDomainParser.parseFiles(bulkDownloads));

        // get description
        FaoMetadata metadata = downloader.getMetaData(domainCode);
        document.setDescriptions(FaoStatDomainParser.parseDescriptions(metadata, language));

        // get URLs of all filters that can be applied to the domain
        document.setSubjects(getSubjectsOfDomain(version, language, domainCode));

        // get dates
        document.setDates(FaoStatDomainParser.parseDates(metadata, language));

        // get web links
        FaoDocuments documents = downloader.getDocuments(domainCode);
        document.setWebLinks(FaoStatDomainParser.parseWebLinks(documents, domain));

        // get contact person
        document.setContributors(FaoStatDomainParser.parseContributors(metadata));

        // get creator
        document.setCreators(FaoStatConstants.CREATORS);

        // create documentList TODO remove after SAI-112
        IJsonObject docWorkAround = new GsonObject((JsonObject) GsonUtils.getGson().toJsonTree(document));
        return Arrays.asList(docWorkAround);
    }


    private List<Subject> getSubjectsOfDomain(String domainCode, String version, String language)
    {
        // get URLs of all filters that can be applied to the domain
        FaoDimensions dimensions = downloader.getDimensions(domainCode);
        List<String> filterUrls = FaoStatDomainParser.parseFilterUrls(dimensions, version, language, domainCode);

        // initialize empty subjects list
        List<Subject> subjects = new LinkedList<>();

        // add every filter option to the subjects
        filterUrls.forEach((String filterUrl) -> {
            FaoFilters filters = downloader.getFilters(filterUrl);
            subjects.addAll(FaoStatDomainParser.parseSubjects(filters, language));
        });

        return subjects;
    }
}
