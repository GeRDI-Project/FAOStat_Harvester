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
package de.gerdiproject.harvest.etls.transformers;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import de.gerdiproject.harvest.etls.AbstractETL;
import de.gerdiproject.harvest.etls.FaoStatETL;
import de.gerdiproject.harvest.etls.extractors.FaoStatDomainVO;
import de.gerdiproject.harvest.fao.constants.FaoDataCiteConstants;
import de.gerdiproject.harvest.fao.json.BulkDownloadResponse.BulkDownload;
import de.gerdiproject.harvest.fao.json.DocumentsResponse.Document;
import de.gerdiproject.harvest.fao.json.DomainsResponse.Domain;
import de.gerdiproject.harvest.fao.json.FaoFilter;
import de.gerdiproject.harvest.fao.json.FaoMetadata;
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
import de.gerdiproject.json.datacite.extension.generic.ResearchData;
import de.gerdiproject.json.datacite.extension.generic.WebLink;
import de.gerdiproject.json.datacite.extension.generic.enums.WebLinkType;
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
    public void init(final AbstractETL<?, ?> etl)
    {
        this.language = ((FaoStatETL)etl).getLanguage();
    }


    @Override
    protected DataCiteJson transformElement(final FaoStatDomainVO source)
    {
        // create the document
        final DataCiteJson document = new DataCiteJson(createIdentifier(source.getDomain()));

        document.setLanguage(language);
        document.setRepositoryIdentifier(FaoDataCiteConstants.REPOSITORY_ID);
        document.setPublicationYear(FaoDataCiteConstants.EARLIEST_PUBLICATION_YEAR);
        document.setResourceType(FaoDataCiteConstants.RESOURCE_TYPE);
        document.addFormats(FaoDataCiteConstants.FORMATS);
        document.addResearchDisciplines(FaoDataCiteConstants.DISCIPLINES);

        // get source
        document.setPublisher(FaoDataCiteConstants.PROVIDER);

        // get a readable name of the domain
        document.addTitles(parseTitles(source.getDomain()));

        // get bulk-download URL
        document.addResearchData(parseFiles(source.getBulkDownloads()));

        // get description
        document.addDescriptions(parseDescriptions(source.getMetadata()));

        // get URLs of all filters that can be applied to the domain
        document.addSubjects(parseSubjects(source.getFilters()));

        // get dates
        document.addDates(parseDates(source.getMetadata()));

        // get web links
        document.addWebLinks(parseWebLinks(source));

        // get contact person
        document.addContributors(parseContributors(source.getMetadata()));

        // get creator
        document.addCreators(FaoDataCiteConstants.CREATORS);

        return document;
    }


    /**
     * Creates a unique identifier of a domain within FAOSTAT.
     *
     * @param domain the domain of which the identifier is created
     *
     * @return a unique identifier of this domain within FAOSTAT
     */
    private String createIdentifier(final Domain domain)
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
    private List<Description> parseDescriptions(final List<FaoMetadata> metadata)
    {
        final List<Description> descriptions = new LinkedList<>();

        for (final FaoMetadata m : metadata) {
            final String label = m.getMetadataLabel();
            final DescriptionType type = FaoDataCiteConstants.RELEVANT_DESCRIPTIONS.get(label);

            if (type != null) {
                final String descriptionText = String.format(FaoDataCiteConstants.DESCRIPTION_FORMAT, label, m.getMetadataText());
                final Description desc = new Description(descriptionText, type);
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
    private List<AbstractDate> parseDates(final List<FaoMetadata> metadata)
    {
        final List<AbstractDate> dates = new LinkedList<>();

        for (final FaoMetadata m : metadata) {
            final String dateText = m.getMetadataText();

            if (dateText == null || dateText.isEmpty())
                continue; // skip this date and go to the next

            switch (m.getMetadataLabel()) {
                case FaoDataCiteConstants.META_DATA_TIME_COVERAGE:
                    final Matcher matcher = FaoDataCiteConstants.TIME_COVERAGE_PATTERN.matcher(dateText);

                    // check if it is a date range
                    if (matcher.find()) {
                        final String startYear = matcher.group(1);
                        final String endYear = matcher.group(2);
                        dates.add(new DateRange(startYear, endYear, DateType.Other));
                    } else
                        dates.add(new Date(dateText, DateType.Other));

                    break;

                case FaoDataCiteConstants.META_DATA_LAST_UPDATE:
                    final Date lastUpdate = new Date(dateText, DateType.Updated);
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
    private List<Title> parseTitles(final Domain domain)
    {
        final List<Title> titles = new LinkedList<>();

        final Title domainTitle = new Title(domain.getDomain_name());
        domainTitle.setLang(language);
        titles.add(domainTitle);

        final Title groupTitle = new Title(domain.getGroup_name());
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
    private List<ResearchData> parseFiles(final List<BulkDownload> bulkDownloads)
    {
        final List<ResearchData> files = new LinkedList<>();

        for (final BulkDownload bdl : bulkDownloads) {
            final String url = bdl.getURL();
            final String label = bdl.getFileContent();
            final String type = bdl.getFileName().substring(bdl.getFileName().lastIndexOf('.') + 1);

            final ResearchData file = new ResearchData(url, label);
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
    private List<WebLink> parseWebLinks(final FaoStatDomainVO source)
    {
        final List<WebLink> webLinks = new LinkedList<>();
        // add view url
        final String viewUrl = String.format(FaoDataCiteConstants.VIEW_URL, source.getDomain().getDomain_code());
        final WebLink viewLink = new WebLink(viewUrl);
        viewLink.setName(source.getDomain().getDomain_name());
        viewLink.setType(WebLinkType.ViewURL);
        webLinks.add(viewLink);

        // add logo url
        webLinks.add(FaoDataCiteConstants.LOGO_WEB_LINK);

        // add related documents
        for (final Document d : source.getDocuments()) {

            // filter out the dummy document
            if (d.getFileTitle().equals(FaoDataCiteConstants.TEMPLATE_DOCUMENT_NAME))
                continue;

            final WebLink link = new WebLink(d.getDownloadPath());
            link.setName(d.getFileTitle());
            link.setType(WebLinkType.Related);
            webLinks.add(link);
        }

        return webLinks;
    }


    /**
     * Parses a list of {@linkplain FaoFilter} object, converting each filter term to a {@linkplain Subject}
     * and returning them in a list.
     *
     * @param filters a list of domain filter categories
     *
     * @return a list of subjects of a domain filter category
     */
    private List<Subject> parseSubjects(final List<FaoFilter> filters)
    {
        final List<Subject> subjects = new LinkedList<>();

        for (final FaoFilter f : filters) {
            final Subject sub = new Subject(f.getLabel());
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
    private List<Contributor> parseContributors(final List<FaoMetadata> metadata)
    {
        final List<Contributor> contributors = new LinkedList<>();

        PersonName name = null;
        final List<String> affiliations = new LinkedList<>();

        for (final FaoMetadata m : metadata) {
            if (!m.getMetadataGroupCode().equals("1"))
                continue;

            switch (m.getMetadataLabel()) {
                case FaoDataCiteConstants.METADATA_CONTACT_NAME:
                    name = new PersonName(m.getMetadataText(), NameType.Personal);
                    break;

                case FaoDataCiteConstants.METADATA_CONTACT_ORGANISATION:
                    affiliations.add(m.getMetadataText());
                    break;

                default:
                    // ignore metadata
            }
        }

        if (name != null) {
            final Contributor contactPerson = new Contributor(name, ContributorType.ContactPerson);
            contactPerson.addAffiliations(affiliations);
            contributors.add(contactPerson);
        }

        return contributors;
    }


    /* (non-Javadoc)
     * @see de.gerdiproject.harvest.etls.transformers.ITransformer#clear()
     */
    @Override
    public void clear()
    {
        // TODO Auto-generated method stub

    }
}
