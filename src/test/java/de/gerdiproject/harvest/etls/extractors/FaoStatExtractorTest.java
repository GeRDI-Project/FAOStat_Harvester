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
package de.gerdiproject.harvest.etls.extractors;

import java.io.File;
import java.nio.charset.StandardCharsets;

import de.gerdiproject.harvest.FaoStatContextListener;
import de.gerdiproject.harvest.application.ContextListener;
import de.gerdiproject.harvest.etls.AbstractIteratorETL;
import de.gerdiproject.harvest.etls.FaoStatETL;
import de.gerdiproject.harvest.utils.data.DiskIO;
import de.gerdiproject.json.GsonUtils;
import de.gerdiproject.json.datacite.DataCiteJson;

/**
 * This class provides Unit Tests for the {@linkplain FaoStatExtractor}.
 *
 * @author Robin Weiss
 */
public class FaoStatExtractorTest extends AbstractIteratorExtractorTest<FaoStatDomainVO>
{
    final DiskIO diskReader = new DiskIO(GsonUtils.createGerdiDocumentGsonBuilder().create(), StandardCharsets.UTF_8);


    @Override
    protected ContextListener getContextListener()
    {
        return new FaoStatContextListener();
    }


    @Override
    protected AbstractIteratorETL<FaoStatDomainVO, DataCiteJson> getEtl()
    {
        return new FaoStatETL();
    }

    
    @Override
    protected File getConfigFile()
    {
        return getResource("config.json");
    }
    

    @Override
    protected File getMockedHttpResponseFolder()
    {
        return getResource("mockedHttpResponses");
    }


    @Override
    protected FaoStatDomainVO getExpectedOutput()
    {
        final File resource = getResource("output.json");
        return diskReader.getObject(resource, FaoStatDomainVO.class);
    }
}
