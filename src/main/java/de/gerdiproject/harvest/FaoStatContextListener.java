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
package de.gerdiproject.harvest;

import de.gerdiproject.harvest.config.parameters.AbstractParameter;
import de.gerdiproject.harvest.config.parameters.StringParameter;
import de.gerdiproject.harvest.fao.constants.FaoParameterConstants;
import de.gerdiproject.harvest.harvester.FaoStatHarvester;

import java.util.Arrays;
import java.util.List;

import javax.servlet.annotation.WebListener;

/**
 * This class initializes the FAOSTAT harvester and all objects that are required.
 *
 * @author Robin Weiss
 */
@WebListener
public class FaoStatContextListener extends ContextListener<FaoStatHarvester>
{
    @Override
    protected List<AbstractParameter<?>> getHarvesterSpecificParameters()
    {
        StringParameter versionParam = new StringParameter(FaoParameterConstants.VERSION_KEY, FaoParameterConstants.VERSION_DEFAULT);
        StringParameter languageParam = new StringParameter(FaoParameterConstants.LANGUAGE_KEY, FaoParameterConstants.LANGUAGE_DEFAULT);

        return Arrays.asList(versionParam, languageParam);
    }
}
