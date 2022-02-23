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
import com.prosysopc.ua.stack.builtintypes.UnsignedInteger;


class MethodIdsInit {
    static ExpandedNodeId initAASFileType_File_Write() {
        return new ExpandedNodeId("http://opcfoundation.org/UA/I4AAS/V3/", UnsignedInteger.valueOf(7013L));
    }


    static ExpandedNodeId initAASFileType_File_Read() {
        return new ExpandedNodeId("http://opcfoundation.org/UA/I4AAS/V3/", UnsignedInteger.valueOf(7011L));
    }


    static ExpandedNodeId initAASFileType_File_Open() {
        return new ExpandedNodeId("http://opcfoundation.org/UA/I4AAS/V3/", UnsignedInteger.valueOf(7010L));
    }


    static ExpandedNodeId initAASFileType_File_Close() {
        return new ExpandedNodeId("http://opcfoundation.org/UA/I4AAS/V3/", UnsignedInteger.valueOf(7008L));
    }


    static ExpandedNodeId initAASFileType_File_SetPosition() {
        return new ExpandedNodeId("http://opcfoundation.org/UA/I4AAS/V3/", UnsignedInteger.valueOf(7012L));
    }


    static ExpandedNodeId initAASFileType_File_GetPosition() {
        return new ExpandedNodeId("http://opcfoundation.org/UA/I4AAS/V3/", UnsignedInteger.valueOf(7009L));
    }


    static ExpandedNodeId initAASOperationType_Operation() {
        return new ExpandedNodeId("http://opcfoundation.org/UA/I4AAS/V3/", UnsignedInteger.valueOf(7001L));
    }
}