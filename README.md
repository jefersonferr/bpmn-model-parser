# bpmn-model-parser

## Overview

The `bpmn-model-parser` is a framework designed to analyze BPMN models and extract extension properties (`extension elements`). These properties allow for the customization of BPMN elements by adding metadata essential for workflow automation and execution.

---

## Using Extension Properties in BPMN Models

Extension properties in BPMN allow you to add custom attributes or metadata to BPMN elements, making your model more flexible and tailored to your specific business needs. These properties enhance workflow functionality, provide additional context, or integrate with external systems.

### Purpose of Extension Properties
- **Custom Metadata**: Add information like process type, stage, or activity details.
- **Integration**: Store data for external system interactions (e.g., API endpoints, database queries).
- **Conditional Logic**: Use properties to define dynamic behavior in gateways or tasks.
- **Reporting and Analytics**: Capture additional data for monitoring and analysis.

### Common Use Cases
- **Tasks**:
  - Define `stage` and `activity` properties to categorize workflow steps.
- **Lanes**:
  - Assign a `stage` to represent different workflow phases.
- **Processes**:
  - Include `process_type` and `process_subtype` for classification.
- **Events**:
  - Track `process_status` at start and end events.
- **Sequence Flows**:
  - Define `conclusion` to indicate decision-making paths.

---

## How to Define Extension Properties

### In BPMN Tools
- Most BPMN modeling tools (e.g., Camunda Modeler, Bizagi, Signavio) allow you to add custom properties to elements.
- Look for a **Properties** panel or **Extension Elements** section.

### In XML (BPMN 2.0)
- Extension properties are defined within the `<extensionElements>` tag in the BPMN XML.
- Example:
  ```xml
  <bpmn:process id="Process_1" name="Order Processing">
    <bpmn:extensionElements>
      <camunda:properties>
        <camunda:property name="process_type" value="order" />
        <camunda:property name="process_subtype" value="purchase" />
      </camunda:properties>
    </bpmn:extensionElements>
  </bpmn:process>
  ```

---

## Configuration Using YAML

The `bpmn-model-parser` is configurable through a `config.yaml` file, defining which properties should be extracted and whether they are required.

### Example YAML Configuration
```yaml
bpmn_model_parser:
  model_properties:
    participant:
      - name: name
      - name: presence
        required: true
    process:
      - name: id
      - name: camunda:versionTag
      - name: documentation
      - name: presence
        required: true
      - name: process_type
        required: true
        extension: true
      - name: process_subtype
        required: true
        extension: true
    lane:
      - name: name
      - name: presence
        required: false
      - name: stage
        required: true
        extension: true
    task:
      - name: name
      - name: stage
        required: true
        extension: true
      - name: activity
        required: true
        extension: true
    startEvent:
      - name: process_status
        required: true
        extension: true
    endEvent:
      - name: process_status
        required: true
        extension: true
    sequenceFlow:
      - name: name
      - name: conclusion
        required: true
        extension: true
      - name: process_status
        extension: true
```

---

## Using Extension Properties in Workflow Execution

### Workflow Engines
- Many workflow engines (e.g., Camunda, Activiti) support extension properties and can use them during execution.
- For example, you can access a `stage` property to determine the current workflow phase.

### Custom Code
- If you’re building a custom workflow solution, you can parse and use extension properties from the BPMN XML.

### Integration
- Use properties to pass data to external systems (e.g., API calls, database updates).

---

## Best Practices
- **Consistency**: Use a consistent naming convention for properties (e.g., camelCase or snake_case).
- **Documentation**: Document the purpose and usage of each extension property.
- **Validation**: Ensure properties are validated during workflow execution to avoid errors.
- **Avoid Overloading**: Only add properties that are necessary for the workflow to function or provide value.

---

## Example Scenario
Let’s say you have a task for approving a purchase order. You can add the following extension properties:
- `stage`: Defines the workflow phase (e.g., "Approval").
- `activity`: Specifies the exact task action (e.g., "Review").
- `process_status`: Indicates the status of the process (e.g., "Pending").

In the BPMN XML:
```xml
<bpmn:task id="Task_ApprovePO" name="Approve Purchase Order">
  <bpmn:extensionElements>
    <camunda:properties>
      <camunda:property name="stage" value="Approval" />
      <camunda:property name="activity" value="Review" />
    </camunda:properties>
  </bpmn:extensionElements>
</bpmn:task>
```

For a sequence flow:
```xml
<bpmn:sequenceFlow id="Flow_1" sourceRef="Task_ApprovePO" targetRef="Task_CompleteOrder">
  <bpmn:extensionElements>
    <camunda:properties>
      <camunda:property name="conclusion" value="approved" />
      <camunda:property name="process_status" value="Completed" />
    </camunda:properties>
  </bpmn:extensionElements>
</bpmn:sequenceFlow>
```

---

## Conclusion

The `bpmn-model-parser` simplifies the extraction and usage of extension properties in BPMN models, enabling greater control and automation of workflows. With flexible YAML-based configuration, the framework can be adapted to various business needs.

