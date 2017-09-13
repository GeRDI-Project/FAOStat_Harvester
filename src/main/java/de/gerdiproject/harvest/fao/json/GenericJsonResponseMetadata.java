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
package de.gerdiproject.harvest.fao.json;

/**
 * This class represents the the metadata object, that is part of every JSON response from FaoSTAT.
 *
 * @author Robin Weiss
 *
 */
public final class GenericJsonResponseMetadata
{
    private double processing_time;
    private String output_type;

    public double getProcessing_time()
    {
        return processing_time;
    }

    public void setProcessing_time(double processing_time)
    {
        this.processing_time = processing_time;
    }

    public String getOutput_type()
    {
        return output_type;
    }

    public void setOutput_type(String output_type)
    {
        this.output_type = output_type;
    }
}
