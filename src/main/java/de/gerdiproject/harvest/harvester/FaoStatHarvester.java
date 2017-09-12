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
import de.gerdiproject.harvest.IDocument;
import de.gerdiproject.harvest.constants.FaoStatDataCiteConstants;
import de.gerdiproject.harvest.utils.FaoStatDomainParser;
import de.gerdiproject.json.datacite.DataCiteJson;
import de.gerdiproject.json.datacite.Subject;
import de.gerdiproject.json.fao.FaoBulkDownloads;
import de.gerdiproject.json.fao.FaoDimensions;
import de.gerdiproject.json.fao.FaoDocuments;
import de.gerdiproject.json.fao.FaoDomains;
import de.gerdiproject.json.fao.FaoDomains.Domain;
import de.gerdiproject.json.fao.FaoFilters;
import de.gerdiproject.json.fao.FaoMetadata;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * A harvester for FAOSTAT (http://www.fao.org/faostat/en/#data).
 *
 * @author Robin Weiss
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

        downloader = new FaoStatDownloader();
        setProperty(PROPERTY_VERSION, DEFAULT_VERSION);
        setProperty(PROPERTY_LANGUAGE, DEFAULT_LANGUAGE);
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
    public void setProperty(String key, String value)
    {
        super.setProperty(key, value);

        if (key.equals(PROPERTY_VERSION))
            downloader.setVersion(value);

        if (key.equals(PROPERTY_LANGUAGE))
            downloader.setLanguage(value);
    }


    @Override
    protected List<IDocument> harvestEntry(Domain domain)
    {
        String language = getProperty(PROPERTY_LANGUAGE);
        String version = getProperty(PROPERTY_VERSION);

        // get the domainCode, an identifier that is used FAOStat-internally
        String domainCode = domain.getDomain_code();

        // set the downloader's domain code
        downloader.setDomainCode(domainCode);

        // create the document
        DataCiteJson document = new DataCiteJson();

        document.setVersion(version);
        document.setLanguage(language);
        document.setPublicationYear(FaoStatDataCiteConstants.EARLIEST_PUBLICATION_YEAR);
        document.setResourceType(FaoStatDataCiteConstants.RESOURCE_TYPE);
        document.setFormats(FaoStatDataCiteConstants.FORMATS);

        // get source
        document.setSources(FaoStatDomainParser.parseSource(domainCode));
        document.setPublisher(document.getSources().getProvider());

        // get a readable name of the domain
        document.setTitles(FaoStatDomainParser.parseTitles(domain, language));

        // get bulk-download URL
        FaoBulkDownloads bulkDownloads = downloader.getBulkDownloads();
        document.setFiles(FaoStatDomainParser.parseFiles(bulkDownloads));

        // get description
        FaoMetadata metadata = downloader.getMetaData();
        document.setDescriptions(FaoStatDomainParser.parseDescriptions(metadata, language));

        // get URLs of all filters that can be applied to the domain
        document.setSubjects(getSubjectsOfDomain(domainCode, version, language));

        // get dates
        document.setDates(FaoStatDomainParser.parseDates(metadata, language));

        // get web links
        FaoDocuments documents = downloader.getDocuments();
        document.setWebLinks(FaoStatDomainParser.parseWebLinks(documents, domain));

        // get contact person
        document.setContributors(FaoStatDomainParser.parseContributors(metadata));

        // get creator
        document.setCreators(FaoStatDataCiteConstants.CREATORS);

        return Arrays.asList(document);
    }


    /**
     * Iterates through a list of so called dimensions of a domain. Dimensions are filter categories
     * for the dataset. Each dimension contains an array of filter strings that are converted to subjects.
     *
     * @param domainCode the domainCode of the domain for which the subjects are harvested
     * @param version the version of FAOSTAT that is to be harvested
     * @param language the language for which the subjects are retrieved
     *
     * @return a list of DataCite subjects
     */
    private List<Subject> getSubjectsOfDomain(String domainCode, String version, String language)
    {
        // get URLs of all filters that can be applied to the domain
        FaoDimensions dimensions = downloader.getDimensions();
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
