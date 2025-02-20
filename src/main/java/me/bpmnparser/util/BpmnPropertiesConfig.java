package me.bpmnparser.util;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class BpmnPropertiesConfig {
    private Map<String, List<ModelProperty>> extensionProperties;
}

