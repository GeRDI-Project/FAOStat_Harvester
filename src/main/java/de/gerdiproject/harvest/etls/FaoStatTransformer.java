/*
 *  Copyright © 2018 Robin Weiss (http://www.gerdi-project.de/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
package de.gerdiproject.harvest.etls;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import de.gerdiproject.harvest.etls.extractors.FaoStatDomainVO;
import de.gerdiproject.harvest.etls.transformers.AbstractIteratorTransformer;
import de.gerdiproject.harvest.fao.constants.FaoDataCiteConstants;
import de.gerdiproject.harvest.fao.json.BulkDownloadResponse.BulkDownload;
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
 * This transformer parses metadata from a {@linkplain FaoStatDomainVO}
 * and adds it to documents.
 *
 * @author Robin Weiss
 */
public class FaoStatTransformer extends AbstractIteratorTransformer<FaoStatDomainVO, DataCiteJson>
{
    private String language;


    @Override
    public void init(AbstractETL<?, ?> etl)
    {
        super.init(etl);

        this.language = ((FaoStatETL)etl).getLanguage();
    }


    @Override
    protected DataCiteJson transformElement(FaoStatDomainVO source)
    {
        // create the document
        final DataCiteJson document = new DataCiteJson(createIdentifier(source.getDomain()));

        document.setLanguage(language);
        document.setRepositoryIdentifier(FaoDataCiteConstants.REPOSITORY_ID);
        document.setPublicationYear(FaoDataCiteConstants.EARLIEST_PUBLICATION_YEAR);
        document.setResourceType(FaoDataCiteConstants.RESOURCE_TYPE);
        document.setFormats(FaoDataCiteConstants.FORMATS);
        document.setResearchDisciplines(FaoDataCiteConstants.DISCIPLINES);

        // get source
        document.setPublisher(FaoDataCiteConstants.PROVIDER);

        // get a readable name of the domain
        document.setTitles(parseTitles(source.getDomain()));

        // get bulk-download URL
        document.setResearchDataList(parseFiles(source.getBulkDownloads()));

        // get description
        document.setDescriptions(parseDescriptions(source.getMetadata()));

        // get URLs of all filters that can be applied to the domain
        document.setSubjects(parseSubjects(source.getFilters()));

        // get dates
        document.setDates(parseDates(source.getMetadata()));

        // get web links
        document.setWebLinks(parseWebLinks(source));

        // get contact person
        document.setContributors(parseContributors(source.getMetadata()));

        // get creator
        document.setCreators(FaoDataCiteConstants.CREATORS);

        return document;
    }


    /**
     * Creates a unique identifier of a domain within FAOSTAT.
     *
     * @param domain the domain of which the identifier is created
     *
     * @return a unique identifier of this domain within FAOSTAT
     */
    private String createIdentifier(Domain domain)
    {
        return String.format(
                   FaoDataCiteConstants.SOURCE_ID,
                   domain.getGroup_code(),
                   domain.getDomain_code(),
                   language);
    }


    /**
     * Parses a list of {@linkplain Metadata}, looking for relevant descriptions and returning
     * them in a list.
     *
     * @param metadata the domain metadata that is to be parsed
     *
     * @return a list of descriptions of a domain
     */
    private List<Description> parseDescriptions(List<Metadata> metadata)
    {
        List<Description> descriptions = new LinkedList<>();

        for (Metadata m : metadata) {
            String label = m.getMetadata_label();
            DescriptionType type = FaoDataCiteConstants.RELEVANT_DESCRIPTIONS.get(label);

            if (type != null) {
                String descriptionText = String.format(FaoDataCiteConstants.DESCRIPTION_FORMAT, label, m.getMetadata_text());
                Description desc = new Description(descriptionText, type);
                desc.setLang(language);
                descriptions.add(desc);
            }
        }

        return descriptions;
    }


    /**
     * Parses a list of {@linkplain Metadata}, looking for relevant dates and returning
     * them in a list.
     *
     * @param metadata the domain metadata that is to be parsed
     *
     * @return a list of dates of a domain
     */
    private  List<AbstractDate> parseDates(List<Metadata> metadata)
    {
        List<AbstractDate> dates = new LinkedList<>();

        for (Metadata m : metadata) {
            String dateText = m.getMetadata_text();

            if (dateText == null || dateText.isEmpty())
                continue; // skip this date and go to the next

            switch (m.getMetadata_label()) {
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

                default:
                    // ignore metadata
            }
        }

        return dates;
    }


    /**
     * Parses a {@linkplain Domain} object, looking for relevant titles and returning
     * them in a list.
     *
     * @param domain the domain that is to be parsed
     *
     * @return a list of titles of a domain
     */
    private List<Title> parseTitles(Domain domain)
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
     * Parses a list of{@linkplain BulkDownload}s, converting each
     * to a {@linkplain ResearchData}.
     *
     * @param bulkDownloads the bulk downloads of the domain
     *
     * @return a list of downloadable files of a domain
     */
    private List<ResearchData> parseFiles(List<BulkDownload> bulkDownloads)
    {
        List<ResearchData> files = new LinkedList<>();

        for (BulkDownload bdl : bulkDownloads) {
            String url = bdl.getURL();
            String label = bdl.getFileContent();
            String type = bdl.getFileName().substring(bdl.getFileName().lastIndexOf('.') + 1);

            ResearchData file = new ResearchData(url, label);
            file.setType(type);

            files.add(file);
        }

        return files;
    }


    /**
     * Parses the {@linkplain FaoStatDomainVO}, looking for relevant web links and returning
     * them in a list.
     *
     * @param source the domainVO that is to be parsed
     *
     * @return a list of web links that are related to a domain
     */
    private List<WebLink> parseWebLinks(FaoStatDomainVO source)
    {
        List<WebLink> webLinks = new LinkedList<>();
        // add view url
        String viewUrl = String.format(FaoDataCiteConstants.VIEW_URL, source.getDomain().getDomain_code());
        WebLink viewLink = new WebLink(viewUrl);
        viewLink.setName(source.getDomain().getDomain_name());
        viewLink.setType(WebLinkType.ViewURL);
        webLinks.add(viewLink);

        // add logo url
        webLinks.add(FaoDataCiteConstants.LOGO_WEB_LINK);

        // add related documents
        for (Document d : source.getDocuments()) {

            // filter out the dummy document
            if (d.getFileTitle().equals(FaoDataCiteConstants.TEMPLATE_DOCUMENT_NAME))
                continue;

            WebLink link = new WebLink(d.getDownloadPath());
            link.setName(d.getFileTitle());
            link.setType(WebLinkType.Related);
            webLinks.add(link);
        }

        return webLinks;
    }


    /**
     * Parses a list of {@linkplain Filter} object, converting each filter term to a {@linkplain Subject}
     * and returning them in a list.
     *
     * @param filters a list of domain filter categories
     *
     * @return a list of subjects of a domain filter category
     */
    private List<Subject> parseSubjects(List<Filter> filters)
    {
        List<Subject> subjects = new LinkedList<>();

        for (Filter f : filters) {
            Subject sub = new Subject(f.getLabel());
            sub.setLang(language);
            subjects.add(sub);
        }

        return subjects;
    }


    /**
     * Parses a list of {@linkplain Metadata}, looking for a contact person and returning
     * it in a list.
     *
     * @param metadata a list of domain metadata
     *
     * @return a list of contributors of a domain
     */
    private List<Contributor> parseContributors(List<Metadata> metadata)
    {
        List<Contributor> contributors = new LinkedList<>();

        Contributor contactPerson = new Contributor("", ContributorType.ContactPerson);

        for (Metadata m : metadata) {
            if (!m.getMetadata_group_code().equals("1"))
                continue;

            switch (m.getMetadata_label()) {
                case FaoDataCiteConstants.METADATA_CONTACT_NAME:
                    contactPerson.setName(new PersonName(m.getMetadata_text(), NameType.Personal));
                    break;

                case FaoDataCiteConstants.METADATA_CONTACT_ORGANISATION:
                    contactPerson.setAffiliations(Arrays.asList(m.getMetadata_text()));
                    break;

                default:
                    // ignore metadata
            }
        }

        if (!contactPerson.getName().getValue().isEmpty())
            contributors.add(contactPerson);

        return contributors;
    }
}
