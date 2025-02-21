package org.bpmnparser.util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModelProperty {
    private String name;
    private boolean required;
    private boolean extension;
}
