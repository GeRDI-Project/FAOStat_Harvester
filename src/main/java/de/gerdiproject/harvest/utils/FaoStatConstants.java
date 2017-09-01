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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.gerdiproject.json.datacite.Creator;
import de.gerdiproject.json.datacite.ResourceType;
import de.gerdiproject.json.datacite.Description.DescriptionType;
import de.gerdiproject.json.datacite.ResourceType.GeneralResourceType;
import de.gerdiproject.json.datacite.WebLink;
import de.gerdiproject.json.datacite.WebLink.WebLinkType;
import de.gerdiproject.json.fao.FaoMetadata;

/**
 * This static class contains constants that are used for harvesting FAOSTAT.
 *
 * @author Robin Weiss
 */
public class FaoStatConstants
{
    // RESOURCE TYPE
    public static final ResourceType RESOURCE_TYPE = createResourceType();

    // CREATOR
    public static final List<Creator> CREATORS = createCreators();

    // SOURCE
    public static final String PROVIDER = "Food and Agriculture Organization of the United Nations (FAO)";
    public static final String PROVIDER_URI = "http://www.fao.org/faostat/en/#home";

    // CONTRIBUTORS
    public static final String METADATA_CONTACT_NAME = "Contact name";
    public static final String METADATA_CONTACT_ORGANISATION = "Contact organisation";
    public static final short EARLIEST_PUBLICATION_YEAR = 1961;

    // WEB LINKS
    public static final String VIEW_URL_PREFIX = "http://www.fao.org/faostat/en/#data/";
    public static final WebLink LOGO_WEB_LINK = createLogoWebLink();
    public static final String TEMPLATE_DOCUMENT_NAME = "About";

    // DATES
    public static final String META_DATA_TIME_COVERAGE = "Time coverage";
    public static final String META_DATA_LAST_UPDATE = "Metadata last update";
    public static final Pattern TIME_COVERAGE_PATTERN = Pattern.compile("(\\d\\d\\d\\d)");
    public static final String DATE_PARSE_ERROR_PREFIX = "Could not parse date: ";

    // DESCRIPTIONS
    public static final String DESCRIPTION_FORMAT = "%s:%n%s";
    public static final Map<String, DescriptionType> RELEVANT_DESCRIPTIONS = createRelevantDescriptions();

    // FORMATS
    public static final List<String> FORMATS = Collections.unmodifiableList(Arrays.asList("CSV"));

    // Download URLS
    public static final String BASE_URL = "http://fenixservices.fao.org/faostat/api/%s/%s/";
    public static final String GROUPS_AND_DOMAINS_URL = "groupsanddomains?section=download";
    public static final String DOCUMENTS_URL = "%sdocuments/%s/";
    public static final String BULK_DOWNLOADS_URL = "%sbulkdownloads/%s/";
    public static final String METADATA_URL = "%smetadata/%s/";
    public static final String DIMENSIONS_URL = "%sdimensions/%s/?full=true";



    /**
     * Initializes a map of metadata names that contain descriptions that are relevant for documents.
     * @return a map of {@linkplain FaoMetadata} metadata_label field values
     */
    private static Map<String, DescriptionType> createRelevantDescriptions()
    {
        Map<String, DescriptionType> relavantDescriptions = new HashMap<>();
        relavantDescriptions.put("Data description", DescriptionType.Abstract);
        relavantDescriptions.put("Statistical concepts and definitions", DescriptionType.TechnicalInfo);
        relavantDescriptions.put("Documentation on methodology", DescriptionType.Methods);
        relavantDescriptions.put("Quality documentation", DescriptionType.Methods);
        return relavantDescriptions;
    }


    /**
     * Initializes a WebLink that leads to the FAOSTAT logo.
     * @return a link to the FAOSTAT logo
     */
    private static WebLink createLogoWebLink()
    {
        WebLink logoLink = new WebLink("http://data.fao.org/developers/api/catalog/resource/findDatastream?authKey=d30aebf0-ab2a-11e1-afa6-0800200c9a66&version=1.0&type=image&database=faostat&resource=logo&datastream=logo");
        logoLink.setType(WebLinkType.ProviderLogoURL);
        return logoLink;
    }


    /**
     * Initializes a Creator dummy for all FAOSTAT documents.
     * @return a Creator that has "FAO" as name
     */
    private static List<Creator> createCreators()
    {
        Creator creator = new Creator(PROVIDER);
        return Arrays.asList(creator);
    }


    /**
     * Initializes the only ResourceType of all FAOSTAT documents.
     * @return a ResourceType representing CSV datasets
     */
    private static ResourceType createResourceType()
    {
        ResourceType resType = new ResourceType();
        resType.setGeneralType(GeneralResourceType.Dataset);
        resType.setValue("CSV");
        return resType;
    }

    /**
     * Private constructor, because this is a static class.
     */
    private FaoStatConstants()
    {
    }
}
