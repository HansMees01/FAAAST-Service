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
package de.fraunhofer.iosb.ilt.faaast.service.assetconnection.opcua;

import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.AssetValueProviderConfig;
import io.adminshell.aas.v3.model.builder.ExtendableBuilder;
import java.util.Objects;


public class OpcUaValueProviderConfig implements AssetValueProviderConfig {

    public static Builder builder() {
        return new Builder();
    }

    private String nodeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OpcUaValueProviderConfig that = (OpcUaValueProviderConfig) o;
        return Objects.equals(nodeId, that.nodeId);
    }


    public String getNodeId() {
        return nodeId;
    }


    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }


    @Override
    public int hashCode() {
        return Objects.hash(nodeId);
    }

    public static class Builder extends AbstractBuilder<OpcUaValueProviderConfig, Builder> {

        @Override
        protected Builder getSelf() {
            return this;
        }


        @Override
        protected OpcUaValueProviderConfig newBuildingInstance() {
            return new OpcUaValueProviderConfig();
        }
    }

    private static abstract class AbstractBuilder<T extends OpcUaValueProviderConfig, B extends AbstractBuilder<T, B>> extends ExtendableBuilder<T, B> {

        public B nodeId(String value) {
            getBuildingInstance().setNodeId(value);
            return getSelf();
        }

    }
}