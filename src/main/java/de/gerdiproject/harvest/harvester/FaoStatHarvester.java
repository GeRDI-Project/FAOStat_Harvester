/**
 * Copyright © 2017 Robin Weiss (http://www.gerdi-project.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.gerdiproject.harvest.harvester;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.gerdiproject.harvest.IDocument;
import de.gerdiproject.harvest.fao.constants.FaoDataCiteConstants;
import de.gerdiproject.harvest.fao.constants.FaoParameterConstants;
import de.gerdiproject.harvest.fao.json.BulkDownloadResponse;
import de.gerdiproject.harvest.fao.json.DimensionsResponse;
import de.gerdiproject.harvest.fao.json.DocumentsResponse;
import de.gerdiproject.harvest.fao.json.DomainsResponse;
import de.gerdiproject.harvest.fao.json.DomainsResponse.Domain;
import de.gerdiproject.harvest.fao.json.FiltersResponse;
import de.gerdiproject.harvest.fao.json.MetadataResponse;
import de.gerdiproject.harvest.fao.utils.DomainParser;
import de.gerdiproject.harvest.fao.utils.FaoStatDownloader;
import de.gerdiproject.json.datacite.DataCiteJson;
import de.gerdiproject.json.datacite.Subject;


/**
 * A harvester for FAOSTAT (http://www.fao.org/faostat/en/#data).
 *
 * @author Robin Weiss
 */
public class FaoStatHarvester extends AbstractListHarvester<Domain>
{
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
    }


    @Override
    protected Collection<Domain> loadEntries()
    {
        DomainsResponse domainsObj = downloader.getDomains();
        return domainsObj.getData();
    }


    @Override
    protected List<IDocument> harvestEntry(Domain domain)
    {
        String language = getProperty(FaoParameterConstants.LANGUAGE_KEY);
        String version = getProperty(FaoParameterConstants.VERSION_KEY);

        // get the domainCode, an identifier that is used FAOStat-internally
        String domainCode = domain.getDomain_code();

        // set the downloader's domain code
        downloader.setDomainCode(domainCode);

        // create the document
        DataCiteJson document = new DataCiteJson(domain.createIdentifier(language));

        document.setVersion(version);
        document.setLanguage(language);
        document.setRepositoryIdentifier(FaoDataCiteConstants.REPOSITORY_ID);
        document.setPublicationYear(FaoDataCiteConstants.EARLIEST_PUBLICATION_YEAR);
        document.setResourceType(FaoDataCiteConstants.RESOURCE_TYPE);
        document.setFormats(FaoDataCiteConstants.FORMATS);
        document.setResearchDisciplines(FaoDataCiteConstants.DISCIPLINES);

        // get source
        document.setPublisher(FaoDataCiteConstants.PROVIDER);

        // get a readable name of the domain
        document.setTitles(DomainParser.parseTitles(domain, language));

        // get bulk-download URL
        BulkDownloadResponse bulkDownloads = downloader.getBulkDownloads();
        document.setResearchDataList(DomainParser.parseFiles(bulkDownloads));

        // get description
        MetadataResponse metadata = downloader.getMetaData();
        document.setDescriptions(DomainParser.parseDescriptions(metadata, language));

        // get URLs of all filters that can be applied to the domain
        document.setSubjects(getSubjectsOfDomain(domainCode, version, language));

        // get dates
        document.setDates(DomainParser.parseDates(metadata, language));

        // get web links
        DocumentsResponse documents = downloader.getDocuments();
        document.setWebLinks(DomainParser.parseWebLinks(documents, domain));

        // get contact person
        document.setContributors(DomainParser.parseContributors(metadata));

        // get creator
        document.setCreators(FaoDataCiteConstants.CREATORS);

        return Arrays.asList(document);
    }


    /**
     * Iterates through a list of so called dimensions of a domain. Dimensions
     * are filter categories for the dataset. Each dimension contains an array
     * of filter strings that are converted to subjects.
     *
     * @param domainCode the domainCode of the domain for which the subjects are
     *            harvested
     * @param version the version of FAOSTAT that is to be harvested
     * @param language the language for which the subjects are retrieved
     *
     * @return a list of DataCite subjects
     */
    private List<Subject> getSubjectsOfDomain(String domainCode, String version, String language)
    {
        // get URLs of all filters that can be applied to the domain
        DimensionsResponse dimensions = downloader.getDimensions();
        List<String> filterUrls = DomainParser.parseFilterUrls(dimensions, version, language, domainCode);

        // initialize empty subjects list
        List<Subject> subjects = new LinkedList<>();

        // add every filter option to the subjects
        filterUrls.forEach((String filterUrl) -> {
            FiltersResponse filters = downloader.getFilters(filterUrl);
            subjects.addAll(DomainParser.parseSubjects(filters, language));
        });

        return subjects;
    }
}
