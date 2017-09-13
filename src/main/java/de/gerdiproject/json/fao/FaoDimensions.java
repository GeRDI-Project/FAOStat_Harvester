package de.gerdiproject.json.fao;


import com.google.gson.JsonArray;

import de.gerdiproject.json.fao.FaoDimensions.Dimension;


/**
 * This class represents the JSON response of a FaoSTAT dimensions request.
 * e.g. http://fenixservices.fao.org/faostat/api/v1/en/dimensions/QC/?full=true
 *
 * @author Robin Weiss
 *
 */
public class FaoDimensions extends FaoJson<Dimension>
{
    private static final String DIMENSION_URL = "http://fenixservices.fao.org/faostat/api/%s/%s%s%s/?show_lists=true";

    public static class Dimension
    {
        private String id;
        private String label;
        private String href;
        private String parameter;

        // these contain data that is irrelevant to the harvest.
        private JsonArray subdimensions;


        /**
         * Retrieves the URL that points to the JSON data of this dimension.
         * e.g. e.g. http://fenixservices.fao.org/faostat/api/v1/en/codes/countries/QC/?show_lists=true
         *
         * @return the URL that points to the JSON data of this dimension
         */
        public String getDimensionUrl(String version, String language, String domainCode)
        {
            return String.format(DIMENSION_URL, version, language, href, domainCode);
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getLabel()
        {
            return label;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

        public String getHref()
        {
            return href;
        }

        public void setHref(String href)
        {
            this.href = href;
        }

        public String getParameter()
        {
            return parameter;
        }

        public void setParameter(String parameter)
        {
            this.parameter = parameter;
        }

        public JsonArray getSubdimensions()
        {
            return subdimensions;
        }

        public void setSubdimensions(JsonArray subdimensions)
        {
            this.subdimensions = subdimensions;
        }
    }
}
