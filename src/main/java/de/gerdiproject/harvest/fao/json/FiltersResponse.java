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

import de.gerdiproject.harvest.fao.json.FiltersResponse.Filter;

/**
 * This class represents the JSON response of a FaoSTAT codes request.
 * e.g. http://fenixservices.fao.org/faostat/api/v1/en/codes/years/QC/?show_lists=true
 *
 * @author Robin Weiss
 *
 */
public final class FiltersResponse extends GenericJsonResponse<Filter>
{
    public final static class Filter
    {
        private String code;
        private String label;
        private String aggregate_type;

        public String getCode()
        {
            return code;
        }

        public void setCode(String code)
        {
            this.code = code;
        }

        public String getLabel()
        {
            return label;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

        public String getAggregate_type()
        {
            return aggregate_type;
        }

        public void setAggregate_type(String aggregate_type)
        {
            this.aggregate_type = aggregate_type;
        }
    }
}
