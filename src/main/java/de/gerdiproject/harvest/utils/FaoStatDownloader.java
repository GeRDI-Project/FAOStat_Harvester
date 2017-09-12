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
package de.gerdiproject.harvest.utils;

import de.gerdiproject.harvest.constants.FaoStatDownloaderConstants;
import de.gerdiproject.json.fao.FaoBulkDownloads;
import de.gerdiproject.json.fao.FaoDimensions;
import de.gerdiproject.json.fao.FaoDocuments;
import de.gerdiproject.json.fao.FaoDomains;
import de.gerdiproject.json.fao.FaoFilters;
import de.gerdiproject.json.fao.FaoMetadata;

/**
 * This class provides functions for downloading FAOSTAT JSON objects.
 *
 * @author Robin Weiss
 */
public class FaoStatDownloader
{
    private String version;
    private String language;
    private String domainCode;
    private String baseUrl;
    private final HttpRequester httpRequester = new HttpRequester();


    /**
     * Changes the "version" part of the FAOSTAT URL.
     *
     * @param version the "version" part of the FAOSTAT URL
     */
    public void setVersion(String version)
    {
        this.version = version;
        this.baseUrl = String.format(FaoStatDownloaderConstants.BASE_URL, version, language);
    }


    /**
     * Changes the "language" part of the FAOSTAT URL.
     *
     * @param language the "language" part of the FAOSTAT URL
     */
    public void setLanguage(String language)
    {
        this.language = language;
        this.baseUrl = String.format(FaoStatDownloaderConstants.BASE_URL, version, language);
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
     * Retrieves an array of "domains". Each domain represents a unique dataset.
     *
     * @return an object representation of the JSON server response to a groupsAndDomains request
     */
    public FaoDomains getDomains()
    {
        String url = baseUrl + FaoStatDownloaderConstants.GROUPS_AND_DOMAINS_URL;
        FaoDomains response = httpRequester.getObjectFromUrl(url, FaoDomains.class);
        return response;
    }


    /**
     * Retrieves an array of "documents". Each document represents a PDF download link that is related
     * to a domain.
     *
     * @return an object representation of the JSON server response to a documents request
     */
    public FaoDocuments getDocuments()
    {
        String url = String.format(FaoStatDownloaderConstants.DOCUMENTS_URL, baseUrl, domainCode);
        FaoDocuments response = httpRequester.getObjectFromUrl(url, FaoDocuments.class);
        return response;
    }


    /**
     * Retrieves an array of "bulk-downloads". Each bulk-download represents a ZIP download link that
     * allows to download complete datasets.
     *
     * @return an object representation of the JSON server response to a bulkDownloads request
     */
    public FaoBulkDownloads getBulkDownloads()
    {
        String url = String.format(FaoStatDownloaderConstants.BULK_DOWNLOADS_URL, baseUrl, domainCode);
        FaoBulkDownloads response = httpRequester.getObjectFromUrl(url, FaoBulkDownloads.class);
        return response;
    }


    /**
     * Retrieves an array of metadata.
     *
     * @return an object representation of the JSON server response to a metadata request
     */
    public FaoMetadata getMetaData()
    {
        String url = String.format(FaoStatDownloaderConstants.METADATA_URL, baseUrl, domainCode);
        FaoMetadata response = httpRequester.getObjectFromUrl(url, FaoMetadata.class);
        return response;
    }


    /**
     * Retrieves an array of "dimensions". Each dimension represents a filter category for the
     * domain dataset.
     *
     * @return an object representation of the JSON server response to a dimensions request
     */
    public FaoDimensions getDimensions()
    {
        String url = String.format(FaoStatDownloaderConstants.DIMENSIONS_URL, baseUrl, domainCode);
        FaoDimensions response = httpRequester.getObjectFromUrl(url, FaoDimensions.class);
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
    public FaoFilters getFilters(String filterUrl)
    {
        FaoFilters response = httpRequester.getObjectFromUrl(filterUrl, FaoFilters.class);
        return response;
    }
}
