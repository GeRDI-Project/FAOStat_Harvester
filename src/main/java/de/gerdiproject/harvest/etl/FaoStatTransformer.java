/*
 *  Copyright © 2018 Robin Weiss (http://www.gerdi-project.de/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
package de.gerdiproject.harvest.etl;

import java.util.LinkedList;
import java.util.List;

import de.gerdiproject.harvest.etls.AbstractETL;
import de.gerdiproject.harvest.etls.transformers.AbstractIteratorTransformer;
import de.gerdiproject.harvest.fao.constants.FaoDataCiteConstants;
import de.gerdiproject.harvest.fao.json.BulkDownloadResponse;
import de.gerdiproject.harvest.fao.json.DimensionsResponse;
import de.gerdiproject.harvest.fao.json.DocumentsResponse;
import de.gerdiproject.harvest.fao.json.DomainsResponse.Domain;
import de.gerdiproject.harvest.fao.json.FiltersResponse;
import de.gerdiproject.harvest.fao.json.MetadataResponse;
import de.gerdiproject.harvest.fao.utils.DomainParser;
import de.gerdiproject.harvest.fao.utils.FaoStatDownloader;
import de.gerdiproject.json.datacite.DataCiteJson;
import de.gerdiproject.json.datacite.Subject;

/**
 * This transformer retrieves metadata from a {@linkplain Domain} object
 * but also downloads more metadata that corresponds to the domain and adds it
 * to the document.
 *
 * @author Robin Weiss
 */
public class FaoStatTransformer extends AbstractIteratorTransformer<Domain, DataCiteJson>
{
    private final FaoStatDownloader downloader;
    private String language;


    /**
     * Constructor that initializes a helper class.
     */
    public FaoStatTransformer()
    {
        super();
        this.downloader = new FaoStatDownloader();
    }


    @Override
    public void init(AbstractETL<?, ?> etl)
    {
        super.init(etl);
        language = ((FaoStatETL)etl).getLanguage();
    }


    @Override
    protected DataCiteJson transformElement(Domain source)
    {
        // get the domainCode, an identifier that is used FAOStat-internally
        final String domainCode = source.getDomain_code();

        // set the downloader's domain code
        downloader.setLanguage(language);
        downloader.setDomainCode(domainCode);

        // create the document
        final DataCiteJson document = new DataCiteJson(source.createIdentifier(language));

        document.setLanguage(language);
        document.setRepositoryIdentifier(FaoDataCiteConstants.REPOSITORY_ID);
        document.setPublicationYear(FaoDataCiteConstants.EARLIEST_PUBLICATION_YEAR);
        document.setResourceType(FaoDataCiteConstants.RESOURCE_TYPE);
        document.setFormats(FaoDataCiteConstants.FORMATS);
        document.setResearchDisciplines(FaoDataCiteConstants.DISCIPLINES);

        // get source
        document.setPublisher(FaoDataCiteConstants.PROVIDER);

        // get a readable name of the domain
        document.setTitles(DomainParser.parseTitles(source, language));

        // get bulk-download URL
        final BulkDownloadResponse bulkDownloads = downloader.getBulkDownloads();
        document.setResearchDataList(DomainParser.parseFiles(bulkDownloads));

        // get description
        final MetadataResponse metadata = downloader.getMetaData();
        document.setDescriptions(DomainParser.parseDescriptions(metadata, language));

        // get URLs of all filters that can be applied to the domain
        document.setSubjects(getSubjectsOfDomain(domainCode));

        // get dates
        document.setDates(DomainParser.parseDates(metadata, language));

        // get web links
        final DocumentsResponse documents = downloader.getDocuments();
        document.setWebLinks(DomainParser.parseWebLinks(documents, source));

        // get contact person
        document.setContributors(DomainParser.parseContributors(metadata));

        // get creator
        document.setCreators(FaoDataCiteConstants.CREATORS);

        return document;
    }


    /**
     * Iterates through a list of so called dimensions of a domain. Dimensions
     * are filter categories for the dataset. Each dimension contains an array
     * of filter strings that are converted to subjects.
     *
     * @param domainCode the domainCode of the domain for which the subjects are
     *            harvested
     *
     * @return a list of DataCite subjects
     */
    private List<Subject> getSubjectsOfDomain(String domainCode)
    {
        // get URLs of all filters that can be applied to the domain
        final DimensionsResponse dimensions = downloader.getDimensions();
        final List<String> filterUrls = DomainParser.parseFilterUrls(dimensions, language, domainCode);

        // initialize empty subjects list
        final List<Subject> subjects = new LinkedList<>();

        // add every filter option to the subjects
        filterUrls.forEach((String filterUrl) -> {
            FiltersResponse filters = downloader.getFilters(filterUrl);
            subjects.addAll(DomainParser.parseSubjects(filters, language));
        });

        return subjects;
    }
}
