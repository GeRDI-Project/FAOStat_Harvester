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

import de.gerdiproject.harvest.fao.constants.FaoDataCiteConstants;
import lombok.Value;

/**
 * This class represents the JSON response of a FaoSTAT documents request.
 * e.g. http://fenixservices.fao.org/faostat/api/v1/en/documents/QC/
 *
 * @author Robin Weiss
 */
@Value
public final class FaoDocument
{
    @SerializedName("DomainCode")
    private final String domainCode;

    @SerializedName("CreatedDate")
    private final String createdDate;

    @SerializedName("FileName")
    private final String fileName;

    @SerializedName("FileTitle")
    private final String fileTitle;

    @SerializedName("FilePath")
    private final String filePath;


    /**
     * Returns a path that leads to the download of the document.
     * <br>e.g. http://fenixservices.fao.org/faostat/static/documents/QC/QC_methodology_e.pdf
     *
     * @return a path that leads to the download of the document
     */
    public String getDownloadPath()
    {
        return String.format(FaoDataCiteConstants.DOCUMENT_URL, fileName);
    }
}
