package de.gerdiproject.harvest.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.gerdiproject.json.datacite.Creator;
import de.gerdiproject.json.datacite.ResourceType;
import de.gerdiproject.json.datacite.Description.DescriptionType;
import de.gerdiproject.json.datacite.ResourceType.ResourceTypeCategory;
import de.gerdiproject.json.datacite.WebLink;
import de.gerdiproject.json.datacite.WebLink.WebLinkType;

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

    // DATES
    public static final String META_DATA_TIME_COVERAGE = "Time coverage";
    public static final String META_DATA_LAST_UPDATE = "Metadata last update";
    public static final Pattern TIME_COVERAGE_PATTERN = Pattern.compile("(\\d\\d\\d\\d)");
    public static final String DATE_PARSE_ERROR_PREFIX = "Could not parse date: ";

    // DESCRIPTIONS
    public static final String DESCRIPTION_FORMAT = "%s:%n%s";
    public static final Map<String, DescriptionType> RELEVANT_DESCRIPTIONS = createRelevantDescriptions();


    private static Map<String, DescriptionType> createRelevantDescriptions()
    {
        Map<String, DescriptionType> relavantDescriptions = new HashMap<>();
        relavantDescriptions.put("Data description", DescriptionType.Abstract);
        relavantDescriptions.put("Statistical concepts and definitions", DescriptionType.TechnicalInfo);
        relavantDescriptions.put("Documentation on methodology", DescriptionType.Methods);
        relavantDescriptions.put("Quality documentation", DescriptionType.Methods);
        return relavantDescriptions;
    }


    private static WebLink createLogoWebLink()
    {
        WebLink logoLink = new WebLink("http://data.fao.org/developers/api/catalog/resource/findDatastream?authKey=d30aebf0-ab2a-11e1-afa6-0800200c9a66&version=1.0&type=image&database=faostat&resource=logo&datastream=logo");
        logoLink.setType(WebLinkType.LogoURL);
        return logoLink;
    }


    private static List<Creator> createCreators()
    {
        Creator creator = new Creator(PROVIDER);
        return Arrays.asList(creator);
    }


    private static ResourceType createResourceType()
    {
        ResourceType resType = new ResourceType();
        resType.setResourceTypeGeneral(ResourceTypeCategory.Dataset);
        resType.setValue("CSV");
        return resType;
    }
}
