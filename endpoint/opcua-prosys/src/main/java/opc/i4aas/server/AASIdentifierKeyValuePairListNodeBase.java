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
package opc.i4aas.server;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.TypeDefinitionId;
import com.prosysopc.ua.server.GeneratedNodeInitializer;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.ServiceContext;
import com.prosysopc.ua.stack.builtintypes.DiagnosticInfo;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.builtintypes.StatusCode;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.types.opcua.server.FolderTypeNode;
import opc.i4aas.AASIdentifierKeyValuePairList;


/**
 * Generated on 2021-12-15 11:39:02
 */
@TypeDefinitionId("nsu=http://opcfoundation.org/UA/I4AAS/V3/;i=1039")
public abstract class AASIdentifierKeyValuePairListNodeBase extends FolderTypeNode implements AASIdentifierKeyValuePairList {
    private static GeneratedNodeInitializer<AASIdentifierKeyValuePairListNode> aASIdentifierKeyValuePairListNodeInitializer;

    protected AASIdentifierKeyValuePairListNodeBase(NodeManagerUaNode nodeManager, NodeId nodeId,
            QualifiedName browseName, LocalizedText displayName) {
        super(nodeManager, nodeId, browseName, displayName);
    }


    @Override
    public void afterCreate() {
        super.afterCreate();

        // Call afterCreate for each sub-node (if the node has any)
        GeneratedNodeInitializer<AASIdentifierKeyValuePairListNode> impl = getAASIdentifierKeyValuePairListNodeInitializer();
        if (impl != null) {
            impl.init((AASIdentifierKeyValuePairListNode) this);
        }
    }


    public static GeneratedNodeInitializer<AASIdentifierKeyValuePairListNode> getAASIdentifierKeyValuePairListNodeInitializer() {
        return aASIdentifierKeyValuePairListNodeInitializer;
    }


    public static void setAASIdentifierKeyValuePairListNodeInitializer(GeneratedNodeInitializer<AASIdentifierKeyValuePairListNode> aASIdentifierKeyValuePairListNodeInitializerNewValue) {
        aASIdentifierKeyValuePairListNodeInitializer = aASIdentifierKeyValuePairListNodeInitializerNewValue;
    }


    @Override
    public Variant[] callMethod(ServiceContext serviceContext, NodeId methodId,
                                Variant[] inputArguments, StatusCode[] inputArgumentResults,
                                DiagnosticInfo[] inputArgumentDiagnosticInfos)
            throws StatusException {
        return super.callMethod(serviceContext, methodId, inputArguments, inputArgumentResults, inputArgumentDiagnosticInfos);
    }
}
