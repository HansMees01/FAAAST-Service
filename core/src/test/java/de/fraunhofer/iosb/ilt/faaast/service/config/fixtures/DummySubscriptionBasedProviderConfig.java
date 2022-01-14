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
package de.fraunhofer.iosb.ilt.faaast.service.config.fixtures;

import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.AssetSubscriptionProviderConfig;
import java.util.Objects;


public class DummySubscriptionBasedProviderConfig implements AssetSubscriptionProviderConfig {

    private String nodeId;
    private long interval;

    public String getNodeId() {
        return nodeId;
    }


    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }


    public long getInterval() {
        return interval;
    }


    public void setInterval(long interval) {
        this.interval = interval;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.nodeId);
        hash = 71 * hash + (int) (this.interval ^ (this.interval >>> 32));
        return hash;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DummySubscriptionBasedProviderConfig other = (DummySubscriptionBasedProviderConfig) obj;
        if (this.interval != other.interval) {
            return false;
        }
        if (!Objects.equals(this.nodeId, other.nodeId)) {
            return false;
        }
        return true;
    }

}
