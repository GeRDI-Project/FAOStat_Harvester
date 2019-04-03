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

import java.util.List;

import de.gerdiproject.harvest.fao.json.BulkDownloadResponse.BulkDownload;
import de.gerdiproject.harvest.fao.json.DimensionsResponse.Dimension;
import de.gerdiproject.harvest.fao.json.DocumentsResponse.Document;
import de.gerdiproject.harvest.fao.json.DomainsResponse.Domain;
import de.gerdiproject.harvest.fao.json.FiltersResponse.Filter;
import de.gerdiproject.harvest.fao.json.MetadataResponse.Metadata;

/**
 * This class is a value object that contains all elements of FAOSTAT server
 * responses regarding a {@linkplain Domain}.
 *
 * @author Robin Weiss
 *
 */
public class FaoStatDomainVO
{
    private final Domain domain;
    private final List<BulkDownload> bulkDownloads;
    private final List<Metadata> metadata;
    private final List<Document> documents;
    private final List<Dimension> dimensions;
    private final List<Filter> filters;

    /**
     * Constructor that requires all values.
     *
     * @param domain the domain to which the rest of the values belong
     * @param bulkdownloads a list of bulk downloads of the domain
     * @param metadata a list of metadata of the domain
     * @param documents a list of documents of the domain
     * @param dimensions a list of dimensions of the domain
     * @param filters a list of filters of the domain
     */
    public FaoStatDomainVO(Domain domain, List<BulkDownload> bulkdownloads, List<Metadata> metadata, List<Document> documents, List<Dimension> dimensions, List<Filter> filters)
    {
        this.domain = domain;
        this.bulkDownloads = bulkdownloads;
        this.metadata = metadata;
        this.documents = documents;
        this.dimensions = dimensions;
        this.filters = filters;
    }


    public Domain getDomain()
    {
        return domain;
    }


    public List<BulkDownload> getBulkDownloads()
    {
        return bulkDownloads;
    }


    public List<Metadata> getMetadata()
    {
        return metadata;
    }


    public List<Document> getDocuments()
    {
        return documents;
    }


    public List<Dimension> getDimensions()
    {
        return dimensions;
    }


    public List<Filter> getFilters()
    {
        return filters;
    }
}
