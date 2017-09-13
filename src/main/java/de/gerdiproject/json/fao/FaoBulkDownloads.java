package de.gerdiproject.json.fao;

import de.gerdiproject.json.fao.FaoBulkDownloads.BulkDownload;

/**
 * This class represents the JSON response of FaoSTAT bulk download links.
 * e.g. http://fenixservices.fao.org/faostat/api/v1/en/bulkdownloads/QC/
 *
 * @author Robin Weiss
 *
 */
public class FaoBulkDownloads extends FaoJson<BulkDownload>
{
    public static class BulkDownload
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
