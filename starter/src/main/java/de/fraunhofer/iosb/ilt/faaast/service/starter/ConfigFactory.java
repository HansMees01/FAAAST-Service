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
package de.fraunhofer.iosb.ilt.faaast.service.starter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.fraunhofer.iosb.ilt.faaast.service.config.ServiceConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConfigFactory {

    public static final String DEFAULT_CONFIG_JSON = "default-config.json";

    private static ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    public static ServiceConfig getDefaultServiceConfig() throws StarterConfigurationException {
        return getDefaultServiceConfig(new HashMap<>());
    }


    public static ServiceConfig getDefaultServiceConfig(Map<String, Object> properties) throws StarterConfigurationException {
        if (properties == null) {
            properties = new HashMap<>();
        }
        try {
            JsonNode configNode = readDefaultConfigFile();
            applyCommandlineProperties(properties, configNode);
            Application.print("Used Configuration:\n" + mapper.writeValueAsString(configNode));
            ServiceConfig config = mapper.readValue(mapper.writeValueAsString(configNode), ServiceConfig.class);
            return config;
        }
        catch (IOException e) {
            throw new StarterConfigurationException("Configuration Error: " + e.getMessage());
        }
    }


    public static ServiceConfig toServiceConfig(File configFile) throws StarterConfigurationException, IOException {
        return toServiceConfig(Files.readString(configFile.toPath()));
    }


    public static ServiceConfig toServiceConfig(String pathToConfigFile) throws IOException, StarterConfigurationException {
        return toServiceConfig(pathToConfigFile, Application.autoCompleteConfiguration, new HashMap<>());
    }


    public static ServiceConfig toServiceConfig(String pathToConfigFile, boolean autoCompleteConfiguration, Map<String, Object> commandLineProperties)
            throws StarterConfigurationException {
        try {
            JsonNode configNode = mapper.readTree(Files.readString(Path.of(pathToConfigFile)));
            if (commandLineProperties != null && !commandLineProperties.isEmpty()) {
                applyCommandlineProperties(commandLineProperties, configNode);
            }
            ServiceConfig serviceConfig = mapper.readValue(mapper.writeValueAsString(configNode), ServiceConfig.class);

            if (autoCompleteConfiguration) {
                ServiceConfig defaultConfig = getDefaultServiceConfig();
                if (serviceConfig.getCore() == null) {
                    serviceConfig.setCore(defaultConfig.getCore());
                }
                if (serviceConfig.getEndpoints() == null || serviceConfig.getEndpoints().size() == 0) {
                    serviceConfig.setEndpoints(defaultConfig.getEndpoints());
                }
                if (serviceConfig.getPersistence() == null) {
                    serviceConfig.setPersistence(defaultConfig.getPersistence());
                }
                if (serviceConfig.getMessageBus() == null) {
                    serviceConfig.setMessageBus(defaultConfig.getMessageBus());
                }
            }
            return serviceConfig;
        }
        catch (NoSuchFileException ex) {
            throw new StarterConfigurationException("Configuration Error - Could not find configuration file: " + ex.getMessage());
        }
        catch (IOException ex) {
            throw new StarterConfigurationException("Configuration Error: " + ex.getMessage());
        }
    }


    private static void applyCommandlineProperties(Map<String, Object> properties, JsonNode configNode) throws StarterConfigurationException {
        for (Map.Entry<String, Object> prop: properties.entrySet()) {
            List<String> pathList = List.of(prop.getKey().split("\\."));
            JsonNode jsonNode = configNode;
            for (int i = 0; i < pathList.size() - 1; i++) {
                String path = pathList.get(i);
                if (isNumeric(path)) {
                    jsonNode = jsonNode.path(Integer.parseInt(path));
                }
                else {
                    jsonNode = jsonNode.path(path);
                }
            }
            if (MissingNode.class.isAssignableFrom(jsonNode.getClass())) {
                throw new StarterConfigurationException("Configuration Error: Could not find attribute with path '" + prop.getKey() + "' in config file");
            }
            ((ObjectNode) jsonNode).put(pathList.get(pathList.size() - 1), prop.getValue().toString());
        }
    }


    private static JsonNode readDefaultConfigFile() throws IOException {
        JsonNode configNode = mapper.readTree(ConfigFactory.class.getClassLoader().getResource(DEFAULT_CONFIG_JSON));
        return configNode;
    }


    private static boolean isNumeric(String s) {
        if (s == null || s.equalsIgnoreCase("")) {
            return false;
        }
        try {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}
