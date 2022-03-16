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
import io.adminshell.aas.v3.model.AssetAdministrationShell;


/**
 * Chapter 6.3.2
 */
public class GetAssetAdministrationShellByIdResponse extends BaseResponseWithPayload<AssetAdministrationShell> {

    public static GetAssetAdministrationShellByIdResponse.Builder builder() {
        return new GetAssetAdministrationShellByIdResponse.Builder();
    }

    public static class Builder extends AbstractBuilder<AssetAdministrationShell, GetAssetAdministrationShellByIdResponse, GetAssetAdministrationShellByIdResponse.Builder> {

        @Override
        protected GetAssetAdministrationShellByIdResponse.Builder getSelf() {
            return this;
        }


        @Override
        protected GetAssetAdministrationShellByIdResponse newBuildingInstance() {
            return new GetAssetAdministrationShellByIdResponse();
        }
    }
}
