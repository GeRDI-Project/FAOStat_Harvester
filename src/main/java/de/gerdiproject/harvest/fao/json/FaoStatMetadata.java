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

import com.google.gson.annotations.SerializedName;

import lombok.Value;

/**
 * This class represents the JSON response of a FaoSTAT metadata request.
 * e.g. http://fenixservices.fao.org/faostat/api/v1/en/metadata/QC
 *
 * @author Robin Weiss
 */
@Value
public final class FaoStatMetadata 
{
        @SerializedName("domain_code")
        private final String domainCode;
        
        @SerializedName("metadata_group_code")
        private final String metadataGroupCode;
        
        @SerializedName("metadata_group_label")
        private final String metadataGroupLabel;
        
        @SerializedName("metadata_code")
        private final String metadataCode;
        
        @SerializedName("metadata_label")
        private final String metadataLabel;
        
        @SerializedName("metadata_text")
        private final String metadataText;
        
        private final int ord;
}
