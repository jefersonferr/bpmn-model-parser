package org.bpmnparser.util;

import org.yaml.snakeyaml.Yaml;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigLoader {
    private static final Logger LOGGER = Logger.getLogger(ConfigLoader.class.getName());

    public static BpmnPropertiesConfig loadConfig(String externalConfigPath) {
        Yaml yaml = new Yaml();
        Path configPath = Path.of(externalConfigPath);

        try (InputStream input = Files.newInputStream(configPath)) {
            // Load raw YAML as a Map
            Map<String, Object> rawConfig = yaml.load(input);

            // Extract only "extension_properties" from "bpmn_model_parser"
            Map<String, List<ModelProperty>> extensionProperties = extractExtensionProperties(rawConfig);

            // Create and return the config object
            BpmnPropertiesConfig config = new BpmnPropertiesConfig();
            config.setExtensionProperties(extensionProperties);
            return config;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load BPMN Extension Properties Config", e);
        }

        return new BpmnPropertiesConfig(); // Return empty config if loading fails
    }

    @SuppressWarnings("unchecked")
    private static Map<String, List<ModelProperty>> extractExtensionProperties(Map<String, Object> rawConfig) {
        if (rawConfig == null || !rawConfig.containsKey("bpmn_model_parser")) {
            LOGGER.warning("Missing 'bpmn_model_parser' in YAML. Returning empty config.");
            return Collections.emptyMap();
        }

        Map<String, Object> bpmnModelParser = (Map<String, Object>) rawConfig.get("bpmn_model_parser");
        if (!bpmnModelParser.containsKey("model_properties")) {
            LOGGER.warning("Missing 'model_properties' in YAML. Returning empty config.");
            return Collections.emptyMap();
        }

        Map<String, Object> rawExtensionProperties = (Map<String, Object>) bpmnModelParser.get("model_properties");
        Map<String, List<ModelProperty>> extensionProperties = new HashMap<>();

        for (Map.Entry<String, Object> entry : rawExtensionProperties.entrySet()) {
            String key = entry.getKey();
            List<Object> rawList = (List<Object>) entry.getValue();

            List<ModelProperty> properties = new ArrayList<>();
            for (Object obj : rawList) {
                if (obj instanceof Map) {
                    Map<String, Object> propertyMap = (Map<String, Object>) obj;
                    ModelProperty property = mapToExtensionProperty(propertyMap);
                    properties.add(property);
                }
            }

            extensionProperties.put(key, properties);
        }

        return extensionProperties;
    }

    private static ModelProperty mapToExtensionProperty(Map<String, Object> propertyMap) {
        ModelProperty property = new ModelProperty();
        property.setName((String) propertyMap.get("name"));
        property.setRequired(Boolean.TRUE.equals(propertyMap.get("required")));
        property.setExtension(Boolean.TRUE.equals(propertyMap.get("extension")));
        return property;
    }
}
