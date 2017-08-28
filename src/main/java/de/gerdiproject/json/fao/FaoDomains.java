package de.gerdiproject.json.fao;

import de.gerdiproject.json.fao.FaoDomains.Domain;

/**
 * This class represents the JSON response of a FaoSTAT groups and domains request.
 * http://fenixservices.fao.org/faostat/api/v1/en/groupsanddomains?section=download
 *
 * @author Robin Weiss
 *
 */
public class FaoDomains extends FaoJson<Domain>
{
    public class Domain
    {
        private String group_code;
        private String group_name;
        private String domain_code;
        private String domain_name;
        private String date_update;
        private String note_update;
        private String release_current;
        private String state_current;
        private String year_current;
        private String release_next;
        private String state_next;
        private String year_next;


        public String getGroup_code()
        {
            return group_code;
        }

        public void setGroup_code(String group_code)
        {
            this.group_code = group_code;
        }

        public String getGroup_name()
        {
            return group_name;
        }

        public void setGroup_name(String group_name)
        {
            this.group_name = group_name;
        }

        public String getDomain_code()
        {
            return domain_code;
        }

        public void setDomain_code(String domain_code)
        {
            this.domain_code = domain_code;
        }

        public String getDomain_name()
        {
            return domain_name;
        }

        public void setDomain_name(String domain_name)
        {
            this.domain_name = domain_name;
        }

        public String getDate_update()
        {
            return date_update;
        }

        public void setDate_update(String date_update)
        {
            this.date_update = date_update;
        }

        public String getNote_update()
        {
            return note_update;
        }

        public void setNote_update(String note_update)
        {
            this.note_update = note_update;
        }

        public String getRelease_current()
        {
            return release_current;
        }

        public void setRelease_current(String release_current)
        {
            this.release_current = release_current;
        }

        public String getState_current()
        {
            return state_current;
        }

        public void setState_current(String state_current)
        {
            this.state_current = state_current;
        }

        public String getYear_current()
        {
            return year_current;
        }

        public void setYear_current(String year_current)
        {
            this.year_current = year_current;
        }

        public String getRelease_next()
        {
            return release_next;
        }

        public void setRelease_next(String release_next)
        {
            this.release_next = release_next;
        }

        public String getState_next()
        {
            return state_next;
        }

        public void setState_next(String state_next)
        {
            this.state_next = state_next;
        }

        public String getYear_next()
        {
            return year_next;
        }

        public void setYear_next(String year_next)
        {
            this.year_next = year_next;
        }
    }
}
