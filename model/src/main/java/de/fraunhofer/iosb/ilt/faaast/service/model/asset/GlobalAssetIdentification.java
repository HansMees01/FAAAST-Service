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
package de.fraunhofer.iosb.ilt.faaast.service.model.asset;

import io.adminshell.aas.v3.model.Reference;
import io.adminshell.aas.v3.model.builder.ExtendableBuilder;
import java.util.Objects;


/**
 * Represents a global asset identification.
 */
public class GlobalAssetIdentification implements AssetIdentification {

    private Reference reference;

    public Reference getReference() {
        return reference;
    }


    public void setReference(Reference reference) {
        this.reference = reference;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GlobalAssetIdentification that = (GlobalAssetIdentification) o;
        return Objects.equals(reference, that.reference);
    }


    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }


    public static Builder builder() {
        return new Builder();
    }

    public abstract static class AbstractBuilder<T extends GlobalAssetIdentification, B extends AbstractBuilder<T, B>> extends ExtendableBuilder<T, B> {

        public B reference(Reference value) {
            getBuildingInstance().setReference(value);
            return getSelf();
        }
    }

    public static class Builder extends AbstractBuilder<GlobalAssetIdentification, Builder> {

        @Override
        protected Builder getSelf() {
            return this;
        }


        @Override
        protected GlobalAssetIdentification newBuildingInstance() {
            return new GlobalAssetIdentification();
        }
    }
}
