package org.bpmnparser.util;

public class BpmnPropertiesLoader {
    private final BpmnPropertiesConfig config;

    public BpmnPropertiesLoader(BpmnPropertiesConfig config) {
        this.config = config;
    }

    ModelProperty getParticipant(String propertyName) {
        return getPropertiesForType("participant", propertyName);
    }

    ModelProperty getProcess(String propertyName) {
        return getPropertiesForType("process", propertyName);
    }

    ModelProperty getLane(String propertyName) {
        return getPropertiesForType("lane", propertyName);
    }

    ModelProperty getTask(String propertyName) {
        return getPropertiesForType("task", propertyName);
    }

    ModelProperty getStartEvent(String propertyName) {
        return getPropertiesForType("startEvent", propertyName);
    }

    ModelProperty getEndEvent(String propertyName) {
        return getPropertiesForType("endEvent", propertyName);
    }

    ModelProperty getSequenceFlow(String propertyName) {
        return getPropertiesForType("sequenceFlow", propertyName);
    }

    private ModelProperty getPropertiesForType(String elementType, String propertyName) {
        ModelProperty modelProperty = null;
        if (config.getExtensionProperties().containsKey(elementType)) {
            for (ModelProperty property : config.getExtensionProperties().get(elementType)) {
                if (property.getName().equals(propertyName)) {
                    modelProperty = property;
                }
            }
        } else {
            System.out.println("No extension properties defined for: " + elementType);
        }
        return modelProperty;
    }
}
