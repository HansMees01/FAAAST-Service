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
package opc.i4aas;

import com.prosysopc.ua.stack.builtintypes.ExpandedNodeId;


/**
 * Generated on 2022-02-08 12:58:54
 */
public interface MethodIds {
    ExpandedNodeId AASFileType_File_Write = MethodIdsInit.initAASFileType_File_Write();

    ExpandedNodeId AASFileType_File_Read = MethodIdsInit.initAASFileType_File_Read();

    ExpandedNodeId AASFileType_File_Open = MethodIdsInit.initAASFileType_File_Open();

    ExpandedNodeId AASFileType_File_Close = MethodIdsInit.initAASFileType_File_Close();

    ExpandedNodeId AASFileType_File_SetPosition = MethodIdsInit.initAASFileType_File_SetPosition();

    ExpandedNodeId AASFileType_File_GetPosition = MethodIdsInit.initAASFileType_File_GetPosition();

    ExpandedNodeId AASOperationType_Operation = MethodIdsInit.initAASOperationType_Operation();
}