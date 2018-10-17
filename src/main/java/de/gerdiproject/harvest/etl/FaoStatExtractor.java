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
package de.gerdiproject.harvest.etl;

import com.google.gson.Gson;

import de.gerdiproject.harvest.etls.AbstractETL;
import de.gerdiproject.harvest.etls.extractors.JsonArrayExtractor;
import de.gerdiproject.harvest.fao.constants.FaoDownloaderConstants;
import de.gerdiproject.harvest.fao.json.DomainsResponse.Domain;

/**
 * This {@linkplain JsonArrayExtractor} implementation extracts all
 * {@linkplain Domain}s of FAOSTAT.<br>
 * (http://fenixservices.fao.org/faostat/api/v1/en/groupsanddomains)
 *
 * @author Robin Weiss
 */
public class FaoStatExtractor extends JsonArrayExtractor<Domain>
{

    /**
     * Simple constructor.
     */
    public FaoStatExtractor()
    {
        super(new Gson(), "data");
    }


    @Override
    public void init(AbstractETL<?, ?> etl)
    {
        final String baseUrl = String.format(
                                   FaoDownloaderConstants.BASE_URL,
                                   ((FaoStatETL)etl).getLanguage()
                               );
        setUrl(baseUrl + FaoDownloaderConstants.GROUPS_AND_DOMAINS_URL);

        super.init(etl);
    }
}
