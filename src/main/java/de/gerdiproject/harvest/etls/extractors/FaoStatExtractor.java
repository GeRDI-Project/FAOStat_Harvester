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
package de.gerdiproject.harvest.etls.extractors;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;

import de.gerdiproject.harvest.etls.AbstractETL;
import de.gerdiproject.harvest.etls.FaoStatETL;
import de.gerdiproject.harvest.fao.constants.FaoDownloaderConstants;
import de.gerdiproject.harvest.fao.json.BulkDownloadResponse;
import de.gerdiproject.harvest.fao.json.BulkDownloadResponse.BulkDownload;
import de.gerdiproject.harvest.fao.json.DimensionsResponse;
import de.gerdiproject.harvest.fao.json.DimensionsResponse.Dimension;
import de.gerdiproject.harvest.fao.json.DocumentsResponse;
import de.gerdiproject.harvest.fao.json.DocumentsResponse.Document;
import de.gerdiproject.harvest.fao.json.DomainsResponse;
import de.gerdiproject.harvest.fao.json.DomainsResponse.Domain;
import de.gerdiproject.harvest.fao.json.FiltersResponse;
import de.gerdiproject.harvest.fao.json.FiltersResponse.Filter;
import de.gerdiproject.harvest.fao.json.MetadataResponse;
import de.gerdiproject.harvest.fao.json.MetadataResponse.Metadata;
import de.gerdiproject.harvest.utils.data.HttpRequester;

/**
 * This {@linkplain JsonArrayExtractor} implementation extracts all
 * {@linkplain Domain}s of FAOSTAT.<br>
 * (http://fenixservices.fao.org/faostat/api/v1/en/groupsanddomains)
 *
 * @author Robin Weiss
 */
public class FaoStatExtractor extends AbstractIteratorExtractor<FaoStatDomainVO>
{
    private final HttpRequester httpRequester;

    private String version;
    private Iterator<Domain> domainIterator;
    private String baseUrl;
    private int size = -1;

    /**
     * Simple constructor.
     */
    public FaoStatExtractor()
    {
        this.httpRequester = new HttpRequester(new Gson(), StandardCharsets.UTF_8);
    }


    @Override
    public void init(AbstractETL<?, ?> etl)
    {
        super.init(etl);

        this.httpRequester.setCharset(etl.getCharset());
        this.baseUrl = String.format(
                           FaoDownloaderConstants.BASE_URL,
                           ((FaoStatETL)etl).getLanguage()
                       );

        // get list of all domains
        final DomainsResponse domainsResponse = httpRequester.getObjectFromUrl(
                                                    baseUrl + FaoDownloaderConstants.GROUPS_AND_DOMAINS_URL,
                                                    DomainsResponse.class);

        this.version = getVersion(domainsResponse.getData());
        this.size = domainsResponse.getData().size();
        this.domainIterator = domainsResponse.getData().iterator();
    }


    @Override
    public String getUniqueVersionString()
    {
        return version;
    }



    @Override
    public int size()
    {
        return size;
    }



    @Override
    protected Iterator<FaoStatDomainVO> extractAll() throws ExtractorException
    {
        return new FaoStatDomainIterator();
    }


    /**
     * Concatenates all update dates of all domains to generate a unique version string.
     *
     * @param domains the domains from which the version must be derived
     *
     * @return a unique version string for all domains
     */
    private String getVersion(List<Domain> domains)
    {
        StringBuilder sb = new StringBuilder();

        for (Domain d : domains) {
            if (d.getDate_update() != null)
                sb.append(d.getDate_update());
        }

        return sb.toString();
    }


    /**
     * Retrieves an array of "filters". Each filter is a term that can be used to filter the
     * dataset of a domain.
     *
     * @param dimensions the dimensions of the domain
     * @param domainCode a unique ID of the domain of which the metadata is to be retrieved
     *
     * @return an object representation of the JSON server response to a request of the filterUrl
     */
    private List<Filter> getFilters(List<Dimension> dimensions, String domainCode)
    {
        final List<Filter> filters = new LinkedList<>();

        final String filterUrlPrefix = baseUrl.substring(0, baseUrl.length() - 1);

        for (Dimension d : dimensions) {

            // exclude the pure numbers of the years filter
            if (d.getId().equals("year"))
                continue;

            // assemble filter URL
            final String filterUrl = filterUrlPrefix + d.getHref() + domainCode + FaoDownloaderConstants.SHOW_LIST_SUFFIX;

            // get filters from URL
            final FiltersResponse response = httpRequester.getObjectFromUrl(filterUrl, FiltersResponse.class);

            if (response != null)
                filters.addAll(response.getData());
        }

        return filters;
    }


    /**
     * This Iterator iterates through {@linkplain Domain}s and downloads additional metadata
     * in order to assemble a {@linkplain FaoStatDomainVO}.
     *
     * @author Robin Weiss
     */
    private class FaoStatDomainIterator implements Iterator<FaoStatDomainVO>
    {
        @Override
        public boolean hasNext()
        {
            return domainIterator.hasNext();
        }


        @Override
        public FaoStatDomainVO next()
        {
            final Domain domain = domainIterator.next();
            final String domainCode = domain.getDomain_code();
            final List<Dimension> dimensions = getDimensions(domainCode);

            return new FaoStatDomainVO(
                       domain,
                       getBulkDownloads(domainCode),
                       getMetaData(domainCode),
                       getDocuments(domainCode),
                       dimensions,
                       getFilters(dimensions, domainCode));
        }

        /**
         * Retrieves an array of "documents". Each document represents a PDF download link that is related
         * to a domain.
         *
         * @param domainCode a unique ID of the domain of which the metadata is to be retrieved
         *
         * @return an object representation of the JSON server response to a documents request
         */
        private List<Document> getDocuments(String domainCode)
        {
            String url = String.format(FaoDownloaderConstants.DOCUMENTS_URL, baseUrl, domainCode);
            DocumentsResponse response = httpRequester.getObjectFromUrl(url, DocumentsResponse.class);
            return response.getData();
        }


        /**
         * Retrieves an array of "bulk-downloads". Each bulk-download represents a ZIP download link that
         * allows to download complete datasets.
         *
         * @param domainCode a unique ID of the domain of which the metadata is to be retrieved
         *
         * @return an object representation of the JSON server response to a bulkDownloads request
         */
        private List<BulkDownload> getBulkDownloads(String domainCode)
        {
            String url = String.format(FaoDownloaderConstants.BULK_DOWNLOADS_URL, baseUrl, domainCode);
            BulkDownloadResponse response = httpRequester.getObjectFromUrl(url, BulkDownloadResponse.class);
            return response.getData();
        }


        /**
         * Retrieves an array of metadata.
         *
         * @param domainCode a unique ID of the domain of which the metadata is to be retrieved
         *
         * @return an object representation of the JSON server response to a metadata request
         */
        private List<Metadata> getMetaData(String domainCode)
        {
            String url = String.format(FaoDownloaderConstants.METADATA_URL, baseUrl, domainCode);
            MetadataResponse response = httpRequester.getObjectFromUrl(url, MetadataResponse.class);
            return response.getData();
        }


        /**
         * Retrieves an array of "dimensions". Each dimension represents a filter category for the
         * domain dataset.
         *
         * @param domainCode a unique ID of the domain of which the metadata is to be retrieved
         *
         * @return an object representation of the JSON server response to a dimensions request
         */
        private List<Dimension> getDimensions(String domainCode)
        {
            String url = String.format(FaoDownloaderConstants.DIMENSIONS_URL, baseUrl, domainCode);
            DimensionsResponse response = httpRequester.getObjectFromUrl(url, DimensionsResponse.class);
            return response.getData();
        }
    }
}
