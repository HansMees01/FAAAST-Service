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
package de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.request.mapper;

import de.fraunhofer.iosb.ilt.faaast.service.ServiceContext;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.exception.InvalidRequestException;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.model.HttpMethod;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.model.HttpRequest;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.OutputModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.response.PutSubmodelResponse;
import de.fraunhofer.iosb.ilt.faaast.service.model.request.PutSubmodelRequest;
import io.adminshell.aas.v3.model.Submodel;
import java.util.Map;


/**
 * class to map HTTP-PUT-Request paths: submodels/{submodelIdentifier}/submodel,
 * shells/{aasIdentifier}/aas/submodels/{submodelIdentifier}/submodel.
 */
public class PutSubmodelRequestMapper extends AbstractSubmodelInterfaceRequestMapper<PutSubmodelRequest, PutSubmodelResponse> {

    private static final String PATTERN = "";

    public PutSubmodelRequestMapper(ServiceContext serviceContext) {
        super(serviceContext, HttpMethod.PUT, PATTERN);
    }


    @Override
    public PutSubmodelRequest doParse(HttpRequest httpRequest, Map<String, String> urlParameters, OutputModifier outputModifier) throws InvalidRequestException {
        return PutSubmodelRequest.builder()
                .submodel(parseBody(httpRequest, Submodel.class))
                .build();
    }
}
