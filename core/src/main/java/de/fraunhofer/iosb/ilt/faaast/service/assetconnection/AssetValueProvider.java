/*
 * Copyright (c) 2021 Fraunhofer IOSB, eine rechtlich nicht selbstaendige
 * Einrichtung der Fraunhofer-Gesellschaft zur Foerderung der angewandten
 * Forschung e.V.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fraunhofer.iosb.ilt.faaast.service.assetconnection;

import de.fraunhofer.iosb.ilt.faaast.service.model.v3.valuedata.DataElementValue;


/**
 * An AssetValueProvider provides methods to reade/write data values from/to an asset.
 */
public interface AssetValueProvider extends AssetProvider {

    /**
     * Read a data value from the asset.
     * 
     * @return the data value
     */
    public DataElementValue getValue();


    /**
     * Sets the data value on an asset.
     * 
     * @param value the value to set
     */
    public void setValue(DataElementValue value);
}
