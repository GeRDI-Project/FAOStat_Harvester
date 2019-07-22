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
 * This class represents the JSON response of a FaoSTAT groups and domains
 * request.
 * http://fenixservices.fao.org/faostat/api/v1/en/groupsanddomains?section=download
 *
 * @author Robin Weiss
 */
@Value
public final class FaoDomain
{
    @SerializedName("group_code")
    private final String groupCode;

    @SerializedName("group_name")
    private final String groupName;

    @SerializedName("domain_code")
    private final String domainCode;

    @SerializedName("domain_name")
    private final String domainName;

    @SerializedName("date_update")
    private final String dateUpdate;

    @SerializedName("note_update")
    private final String noteUpdate;

    @SerializedName("release_current")
    private final String releaseCurrent;

    @SerializedName("state_current")
    private final String stateCurrent;

    @SerializedName("year_current")
    private final String yearCurrent;

    @SerializedName("release_next")
    private final String releaseNext;

    @SerializedName("state_next")
    private final String stateNext;

    @SerializedName("year_next")
    private final String yearNext;
}
