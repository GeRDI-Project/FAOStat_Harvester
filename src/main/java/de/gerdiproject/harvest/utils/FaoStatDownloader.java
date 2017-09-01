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



import de.gerdiproject.json.fao.FaoBulkDownloads;
import de.gerdiproject.json.fao.FaoDimensions;
import de.gerdiproject.json.fao.FaoDocuments;
import de.gerdiproject.json.fao.FaoDomains;
import de.gerdiproject.json.fao.FaoFilters;
import de.gerdiproject.json.fao.FaoMetadata;

public class FaoStatDownloader
{
    private static final String BASE_URL = "http://fenixservices.fao.org/faostat/api/%s/%s/";
    private static final String GROUPS_AND_DOMAINS = "groupsanddomains?section=download";
    private static final String DOCUMENTS = "%sdocuments/%s/";
    private static final String BULK_DOWNLOADS = "%sbulkdownloads/%s/";
    private static final String METADATA = "%smetadata/%s/";
    private static final String DIMENSIONS = "%sdimensions/%s/?full=true";

    private final String baseUrl;
    private final HttpRequester httpRequester;

    public FaoStatDownloader(String version, String language)
    {
        this.baseUrl = String.format(BASE_URL, version, language);
        this.httpRequester = new HttpRequester();
    }

    public FaoDomains getDomains()
    {
        String url = baseUrl + GROUPS_AND_DOMAINS;
        FaoDomains response = httpRequester.getObjectFromUrl(url, FaoDomains.class);
        return response;
    }


    public FaoDocuments getDocuments(String domainCode)
    {
        String url = String.format(DOCUMENTS, baseUrl, domainCode);
        FaoDocuments response = httpRequester.getObjectFromUrl(url, FaoDocuments.class);
        return response;
    }


    public FaoBulkDownloads getBulkDownloads(String domainCode)
    {
        String url = String.format(BULK_DOWNLOADS, baseUrl, domainCode);
        FaoBulkDownloads response = httpRequester.getObjectFromUrl(url, FaoBulkDownloads.class);
        return response;
    }


    public FaoMetadata getMetaData(String domainCode)
    {
        String url = String.format(METADATA, baseUrl, domainCode);
        FaoMetadata response = httpRequester.getObjectFromUrl(url, FaoMetadata.class);
        return response;
    }


    public FaoDimensions getDimensions(String domainCode)
    {
        String url = String.format(DIMENSIONS, baseUrl, domainCode);
        FaoDimensions response = httpRequester.getObjectFromUrl(url, FaoDimensions.class);
        return response;
    }


    public FaoFilters getFilters(String filterUrl)
    {
        FaoFilters response = httpRequester.getObjectFromUrl(filterUrl, FaoFilters.class);
        return response;
    }
}
