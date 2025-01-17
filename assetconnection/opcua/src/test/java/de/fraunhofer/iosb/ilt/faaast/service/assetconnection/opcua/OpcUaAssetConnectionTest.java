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
package de.fraunhofer.iosb.ilt.faaast.service.assetconnection.opcua;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.github.valfirst.slf4jtest.LoggingEvent;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import de.fraunhofer.iosb.ilt.faaast.service.ServiceContext;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.AssetConnectionException;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.NewDataListener;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.opcua.provider.config.ArgumentMapping;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.opcua.provider.config.OpcUaOperationProviderConfig;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.opcua.provider.config.OpcUaSubscriptionProviderConfig;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.opcua.provider.config.OpcUaValueProviderConfig;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.opcua.util.OpcUaHelper;
import de.fraunhofer.iosb.ilt.faaast.service.config.CoreConfig;
import de.fraunhofer.iosb.ilt.faaast.service.exception.ConfigurationInitializationException;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.DataElementValue;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.PropertyValue;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.mapper.ElementValueMapper;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.primitive.Datatype;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.primitive.DateTimeValue;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.primitive.ValueFormatException;
import de.fraunhofer.iosb.ilt.faaast.service.typing.ElementValueTypeInfo;
import de.fraunhofer.iosb.ilt.faaast.service.typing.TypeInfo;
import io.adminshell.aas.v3.dataformat.core.util.AasUtils;
import io.adminshell.aas.v3.model.OperationVariable;
import io.adminshell.aas.v3.model.Property;
import io.adminshell.aas.v3.model.Reference;
import io.adminshell.aas.v3.model.impl.DefaultOperationVariable;
import io.adminshell.aas.v3.model.impl.DefaultProperty;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.server.identity.AnonymousIdentityValidator;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.junit.Assert;
import org.junit.Test;
import uk.org.lidalia.slf4jext.Level;


public class OpcUaAssetConnectionTest extends AbstractOpcUaBasedTest {

    @Test
    public void testSubscriptionProvider()
            throws AssetConnectionException, InterruptedException, ValueFormatException, ExecutionException, UaException, ConfigurationInitializationException, Exception {
        assertSubscribe("ns=2;s=HelloWorld/ScalarTypes/Double", PropertyValue.of(Datatype.DOUBLE, "0.1"));
    }


    @Test
    public void testReconnect()
            throws AssetConnectionException, InterruptedException, ValueFormatException, ExecutionException, UaException, ConfigurationInitializationException, Exception {
        int localTcpPort = findFreePort();
        int localHttpsPort = findFreePort();
        EmbeddedOpcUaServer localServer = new EmbeddedOpcUaServer(AnonymousIdentityValidator.INSTANCE, localTcpPort, localHttpsPort);
        localServer.startup().get();
        Thread.sleep(5000);
        String localServerUrl = "opc.tcp://localhost:" + localTcpPort + "/milo";
        final Predicate<LoggingEvent> logConnectionLost = x -> x.getLevel() == Level.WARN && x.getMessage().startsWith("OPC UA asset connection lost");
        String nodeId = "ns=2;s=HelloWorld/ScalarTypes/Double";
        PropertyValue expected = PropertyValue.of(Datatype.DOUBLE, "0.1");
        Reference reference = AasUtils.parseReference("(Property)[ID_SHORT]Temperature");
        long interval = 1000;
        ServiceContext serviceContext = mock(ServiceContext.class);
        TestLogger logger = TestLoggerFactory.getTestLogger(OpcUaAssetConnection.class);
        TypeInfo infoExample = ElementValueTypeInfo.builder()
                .type(PropertyValue.class)
                .datatype(expected.getValue().getDataType())
                .build();
        doReturn(infoExample).when(serviceContext).getTypeInfo(reference);
        OpcUaAssetConnection connection = new OpcUaAssetConnection();
        connection.init(
                CoreConfig.builder()
                        .build(),
                OpcUaAssetConnectionConfig.builder()
                        .host(localServerUrl)
                        .subscriptionProvider(reference, OpcUaSubscriptionProviderConfig.builder()
                                .nodeId(nodeId)
                                .interval(interval)
                                .build())
                        .valueProvider(reference,
                                OpcUaValueProviderConfig.builder()
                                        .nodeId(nodeId)
                                        .build())
                        .build(),
                serviceContext);
        long waitTime = 5 * interval;
        TimeUnit waitTimeUnit = isDebugging() ? TimeUnit.SECONDS : TimeUnit.MILLISECONDS;
        // first value should always be the current value
        OpcUaClient client = OpcUaHelper.createClient(localServerUrl, AnonymousProvider.INSTANCE);
        client.connect().get();
        DataValue originalValue = OpcUaHelper.readValue(client, nodeId);
        client.disconnect().get();
        final AtomicReference<DataElementValue> originalValueResponse = new AtomicReference<>();
        CountDownLatch conditionOriginalValue = new CountDownLatch(1);
        connection.getSubscriptionProviders().get(reference).addNewDataListener(new NewDataListener() {
            @Override
            public void newDataReceived(DataElementValue data) {
                originalValueResponse.set(data);
                conditionOriginalValue.countDown();
            }
        });
        Assert.assertTrue(String.format("test failed because there was no response within defined time (%d %s)", waitTime, waitTimeUnit),
                conditionOriginalValue.await(waitTime, waitTimeUnit));
        Assert.assertEquals(
                PropertyValue.of(expected.getValue().getDataType(), originalValue.getValue().getValue().toString()),
                originalValueResponse.get());
        // second value should be new value
        final AtomicReference<DataElementValue> newValueResponse = new AtomicReference<>();
        CountDownLatch conditionNewValue = new CountDownLatch(1);
        connection.getSubscriptionProviders().get(reference).addNewDataListener(new NewDataListener() {
            @Override
            public void newDataReceived(DataElementValue data) {
                newValueResponse.set(data);
                conditionNewValue.countDown();
            }
        });
        localServer.shutdown().get();
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> logger.getAllLoggingEvents().stream().anyMatch(logConnectionLost));
        localServer = new EmbeddedOpcUaServer(AnonymousIdentityValidator.INSTANCE, localTcpPort, localHttpsPort);
        localServer.startup().get();
        connection.getValueProviders().get(reference).setValue(expected);
        Assert.assertTrue(String.format("test failed because there was no response within defined time (%d %s)", waitTime, waitTimeUnit),
                conditionNewValue.await(waitTime, waitTimeUnit));
        Assert.assertEquals(expected, newValueResponse.get());
        localServer.shutdown().get();
    }


    private void assertSubscribe(String nodeId, PropertyValue expected)
            throws AssetConnectionException, InterruptedException, ExecutionException, UaException, ConfigurationInitializationException, Exception {
        Reference reference = AasUtils.parseReference("(Property)[ID_SHORT]Temperature");
        long interval = 1000;
        ServiceContext serviceContext = mock(ServiceContext.class);
        TypeInfo infoExample = ElementValueTypeInfo.builder()
                .type(PropertyValue.class)
                .datatype(expected.getValue().getDataType())
                .build();
        doReturn(infoExample).when(serviceContext).getTypeInfo(reference);
        OpcUaAssetConnection connection = new OpcUaAssetConnection();
        connection.init(
                CoreConfig.builder()
                        .build(),
                OpcUaAssetConnectionConfig.builder()
                        .host(serverUrl)
                        .subscriptionProvider(reference, OpcUaSubscriptionProviderConfig.builder()
                                .nodeId(nodeId)
                                .interval(interval)
                                .build())
                        .valueProvider(reference,
                                OpcUaValueProviderConfig.builder()
                                        .nodeId(nodeId)
                                        .build())
                        .build(),
                serviceContext);
        long waitTime = 5 * interval;
        TimeUnit waitTimeUnit = isDebugging() ? TimeUnit.SECONDS : TimeUnit.MILLISECONDS;
        // first value should always be the current value
        OpcUaClient client = OpcUaHelper.createClient(serverUrl, AnonymousProvider.INSTANCE);
        client.connect().get();
        DataValue originalValue = OpcUaHelper.readValue(client, nodeId);
        client.disconnect().get();
        final AtomicReference<DataElementValue> originalValueResponse = new AtomicReference<>();
        CountDownLatch conditionOriginalValue = new CountDownLatch(1);
        connection.getSubscriptionProviders().get(reference).addNewDataListener(new NewDataListener() {
            @Override
            public void newDataReceived(DataElementValue data) {
                originalValueResponse.set(data);
                conditionOriginalValue.countDown();
            }
        });
        Assert.assertTrue(String.format("test failed because there was no response within defined time (%d %s)", waitTime, waitTimeUnit),
                conditionOriginalValue.await(waitTime, waitTimeUnit));
        Assert.assertEquals(
                PropertyValue.of(expected.getValue().getDataType(), originalValue.getValue().getValue().toString()),
                originalValueResponse.get());
        // second value should be new value
        final AtomicReference<DataElementValue> newValueResponse = new AtomicReference<>();
        CountDownLatch conditionNewValue = new CountDownLatch(1);
        connection.getSubscriptionProviders().get(reference).addNewDataListener(new NewDataListener() {
            @Override
            public void newDataReceived(DataElementValue data) {
                newValueResponse.set(data);
                conditionNewValue.countDown();
            }
        });
        connection.getValueProviders().get(reference).setValue(expected);
        Assert.assertTrue(String.format("test failed because there was no response within defined time (%d %s)", waitTime, waitTimeUnit),
                conditionNewValue.await(waitTime, waitTimeUnit));
        Assert.assertEquals(expected, newValueResponse.get());
    }


    private static boolean isDebugging() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    }


    private void assertWriteReadValue(String nodeId, PropertyValue expected) throws AssetConnectionException, InterruptedException, ConfigurationInitializationException {
        Reference reference = AasUtils.parseReference("(Property)[ID_SHORT]Temperature");
        ServiceContext serviceContext = mock(ServiceContext.class);
        doReturn(ElementValueTypeInfo.builder()
                .type(expected.getClass())
                .datatype(expected.getValue().getDataType())
                .build())
                        .when(serviceContext)
                        .getTypeInfo(reference);
        OpcUaAssetConnection connection = new OpcUaAssetConnection(
                CoreConfig.builder()
                        .build(),
                OpcUaAssetConnectionConfig.builder()
                        .valueProvider(reference,
                                OpcUaValueProviderConfig.builder()
                                        .nodeId(nodeId)
                                        .build())
                        .host(serverUrl)
                        .build(),
                serviceContext);
        connection.getValueProviders().get(reference).setValue(expected);
        DataElementValue actual = connection.getValueProviders().get(reference).getValue();
        connection.close();
        Assert.assertEquals(expected, actual);
    }


    @Test
    public void testValueProvider() throws AssetConnectionException, InterruptedException, ValueFormatException, ConfigurationInitializationException {
        assertWriteReadValue("ns=2;s=HelloWorld/ScalarTypes/Double", PropertyValue.of(Datatype.DOUBLE, "3.3"));
        assertWriteReadValue("ns=2;s=HelloWorld/ScalarTypes/String", PropertyValue.of(Datatype.STRING, "hello world!"));
        assertWriteReadValue("ns=2;s=HelloWorld/ScalarTypes/Integer", PropertyValue.of(Datatype.INTEGER, "42"));
        assertWriteReadValue("ns=2;s=HelloWorld/ScalarTypes/Boolean", PropertyValue.of(Datatype.BOOLEAN, "true"));
        assertWriteReadValue("ns=2;s=HelloWorld/ScalarTypes/DateTime",
                PropertyValue.of(Datatype.DATE_TIME, ZonedDateTime.of(2022, 11, 28, 14, 12, 35, 0, ZoneId.of(DateTimeValue.DEFAULT_TIMEZONE)).toString()));
    }


    private void assertInvokeOperation(String nodeId,
                                       boolean sync,
                                       Map<String, PropertyValue> input,
                                       Map<String, PropertyValue> inoutput,
                                       Map<String, PropertyValue> expectedInoutput,
                                       Map<String, PropertyValue> expectedOutput)
            throws AssetConnectionException, InterruptedException, ConfigurationInitializationException {
        assertInvokeOperation(nodeId, sync, input, inoutput, expectedInoutput, expectedOutput, null, null);
    }


    private void assertInvokeOperation(String nodeId,
                                       boolean sync,
                                       Map<String, PropertyValue> input,
                                       Map<String, PropertyValue> inoutput,
                                       Map<String, PropertyValue> expectedInoutput,
                                       Map<String, PropertyValue> expectedOutput,
                                       List<ArgumentMapping> inputMapping,
                                       List<ArgumentMapping> outputMapping)
            throws AssetConnectionException, InterruptedException, ConfigurationInitializationException {
        Reference reference = AasUtils.parseReference("(Property)[ID_SHORT]Temperature");
        OpcUaAssetConnectionConfig config = OpcUaAssetConnectionConfig.builder()
                .host(serverUrl)
                .operationProvider(reference,
                        OpcUaOperationProviderConfig.builder()
                                .nodeId(nodeId)
                                .inputArgumentMappings(inputMapping)
                                .outputArgumentMappings(outputMapping)
                                .build())
                .build();
        OperationVariable[] inputVariables = input == null
                ? new OperationVariable[0]
                : input.entrySet().stream().map(x -> {
                    Property property = new DefaultProperty.Builder()
                            .idShort(x.getKey())
                            .build();
                    ElementValueMapper.setValue(property, x.getValue());
                    return new DefaultOperationVariable.Builder()
                            .value(property)
                            .build();
                }).toArray(OperationVariable[]::new);
        OperationVariable[] inoutputVariables = inoutput == null
                ? new OperationVariable[0]
                : inoutput.entrySet().stream().map(x -> {
                    Property property = new DefaultProperty.Builder()
                            .idShort(x.getKey())
                            .build();
                    ElementValueMapper.setValue(property, x.getValue());
                    return new DefaultOperationVariable.Builder()
                            .value(property)
                            .build();
                }).toArray(OperationVariable[]::new);
        OperationVariable[] expectedInOut = expectedInoutput == null
                ? new OperationVariable[0]
                : expectedInoutput.entrySet().stream().map(x -> {
                    Property property = new DefaultProperty.Builder()
                            .idShort(x.getKey())
                            .build();
                    ElementValueMapper.setValue(property, x.getValue());
                    return new DefaultOperationVariable.Builder()
                            .value(property)
                            .build();
                }).toArray(OperationVariable[]::new);
        OperationVariable[] expectedOut = expectedOutput == null
                ? new OperationVariable[0]
                : expectedOutput.entrySet().stream().map(x -> {
                    Property property = new DefaultProperty.Builder()
                            .idShort(x.getKey())
                            .build();
                    ElementValueMapper.setValue(property, x.getValue());
                    return new DefaultOperationVariable.Builder()
                            .value(property)
                            .build();
                }).toArray(OperationVariable[]::new);
        ServiceContext serviceContext = mock(ServiceContext.class);
        doReturn(expectedOut)
                .when(serviceContext)
                .getOperationOutputVariables(reference);
        OpcUaAssetConnection connection = new OpcUaAssetConnection(CoreConfig.builder().build(), config, serviceContext);
        OperationVariable[] actual;
        if (sync) {
            actual = connection.getOperationProviders().get(reference).invoke(inputVariables, inoutputVariables);
        }
        else {
            final AtomicReference<OperationVariable[]> operationResult = new AtomicReference<>();
            final AtomicReference<OperationVariable[]> operationInout = new AtomicReference<>();
            CountDownLatch condition = new CountDownLatch(1);
            connection.getOperationProviders().get(reference).invokeAsync(inputVariables, inoutputVariables, (res, inout) -> {
                operationResult.set(res);
                operationInout.set(inout);
                condition.countDown();
            });
            condition.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
            actual = operationResult.get();
            inoutputVariables = operationInout.get();
        }
        connection.close();
        Assert.assertArrayEquals(expectedOut, actual);
        Assert.assertArrayEquals(expectedInOut, inoutputVariables);
    }


    @Test
    public void testOperationProvider() throws AssetConnectionException, InterruptedException, ValueFormatException, ConfigurationInitializationException {
        String nodeIdSqrt = "ns=2;s=HelloWorld/sqrt(x)";
        assertInvokeOperation(nodeIdSqrt,
                true,
                Map.of("x", PropertyValue.of(Datatype.DOUBLE, "9.0")),
                null,
                null,
                Map.of("x_sqrt", PropertyValue.of(Datatype.DOUBLE, "3.0")));
        assertInvokeOperation(nodeIdSqrt,
                false,
                Map.of("x", PropertyValue.of(Datatype.DOUBLE, "9.0")),
                null,
                null,
                Map.of("x_sqrt", PropertyValue.of(Datatype.DOUBLE, "3.0")));
        assertInvokeOperation(nodeIdSqrt,
                true,
                null,
                Map.of("x", PropertyValue.of(Datatype.DOUBLE, "4.0"),
                        "x_sqrt", PropertyValue.of(Datatype.DOUBLE, "4.0")),
                Map.of("x", PropertyValue.of(Datatype.DOUBLE, "4.0"),
                        "x_sqrt", PropertyValue.of(Datatype.DOUBLE, "2.0")),
                Map.of("x_sqrt", PropertyValue.of(Datatype.DOUBLE, "2.0")));
        assertInvokeOperation(nodeIdSqrt,
                false,
                null,
                Map.of("x", PropertyValue.of(Datatype.DOUBLE, "4.0"),
                        "x_sqrt", PropertyValue.of(Datatype.DOUBLE, "4.0")),
                Map.of("x", PropertyValue.of(Datatype.DOUBLE, "4.0"),
                        "x_sqrt", PropertyValue.of(Datatype.DOUBLE, "2.0")),
                Map.of("x_sqrt", PropertyValue.of(Datatype.DOUBLE, "2.0")));
    }


    @Test
    public void testOperationProviderMapping() throws AssetConnectionException, InterruptedException, ValueFormatException, ConfigurationInitializationException {
        String nodeIdSqrt = "ns=2;s=HelloWorld/sqrt(x)";
        assertInvokeOperation(nodeIdSqrt,
                true,
                Map.of("x_aas", PropertyValue.of(Datatype.DOUBLE, "4.0")),
                null,
                null,
                Map.of("x_sqrt", PropertyValue.of(Datatype.DOUBLE, "2.0")),
                List.of(new ArgumentMapping("x_aas", "x")),
                null);
        assertInvokeOperation(nodeIdSqrt,
                false,
                Map.of("x", PropertyValue.of(Datatype.DOUBLE, "4.0")),
                null,
                null,
                Map.of("x_sqrt_aas", PropertyValue.of(Datatype.DOUBLE, "2.0")),
                null,
                List.of(new ArgumentMapping("x_sqrt_aas", "x_sqrt")));
        assertInvokeOperation(nodeIdSqrt,
                true,
                null,
                Map.of("x_aas", PropertyValue.of(Datatype.DOUBLE, "4.0"),
                        "x_sqrt_aas", PropertyValue.of(Datatype.DOUBLE, "4.0")),
                Map.of("x_aas", PropertyValue.of(Datatype.DOUBLE, "4.0"),
                        "x_sqrt_aas", PropertyValue.of(Datatype.DOUBLE, "2.0")),
                Map.of("x_sqrt_aas", PropertyValue.of(Datatype.DOUBLE, "2.0")),
                List.of(new ArgumentMapping("x_aas", "x")),
                List.of(new ArgumentMapping("x_sqrt_aas", "x_sqrt")));
        assertInvokeOperation(nodeIdSqrt,
                false,
                null,
                Map.of("x_aas", PropertyValue.of(Datatype.DOUBLE, "4.0"),
                        "x_sqrt_aas", PropertyValue.of(Datatype.DOUBLE, "4.0")),
                Map.of("x_aas", PropertyValue.of(Datatype.DOUBLE, "4.0"),
                        "x_sqrt_aas", PropertyValue.of(Datatype.DOUBLE, "2.0")),
                Map.of("x_sqrt_aas", PropertyValue.of(Datatype.DOUBLE, "2.0")),
                List.of(new ArgumentMapping("x_aas", "x")),
                List.of(new ArgumentMapping("x_sqrt_aas", "x_sqrt")));
    }
}
