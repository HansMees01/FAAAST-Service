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

import com.prosysopc.ua.ApplicationIdentity;
import com.prosysopc.ua.SecureIdentityException;
import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.client.AddressSpaceException;
import com.prosysopc.ua.client.UaClient;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.nodes.UaReference;
import com.prosysopc.ua.nodes.UaType;
import com.prosysopc.ua.nodes.UaVariable;
import com.prosysopc.ua.stack.builtintypes.DataValue;
import com.prosysopc.ua.stack.builtintypes.ExpandedNodeId;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.builtintypes.StatusCode;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.common.ServiceResultException;
import com.prosysopc.ua.stack.core.ApplicationDescription;
import com.prosysopc.ua.stack.core.ApplicationType;
import com.prosysopc.ua.stack.core.BrowseDirection;
import com.prosysopc.ua.stack.core.BrowsePathResult;
import com.prosysopc.ua.stack.core.BrowsePathTarget;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.stack.core.ReferenceDescription;
import com.prosysopc.ua.stack.core.RelativePath;
import com.prosysopc.ua.stack.core.RelativePathElement;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.opcua.ValueConverter;
import io.adminshell.aas.v3.model.Qualifier;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import opc.i4aas.AASAssetKindDataType;
import opc.i4aas.AASIdentifierTypeDataType;
import opc.i4aas.AASKeyDataType;
import opc.i4aas.AASKeyElementsDataType;
import opc.i4aas.AASKeyTypeDataType;
import opc.i4aas.AASModelingKindDataType;
import opc.i4aas.AASQualifierType;
import opc.i4aas.AASValueTypeDataType;
import org.junit.Assert;


/**
 * Test utilities
 */
public class TestUtils {

    public static void initialize(UaClient client) throws SecureIdentityException, IOException, UnknownHostException {
        ApplicationDescription appDescription = new ApplicationDescription();
        appDescription.setApplicationName(new LocalizedText("AAS UnitTest Client", Locale.ENGLISH));
        // 'localhost' (all lower case) in the URI is converted to the actual
        // host name of the computer in which the application is run
        appDescription.setApplicationUri("urn:localhost:UA:AASUnitTestClient");
        appDescription.setProductUri("urn:iosb.fraunhofer.de:UA:AASUnitTestClient");
        appDescription.setApplicationType(ApplicationType.Client);

        final ApplicationIdentity identity = new ApplicationIdentity();
        identity.setApplicationDescription(appDescription);
        client.setApplicationIdentity(identity);
    }


    public static void checkBrowseName(UaClient client, NodeId nodeId, String desiredName) throws ServiceException, AddressSpaceException {
        UaNode node = client.getAddressSpace().getNode(nodeId);
        Assert.assertNotNull("Node is null: " + desiredName, node);
        checkBrowseName(node, desiredName);
    }


    public static void checkBrowseName(UaNode node, String desiredName) {
        QualifiedName qname = node.getBrowseName();
        Assert.assertNotNull(qname);
        Assert.assertEquals("BrowseName not equal", desiredName, qname.getName());
    }


    public static void checkDisplayName(UaClient client, NodeId nodeId, String desiredName) throws ServiceException, AddressSpaceException {
        UaNode node = client.getAddressSpace().getNode(nodeId);
        Assert.assertNotNull("Node is null: " + desiredName, node);
        LocalizedText lt = node.getDisplayName();
        Assert.assertEquals(desiredName, lt.getText());
    }


    public static void checkIdentificationNode(UaClient client, NodeId baseNode, int aasns, AASIdentifierTypeDataType idType, String id)
            throws ServiceException, StatusException, ServiceResultException, AddressSpaceException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, TestConstants.IDENTIFICATION_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(baseNode, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkIdentificationNode Browse Result Null", bpres);
        Assert.assertEquals("checkIdentificationNode Browse Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkIdentificationNode Browse Target Node Null", targets);
        Assert.assertTrue("checkIdentificationNode Browse targets empty", targets.length > 0);

        checkIdentification(client, client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId()), aasns, idType, id);
    }


    public static void checkModelingKindNode(UaClient client, NodeId baseNode, int aasns, AASModelingKindDataType modelingKind)
            throws ServiceException, AddressSpaceException, StatusException, ServiceResultException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.MODELING_KIND_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(baseNode, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkModelingKindNode Browse Result Null", bpres);
        Assert.assertEquals("checkModelingKindNode Browse Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkModelingKindNode Browse Target Node Null", targets);
        Assert.assertTrue("checkModelingKindNode Browse targets empty", targets.length > 0);

        checkModelingKind(client, client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId()), modelingKind);
    }


    public static void checkCategoryNode(UaClient client, NodeId node, int aasns, String category)
            throws ServiceException, StatusException, AddressSpaceException, ServiceResultException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.CATEGORY_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(node, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("Category Result Null", bpres);
        Assert.assertEquals("Category Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("Browse Category Null", targets);
        Assert.assertTrue("Category targets empty", targets.length > 0);
        checkType(client, targets[0].getTargetId(), Identifiers.PropertyType);

        DataValue value = client.readValue(targets[0].getTargetId());
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        String str = "";
        if (!value.getValue().isEmpty()) {
            str = value.getValue().toString();
        }
        Assert.assertEquals(category.isEmpty(), str.isEmpty());
        if (!category.isEmpty()) {
            Assert.assertEquals(category, value.getValue().toString());
        }
    }


    public static void checkDataSpecificationNode(UaClient client, NodeId node, int aasns) throws ServiceException, ServiceResultException, AddressSpaceException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, TestConstants.DATA_SPECIFICATION_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(node, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkDataSpecificationNode Browse Result Null", bpres);
        Assert.assertEquals("checkDataSpecificationNode Browse Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkDataSpecificationNode Node Targets Null", targets);
        Assert.assertTrue("checkDataSpecificationNode Node targets empty", targets.length > 0);

        // Currently we only check that the NodeId is not null and we have the correct type
        NodeId dataSpecNode = client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId());
        Assert.assertFalse("checkDataSpecificationNode Node not found", NodeId.isNull(dataSpecNode));

        checkType(client, dataSpecNode, new NodeId(aasns, TestConstants.AAS_REFERENCE_LIST_ID));
    }


    public static void checkBillOfMaterialNode(UaClient client, NodeId node, int aasns) throws ServiceException, AddressSpaceException, ServiceResultException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, TestConstants.BILL_OF_MATERIAL_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(node, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkBillOfMaterialNode Browse Result Null", bpres);
        Assert.assertEquals("checkBillOfMaterialNode Browse Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkBillOfMaterialNode Node Targets Null", targets);
        Assert.assertTrue("checkBillOfMaterialNode Node targets empty", targets.length > 0);

        // Currently we only check that the NodeId is not null and we have the correct type
        NodeId billNode = client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId());
        Assert.assertFalse("checkBillOfMaterialNode Node not found", NodeId.isNull(billNode));

        checkType(client, billNode, new NodeId(aasns, TestConstants.AAS_REFERENCE_LIST_ID));
    }


    public static void checkQualifierNode(UaClient client, NodeId node, int aasns, List<Qualifier> qualifierList)
            throws ServiceException, ServiceResultException, AddressSpaceException, StatusException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, TestConstants.QUALIFIER_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(node, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkQualifierNode Browse Result Null", bpres);
        Assert.assertEquals("checkQualifierNode Browse Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkQualifierNode Node Targets Null", targets);
        Assert.assertTrue("checkQualifierNode Node targets empty", targets.length > 0);

        // Currently we only check that the NodeId is not null and we have the correct type
        NodeId qualNode = client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId());
        Assert.assertFalse("checkQualifierNode Node not found", NodeId.isNull(qualNode));

        checkType(client, qualNode, new NodeId(aasns, TestConstants.AAS_QUALIFIER_LIST_ID));

        List<AASQualifierType> nodeList = new ArrayList<>();
        List<ReferenceDescription> refs = client.getAddressSpace().browse(qualNode);
        for (ReferenceDescription ref: refs) {
            NodeId nid = client.getAddressSpace().getNamespaceTable().toNodeId(ref.getNodeId());
            checkType(client, nid, new NodeId(aasns, TestConstants.AAS_QUALIFIER_TYPE_ID));
            UaNode qnode = client.getAddressSpace().getNode(nid);
            if (qnode instanceof AASQualifierType) {
                nodeList.add((AASQualifierType) qnode);
            }
        }

        checkQualifierList(qualifierList, nodeList);
    }


    public static void checkAdministrationNode(UaClient client, NodeId baseNode, int aasns, String version, String revision)
            throws ServiceException, StatusException, ServiceResultException, AddressSpaceException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, TestConstants.ADMINISTRATION_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(baseNode, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkAdministrationNode Browse(1) Result Null", bpres);
        Assert.assertEquals("checkAdministrationNode Browse(1) Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkAdministrationNode Browse Administration Node Null", targets);
        Assert.assertTrue("checkAdministrationNode Browse Administration targets empty", targets.length > 0);
        NodeId administrationNode = client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId());

        checkType(client, administrationNode, new NodeId(aasns, TestConstants.AAS_ADMIN_INFO_TYPE_ID));

        Assert.assertNotNull(administrationNode);
        Assert.assertNotEquals(NodeId.NULL, administrationNode);

        relPath.clear();

        int size = 0;
        if (version != null) {
            browsePath.clear();
            browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.VERSION_NAME)));
            relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));
            size++;
        }

        if (revision != null) {
            browsePath.clear();
            browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.REVISION_NAME)));
            relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));
            size++;
        }

        if (size > 0) {
            bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(administrationNode, relPath.toArray(RelativePath[]::new));
            Assert.assertNotNull("checkAdministrationNode Browse(2) Result Null", bpres);
            Assert.assertEquals("checkAdministrationNode Browse(2) Result: size doesn't match", size, bpres.length);

            int index = 0;
            if (version != null) {
                targets = bpres[index].getTargets();
                Assert.assertNotNull("checkAdministrationNode Browse Version Node Null", targets);
                Assert.assertTrue("checkAdministrationNode Browse Version targets empty", targets.length > 0);

                DataValue value = client.readValue(targets[0].getTargetId());
                Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
                Assert.assertEquals(version, value.getValue().toString());
                index++;
            }

            if (revision != null) {
                targets = bpres[index].getTargets();
                Assert.assertNotNull("checkAdministrationNode Browse Revision Node Null", targets);
                Assert.assertTrue("checkAdministrationNode Browse Revision targets empty", targets.length > 0);

                DataValue value = client.readValue(targets[0].getTargetId());
                Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
                Assert.assertEquals(revision, value.getValue().toString());
                index++;
            }
        }
    }


    public static void checkAssetInformationNode(UaClient client, NodeId baseNode, int aasns)
            throws ServiceException, ServiceResultException, AddressSpaceException, StatusException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, TestConstants.ASSET_INFORMATION_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(baseNode, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkAssetInformationNode Browse(1) Result Null", bpres);
        Assert.assertEquals("checkAssetInformationNode Browse(1) Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkAssetInformationNode Browse AssetInfo Node Null", targets);
        Assert.assertTrue("checkAssetInformationNode Browse AssetInfo targets empty", targets.length > 0);
        NodeId assetInfoNode = client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId());

        Assert.assertNotNull(assetInfoNode);
        Assert.assertNotEquals(NodeId.NULL, assetInfoNode);

        checkType(client, assetInfoNode, new NodeId(aasns, TestConstants.AAS_ASSET_INFO_TYPE_ID));
        checkAssetKindNode(client, assetInfoNode, aasns, AASAssetKindDataType.Instance);
        checkBillOfMaterialNode(client, assetInfoNode, aasns);
        checkAasPropertyFile(client, assetInfoNode, aasns, TestConstants.DEFAULT_THUMB_NAME, AASModelingKindDataType.Instance, "", "image/png",
                "https://github.com/admin-shell/io/blob/master/verwaltungsschale-detail-part1.png", 0);

        List<AASKeyDataType> keyList = new ArrayList<>();
        keyList.add(new AASKeyDataType(AASKeyElementsDataType.Asset, "http://customer.com/assets/KHBVZJSQKIY", AASKeyTypeDataType.IRI));
        checkAasReferenceNode(client, assetInfoNode, aasns, TestConstants.GLOBAL_ASSET_ID_NAME, keyList);

        Map<String, String> map = new HashMap<>();
        map.put("DeviceID", "QjYgPggjwkiHk4RrQiYSLg==");
        map.put("EquipmentID", "538fd1b3-f99f-4a52-9c75-72e9fa921270");
        checkIdentifierKeyValuePairListNode(client, assetInfoNode, aasns, TestConstants.SPECIFIC_ASSET_ID_NAME, map);
    }


    public static void checkVariableBool(UaClient client, NodeId node, int aasns, String name, boolean propValue) throws ServiceException, StatusException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, name)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(node, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkVariableBool Browse Result Null", bpres);
        Assert.assertEquals("checkVariableBool Browse Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkVariableBool Node Targets Null", targets);
        Assert.assertTrue("checkVariableBool Node targets empty", targets.length > 0);

        DataValue value = client.readValue(targets[0].getTargetId());
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        Assert.assertEquals(propValue, value.getValue().booleanValue());
    }


    public static void checkAasPropertyString(UaClient client, NodeId node, int aasns, String name, AASModelingKindDataType kind, String category, AASValueTypeDataType valueType,
                                              String propValue, List<Qualifier> qualifierList)
            throws ServiceException, AddressSpaceException, StatusException, ServiceResultException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, name)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(node, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkAasPropertyString Browse Property Result Null", bpres);
        Assert.assertEquals("checkAasPropertyString Browse Property Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkAasPropertyString Property Null", targets);
        Assert.assertTrue("checkAasPropertyString Property empty", targets.length > 0);
        NodeId propertyNode = client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId());

        checkDisplayName(client, propertyNode, name);
        checkCategoryNode(client, propertyNode, aasns, category);
        checkModelingKindNode(client, propertyNode, aasns, kind);
        checkDataSpecificationNode(client, propertyNode, aasns);
        checkQualifierNode(client, propertyNode, aasns, qualifierList);

        relPath.clear();
        browsePath.clear();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.PROPERTY_VALUE_TYPE_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));
        browsePath.clear();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.PROPERTY_VALUE_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(propertyNode, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkAasPropertyString Browse Value & Type Result Null", bpres);
        Assert.assertEquals("checkAasPropertyString Browse Value & Type Result: size doesn't match", 2, bpres.length);

        targets = bpres[0].getTargets();
        Assert.assertNotNull("checkAasPropertyString ValueType Null", targets);
        Assert.assertTrue("checkAasPropertyString ValueType empty", targets.length > 0);
        DataValue value = client.readValue(targets[0].getTargetId());
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        Assert.assertEquals(valueType.ordinal(), value.getValue().intValue());

        targets = bpres[1].getTargets();
        Assert.assertNotNull("checkAasPropertyString Value Null", targets);
        Assert.assertTrue("checkAasPropertyString value empty", targets.length > 0);
        value = client.readValue(targets[0].getTargetId());
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        //        Variant var;
        //        if (valueType == AASValueTypeDataType.LocalizedText) {
        //            var = new Variant(LocalizedText.english(propValue));
        //        }
        //        else {
        //            var = new Variant(propValue);
        //        }

        Variant var = new Variant(propValue);
        Assert.assertEquals(var, value.getValue());
    }


    public static void checkAasPropertyObject(UaClient client, NodeId node, int aasns, String name, AASModelingKindDataType kind, String category, AASValueTypeDataType valueType,
                                              Object propValue, List<Qualifier> qualifierList)
            throws ServiceException, AddressSpaceException, StatusException, ServiceResultException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, name)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(node, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkAasPropertyObject Browse Property Result Null", bpres);
        Assert.assertEquals("checkAasPropertyObject Browse Property Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkAasPropertyObject Property Null", targets);
        Assert.assertTrue("checkAasPropertyObject Property empty", targets.length > 0);
        NodeId propertyNode = client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId());

        checkType(client, propertyNode, new NodeId(aasns, TestConstants.AAS_PROPERTY_TYPE_ID));
        checkDisplayName(client, propertyNode, name);
        checkCategoryNode(client, propertyNode, aasns, category);
        checkModelingKindNode(client, propertyNode, aasns, kind);
        checkDataSpecificationNode(client, propertyNode, aasns);
        checkQualifierNode(client, propertyNode, aasns, qualifierList);

        relPath.clear();
        browsePath.clear();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.PROPERTY_VALUE_TYPE_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));
        browsePath.clear();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.PROPERTY_VALUE_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(propertyNode, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkAasPropertyObject Browse Value & Type Result Null", bpres);
        Assert.assertEquals("checkAasPropertyObject Browse Value & Type Result: size doesn't match", 2, bpres.length);

        targets = bpres[0].getTargets();
        Assert.assertNotNull("checkAasPropertyObject ValueType Null", targets);
        Assert.assertTrue("checkAasPropertyObject ValueType empty", targets.length > 0);
        DataValue value = client.readValue(targets[0].getTargetId());
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        Assert.assertEquals(valueType.ordinal(), value.getValue().intValue());

        targets = bpres[1].getTargets();
        Assert.assertNotNull("checkAasPropertyObject Value Null", targets);
        Assert.assertTrue("checkAasPropertyObject value empty", targets.length > 0);
        value = client.readValue(targets[0].getTargetId());
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());

        Variant var = new Variant(propValue);
        Assert.assertEquals(var, value.getValue());
    }


    public static void checkAasPropertyFile(UaClient client, NodeId node, int aasns, String name, AASModelingKindDataType kind, String category, String mimeType, String propValue,
                                            int fileSize)
            throws ServiceException, ServiceResultException, AddressSpaceException, StatusException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, name)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(node, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkAasPropertyFile Browse Property Result Null", bpres);
        Assert.assertEquals("checkAasPropertyFile Browse Property Result: size doesn't match", 1, bpres.length);
        Assert.assertTrue("checkAasPropertyFile Browse Result Good", bpres[0].getStatusCode().isGood());

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkAasPropertyFile Property Null", targets);
        Assert.assertTrue("checkAasPropertyFile Property empty", targets.length > 0);
        NodeId propertyNode = client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId());

        checkDisplayName(client, propertyNode, name);
        checkCategoryNode(client, propertyNode, aasns, category);
        checkModelingKindNode(client, propertyNode, aasns, kind);
        checkDataSpecificationNode(client, propertyNode, aasns);
        checkQualifierNode(client, propertyNode, aasns, new ArrayList<>());

        relPath.clear();
        browsePath.clear();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.PROPERTY_MIME_TYPE_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));
        browsePath.clear();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.PROPERTY_VALUE_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));
        browsePath.clear();
        browsePath.add(new RelativePathElement(Identifiers.HasAddIn, false, true, new QualifiedName(aasns, TestConstants.PROPERTY_FILE_NAME)));
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(0, TestConstants.PROPERTY_SIZE_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(propertyNode, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkAasPropertyFile Browse Value & Type Result Null", bpres);
        Assert.assertEquals("checkAasPropertyFile Browse Value & Type Result: size doesn't match", 3, bpres.length);

        // MimeType
        targets = bpres[0].getTargets();
        Assert.assertNotNull("checkAasPropertyFile MimeType Null", targets);
        Assert.assertTrue("checkAasPropertyFile MimeType empty", targets.length > 0);
        DataValue value = client.readValue(targets[0].getTargetId());
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        Assert.assertEquals(mimeType, value.getValue().toString());

        // Value
        targets = bpres[1].getTargets();
        Assert.assertNotNull("checkAasPropertyFile Value Null", targets);
        Assert.assertTrue("checkAasPropertyFile value empty", targets.length > 0);
        value = client.readValue(targets[0].getTargetId());
        Assert.assertEquals(propValue, value.getValue().toString());

        if (fileSize > 0) {
            // File Size
            if (bpres[2].getStatusCode().isBad()) {
                System.out.println("checkAasPropertyFile File Status " + bpres[2].getStatusCode());
            }
            targets = bpres[2].getTargets();
            Assert.assertNotNull("checkAasPropertyFile File Null", targets);
            Assert.assertTrue("checkAasPropertyFile File empty", targets.length > 0);
            value = client.readValue(targets[0].getTargetId());
            Assert.assertEquals(fileSize, value.getValue().intValue());
        }
    }


    public static void checkType(UaClient client, ExpandedNodeId node, NodeId typeNode) throws ServiceException, AddressSpaceException, ServiceResultException {
        checkType(client, client.getAddressSpace().getNamespaceTable().toNodeId(node), typeNode);
    }


    public static void checkType(UaClient client, NodeId node, NodeId typeNode) throws ServiceException, AddressSpaceException, ServiceResultException {
        UaNode uanode = client.getAddressSpace().getNode(node);
        Assert.assertNotNull("checkType UaNode Null", uanode);
        UaReference ref = uanode.getReference(Identifiers.HasTypeDefinition, false);
        Assert.assertNotNull("checkType Reference Null", ref);

        NodeId refId = client.getAddressSpace().getNamespaceTable().toNodeId(ref.getTargetId());
        Assert.assertEquals("type not equal", typeNode, refId);
    }


    public static void checkSubmodelRef(UaClient client, NodeId baseNode, int aasns, String name, NodeId submodelNode)
            throws ServiceException, ServiceResultException, AddressSpaceException, StatusException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, name)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(baseNode, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkSubmodelRef Browse Result Null", bpres);
        Assert.assertEquals("checkSubmodelRef Browse Result: size doesn't match", 1, bpres.length);
        Assert.assertTrue("checkSubmodelRef Browse Result Good", bpres[0].getStatusCode().isGood());

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkSubmodelRef Target Null", targets);
        Assert.assertTrue("checkSubmodelRef Target empty", targets.length > 0);
        NodeId refNode = client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId());
        Assert.assertNotNull("checkSubmodelRef RefNode Null", refNode);
        checkType(client, refNode, new NodeId(aasns, TestConstants.AAS_REFERENCE_TYPE_ID));

        // check AAS Reference
        List<AASKeyDataType> refKeys = new ArrayList<>();
        refKeys.add(new AASKeyDataType(AASKeyElementsDataType.Submodel, name, AASKeyTypeDataType.IRI));
        checkAasReference(client, refNode, aasns, refKeys);

        // check Reference to Submodel
        List<ReferenceDescription> refs = client.getAddressSpace().browse(refNode, BrowseDirection.Forward, Identifiers.HasAddIn);
        Assert.assertEquals(1, refs.size());
        NodeId smNode = client.getAddressSpace().getNamespaceTable().toNodeId(refs.get(0).getNodeId());
        Assert.assertEquals(smNode, submodelNode);
    }


    public static void writeNewValueIntern(UaClient client, NodeId writeNode, Object oldValue, Object newValue) throws ServiceException, StatusException, InterruptedException {
        DataValue value = client.readValue(writeNode);
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        if (oldValue == null) {
            Assert.assertTrue("intial null value not equal", value.getValue().isEmpty());
        }
        else {
            Assert.assertEquals("intial value not equal", oldValue, value.getValue().getValue());
        }

        client.writeValue(writeNode, newValue);

        // read new value
        value = client.readValue(writeNode);
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        Assert.assertEquals("new value not equal", newValue, value.getValue().getValue());
    }


    public static void writeNewValueArray(UaClient client, NodeId writeNode, LocalizedText[] oldValue, LocalizedText[] newValue)
            throws ServiceException, StatusException, InterruptedException {
        DataValue value = client.readValue(writeNode);
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        Assert.assertArrayEquals("intial value not equal", oldValue, (LocalizedText[]) value.getValue().getValue());

        client.writeValue(writeNode, newValue);

        // read new value
        value = client.readValue(writeNode);
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        Assert.assertArrayEquals("new value not equal", newValue, (LocalizedText[]) value.getValue().getValue());
    }


    public static void writeNewValueArray(UaClient client, NodeId writeNode, AASKeyDataType[] oldValue, AASKeyDataType[] newValue)
            throws ServiceException, StatusException, InterruptedException {
        DataValue value = client.readValue(writeNode);
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        Assert.assertArrayEquals("intial value not equal", oldValue, (AASKeyDataType[]) value.getValue().getValue());

        client.writeValue(writeNode, newValue);

        // read new value
        value = client.readValue(writeNode);
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        Assert.assertArrayEquals("new value not equal", newValue, (AASKeyDataType[]) value.getValue().getValue());
    }


    private static void checkIdentification(UaClient client, NodeId identificationNode, int aasns, AASIdentifierTypeDataType idType, String id)
            throws ServiceException, StatusException, AddressSpaceException, ServiceResultException {
        checkType(client, identificationNode, new NodeId(aasns, TestConstants.AAS_IDENTIFIER_TYPE_ID));

        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, "IdType")));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));
        browsePath.clear();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, "Id")));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(identificationNode, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkIdentification Browse Result Null", bpres);
        Assert.assertEquals("checkIdentification Browse Result: size doesn't match", 2, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkIdentification IdType Null", targets);
        Assert.assertTrue("checkIdentification IdType empty", targets.length > 0);
        DataValue value = client.readValue(targets[0].getTargetId());
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        Assert.assertEquals(idType.ordinal(), value.getValue().intValue());

        targets = bpres[1].getTargets();
        Assert.assertNotNull("checkIdentification Id Null", targets);
        Assert.assertTrue("checkIdentification Id empty", targets.length > 0);
        value = client.readValue(targets[0].getTargetId());
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        Assert.assertEquals(id, value.getValue().toString());
    }


    private static void checkModelingKind(UaClient client, NodeId kindNode, AASModelingKindDataType modelingKind)
            throws ServiceException, AddressSpaceException, StatusException, ServiceResultException {
        checkDisplayName(client, kindNode, TestConstants.MODELING_KIND_NAME);
        checkType(client, kindNode, Identifiers.PropertyType);

        DataValue value = client.readValue(kindNode);
        Assert.assertEquals(modelingKind.ordinal(), value.getValue().intValue());
    }


    private static void checkAssetKindNode(UaClient client, NodeId baseNode, int aasns, AASAssetKindDataType assetKind)
            throws ServiceException, AddressSpaceException, StatusException, ServiceResultException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.ASSET_KIND_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(baseNode, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkAssetKindNode Browse Result Null", bpres);
        Assert.assertEquals("checkAssetKindNode Browse Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkAssetKindNode Browse Target Node Null", targets);
        Assert.assertTrue("checkAssetKindNode Browse targets empty", targets.length > 0);

        checkAssetKind(client, client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId()), assetKind);
    }


    private static void checkAssetKind(UaClient client, NodeId kindNode, AASAssetKindDataType assetKind)
            throws ServiceException, AddressSpaceException, StatusException, ServiceResultException {
        checkDisplayName(client, kindNode, TestConstants.ASSET_KIND_NAME);
        checkType(client, kindNode, Identifiers.PropertyType);

        DataValue value = client.readValue(kindNode);
        Assert.assertEquals(assetKind.ordinal(), value.getValue().intValue());
    }


    private static void checkAasReferenceNode(UaClient client, NodeId baseNode, int aasns, String name, List<AASKeyDataType> refKeys)
            throws ServiceException, ServiceResultException, AddressSpaceException, StatusException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, name)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(baseNode, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkAasReferenceNode Browse Result Null", bpres);
        Assert.assertEquals("checkAasReferenceNode Browse Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkAasReferenceNode Browse Target Node Null", targets);
        Assert.assertTrue("checkAasReferenceNode Browse targets empty", targets.length > 0);
        NodeId refNode = client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId());
        Assert.assertNotNull("checkAasReferenceNode Ref Node Null", refNode);
        Assert.assertNotEquals("checkAasReferenceNode Ref Node Null", NodeId.NULL, refNode);
        checkAasReference(client, refNode, aasns, refKeys);
    }


    private static void checkAasReference(UaClient client, NodeId node, int aasns, List<AASKeyDataType> refKeys)
            throws ServiceException, AddressSpaceException, ServiceResultException, StatusException {
        checkType(client, node, new NodeId(aasns, TestConstants.AAS_REFERENCE_TYPE_ID));

        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, "Keys")));
        BrowsePathTarget[] targetsProp = client.getAddressSpace().translateBrowsePathToNodeId(node, browsePath.toArray(RelativePathElement[]::new));
        Assert.assertNotNull("Property Keys Null", targetsProp);
        Assert.assertTrue("Property Keys empty", targetsProp.length > 0);

        checkType(client, targetsProp[0].getTargetId(), Identifiers.PropertyType);
        UaVariable variable = (UaVariable) client.getAddressSpace().getNode(targetsProp[0].getTargetId());
        UaType dataType = variable.getDataType();
        Assert.assertNotNull("DataType null", dataType);
        Assert.assertEquals("DataType not equal", new NodeId(aasns, TestConstants.AAS_KEY_DATA_TYPE_ID), dataType.getNodeId());

        DataValue value = client.readValue(targetsProp[0].getTargetId());
        Assert.assertEquals(StatusCode.GOOD, value.getStatusCode());
        Assert.assertNotNull("Value null", value.getValue());
        Variant var = value.getValue();
        Object o = var.getValue();
        Assert.assertTrue("Keys no array", var.isArray());

        AASKeyDataType[] arr = (AASKeyDataType[]) o;
        Assert.assertEquals(refKeys.size(), arr.length);
        Assert.assertArrayEquals(refKeys.toArray(), arr);
    }


    private static void checkIdentifierKeyValuePairListNode(UaClient client, NodeId baseNode, int aasns, String name, Map<String, String> map)
            throws ServiceException, ServiceResultException, AddressSpaceException, StatusException {
        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, new QualifiedName(aasns, name)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(baseNode, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkIdentifierKeyValuePairListNode Browse Result Null", bpres);
        Assert.assertEquals("checkIdentifierKeyValuePairListNode Browse Result: size doesn't match", 1, bpres.length);

        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkIdentifierKeyValuePairListNode Browse Target Node Null", targets);
        Assert.assertTrue("checkIdentifierKeyValuePairListNode Browse targets empty", targets.length > 0);
        NodeId listNode = client.getAddressSpace().getNamespaceTable().toNodeId(targets[0].getTargetId());
        Assert.assertNotNull("checkIdentifierKeyValuePairListNode Ref Node Null", listNode);
        Assert.assertNotEquals("checkIdentifierKeyValuePairListNode Ref Node Null", NodeId.NULL, listNode);

        checkType(client, listNode, new NodeId(aasns, TestConstants.AAS_ID_KEY_VALUE_PAIR_LIST_ID));

        List<NodeId> nodeList = new ArrayList<>();
        List<ReferenceDescription> refs = client.getAddressSpace().browse(listNode);
        for (ReferenceDescription ref: refs) {
            NodeId nid = client.getAddressSpace().getNamespaceTable().toNodeId(ref.getNodeId());
            nodeList.add(nid);
        }

        for (NodeId node: nodeList) {
            checkIdentifierKeyValuePairNode(client, node, aasns, map);
        }
    }


    private static void checkIdentifierKeyValuePairNode(UaClient client, NodeId node, int aasns, Map<String, String> map)
            throws ServiceException, AddressSpaceException, ServiceResultException, StatusException {
        checkType(client, node, new NodeId(aasns, TestConstants.AAS_ID_KEY_VALUE_PAIR_ID));

        List<RelativePath> relPath = new ArrayList<>();
        List<RelativePathElement> browsePath = new ArrayList<>();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.ID_KEY_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));
        browsePath.clear();
        browsePath.add(new RelativePathElement(Identifiers.HasProperty, false, true, new QualifiedName(aasns, TestConstants.ID_VALUE_NAME)));
        relPath.add(new RelativePath(browsePath.toArray(RelativePathElement[]::new)));

        BrowsePathResult[] bpres = client.getAddressSpace().translateBrowsePathsToNodeIds(node, relPath.toArray(RelativePath[]::new));
        Assert.assertNotNull("checkIdentifierKeyValuePairNode Browse Result Null", bpres);
        Assert.assertEquals("checkIdentifierKeyValuePairNode Browse Result: size doesn't match", 2, bpres.length);

        // Key
        BrowsePathTarget[] targets = bpres[0].getTargets();
        Assert.assertNotNull("checkIdentifierKeyValuePairNode Browse Key Null", targets);
        Assert.assertTrue("checkIdentifierKeyValuePairNode Browse Key empty", targets.length > 0);
        DataValue dataValue = client.readValue(targets[0].getTargetId());
        Assert.assertEquals(StatusCode.GOOD, dataValue.getStatusCode());
        String key = dataValue.getValue().toString();

        // Value
        targets = bpres[1].getTargets();
        Assert.assertNotNull("checkIdentifierKeyValuePairNode Browse Value Null", targets);
        Assert.assertTrue("checkIdentifierKeyValuePairNode Browse Value empty", targets.length > 0);
        dataValue = client.readValue(targets[0].getTargetId());
        Assert.assertEquals(StatusCode.GOOD, dataValue.getStatusCode());
        String value = dataValue.getValue().toString();

        Assert.assertTrue("Key not found in Map", map.containsKey(key));
        Assert.assertEquals("Value not equal", map.get(key), value);
    }


    private static void checkQualifierList(List<Qualifier> listExpected, List<AASQualifierType> listCurrent) {
        Assert.assertEquals(listExpected.size(), listCurrent.size());

        for (int i = 0; i < listExpected.size(); i++) {
            Qualifier exp = listExpected.get(i);
            AASQualifierType curr = listCurrent.get(i);
            Assert.assertEquals("Qualifier Type not equal", exp.getType(), curr.getType());
            Assert.assertEquals("Qualifier ValueType not equal", ValueConverter.stringToValueType(exp.getValueType()), curr.getValueType());
            Assert.assertEquals("Qualifier Value not equal", exp.getValue(), curr.getValue());
        }
    }
}
