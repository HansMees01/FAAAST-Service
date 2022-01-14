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
package de.fraunhofer.iosb.ilt.faaast.service.model.v3.api.request;

import de.fraunhofer.iosb.ilt.faaast.service.model.v3.api.BaseRequest;
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.api.OutputModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.api.response.GetSubmodelElementByPathResponse;
import io.adminshell.aas.v3.model.Identifier;
import io.adminshell.aas.v3.model.Key;
import io.adminshell.aas.v3.model.builder.ExtendableBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Chapter 4.3.4
 */
public class GetSubmodelElementByPathRequest extends BaseRequest<GetSubmodelElementByPathResponse> {
    private Identifier id;
    private OutputModifier outputModifier;
    private List<Key> path;

    public GetSubmodelElementByPathRequest() {
        this.path = new ArrayList<>();
        this.outputModifier = OutputModifier.DEFAULT;
    }


    public OutputModifier getOutputModifier() {
        return outputModifier;
    }


    public void setOutputModifier(OutputModifier outputModifier) {
        this.outputModifier = outputModifier;
    }


    public List<Key> getPath() {
        return path;
    }


    public void setPath(List<Key> path) {
        this.path = path;
    }


    public Identifier getId() {
        return id;
    }


    public void setId(Identifier id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GetSubmodelElementByPathRequest that = (GetSubmodelElementByPathRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(outputModifier, that.outputModifier) && Objects.equals(path, that.path);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, outputModifier, path);
    }


    public static Builder builder() {
        return new Builder();
    }

    public static abstract class AbstractBuilder<T extends GetSubmodelElementByPathRequest, B extends AbstractBuilder<T, B>> extends ExtendableBuilder<T, B> {
        public B id(Identifier value) {
            getBuildingInstance().setId(value);
            return getSelf();
        }


        public B outputModifier(OutputModifier value) {
            getBuildingInstance().setOutputModifier(value);
            return getSelf();
        }


        public B path(List<Key> value) {
            getBuildingInstance().setPath(value);
            return getSelf();
        }
    }

    public static class Builder extends AbstractBuilder<GetSubmodelElementByPathRequest, Builder> {

        @Override
        protected Builder getSelf() {
            return this;
        }


        @Override
        protected GetSubmodelElementByPathRequest newBuildingInstance() {
            return new GetSubmodelElementByPathRequest();
        }
    }
}
