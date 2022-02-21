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
package de.fraunhofer.iosb.ilt.faaast.service.requesthandlers.submodelelements;

import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.AssetConnectionManager;
import de.fraunhofer.iosb.ilt.faaast.service.exception.ResourceNotFoundException;
import de.fraunhofer.iosb.ilt.faaast.service.messagebus.MessageBus;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.StatusCode;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Extend;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Level;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.QueryModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.response.PutSubmodelElementByPathResponse;
import de.fraunhofer.iosb.ilt.faaast.service.model.request.PutSubmodelElementByPathRequest;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.ElementValue;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.Persistence;
import de.fraunhofer.iosb.ilt.faaast.service.requesthandlers.RequestHandler;
import de.fraunhofer.iosb.ilt.faaast.service.requesthandlers.Util;
import de.fraunhofer.iosb.ilt.faaast.service.util.ElementValueMapper;
import io.adminshell.aas.v3.model.Reference;
import io.adminshell.aas.v3.model.Submodel;
import io.adminshell.aas.v3.model.SubmodelElement;
import java.util.Objects;


public class PutSubmodelElementByPathRequestHandler extends RequestHandler<PutSubmodelElementByPathRequest, PutSubmodelElementByPathResponse> {

    public PutSubmodelElementByPathRequestHandler(Persistence persistence, MessageBus messageBus, AssetConnectionManager assetConnectionManager) {
        super(persistence, messageBus, assetConnectionManager);
    }


    @Override
    public PutSubmodelElementByPathResponse process(PutSubmodelElementByPathRequest request) {
        PutSubmodelElementByPathResponse response = new PutSubmodelElementByPathResponse();
        try {
            Reference reference = Util.toReference(request.getPath(), request.getId(), Submodel.class);

            //Check if submodelelement does exist
            SubmodelElement currentSubmodelElement = persistence.get(reference, new QueryModifier.Builder()
                    .extend(Extend.WithoutBLOBValue)
                    .level(Level.Core)
                    .build());
            SubmodelElement newSubmodelElement = request.getSubmodelElement();

            ElementValue oldValue = ElementValueMapper.toValue(currentSubmodelElement);
            ElementValue newValue = ElementValueMapper.toValue(newSubmodelElement);
            currentSubmodelElement = persistence.put(null, reference, newSubmodelElement);
            response.setPayload(currentSubmodelElement);
            response.setStatusCode(StatusCode.Success);

            if (!Objects.equals(oldValue, newValue)) {
                writeValueToAssetConnection(reference, ElementValueMapper.toValue(currentSubmodelElement));
            }

            publishElementUpdateEventMessage(reference, currentSubmodelElement);
        }
        catch (ResourceNotFoundException ex) {
            response.setStatusCode(StatusCode.ClientErrorResourceNotFound);
        }
        catch (Exception ex) {
            response.setStatusCode(StatusCode.ServerInternalError);
        }
        return response;
    }

}
