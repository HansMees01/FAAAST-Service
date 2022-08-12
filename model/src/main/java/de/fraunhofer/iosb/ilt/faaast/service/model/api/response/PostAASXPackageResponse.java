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

import de.fraunhofer.iosb.ilt.faaast.service.model.api.AbstractResponseWithPayload;


/**
 * Chapter 4.5.4
 */
public class PostAASXPackageResponse extends AbstractResponseWithPayload<String> {

    public static PostAASXPackageResponse.Builder builder() {
        return new PostAASXPackageResponse.Builder();
    }

    public static class Builder extends AbstractBuilder<String, PostAASXPackageResponse, PostAASXPackageResponse.Builder> {

        @Override
        protected PostAASXPackageResponse.Builder getSelf() {
            return this;
        }


        @Override
        protected PostAASXPackageResponse newBuildingInstance() {
            return new PostAASXPackageResponse();
        }
    }
}
