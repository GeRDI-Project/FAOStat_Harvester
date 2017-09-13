package de.gerdiproject.json.fao;

import de.gerdiproject.json.fao.FaoFilters.Filter;

/**
 * This class represents the JSON response of a FaoSTAT codes request.
 * e.g. http://fenixservices.fao.org/faostat/api/v1/en/codes/years/QC/?show_lists=true
 *
 * @author Robin Weiss
 *
 */
public class FaoFilters extends FaoJson<Filter>
{
    public static class Filter
    {
        private String code;
        private String label;
        private String aggregate_type;

        public String getCode()
        {
            return code;
        }

        public void setCode(String code)
        {
            this.code = code;
        }

        public String getLabel()
        {
            return label;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

        public String getAggregate_type()
        {
            return aggregate_type;
        }

        public void setAggregate_type(String aggregate_type)
        {
            this.aggregate_type = aggregate_type;
        }
    }
}
