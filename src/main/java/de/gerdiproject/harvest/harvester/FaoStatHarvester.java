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
package de.gerdiproject.harvest.harvester;

import de.gerdiproject.json.IJsonArray;
import de.gerdiproject.json.IJsonObject;
import de.gerdiproject.json.utils.JsonHelper;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A harvester for FAOSTAT (http://www.fao.org/faostat/en/#data).
 *
 * @author row
 */
public class FaoStatHarvester extends AbstractJsonArrayHarvester
{
    private final static String BASE_URL = "http://fenixservices.fao.org/faostat/api/%s/%s/";
    private final static String LOGO_URL = "http://data.fao.org/developers/api/catalog/resource/findDatastream?authKey=d30aebf0-ab2a-11e1-afa6-0800200c9a66&version=1.0&type=image&database=faostat&resource=logo&datastream=logo";
    private final static String VIEW_URL = "http://www.fao.org/faostat/%s/#data/";
    private final static String META_DATA_URL_SUFFIX = "metadata/";
    private final static String BULK_DOWNLOAD_URL_SUFFIX = "bulkdownloads/";
    private final static String DOMAINS_URL_SUFFIX = "domains/";

    private final static String CODE_JSON_KEY = "code";
    private final static String LABEL_JSON_KEY = "label";
    private final static String DATE_UPDATE_JSON_KEY = "date_update";
    private final static String URL_JSON_KEY = "URL";
    private final static String META_DATA_LABEL_JSON_KEY = "metadata_label";
    private final static String META_DATA_TEXT_JSON_KEY = "metadata_text";

    private final static String META_DATA_DESCRIPTION_JSON_VALUE = "Data description";

    private final static String ITEMS_TAGS_URL_SUFFIX = "codes/items/";
    private final static String ITEMS_AGGREGATED_TAGS_URL_SUFFIX = "codes/itemsagg/";
    private final static String ELEMENTS_TAGS_URL_SUFFIX = "codes/elements/";
    private final static String YEARS_TAGS_URL_SUFFIX = "codes/years/";
    private final static String REGIONS_TAGS_URL_SUFFIX = "codes/regions/";
    private final static String COUNTRIES_TAGS_URL_SUFFIX = "codes/countries/";
    private final static String SPECIAL_GROUPS_TAGS_URL_SUFFIX = "codes/specialgroups/";
    private final static String BREAKDOWN_BY_VAR_TAGS_URL_SUFFIX = "codes/breakdownbyvar/";
    private final static String BREAKDOWN_BY_SEX_TAGS_URL_SUFFIX = "codes/breakdownbysex/";
    private final static String INDICATORS_TAGS_URL_SUFFIX = "codes/indicators/";

    private final static String PROPERTY_VERSION = "version";
    private final static String PROPERTY_LANGUAGE = "language";

    private final static List<String> VALID_PARAMS = Arrays.asList(PROPERTY_VERSION, PROPERTY_LANGUAGE);

    private final static String DEFAULT_VERSION = "v1";
    private final static String DEFAULT_LANGUAGE = "en";


    /**
     * Default Constructor. Sets language to "en" and version to "v1". Version
     * and language are essential parts of the URL.
     */
    public FaoStatHarvester()
    {
        // only one document is created per harvested entry
        super(1);

        super.setProperty(PROPERTY_VERSION, DEFAULT_VERSION);
        super.setProperty(PROPERTY_LANGUAGE, DEFAULT_LANGUAGE);
    }


    @Override
    public List<String> getValidProperties()
    {
        return VALID_PARAMS;
    }


    @Override
    protected IJsonArray getJsonArray()
    {
        String version = getProperty(PROPERTY_VERSION);
        String language = getProperty(PROPERTY_LANGUAGE);
        String sourceUrl = String.format(BASE_URL, version, language);
        String domainsUrl = sourceUrl + DOMAINS_URL_SUFFIX;

        // get list of all FAOStat datasets, a.k.a. "domains"
        return httpRequester.getJsonArrayFromUrl(domainsUrl);
    }


    @Override
    protected List<IJsonObject> harvestJsonArrayEntry(IJsonObject domainItem)
    {
        String version = getProperty(PROPERTY_VERSION);
        String language = getProperty(PROPERTY_LANGUAGE);

        String sourceUrl = String.format(BASE_URL, version, language);
        String basicViewUrl = String.format(VIEW_URL, language);

        // get the domainCode, an identifier that is used FAOStat-internally
        String domainCode = domainItem.getString(CODE_JSON_KEY);

        // get basic properties
        String label = domainItem.getString(LABEL_JSON_KEY);

        // get the date of the last update
        Date lastUpdated = Date.valueOf(domainItem.getString(DATE_UPDATE_JSON_KEY));

        // get URL that directs the user to the database's website
        String viewUrl = basicViewUrl + domainCode;

        // get bulk-download URL
        IJsonArray downloadUrls = getBulkDownloadLinks(sourceUrl, domainCode);

        // get description
        IJsonArray descriptions = getDescriptions(sourceUrl, domainCode);

        // get search tags
        IJsonArray searchTags = getSearchTags(sourceUrl, domainCode);

        // get documented years
        IJsonArray years = getYears(sourceUrl, domainCode);

        // create document
        final IJsonObject document = searchIndexFactory.createSearchableDocument(
                                         label,
                                         lastUpdated,
                                         viewUrl,
                                         downloadUrls,
                                         LOGO_URL,
                                         descriptions,
                                         null,
                                         years,
                                         searchTags
                                     );

        // create documentList
        final List<IJsonObject> documentList = new ArrayList<>(numberOfDocumentsPerEntry);
        documentList.add(document);

        return documentList;
    }


    /**
     * Reads a JSON object that contains multiple bulk download URLs and
     * retrieves the URL to download ALL DATA. If such an URL does not exist,
     * the URL that points to ALL DATA NORMALIZED will be retrieved instead.
     *
     * @param sourceUrl the base URL for fenixservices.fao.org from which most
     * data is retrieved
     * @param domainCode an identifier for the domain database which is to be
     * harvested
     * @return an URL string
     */
    private IJsonArray getBulkDownloadLinks(String sourceUrl, String domainCode)
    {
        // get list of bulk download infos
        IJsonArray bulkDownloadInfoArray = httpRequester.getJsonArrayFromUrl(sourceUrl + BULK_DOWNLOAD_URL_SUFFIX + domainCode);

        // extract urls from the info objects
        List<String> urls = JsonHelper.arrayToStringList(bulkDownloadInfoArray, URL_JSON_KEY);

        // store urls in a new array
        IJsonArray downloadUrls = jsonBuilder.createArrayFromLists(urls);
        return downloadUrls;
    }


    /**
     * Retrieves the domain description from metadata.
     *
     * @param sourceUrl the base URL for fenixservices.fao.org from which most
     * data is retrieved
     * @param domainCode an identifier for the domain database which is to be
     * harvested
     * @return a descriptive text of the domain
     */
    private IJsonArray getDescriptions(String sourceUrl, String domainCode)
    {
        // get list of meta data
        IJsonArray metaDataList = httpRequester.getJsonArrayFromUrl(sourceUrl + META_DATA_URL_SUFFIX + domainCode);
        IJsonObject descriptionElement = JsonHelper.findObjectInArray(metaDataList, META_DATA_LABEL_JSON_KEY, META_DATA_DESCRIPTION_JSON_VALUE);

        if (descriptionElement != null) {
            String description = descriptionElement.getString(META_DATA_TEXT_JSON_KEY, null);
            return description != null ? jsonBuilder.createArrayFromObjects(description) : null;
        }

        return null;
    }


    /**
     * Retrieves an array of searchable tags for a domain database.
     *
     * @param sourceUrl the base URL for fenixservices.fao.org from which most
     * data is retrieved
     * @param domainCode an identifier for the domain database which is to be
     * harvested
     * @return a JsonArray of search tags
     */
    private IJsonArray getSearchTags(String sourceUrl, String domainCode)
    {
        IJsonArray searchTags = jsonBuilder.createArray();
        String[] searchableTagUrls = {
            sourceUrl + ITEMS_TAGS_URL_SUFFIX,
            sourceUrl + ITEMS_AGGREGATED_TAGS_URL_SUFFIX,
            sourceUrl + ELEMENTS_TAGS_URL_SUFFIX,
            sourceUrl + REGIONS_TAGS_URL_SUFFIX,
            sourceUrl + COUNTRIES_TAGS_URL_SUFFIX,
            sourceUrl + SPECIAL_GROUPS_TAGS_URL_SUFFIX,
            sourceUrl + BREAKDOWN_BY_VAR_TAGS_URL_SUFFIX,
            sourceUrl + BREAKDOWN_BY_SEX_TAGS_URL_SUFFIX,
            sourceUrl + INDICATORS_TAGS_URL_SUFFIX
        };

        // try to add relevant words to searchable tags
        int s = searchableTagUrls.length;

        while (s != 0) {
            s--;

            // suppress warnings, there will be many non-existent URLs
            httpRequester.suppressWarnings = true;

            // get list of stuff
            IJsonArray jsonArray = httpRequester.getJsonArrayFromUrl(searchableTagUrls[s] + domainCode);

            if (jsonArray != null) {
                jsonArray.forEach((element) ->
                                  searchTags.add(((IJsonObject) element).getString(LABEL_JSON_KEY))
                                 );
            }

            // re-enable warnings
            httpRequester.suppressWarnings = false;
        }

        return searchTags;
    }


    /**
     * Retrieves an array of documented years for a domain database.
     *
     * @param sourceUrl the base URL for fenixservices.fao.org from which most
     * data is retrieved
     * @param domainCode an identifier for the domain database which is to be
     * harvested
     * @return a JsonArray of years
     */
    private IJsonArray getYears(String sourceUrl, String domainCode)
    {
        String yearsUrl = sourceUrl + YEARS_TAGS_URL_SUFFIX + domainCode;

        // suppress warnings, there may be a non-existent URLs
        httpRequester.suppressWarnings = true;

        // get list of years
        IJsonArray jsonArray = httpRequester.getJsonArrayFromUrl(yearsUrl);

        // re-enable warnings
        httpRequester.suppressWarnings = false;

        if (jsonArray != null) {
            IJsonArray yearsArray = jsonBuilder.createArray();

            jsonArray.forEach((element) -> {
                try
                {
                    int year = Integer.parseUnsignedInt(((IJsonObject) element).getString(LABEL_JSON_KEY));
                    yearsArray.add(year);
                } catch (NumberFormatException e)
                {
                    // skip year, if it is not well-formed
                }
            });
            return yearsArray;
        }

        return null;
    }
}
