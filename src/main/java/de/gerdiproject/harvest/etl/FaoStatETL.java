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

import de.gerdiproject.harvest.config.Configuration;
import de.gerdiproject.harvest.config.parameters.StringParameter;
import de.gerdiproject.harvest.etls.StaticIteratorETL;
import de.gerdiproject.harvest.fao.constants.FaoParameterConstants;
import de.gerdiproject.harvest.fao.json.DomainsResponse.Domain;
import de.gerdiproject.json.datacite.DataCiteJson;

/**
 * This ETL component harvests FAOSTAT (http://www.fao.org/faostat/en/#data).
 *
 * @author Robin Weiss
 */
public class FaoStatETL extends StaticIteratorETL<Domain, DataCiteJson>
{
    private volatile StringParameter languageParameter;

    /**
     * Constructor
     */
    public FaoStatETL()
    {
        super(new FaoStatExtractor(), new FaoStatTransformer());
    }


    @Override
    protected void registerParameters()
    {
        super.registerParameters();

        this.languageParameter =
            Configuration.registerParameter(new StringParameter(
                                                FaoParameterConstants.LANGUAGE_KEY,
                                                harvesterCategory,
                                                FaoParameterConstants.LANGUAGE_DEFAULT));
    }


    /**
     * Retrieves the language in the harvest URL path.
     *
     * @return the language in the harvest URL path
     */
    public String getLanguage()
    {
        return languageParameter.getStringValue();
    }

}
