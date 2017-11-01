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
package de.gerdiproject.harvest.fao.json;

import de.gerdiproject.harvest.fao.json.MetadataResponse.Metadata;

/**
 * This class represents the JSON response of a FaoSTAT metadata request.
 * e.g. http://fenixservices.fao.org/faostat/api/v1/en/metadata/QC
 *
 * @author Robin Weiss
 */
public final class MetadataResponse extends GenericJsonResponse<Metadata>
{
    public final static class Metadata
    {
        private String domain_code;
        private String metadata_group_code;
        private String metadata_group_label;
        private String metadata_code;
        private String metadata_label;
        private String metadata_text;
        private int ord;

        public String getDomain_code()
        {
            return domain_code;
        }

        public void setDomain_code(String domain_code)
        {
            this.domain_code = domain_code;
        }

        public String getMetadata_group_code()
        {
            return metadata_group_code;
        }

        public void setMetadata_group_code(String metadata_group_code)
        {
            this.metadata_group_code = metadata_group_code;
        }

        public String getMetadata_group_label()
        {
            return metadata_group_label;
        }

        public void setMetadata_group_label(String metadata_group_label)
        {
            this.metadata_group_label = metadata_group_label;
        }

        public String getMetadata_code()
        {
            return metadata_code;
        }

        public void setMetadata_code(String metadata_code)
        {
            this.metadata_code = metadata_code;
        }

        public String getMetadata_label()
        {
            return metadata_label;
        }

        public void setMetadata_label(String metadata_label)
        {
            this.metadata_label = metadata_label;
        }

        public String getMetadata_text()
        {
            return metadata_text;
        }

        public void setMetadata_text(String metadata_text)
        {
            this.metadata_text = metadata_text;
        }

        public int getOrd()
        {
            return ord;
        }

        public void setOrd(int ord)
        {
            this.ord = ord;
        }
    }
}
