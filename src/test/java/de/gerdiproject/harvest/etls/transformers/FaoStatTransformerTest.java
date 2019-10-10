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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import de.gerdiproject.harvest.AbstractIteratorTransformerTest;
import de.gerdiproject.harvest.FaoStatContextListener;
import de.gerdiproject.harvest.application.ContextListener;
import de.gerdiproject.harvest.etls.FaoStatETL;
import de.gerdiproject.harvest.etls.extractors.FaoStatDomainVO;
import de.gerdiproject.harvest.etls.transformers.FaoStatTransformer;
import de.gerdiproject.harvest.utils.data.DiskIO;
import de.gerdiproject.json.GsonUtils;
import de.gerdiproject.json.datacite.DataCiteJson;

/**
 * This class provides Unit Tests for the {@linkplain FaoStatTransformer}.
 *
 * @author Robin Weiss
 */
public class FaoStatTransformerTest extends AbstractIteratorTransformerTest<FaoStatTransformer, FaoStatDomainVO, DataCiteJson>
{
    final DiskIO diskReader = new DiskIO(GsonUtils.createGerdiDocumentGsonBuilder().create(), StandardCharsets.UTF_8);


    /**
     * Default Test Constructor.
     */
    public FaoStatTransformerTest()
    {
        super(new FaoStatETL(), new FaoStatTransformer());
    }


    @Override
    protected Map<String, String> getParameterValues()
    {
        return null;
    }


    @Override
    protected ContextListener getContextListener()
    {
        return new FaoStatContextListener();
    }


    @Override
    protected FaoStatDomainVO getMockedInput()
    {
        final File resource = new File(getResourceDirectory(), "input.json");
        return diskReader.getObject(resource, FaoStatDomainVO.class);
    }


    @Override
    protected DataCiteJson getExpectedOutput()
    {
        final File resource = new File(getResourceDirectory(), "output.json");
        return diskReader.getObject(resource, DataCiteJson.class);
    }
}
