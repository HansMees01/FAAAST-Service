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
package de.fraunhofer.iosb.ilt.faaast.service.util;

import com.google.common.reflect.TypeToken;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.ValueMappingException;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.DataElementValue;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.ElementValue;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.mapper.ElementValueMapper;
import io.adminshell.aas.v3.model.AnnotatedRelationshipElement;
import io.adminshell.aas.v3.model.DataElement;
import io.adminshell.aas.v3.model.Entity;
import io.adminshell.aas.v3.model.OperationVariable;
import io.adminshell.aas.v3.model.ReferenceElement;
import io.adminshell.aas.v3.model.RelationshipElement;
import io.adminshell.aas.v3.model.Submodel;
import io.adminshell.aas.v3.model.SubmodelElement;
import io.adminshell.aas.v3.model.SubmodelElementCollection;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Helper class for {@link de.fraunhofer.iosb.ilt.faaast.service.model.value.ElementValue}.
 */
public class ElementValueHelper {

    private static final Type COLLECTION_GENERIC_TOKEN;
    private static final Type MAP_GENERIC_TOKEN;

    static {
        try {
            COLLECTION_GENERIC_TOKEN = TypeToken.of(Collection.class.getMethod("iterator").getGenericReturnType()).resolveType(Iterator.class.getTypeParameters()[0]).getType();
            MAP_GENERIC_TOKEN = Map.class.getMethod("get", Object.class).getGenericReturnType();
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException("static initialization of ElementValueHelper failed", e);
        }
    }

    private ElementValueHelper() {}


    /**
     * Checks if an object can be converted to an element value.
     *
     * @param obj which should be checked
     * @return true if the object can be converted to an element value, false otherwise
     */
    public static boolean isValueOnlySupported(Object obj) {
        if (obj == null) {
            return true;
        }
        Class type = obj.getClass();
        if (type.isArray()) {
            return Stream.of((Object[]) obj).allMatch(x -> isValueOnlySupported(x));
        }
        if (Collection.class.isAssignableFrom(type)) {
            return ((Collection) obj).stream().allMatch(x -> isValueOnlySupported(x));
        }
        if (Map.class.isAssignableFrom(type)) {
            return ((Map) obj).values().stream().allMatch(x -> isValueOnlySupported(x));
        }
        return isValueOnlySupported(type);
    }


    /**
     * Checks if an object of a specific class can be converted to an element value.
     *
     * @param type which should be checked
     * @return true if an object of the type can be converted to an element value, false otherwise
     */
    public static boolean isValueOnlySupported(Class<?> type) {
        if (isSerializableAsValue(type) || Submodel.class.isAssignableFrom(type) || ElementValue.class.isAssignableFrom(type)) {
            return true;
        }
        if (type.isArray()) {
            return isValueOnlySupported(TypeToken.of(type).getComponentType());
        }
        if (Collection.class.isAssignableFrom(type)) {
            return isValueOnlySupported(TypeToken.of(type).resolveType(COLLECTION_GENERIC_TOKEN).getRawType());
        }
        if (Map.class.isAssignableFrom(type)) {
            return isValueOnlySupported(TypeToken.of(type).resolveType(MAP_GENERIC_TOKEN).getRawType());
        }
        return false;
    }


    /**
     * Checks if an object of a specific class can be converted to an element value.
     *
     * @param type which should be checked
     * @return true if an object of the type can be converted to an element value, false otherwise
     */
    public static boolean isSerializableAsValue(Class<?> type) {
        return DataElement.class.isAssignableFrom(type)
                || SubmodelElementCollection.class.isAssignableFrom(type)
                || ReferenceElement.class.isAssignableFrom(type)
                || RelationshipElement.class.isAssignableFrom(type)
                || AnnotatedRelationshipElement.class.isAssignableFrom(type)
                || Entity.class.isAssignableFrom(type);
    }


    /**
     * Checks if given value is a valid {@link DataElementValue}.
     *
     * @param value the value to check
     * @return true if value is valid value of type {@link DataElementValue}, false otherwise
     */
    public static boolean isValidDataElementValue(Object value) {
        return value == null || DataElementValue.class.isAssignableFrom(value.getClass());
    }


    /**
     * Converts a list of {@link io.adminshell.aas.v3.model.OperationVariable} to a list of
     * {@link de.fraunhofer.iosb.ilt.faaast.service.model.value.ElementValue}.
     *
     * @param variables list of operation variables
     * @return the corresponding list of element values
     * @throws de.fraunhofer.iosb.ilt.faaast.service.model.exception.ValueMappingException if mapping of element values
     *             fails
     */
    public static List<ElementValue> toValues(List<OperationVariable> variables) throws ValueMappingException {
        return variables.stream()
                .map(LambdaExceptionHelper.rethrowFunction(
                        x -> ElementValueMapper.<SubmodelElement, ElementValue> toValue(x.getValue())))
                .collect(Collectors.toList());
    }
}
