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
package de.gerdiproject.harvest.fao.json;

import java.util.List;

import lombok.Value;

/**
 * This class represents a generic FaoSTAT JSON response.
 *
 * @param <T> the type of data, carried by the response
 *
 * @author Robin Weiss
 */
@Value
public class GenericFaoResponse <T>
{
    private final GenericFaoResponseMetadata metadata;
    private final List<T> data;
}
