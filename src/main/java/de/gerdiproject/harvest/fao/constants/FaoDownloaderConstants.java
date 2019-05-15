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

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

import de.gerdiproject.harvest.fao.json.FaoDimension;
import de.gerdiproject.harvest.fao.json.FaoDocument;
import de.gerdiproject.harvest.fao.json.FaoDomain;
import de.gerdiproject.harvest.fao.json.FaoFilter;
import de.gerdiproject.harvest.fao.json.FaoMetadata;
import de.gerdiproject.harvest.fao.json.GenericJsonResponse;

/**
 * This static class contains download URLs that are used for downloading (meta-) data from FAOSTAT.
 *
 * @author Robin Weiss
 */
public class FaoDownloaderConstants
{
    public static final String BASE_URL = "http://fenixservices.fao.org/faostat/api/v1/%s/";
    public static final String GROUPS_AND_DOMAINS_URL = "groupsanddomains?section=download";
    public static final String DOCUMENTS_URL = "%sdocuments/%s/";
    public static final String BULK_DOWNLOADS_URL = "%sbulkdownloads/%s/";
    public static final String METADATA_URL = "%smetadata/%s/";
    public static final String DIMENSIONS_URL = "%sdimensions/%s/?full=true";
    public static final String SHOW_LIST_SUFFIX = "/?show_lists=true";

    public static final Type METADATA_RESPONSE_TYPE = new TypeToken<GenericJsonResponse<FaoMetadata>>() {} .getType();
    public static final Type FILTER_RESPONSE_TYPE = new TypeToken<GenericJsonResponse<FaoFilter>>() {} .getType();
    public static final Type DOMAIN_RESPONSE_TYPE = new TypeToken<GenericJsonResponse<FaoDomain>>() {} .getType();
    public static final Type DOCUMENT_RESPONSE_TYPE = new TypeToken<GenericJsonResponse<FaoDocument>>() {} .getType();
    public static final Type DIMENSION_RESPONSE_TYPE = new TypeToken<GenericJsonResponse<FaoDimension>>() {} .getType();

    /**
     * Private constructor, because this is a static class.
     */
    private FaoDownloaderConstants()
    {
    }
}
