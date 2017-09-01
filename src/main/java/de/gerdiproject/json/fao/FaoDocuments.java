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
package de.gerdiproject.json.fao;

import de.gerdiproject.json.fao.FaoDocuments.Document;

/**
 * This class represents the JSON response of a FaoSTAT documents request.
 * e.g. http://fenixservices.fao.org/faostat/api/v1/en/documents/QC/
 *
 * @author Robin Weiss
 *
 */
public class FaoDocuments extends FaoJson<Document>
{
    private static final String DOWNLOAD_PATH_PREFIX = "http://fenixservices.fao.org/faostat/static/documents/";

    public static class Document
    {
        private String DomainCode;
        private String CreatedDate;
        private String FileName;
        private String FileTitle;
        private String FilePath;

        /**
         * Returns a path that leads to the download of the document.
         * @return a path that leads to the download of the file
         */
        public String getDownloadPath()
        {
            // e.g. http://fenixservices.fao.org/faostat/static/documents/QC/QC_methodology_e.pdf
            return DOWNLOAD_PATH_PREFIX + FileName;
        }

        public String getDomainCode()
        {
            return DomainCode;
        }

        public void setDomainCode(String domainCode)
        {
            DomainCode = domainCode;
        }

        public String getCreatedDate()
        {
            return CreatedDate;
        }

        public void setCreatedDate(String createdDate)
        {
            CreatedDate = createdDate;
        }

        public String getFileName()
        {
            return FileName;
        }

        public void setFileName(String fileName)
        {
            FileName = fileName;
        }

        public String getFileTitle()
        {
            return FileTitle;
        }

        public void setFileTitle(String fileTitle)
        {
            FileTitle = fileTitle;
        }

        public String getFilePath()
        {
            return FilePath;
        }

        public void setFilePath(String filePath)
        {
            FilePath = filePath;
        }
    }
}
