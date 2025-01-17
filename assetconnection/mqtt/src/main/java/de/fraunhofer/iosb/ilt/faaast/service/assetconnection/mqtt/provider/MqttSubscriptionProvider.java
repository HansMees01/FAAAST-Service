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
package de.fraunhofer.iosb.ilt.faaast.service.assetconnection.mqtt.provider;

import de.fraunhofer.iosb.ilt.faaast.service.ServiceContext;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.AssetConnectionException;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.common.provider.MultiFormatSubscriptionProvider;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.mqtt.provider.config.MqttSubscriptionProviderConfig;
import de.fraunhofer.iosb.ilt.faaast.service.typing.TypeInfo;
import de.fraunhofer.iosb.ilt.faaast.service.util.Ensure;
import io.adminshell.aas.v3.dataformat.core.util.AasUtils;
import io.adminshell.aas.v3.model.Reference;
import java.util.Objects;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;


/**
 * SubscriptionProvider for MQTT protocol.
 */
public class MqttSubscriptionProvider extends MultiFormatSubscriptionProvider<MqttSubscriptionProviderConfig> {

    private final ServiceContext serviceContext;
    private final Reference reference;
    private final MqttClient client;

    public MqttSubscriptionProvider(ServiceContext serviceContext, Reference reference, MqttClient client, MqttSubscriptionProviderConfig config) {
        super(config);
        Ensure.requireNonNull(serviceContext, "serviceContext must be non-null");
        Ensure.requireNonNull(reference, "reference must be non-null");
        Ensure.requireNonNull(client, "client must be non-null");
        this.serviceContext = serviceContext;
        this.reference = reference;
        this.client = client;
    }


    @Override
    protected TypeInfo getTypeInfo() {
        return serviceContext.getTypeInfo(reference);
    }


    @Override
    public void subscribe() throws AssetConnectionException {
        try {
            client.subscribe(config.getTopic(), (topic, message) -> fireNewDataReceived(message.getPayload()));
        }
        catch (MqttException e) {
            throw new AssetConnectionException(
                    String.format("error subscribing to MQTT asset connection (reference: %s, topic: %s)",
                            AasUtils.asString(reference),
                            config.getTopic()),
                    e);
        }
    }


    @Override
    protected void unsubscribe() throws AssetConnectionException {
        if (client != null && client.isConnected()) {
            try {
                client.unsubscribe(config.getTopic());
            }
            catch (MqttException e) {
                throw new AssetConnectionException(
                        String.format("error unsubscribing from MQTT asset connection (reference: %s, topic: %s)",
                                AasUtils.asString(reference),
                                config.getTopic()),
                        e);
            }
        }
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), serviceContext, client, reference);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MqttSubscriptionProvider)) {
            return false;
        }
        final MqttSubscriptionProvider that = (MqttSubscriptionProvider) obj;
        return super.equals(obj)
                && Objects.equals(serviceContext, that.serviceContext)
                && Objects.equals(client, that.client)
                && Objects.equals(reference, that.reference);
    }
}
