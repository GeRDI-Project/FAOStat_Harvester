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
package de.gerdiproject.harvest.fao.utils;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import de.gerdiproject.harvest.fao.constants.FaoDownloaderConstants;
import de.gerdiproject.harvest.fao.json.BulkDownloadResponse;
import de.gerdiproject.harvest.fao.json.DimensionsResponse;
import de.gerdiproject.harvest.fao.json.DocumentsResponse;
import de.gerdiproject.harvest.fao.json.FiltersResponse;
import de.gerdiproject.harvest.fao.json.MetadataResponse;
import de.gerdiproject.harvest.utils.data.HttpRequester;

/**
 * This class provides functions for downloading FAOSTAT JSON objects.
 *
 * @author Robin Weiss
 */
public class FaoStatDownloader
{
    private String domainCode;
    private String baseUrl;
    private final HttpRequester httpRequester;

    public FaoStatDownloader()
    {
        this.httpRequester = new HttpRequester(new Gson(), StandardCharsets.UTF_8);
    }


    /**
     * Changes the "language" part of the FAOSTAT URL.
     *
     * @param language the "language" part of the FAOSTAT URL
     */
    public void setLanguage(String language)
    {
        this.baseUrl = String.format(FaoDownloaderConstants.BASE_URL, language);
    }


    /**
     * Changes the domainCode, used for most HTTP requests.
     *
     * @param domainCode a unique ID of a domain
     */
    public void setDomainCode(String domainCode)
    {
        this.domainCode = domainCode;
    }


    /**
     * Retrieves an array of "documents". Each document represents a PDF download link that is related
     * to a domain.
     *
     * @return an object representation of the JSON server response to a documents request
     */
    public DocumentsResponse getDocuments()
    {
        String url = String.format(FaoDownloaderConstants.DOCUMENTS_URL, baseUrl, domainCode);
        DocumentsResponse response = httpRequester.getObjectFromUrl(url, DocumentsResponse.class);
        return response;
    }


    /**
     * Retrieves an array of "bulk-downloads". Each bulk-download represents a ZIP download link that
     * allows to download complete datasets.
     *
     * @return an object representation of the JSON server response to a bulkDownloads request
     */
    public BulkDownloadResponse getBulkDownloads()
    {
        String url = String.format(FaoDownloaderConstants.BULK_DOWNLOADS_URL, baseUrl, domainCode);
        BulkDownloadResponse response = httpRequester.getObjectFromUrl(url, BulkDownloadResponse.class);
        return response;
    }


    /**
     * Retrieves an array of metadata.
     *
     * @return an object representation of the JSON server response to a metadata request
     */
    public MetadataResponse getMetaData()
    {
        String url = String.format(FaoDownloaderConstants.METADATA_URL, baseUrl, domainCode);
        MetadataResponse response = httpRequester.getObjectFromUrl(url, MetadataResponse.class);
        return response;
    }


    /**
     * Retrieves an array of "dimensions". Each dimension represents a filter category for the
     * domain dataset.
     *
     * @return an object representation of the JSON server response to a dimensions request
     */
    public DimensionsResponse getDimensions()
    {
        String url = String.format(FaoDownloaderConstants.DIMENSIONS_URL, baseUrl, domainCode);
        DimensionsResponse response = httpRequester.getObjectFromUrl(url, DimensionsResponse.class);
        return response;
    }


    /**
     * Retrieves an array of "filters". Each filter is a term that can be used to filter the
     * dataset of a domain.
     *
     * @param filterUrl a complete URL that leads to an array of filter terms of the dataset
     *
     * @return an object representation of the JSON server response to a request of the filterUrl
     */
    public FiltersResponse getFilters(String filterUrl)
    {
        FiltersResponse response = httpRequester.getObjectFromUrl(filterUrl, FiltersResponse.class);
        return response;
    }
}
