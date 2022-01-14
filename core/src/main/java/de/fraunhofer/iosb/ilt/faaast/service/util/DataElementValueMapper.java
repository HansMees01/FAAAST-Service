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
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.valuedata.ElementValue;
import de.fraunhofer.iosb.ilt.faaast.service.util.datavaluemapper.DataValueMapper;
import io.adminshell.aas.v3.dataformat.core.ReflectionHelper;
import io.adminshell.aas.v3.model.SubmodelElement;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Finds available DataValue mappers and forward the request to the right class
 */
public class DataElementValueMapper {

    private static Logger logger = LoggerFactory.getLogger(DataElementValueMapper.class);
    private static Map<Class<? extends SubmodelElement>, ? extends DataValueMapper> mappers;

    private static void init() {
        if (mappers == null) {
            ScanResult scanResult = new ClassGraph()
                    .enableAllInfo()
                    .acceptPackages(DataElementValueMapper.class.getPackageName())
                    .scan();

            mappers = scanResult.getSubclasses(DataValueMapper.class).loadClasses().stream()
                    .map(x -> (Class<? extends DataValueMapper>) x)
                    .collect(Collectors.toMap(
                            x -> (Class<? extends SubmodelElement>) TypeToken.of(x).resolveType(DataValueMapper.class.getTypeParameters()[0]).getRawType(),
                            x -> {
                                try {
                                    Constructor<? extends DataValueMapper> constructor = x.getConstructor();
                                    return constructor.newInstance();
                                }
                                catch (NoSuchMethodException | SecurityException ex) {
                                    logger.warn("data-element-value mapper implementation could not be loaded, "
                                            + "reason: missing constructor (implementation class: {})",
                                            x.getName());
                                }
                                catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                    logger.warn("data-element-value mapper implementation  could not be loaded, "
                                            + "reason: calling constructor failed (implementation class: {}",
                                            x.getName());
                                }
                                return null;
                            }));
        }
    }


    /**
     * Wraps the values of the SubmodelElement into a belonging DataElementValue instance
     *
     * @param submodelElement for which a DataElementValue should be created
     * @param <I> type of the input SubmodelElement
     * @param <O> type of the output ElementValue
     * @return a DataElementValue for the given SubmodelElement
     */
    public static <I extends SubmodelElement, O extends ElementValue> O toDataElement(SubmodelElement submodelElement) {
        init();
        if (submodelElement == null) {
            throw new IllegalArgumentException("submodelElement must be non-null");
        }
        if (!mappers.containsKey(ReflectionHelper.getAasInterface(submodelElement.getClass()))) {
            throw new RuntimeException("no mapper defined for submodelElement type " + submodelElement.getClass().getSimpleName());
        }
        return (O) mappers.get(ReflectionHelper.getAasInterface(submodelElement.getClass())).toDataElementValue(submodelElement);
    }


    /**
     * Set the values of the DataElementValue to the SubmodelElement
     *
     * @param submodelElement for which the values will be set
     * @param dataElementValue which contains the values for the SubmodelElement
     * @param <I> type of the input/output SubmodelElement
     * @param <O> type of the input ElementValue
     * @return the SubmodelElement instance with the DataElementValue values set
     */
    public static <I extends SubmodelElement, O extends ElementValue> I setDataElementValue(SubmodelElement submodelElement, ElementValue dataElementValue) {
        init();
        if (submodelElement == null) {
            throw new IllegalArgumentException("submodelElement must be non-null");
        }
        if (!mappers.containsKey(ReflectionHelper.getAasInterface(submodelElement.getClass()))) {
            throw new RuntimeException("no mapper defined for submodelElement type " + submodelElement.getClass().getSimpleName());
        }
        return (I) mappers.get(ReflectionHelper.getAasInterface(submodelElement.getClass())).setDataElementValue(submodelElement, dataElementValue);
    }

}
