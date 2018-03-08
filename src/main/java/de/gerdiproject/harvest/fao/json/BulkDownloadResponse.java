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

import de.gerdiproject.harvest.fao.json.BulkDownloadResponse.BulkDownload;

/**
 * This class represents the JSON response of FaoSTAT bulk download links.
 * e.g. http://fenixservices.fao.org/faostat/api/v1/en/bulkdownloads/QC/
 *
 * @author Robin Weiss
 */
public final class BulkDownloadResponse extends GenericJsonResponse<BulkDownload>
{
    public final static class BulkDownload
    {
        private String DomainCode;
        private String Source;
        private String FileName;
        private String FileContent;
        private String CreatedDate;
        private int FileSize;
        private String FileSizeUnit;
        private String Type;
        private String URL;

        public String getDomainCode()
        {
            return DomainCode;
        }

        public void setDomainCode(String domainCode)
        {
            DomainCode = domainCode;
        }

        public String getSource()
        {
            return Source;
        }

        public void setSource(String source)
        {
            Source = source;
        }

        public String getFileName()
        {
            return FileName;
        }

        public void setFileName(String fileName)
        {
            FileName = fileName;
        }

        public String getFileContent()
        {
            return FileContent;
        }

        public void setFileContent(String fileContent)
        {
            FileContent = fileContent;
        }
        public String getCreatedDate()
        {
            return CreatedDate;
        }

        public void setCreatedDate(String createdDate)
        {
            CreatedDate = createdDate;
        }

        public int getFileSize()
        {
            return FileSize;
        }

        public void setFileSize(int fileSize)
        {
            FileSize = fileSize;
        }

        public String getFileSizeUnit()
        {
            return FileSizeUnit;
        }

        public void setFileSizeUnit(String fileSizeUnit)
        {
            FileSizeUnit = fileSizeUnit;
        }

        public String getType()
        {
            return Type;
        }

        public void setType(String type)
        {
            Type = type;
        }

        public String getURL()
        {
            return URL;
        }

        public void setURL(String uRL)
        {
            URL = uRL;
        }
    }
}
