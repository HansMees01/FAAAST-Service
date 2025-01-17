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

import io.adminshell.aas.v3.dataformat.DeserializationException;
import io.adminshell.aas.v3.dataformat.SerializationException;
import io.adminshell.aas.v3.dataformat.json.JsonDeserializer;
import io.adminshell.aas.v3.dataformat.json.JsonSerializer;
import io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment;
import io.adminshell.aas.v3.model.Referable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Helper class with methods to create deep copies. Following types are supported:
 * <ul>
 * <li>{@link io.adminshell.aas.v3.model.Identifiable}
 * <li>{@link io.adminshell.aas.v3.model.Referable}
 * <li>{@link io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment}
 * </ul>
 */
public class DeepCopyHelper {

    private DeepCopyHelper() {}


    /**
     * Create a deep copy of a {@link io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment} object.
     *
     * @param env the asset administration shell environment which should be deep copied
     * @return a deep copied instance of the asset administration shell environment
     * @throws RuntimeException when operation fails
     */
    public static AssetAdministrationShellEnvironment deepCopy(AssetAdministrationShellEnvironment env) {
        try {
            return new JsonDeserializer().read(new JsonSerializer().write(env));
        }
        catch (SerializationException | DeserializationException e) {
            throw new IllegalArgumentException("deep copy of AAS environment failed", e);
        }
    }


    /**
     * Create a deep copy of a {@link io.adminshell.aas.v3.model.Referable} object.
     *
     * @param referable which should be deep copied
     * @param outputClass of the referable
     * @param <T> type of the referable
     * @return the deep copied referable
     * @throws IllegalArgumentException if outputClass is null
     * @throws IllegalArgumentException if type of referable if not a subclass of outputClass
     * @throws RuntimeException when operation fails
     */
    public static <T extends Referable> T deepCopy(Referable referable, Class<T> outputClass) {
        if (outputClass == null) {
            throw new IllegalArgumentException("outputClass must be non-null");
        }
        if (referable != null && !outputClass.isAssignableFrom(referable.getClass())) {
            throw new IllegalArgumentException(
                    String.format("type mismatch - can not create deep copy of instance of type %s with target type %s", referable.getClass(), outputClass));
        }
        try {
            return (T) new JsonDeserializer().readReferable(new JsonSerializer().write(referable), outputClass);
        }
        catch (SerializationException | DeserializationException e) {
            throw new RuntimeException("deep copy of AAS environment failed", e);
        }
    }


    /**
     * Create a deep copy of a list of {@link io.adminshell.aas.v3.model.Referable} objects.
     *
     * @param referables list with referables which should be deep copied
     * @param outputClass of the referables
     * @param <T> type of the referables
     * @return a list with deep copied referables
     * @throws IllegalArgumentException if outputClass is null
     * @throws IllegalArgumentException if referables is null
     * @throws RuntimeException when operation fails
     */
    public static <T extends Referable> List<T> deepCopy(Collection<? extends T> referables,
                                                         Class<? extends T> outputClass) {
        if (outputClass == null) {
            throw new IllegalArgumentException("outputClass must be non-null");
        }
        if (referables == null) {
            throw new IllegalArgumentException("referables must be non-null");
        }
        return referables.stream().map(x -> deepCopy(x, outputClass)).collect(Collectors.toList());
    }
}
