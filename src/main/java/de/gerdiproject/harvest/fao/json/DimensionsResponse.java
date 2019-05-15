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
package de.gerdiproject.harvest.fao.json;


import com.google.gson.JsonArray;

import de.gerdiproject.harvest.fao.json.DimensionsResponse.Dimension;


/**
 * This class represents the JSON response of a FaoSTAT dimensions request.
 * e.g. http://fenixservices.fao.org/faostat/api/v1/en/dimensions/QC/?full=true
 *
 * @author Robin Weiss
 */
public final class DimensionsResponse extends GenericJsonResponse<Dimension>
{
    public final static class Dimension
    {
        private String id;
        private String label;
        private String href;
        private String parameter;

        // these contain data that is irrelevant to the harvest.
        private JsonArray subdimensions;


        public String getId()
        {
            return id;
        }

        public void setId(final String id)
        {
            this.id = id;
        }

        public String getLabel()
        {
            return label;
        }

        public void setLabel(final String label)
        {
            this.label = label;
        }

        public String getHref()
        {
            return href;
        }

        public void setHref(final String href)
        {
            this.href = href;
        }

        public String getParameter()
        {
            return parameter;
        }

        public void setParameter(final String parameter)
        {
            this.parameter = parameter;
        }

        public JsonArray getSubdimensions()
        {
            return subdimensions;
        }

        public void setSubdimensions(final JsonArray subdimensions)
        {
            this.subdimensions = subdimensions;
        }
    }
}
