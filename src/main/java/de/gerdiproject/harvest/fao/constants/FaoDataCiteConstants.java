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
package de.gerdiproject.harvest.fao.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.gerdiproject.harvest.fao.json.MetadataResponse;
import de.gerdiproject.json.datacite.Creator;
import de.gerdiproject.json.datacite.ResourceType;
import de.gerdiproject.json.datacite.enums.DescriptionType;
import de.gerdiproject.json.datacite.enums.NameType;
import de.gerdiproject.json.datacite.enums.ResourceTypeGeneral;
import de.gerdiproject.json.datacite.extension.WebLink;
import de.gerdiproject.json.datacite.extension.abstr.AbstractResearch;
import de.gerdiproject.json.datacite.extension.constants.ResearchDisciplineConstants;
import de.gerdiproject.json.datacite.extension.enums.WebLinkType;
import de.gerdiproject.json.datacite.nested.PersonName;

/**
 * This static class contains constants that are used for creating DataCite
 * documents of FAOSTAT.
 *
 * @author Robin Weiss
 */
public class FaoDataCiteConstants
{
    // source id
    public static final String SOURCE_ID = "%s_%s_%s";


    // RESOURCE TYPE
    public static final ResourceType RESOURCE_TYPE = createResourceType();

    // CREATOR
    public static final List<Creator> CREATORS = createCreators();

    // SOURCE
    public static final String PROVIDER = "Food and Agriculture Organization of the United Nations (FAO)";
    public static final String PROVIDER_URI = "http://www.fao.org/faostat/en/#home";
    public static final String REPOSITORY_ID = "FAOSTAT";
    public static final List<AbstractResearch> DISCIPLINES = createResearchDisciplines();

    // CONTRIBUTORS
    public static final String METADATA_CONTACT_NAME = "Contact name";
    public static final String METADATA_CONTACT_ORGANISATION = "Contact organisation";
    public static final short EARLIEST_PUBLICATION_YEAR = 1961;

    // WEB LINKS
    public static final String VIEW_URL = "http://www.fao.org/faostat/en/#data/%s";
    public static final WebLink LOGO_WEB_LINK = createLogoWebLink();
    public static final String TEMPLATE_DOCUMENT_NAME = "About";

    // DATES
    public static final String META_DATA_TIME_COVERAGE = "Time coverage";
    public static final String META_DATA_LAST_UPDATE = "Metadata last update";
    public static final Pattern TIME_COVERAGE_PATTERN =
        Pattern.compile("\\D+(\\d\\d\\d\\d)\\D(\\d\\d\\d\\d)[\\d\\D]+$");
    public static final String DATE_PARSE_ERROR = "Could not parse date: %s";

    // DESCRIPTIONS
    public static final String DESCRIPTION_FORMAT = "%s:%n%s";
    public static final Map<String, DescriptionType> RELEVANT_DESCRIPTIONS = createRelevantDescriptions();

    // FORMATS
    public static final List<String> FORMATS = Collections.unmodifiableList(Arrays.asList("CSV"));

    // DOCUMENTS
    public static final String DOCUMENT_URL = "http://fenixservices.fao.org/faostat/static/documents/%s";

    // DIMENSIONS
    public static final String DIMENSION_URL = "http://fenixservices.fao.org/faostat/api/%s/%s%s%s/?show_lists=true";


    /**
     * Private constructor, because this is a static class.
     */
    private FaoDataCiteConstants()
    {
    }


    /**
     * @return
     */
    private static List<AbstractResearch> createResearchDisciplines()
    {
        return Collections.unmodifiableList(
                   Arrays.asList(
                       ResearchDisciplineConstants.AGRICULTURAL_ECONOMICS_AND_SOCIOLOGY,
                       ResearchDisciplineConstants.STATISTICS_AND_ECONOMETRICS,
                       ResearchDisciplineConstants.ECOLOGY_OF_AGRICULTURAL_LANDSCAPES));
    }


    /**
     * Initializes a map of metadata names that contain descriptions that are
     * relevant for documents.
     *
     * @return a map of {@linkplain MetadataResponse} metadata_label field
     *         values
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
     *
     * @return a link to the FAOSTAT logo
     */
    private static WebLink createLogoWebLink()
    {
        WebLink logoLink = new WebLink(
            "https://commons.wikimedia.org/wiki/File:FAO_logo.svg");
        logoLink.setType(WebLinkType.ProviderLogoURL);
        return logoLink;
    }


    /**
     * Initializes a Creator dummy for all FAOSTAT documents.
     *
     * @return a Creator that has "FAO" as name
     */
    private static List<Creator> createCreators()
    {
        Creator creator = new Creator(new PersonName(PROVIDER, NameType.Organisational));
        return Arrays.asList(creator);
    }


    /**
     * Initializes the only ResourceType of all FAOSTAT documents.
     *
     * @return a ResourceType representing CSV datasets
     */
    private static ResourceType createResourceType()
    {
        ResourceType resType = new ResourceType("CSV", ResourceTypeGeneral.Dataset);
        return resType;
    }
}
