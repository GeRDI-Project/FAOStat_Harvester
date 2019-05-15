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

import de.gerdiproject.harvest.fao.constants.FaoDataCiteConstants;
import de.gerdiproject.harvest.fao.json.DocumentsResponse.Document;

/**
 * This class represents the JSON response of a FaoSTAT documents request.
 * e.g. http://fenixservices.fao.org/faostat/api/v1/en/documents/QC/
 *
 * @author Robin Weiss
 */
public final class DocumentsResponse extends GenericJsonResponse<Document>
{
    public final static class Document
    {
        private String DomainCode;
        private String CreatedDate;
        private String FileName;
        private String FileTitle;
        private String FilePath;

        /**
         * Returns a path that leads to the download of the document.
         * <br>e.g. http://fenixservices.fao.org/faostat/static/documents/QC/QC_methodology_e.pdf
         *
         * @return a path that leads to the download of the document
         */
        public String getDownloadPath()
        {
            return String.format(FaoDataCiteConstants.DOCUMENT_URL, FileName);
        }

        public String getDomainCode()
        {
            return DomainCode;
        }

        public void setDomainCode(final String domainCode)
        {
            DomainCode = domainCode;
        }

        public String getCreatedDate()
        {
            return CreatedDate;
        }

        public void setCreatedDate(final String createdDate)
        {
            CreatedDate = createdDate;
        }

        public String getFileName()
        {
            return FileName;
        }

        public void setFileName(final String fileName)
        {
            FileName = fileName;
        }

        public String getFileTitle()
        {
            return FileTitle;
        }

        public void setFileTitle(final String fileTitle)
        {
            FileTitle = fileTitle;
        }

        public String getFilePath()
        {
            return FilePath;
        }

        public void setFilePath(final String filePath)
        {
            FilePath = filePath;
        }
    }
}
