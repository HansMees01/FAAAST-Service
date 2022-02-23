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
package de.fraunhofer.iosb.ilt.faaast.service.model.api.response;

import de.fraunhofer.iosb.ilt.faaast.service.model.api.BaseResponseWithPayload;
import io.adminshell.aas.v3.model.IdentifierKeyValuePair;


/**
 * Chapter 7.2.4
 */
public class PostAllAssetLinksByIdResponse extends BaseResponseWithPayload<IdentifierKeyValuePair> {
    public static PostAllAssetLinksByIdResponse.Builder builder() {
        return new PostAllAssetLinksByIdResponse.Builder();
    }

    public static class Builder extends AbstractBuilder<PostAllAssetLinksByIdResponse, PostAllAssetLinksByIdResponse.Builder> {

        @Override
        protected PostAllAssetLinksByIdResponse.Builder getSelf() {
            return this;
        }


        public PostAllAssetLinksByIdResponse.Builder payload(IdentifierKeyValuePair value) {
            getBuildingInstance().setPayload(value);
            return getSelf();
        }


        @Override
        protected PostAllAssetLinksByIdResponse newBuildingInstance() {
            return new PostAllAssetLinksByIdResponse();
        }
    }
}