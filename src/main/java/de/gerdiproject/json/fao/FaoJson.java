/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
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
package de.gerdiproject.json.fao;

import java.util.List;

/**
 * This class represents a generic FaoSTAT JSON response.
 * @author Robin Weiss
 *
 * @param <T> the type of data, carried by the response
 */
public class FaoJson <T>
{
    private FaoResponseMetadata metadata;
    private List<T> data;


    public FaoResponseMetadata getMetadata()
    {
        return metadata;
    }

    public void setMetadata(FaoResponseMetadata metadata)
    {
        this.metadata = metadata;
    }

    public List<T> getData()
    {
        return data;
    }

    public void setData(List<T> data)
    {
        this.data = data;
    }
}
