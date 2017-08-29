package de.gerdiproject.harvest.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gerdiproject.json.datacite.Contributor;
import de.gerdiproject.json.datacite.Contributor.ContributorType;
import de.gerdiproject.json.datacite.Date;
import de.gerdiproject.json.datacite.Date.DateType;
import de.gerdiproject.json.datacite.Description;
import de.gerdiproject.json.datacite.Description.DescriptionType;
import de.gerdiproject.json.datacite.File;
import de.gerdiproject.json.datacite.Source;
import de.gerdiproject.json.datacite.Subject;
import de.gerdiproject.json.datacite.Title;
import de.gerdiproject.json.datacite.Title.TitleType;
import de.gerdiproject.json.datacite.WebLink.WebLinkType;
import de.gerdiproject.json.datacite.WebLink;
import de.gerdiproject.json.fao.FaoBulkDownloads;
import de.gerdiproject.json.fao.FaoBulkDownloads.BulkDownload;
import de.gerdiproject.json.fao.FaoDimensions;
import de.gerdiproject.json.fao.FaoDocuments;
import de.gerdiproject.json.fao.FaoDocuments.Document;
import de.gerdiproject.json.fao.FaoDimensions.Dimension;
import de.gerdiproject.json.fao.FaoDomains.Domain;
import de.gerdiproject.json.fao.FaoFilters;
import de.gerdiproject.json.fao.FaoFilters.Filter;
import de.gerdiproject.json.fao.FaoMetadata;
import de.gerdiproject.json.fao.FaoMetadata.Metadata;

public class FaoStatDomainParser
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FaoStatDomainParser.class);
    private static final SimpleDateFormat UPDATE_DATE_FORMAT = new SimpleDateFormat("MMM.' 'yyyy");


    public static List<Description> parseDescriptions(FaoMetadata metadata, String language)
    {
        List<Description> descriptions = new LinkedList<>();

        List<Metadata> metadataList = metadata.getData();

        metadataList.forEach((Metadata m) -> {
            String label = m.getMetadata_label();
            DescriptionType type = FaoStatConstants.RELEVANT_DESCRIPTIONS.get(label);

            if (type != null)
            {
                String descriptionText = String.format(FaoStatConstants.DESCRIPTION_FORMAT, label, m.getMetadata_text());
                Description desc = new Description(descriptionText, type);
                desc.setLang(language);
                descriptions.add(desc);
            }
        });

        return descriptions;
    }


    public static List<Date> parseDates(FaoMetadata metadata, String language)
    {
        List<Date> dates = new LinkedList<>();

        List<Metadata> metadataList = metadata.getData();

        metadataList.forEach((Metadata m) -> {
            String dateText = m.getMetadata_text();

            switch (m.getMetadata_label())
            {
                case FaoStatConstants.META_DATA_TIME_COVERAGE:
                    Matcher matcher = FaoStatConstants.TIME_COVERAGE_PATTERN.matcher(dateText);

                    try {
                        // retrieve first date from text
                        matcher.find();
                        int from = Integer.parseInt(matcher.group());

                        // retrieve second date from text
                        matcher.find();
                        int to = Integer.parseInt(matcher.group());

                        // convert years to dates
                        Calendar cal = Calendar.getInstance();

                        cal.set(from, 0, 1);
                        Date timeCoverageFrom = new Date(cal);
                        timeCoverageFrom.setType(DateType.Collected);

                        cal.set(to, 0, 1);
                        Date timeCoverageTo = new Date(cal);
                        timeCoverageTo.setType(DateType.Collected);

                        // add dates to list
                        dates.add(timeCoverageFrom);
                        dates.add(timeCoverageTo);

                        // TODO: find a way to accept date ranges in ES

                    } catch (IllegalStateException | NumberFormatException e) {
                        LOGGER.warn(FaoStatConstants.DATE_PARSE_ERROR_PREFIX + dateText);
                    }

                    break;

                case FaoStatConstants.META_DATA_LAST_UPDATE:
                    try {
                        // parse update date (e.g. "Nov. 2015")
                        Date lastUpdate = new Date(UPDATE_DATE_FORMAT.parse(dateText));
                        lastUpdate.setType(DateType.Updated);

                        dates.add(lastUpdate);
                    } catch (ParseException e) { // NOPMD - if the update cannot be parsed, we simply cannot add it
                        LOGGER.warn(FaoStatConstants.DATE_PARSE_ERROR_PREFIX + dateText);
                    }

                    break;
            }
        });
        return dates;
    }

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


    public static List<File> parseFiles(FaoBulkDownloads bulkDownloads)
    {
        List<File> files = new LinkedList<>();
        List<BulkDownload> bulkList = bulkDownloads.getData();

        bulkList.forEach((BulkDownload bdl) -> {
            String url = bdl.getURL();
            String label = bdl.getFileContent();
            String identifier = String.valueOf(url.hashCode());
            String type = bdl.getFileName().substring(bdl.getFileName().lastIndexOf('.') + 1);

            File file = new File(url, label, identifier);
            file.setType(type);

            files.add(file);
        });

        return files;
    }

    public static List<WebLink> parseWebLinks(FaoDocuments documents, Domain domain)
    {
        List<WebLink> webLinks = new LinkedList<>();
        List<Document> documentList = documents.getData();

        // add view url
        WebLink viewLink = new WebLink(FaoStatConstants.VIEW_URL_PREFIX + domain.getDomain_code());
        viewLink.setName(domain.getDomain_name());
        viewLink.setType(WebLinkType.ViewURL);
        webLinks.add(viewLink);

        // add logo url
        webLinks.add(FaoStatConstants.LOGO_WEB_LINK);

        // add related documents
        documentList.forEach((Document d) -> {
            WebLink link = new WebLink(d.getDownloadPath());
            link.setName(d.getFileTitle());
            link.setType(WebLinkType.Related);
            webLinks.add(link);
        });

        return webLinks;
    }

    public static List<String> parseFilterUrls(FaoDimensions dimensions, String version, String language, String domainCode)
    {
        List<String> filterUrls = new LinkedList<>();
        List<Dimension> dimensionList = dimensions.getData();

        dimensionList.forEach((Dimension d) ->
                              filterUrls.add(d.getDimensionUrl(version, language, domainCode))
                             );

        return filterUrls;
    }


    public static List<Subject> parseSubjects(FaoFilters filters, String language)
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


    public static Source parseSource(String domainCode)
    {
        Source source = new Source(FaoStatConstants.VIEW_URL_PREFIX + domainCode, FaoStatConstants.PROVIDER);
        source.setProviderURI(FaoStatConstants.PROVIDER_URI);
        return source;
    }


    public static List<Contributor> parseContributors(FaoMetadata metadata)
    {
        List<Contributor> contributors = new LinkedList<>();
        List<Metadata> metadataList = metadata.getData();

        Contributor contactPerson = new Contributor(null, ContributorType.ContactPerson);

        for (Metadata m : metadataList) {
            if (m.getMetadata_group_code().equals("1")) {
                switch (m.getMetadata_label()) {
                    case FaoStatConstants.METADATA_CONTACT_NAME:
                        contactPerson.setName(m.getMetadata_text());
                        break;

                    case FaoStatConstants.METADATA_CONTACT_ORGANISATION:
                        contactPerson.setAffiliation(m.getMetadata_text());
                        break;

                    default:
                        // ignore the metadata
                }
            }
        }

        return contributors;
    }

    /**
     * Private Constructor, because this is a static class.
     */
    private FaoStatDomainParser()
    {
    }
}
