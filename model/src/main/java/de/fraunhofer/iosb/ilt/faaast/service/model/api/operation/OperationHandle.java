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
package de.fraunhofer.iosb.ilt.faaast.service.model.api.operation;

import io.adminshell.aas.v3.model.builder.ExtendableBuilder;
import java.util.Objects;


/**
 * An operation handle is used to identify async operations after invocation.
 */
public class OperationHandle {

    private String requestId;
    private String handleId;

    public String getRequestId() {
        return requestId;
    }


    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    public String getHandleId() {
        return handleId;
    }


    public void setHandleId(String handleId) {
        this.handleId = handleId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OperationHandle that = (OperationHandle) o;
        return Objects.equals(requestId, that.requestId) && Objects.equals(handleId, that.handleId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(requestId, handleId);
    }


    public static Builder builder() {
        return new Builder();
    }

    private abstract static class AbstractBuilder<T extends OperationHandle, B extends AbstractBuilder<T, B>> extends ExtendableBuilder<T, B> {

        public B requestId(String value) {
            getBuildingInstance().setRequestId(value);
            return getSelf();
        }


        public B handleId(String value) {
            getBuildingInstance().setHandleId(value);
            return getSelf();
        }
    }

    public static class Builder extends AbstractBuilder<OperationHandle, Builder> {

        @Override
        protected Builder getSelf() {
            return this;
        }


        @Override
        protected OperationHandle newBuildingInstance() {
            return new OperationHandle();
        }
    }
}
