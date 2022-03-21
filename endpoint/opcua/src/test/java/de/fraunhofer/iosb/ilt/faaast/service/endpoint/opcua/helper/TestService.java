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
package de.fraunhofer.iosb.ilt.faaast.service.endpoint.opcua.helper;

import de.fraunhofer.iosb.ilt.faaast.service.Service;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.AssetConnection;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.AssetConnectionException;
import de.fraunhofer.iosb.ilt.faaast.service.config.CoreConfig;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.Endpoint;
import de.fraunhofer.iosb.ilt.faaast.service.exception.ConfigurationException;
import de.fraunhofer.iosb.ilt.faaast.service.messagebus.internal.MessageBusInternal;
import de.fraunhofer.iosb.ilt.faaast.service.model.AASFull;
import de.fraunhofer.iosb.ilt.faaast.service.model.AASSimple;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.memory.PersistenceInMemory;
import java.util.ArrayList;
import java.util.List;


/**
 * A AAS Test service.
 *
 * @author Tino Bischoff
 */
public class TestService extends Service {

    /**
     * Creates a new instance of TestService
     *
     * @param endpoint
     * @param assetConnection The desired AssetConnection
     * @param full True if the full example is requested, otherwise the simple
     *            is used
     * @throws ConfigurationException If the operation fails
     */
    public TestService(Endpoint endpoint, AssetConnection assetConnection, boolean full) throws ConfigurationException, AssetConnectionException {
        super(
                CoreConfig.builder().build(),
                full ? AASFull.ENVIRONMENT : AASSimple.ENVIRONMENT,
                new PersistenceInMemory(),
                new MessageBusInternal(),
                List.of(endpoint),
                assetConnection != null ? List.of(assetConnection) : new ArrayList<>());
    }
}
