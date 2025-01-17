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
package de.fraunhofer.iosb.ilt.faaast.service.assetconnection.opcua.provider;

import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.opcua.AbstractOpcUaBasedTest;
import java.util.concurrent.ExecutionException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedSubscription;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.junit.Test;


public class OpcUaSubscriptionProviderTest extends AbstractOpcUaBasedTest {

    @Test
    public void testEquals() throws UaException, InterruptedException, ExecutionException {
        OpcUaClient client1 = OpcUaClient.create(serverUrl);
        client1.connect().get();
        OpcUaClient client2 = OpcUaClient.create(serverUrl);
        client2.connect().get();
        EqualsVerifier.simple().forClass(OpcUaSubscriptionProvider.class)
                .withPrefabValues(OpcUaClient.class, client1, client2)
                .withPrefabValues(ManagedSubscription.class, ManagedSubscription.create(client1), ManagedSubscription.create(client2))
                .verify();
        client1.disconnect();
        client2.disconnect();
    }

}
