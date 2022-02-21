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
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.model.HttpMethod;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.model.HttpRequest;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.Request;
import de.fraunhofer.iosb.ilt.faaast.service.model.request.GetAllAssetLinksByIdRequest;
import de.fraunhofer.iosb.ilt.faaast.service.util.EncodingHelper;


/**
 * class to map HTTP-GET-Request path: lookup/shells/{aasIdentifier}
 */
public class GetAllAssetLinksByIdRequestMapper extends RequestMapper {

    private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    private static final String PATTERN = "^lookup/shells/(.*)$";

    public GetAllAssetLinksByIdRequestMapper(ServiceContext serviceContext) {
        super(serviceContext);
    }


    @Override
    public Request parse(HttpRequest httpRequest) {
        return GetAllAssetLinksByIdRequest.builder()
                .aasIdentifier(EncodingHelper.base64Decode(httpRequest.getPathElements().get(2)))
                .build();
    }


    @Override
    public boolean matches(HttpRequest httpRequest) {
        return httpRequest.getMethod().equals(HTTP_METHOD)
                && httpRequest.getPath().matches(PATTERN);
    }
}
