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
package de.gerdiproject.harvest.fao.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import de.gerdiproject.harvest.fao.constants.FaoDataCiteConstants;
import de.gerdiproject.harvest.fao.json.BulkDownloadResponse;
import de.gerdiproject.harvest.fao.json.DimensionsResponse;
import de.gerdiproject.harvest.fao.json.DocumentsResponse;
import de.gerdiproject.harvest.fao.json.FiltersResponse;
import de.gerdiproject.harvest.fao.json.MetadataResponse;
import de.gerdiproject.harvest.fao.json.BulkDownloadResponse.BulkDownload;
import de.gerdiproject.harvest.fao.json.DimensionsResponse.Dimension;
import de.gerdiproject.harvest.fao.json.DocumentsResponse.Document;
import de.gerdiproject.harvest.fao.json.DomainsResponse.Domain;
import de.gerdiproject.harvest.fao.json.FiltersResponse.Filter;
import de.gerdiproject.harvest.fao.json.MetadataResponse.Metadata;
import de.gerdiproject.json.datacite.Contributor;
import de.gerdiproject.json.datacite.DataCiteJson;
import de.gerdiproject.json.datacite.Date;
import de.gerdiproject.json.datacite.DateRange;
import de.gerdiproject.json.datacite.Description;
import de.gerdiproject.json.datacite.Subject;
import de.gerdiproject.json.datacite.Title;
import de.gerdiproject.json.datacite.abstr.AbstractDate;
import de.gerdiproject.json.datacite.enums.ContributorType;
import de.gerdiproject.json.datacite.enums.DateType;
import de.gerdiproject.json.datacite.enums.DescriptionType;
import de.gerdiproject.json.datacite.enums.NameType;
import de.gerdiproject.json.datacite.enums.TitleType;
import de.gerdiproject.json.datacite.extension.ResearchData;
import de.gerdiproject.json.datacite.extension.WebLink;
import de.gerdiproject.json.datacite.extension.enums.WebLinkType;
import de.gerdiproject.json.datacite.nested.PersonName;


/**
 * This class provides parsers for FAOSTAT JSON responses retrieved via a {@linkplain Downloader}.
 * The parser functions generate {@linkplain DataCiteJson} compliant fields.
 *
 * @author Robin Weiss
 */
public class DomainParser
{
    /**
     * Private Constructor, because this is a static class.
     */
    private DomainParser()
    {
    }


    /**
     * Parses a {@linkplain MetadataResponse} object, looking for relevant descriptions and returning
     * them in a list.
     *
     * @param metadata the domain metadata that is to be parsed
     * @param language the language that is set for harvesting FAOSTAT
     *
     * @return a list of descriptions of a domain
     */
    public static List<Description> parseDescriptions(MetadataResponse metadata, String language)
    {
        List<Description> descriptions = new LinkedList<>();

        List<Metadata> metadataList = metadata.getData();

        metadataList.forEach((Metadata m) -> {
            String label = m.getMetadata_label();
            DescriptionType type = FaoDataCiteConstants.RELEVANT_DESCRIPTIONS.get(label);

            if (type != null)
            {
                String descriptionText = String.format(FaoDataCiteConstants.DESCRIPTION_FORMAT, label, m.getMetadata_text());
                Description desc = new Description(descriptionText, type);
                desc.setLang(language);
                descriptions.add(desc);
            }
        });
        return descriptions;
    }


    /**
     * Parses a {@linkplain MetadataResponse} object, looking for relevant dates and returning
     * them in a list.
     *
     * @param metadata the domain metadata that is to be parsed
     * @param language the language that is set for harvesting FAOSTAT
     *
     * @return a list of dates of a domain
     */
    public static List<AbstractDate> parseDates(MetadataResponse metadata, String language)
    {
        List<AbstractDate> dates = new LinkedList<>();

        List<Metadata> metadataList = metadata.getData();

        metadataList.forEach((Metadata m) -> {
            String dateText = m.getMetadata_text();

            if (dateText == null || dateText.isEmpty())
                return; // skip this date and go to the next

            switch (m.getMetadata_label())
            {
                case FaoDataCiteConstants.META_DATA_TIME_COVERAGE:
                    Matcher matcher = FaoDataCiteConstants.TIME_COVERAGE_PATTERN.matcher(dateText);

                    // check if it is a date range
                    if (matcher.find()) {
                        String startYear = matcher.group(1);
                        String endYear = matcher.group(2);
                        dates.add(new DateRange(startYear, endYear, DateType.Other));
                    } else
                        dates.add(new Date(dateText, DateType.Other));

                    break;

                case FaoDataCiteConstants.META_DATA_LAST_UPDATE:
                    Date lastUpdate = new Date(dateText, DateType.Updated);
                    dates.add(lastUpdate);
                    break;
            }
        });
        return dates;
    }


    /**
     * Parses a {@linkplain Domain} object, looking for relevant titles and returning
     * them in a list.
     *
     * @param domain the domain that is to be parsed
     * @param language the language that is set for harvesting FAOSTAT
     *
     * @return a list of titles of a domain
     */
    public static List<Title> parseTitles(Domain domain, String language)
    {
        List<Title> titles = new LinkedList<>();

        Title domainTitle = new Title(domain.getDomain_name());
        domainTitle.setLang(language);
        titles.add(domainTitle);

        Title groupTitle = new Title(domain.getGroup_name());
        groupTitle.setLang(language);
        groupTitle.setType(TitleType.Other);
        titles.add(groupTitle);

        return titles;
    }


    /**
     * Parses a {@linkplain BulkDownloadResponse} object, converting each {@linkplain BulkDownload}
     * to a {@linkplain File} and returning all files in a list.
     *
     * @param bulkDownloads the bulkDownloads object that is to be parsed
     *
     * @return a list of downloadable files of a domain
     */
    public static List<ResearchData> parseFiles(BulkDownloadResponse bulkDownloads)
    {
        List<ResearchData> files = new LinkedList<>();
        List<BulkDownload> bulkList = bulkDownloads.getData();

        bulkList.forEach((BulkDownload bdl) -> {
            String url = bdl.getURL();
            String label = bdl.getFileContent();
            String type = bdl.getFileName().substring(bdl.getFileName().lastIndexOf('.') + 1);

            ResearchData file = new ResearchData(url, label);
            file.setType(type);

            files.add(file);
        });

        return files;
    }


    /**
     * Parses a {@linkplain DocumentsResponse} and {@linkplain Domain} object, looking for relevant web links and returning
     * them in a list.
     *
     * @param documents the documents that are to be parsed
     * @param domain the domain that is to be parsed
     *
     * @return a list of web links that are related to a domain
     */
    public static List<WebLink> parseWebLinks(DocumentsResponse documents, Domain domain)
    {
        List<WebLink> webLinks = new LinkedList<>();
        List<Document> documentList = documents.getData();

        // add view url
        String viewUrl = String.format(FaoDataCiteConstants.VIEW_URL, domain.getDomain_code());
        WebLink viewLink = new WebLink(viewUrl);
        viewLink.setName(domain.getDomain_name());
        viewLink.setType(WebLinkType.ViewURL);
        webLinks.add(viewLink);

        // add logo url
        webLinks.add(FaoDataCiteConstants.LOGO_WEB_LINK);

        // add related documents
        documentList.forEach((Document d) -> {
            // filter out the dummy document
            if (!d.getFileTitle().equals(FaoDataCiteConstants.TEMPLATE_DOCUMENT_NAME))
            {
                WebLink link = new WebLink(d.getDownloadPath());
                link.setName(d.getFileTitle());
                link.setType(WebLinkType.Related);
                webLinks.add(link);
            }
        });

        return webLinks;
    }


    /**
     * Parses a {@linkplain DimensionsResponse} object, looking for relevant filter categories and returning
     * the URLs that lead to {@linkplain FiltersResponse} responses in a list.
     *
     * @param dimensions the dimensions that are to be parsed
     * @param version the version that is set for harvesting FAOSTAT
     * @param language the language that is set for harvesting FAOSTAT
     * @param domainCode a unique ID of the domain of which the filter URLs are retrieved
     *
     * @return a list of URLs of a domain's filter categories
     */
    public static List<String> parseFilterUrls(DimensionsResponse dimensions, String version, String language, String domainCode)
    {
        List<String> filterUrls = new LinkedList<>();
        List<Dimension> dimensionList = dimensions.getData();

        dimensionList.forEach((Dimension d) -> {
            // exclude the pure numbers of the years filter
            if (!d.getId().equals("year"))
            {
                filterUrls.add(d.getDimensionUrl(version, language, domainCode));
            }
        });

        return filterUrls;
    }


    /**
     * Parses a {@linkplain FiltersResponse} object, converting each filter term to a {@linkplain Subject}
     * and returning them in a list.
     *
     * @param filters the filter category that is to be parsed
     * @param language the language that is set for harvesting FAOSTAT
     *
     * @return a list of subjects of a domain filter category
     */
    public static List<Subject> parseSubjects(FiltersResponse filters, String language)
    {
        List<Subject> subjects = new LinkedList<>();
        List<Filter> filterList = filters.getData();

        filterList.forEach((Filter f) -> {
            Subject sub = new Subject(f.getLabel());
            sub.setLang(language);
            subjects.add(sub);
        });

        return subjects;
    }


    /**
     * Parses a {@linkplain MetadataResponse} object, looking for a contact person and returning
     * it in a list.
     *
     * @param metadata the domain metadata that is to be parsed
     *
     * @return a list of contributors of a domain
     */
    public static List<Contributor> parseContributors(MetadataResponse metadata)
    {
        List<Contributor> contributors = new LinkedList<>();
        List<Metadata> metadataList = metadata.getData();

        Contributor contactPerson = new Contributor("", ContributorType.ContactPerson);

        for (Metadata m : metadataList) {
            if (m.getMetadata_group_code().equals("1")) {
                switch (m.getMetadata_label()) {
                    case FaoDataCiteConstants.METADATA_CONTACT_NAME:
                        contactPerson.setName(new PersonName(m.getMetadata_text(), NameType.Personal));
                        break;

                    case FaoDataCiteConstants.METADATA_CONTACT_ORGANISATION:
                        contactPerson.setAffiliations(Arrays.asList(m.getMetadata_text()));
                        break;

                    default:
                        // ignore the metadata
                }
            }
        }

        if (!contactPerson.getName().getValue().isEmpty())
            contributors.add(contactPerson);

        return contributors;
    }
}
