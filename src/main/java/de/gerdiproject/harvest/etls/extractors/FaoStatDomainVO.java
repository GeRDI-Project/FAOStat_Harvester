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
import de.gerdiproject.harvest.fao.json.FaoFilter;
import de.gerdiproject.harvest.fao.json.FaoMetadata;
import lombok.Value;

/**
 * This class is a value object that contains all elements of FAOSTAT server
 * responses regarding a {@linkplain Domain}.
 *
 * @author Robin Weiss
 *
 */
@Value
public class FaoStatDomainVO
{
    private final Domain domain;
    private final List<BulkDownload> bulkDownloads;
    private final List<FaoMetadata> metadata;
    private final List<Document> documents;
    private final List<Dimension> dimensions;
    private final List<FaoFilter> filters;
}
