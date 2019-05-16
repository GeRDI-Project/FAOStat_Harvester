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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.gerdiproject.harvest.etls.AbstractETL;
import de.gerdiproject.harvest.etls.FaoStatETL;
import de.gerdiproject.harvest.fao.constants.FaoExtractorConstants;
import de.gerdiproject.harvest.fao.json.FaoBulkDownload;
import de.gerdiproject.harvest.fao.json.FaoDimension;
import de.gerdiproject.harvest.fao.json.FaoDocument;
import de.gerdiproject.harvest.fao.json.FaoDomain;
import de.gerdiproject.harvest.fao.json.FaoFilter;
import de.gerdiproject.harvest.fao.json.FaoMetadata;
import de.gerdiproject.harvest.fao.json.GenericFaoResponse;
import de.gerdiproject.harvest.utils.data.HttpRequester;

/**
 * This {@linkplain JsonArrayExtractor} implementation extracts all
 * {@linkplain FaoDomain}s of FAOSTAT.<br>
 * (http://fenixservices.fao.org/faostat/api/v1/en/groupsanddomains)
 *
 * @author Robin Weiss
 */
public class FaoStatExtractor extends AbstractIteratorExtractor<FaoStatDomainVO>
{
    // these protected fields are used by the inner iterator class
    protected final HttpRequester httpRequester = new HttpRequester();
    protected Iterator<FaoDomain> domainIterator;
    protected String baseUrl;

    private String version;
    private int domainCount = -1;


    @Override
    public void init(final AbstractETL<?, ?> etl)
    {
        super.init(etl);

        this.httpRequester.setCharset(etl.getCharset());
        this.baseUrl = String.format(
                           FaoExtractorConstants.BASE_URL,
                           ((FaoStatETL)etl).getLanguage()
                       );

        // get list of all domains
        final GenericFaoResponse<FaoDomain> domainsResponse = httpRequester.getObjectFromUrl(
                                                                  baseUrl + FaoExtractorConstants.GROUPS_AND_DOMAINS_URL,
                                                                  FaoExtractorConstants.DOMAIN_RESPONSE_TYPE);

        this.version = getVersion(domainsResponse.getData());
        this.domainCount = domainsResponse.getData().size();
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
        return domainCount;
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
    private String getVersion(final List<FaoDomain> domains)
    {
        final StringBuilder sb = new StringBuilder();

        for (final FaoDomain d : domains) {
            if (d.getDateUpdate() != null)
                sb.append(d.getDateUpdate());
        }

        return sb.toString();
    }


    @Override
    public void clear()
    {
        // nothing to clean up
    }


    /**
     * This Iterator iterates through {@linkplain FaoDomain}s and downloads additional metadata
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
            final FaoDomain domain = domainIterator.next();
            final String domainCode = domain.getDomainCode();
            final List<FaoDimension> dimensions = getDimensions(domainCode);

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
        private List<FaoDocument> getDocuments(final String domainCode)
        {
            final String url = String.format(FaoExtractorConstants.DOCUMENTS_URL, baseUrl, domainCode);
            final GenericFaoResponse<FaoDocument> response =
                httpRequester.getObjectFromUrl(url, FaoExtractorConstants.DOCUMENT_RESPONSE_TYPE);
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
        private List<FaoBulkDownload> getBulkDownloads(final String domainCode)
        {
            final String url = String.format(FaoExtractorConstants.BULK_DOWNLOADS_URL, baseUrl, domainCode);
            final GenericFaoResponse<FaoBulkDownload> response =
                httpRequester.getObjectFromUrl(url, FaoExtractorConstants.DIMENSION_RESPONSE_TYPE);
            return response.getData();
        }


        /**
         * Retrieves an array of metadata.
         *
         * @param domainCode a unique ID of the domain of which the metadata is to be retrieved
         *
         * @return an object representation of the JSON server response to a metadata request
         */
        private List<FaoMetadata> getMetaData(final String domainCode)
        {
            final String url = String.format(FaoExtractorConstants.METADATA_URL, baseUrl, domainCode);
            final GenericFaoResponse<FaoMetadata> response =
                httpRequester.getObjectFromUrl(url, FaoExtractorConstants.METADATA_RESPONSE_TYPE);
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
        private List<FaoDimension> getDimensions(final String domainCode)
        {
            final String url = String.format(FaoExtractorConstants.DIMENSIONS_URL, baseUrl, domainCode);
            final GenericFaoResponse<FaoDimension> response =
                httpRequester.getObjectFromUrl(url, FaoExtractorConstants.DIMENSION_RESPONSE_TYPE);
            return response.getData();
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
        private List<FaoFilter> getFilters(final List<FaoDimension> dimensions, final String domainCode)
        {
            final List<FaoFilter> filters = new LinkedList<>();

            final String filterUrlPrefix = baseUrl.substring(0, baseUrl.length() - 1);

            for (final FaoDimension d : dimensions) {

                // exclude the pure numbers of the years filter
                if (d.getId().equals("year"))
                    continue;

                // assemble filter URL
                final String filterUrl = filterUrlPrefix + d.getHref() + domainCode + FaoExtractorConstants.SHOW_LIST_SUFFIX;

                // get filters from URL
                final GenericFaoResponse<FaoFilter> response =
                    httpRequester.getObjectFromUrl(filterUrl, FaoExtractorConstants.FILTER_RESPONSE_TYPE);

                if (response != null)
                    filters.addAll(response.getData());
            }

            return filters;
        }
    }
}
