# 🚀 BPMNFlow

> Lightweight BPMN Model Parser for Model-Driven Workflow Automation

![Java](https://img.shields.io/badge/Java-17-blue)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)
![Maintained](https://img.shields.io/badge/maintained-yes-brightgreen)
![BPMN Support](https://img.shields.io/badge/BPMN-supported-brightgreen)

---
## 📑 Table of Contents

- [⭐ Why BPMNFlow?](#-why-bpmnflow)
- [⚖️ BPMNFlow vs Traditional BPMN Engines](#-bpmnflow-vs-traditional-bpmn-engines)
- [📖 Overview](#-overview)
- [🎯 Strategic Vision & Technical Value](#-strategic-vision--technical-value)
- [🚀 Quick Start](#-quick-start)
- [🧩 Using Extension Properties in BPMN Models](#-using-extension-properties-in-bpmn-models)
- [🛠️ How to Define Extension Properties](#-how-to-define-extension-properties)
- [⚙️ Dynamic Configuration](#-dynamic-configuration-yaml-driven-extraction)
- [⚙️ Workflow Execution](#-using-extension-properties-in-workflow-execution)
- [✅ Best Practices](#-best-practices)
- [🧪 Example Scenario](#-example-scenario)
- [🚧 What’s Next](#-whats-next)
- [🧾 Conclusion](#-conclusion)

---
## ⭐ Why BPMNFlow?

`BPMNFlow` was created to solve a common problem: **using BPMN without the overhead of a full workflow engine**.

### 🚀 Key Advantages

- ⚡ **Lightweight by Design** — No runtime engine, no state persistence, no complexity
- 🧠 **Model-as-Code** — BPMN becomes a dynamic configuration layer
- 🔗 **Perfect for Microservices** — Trigger logic without orchestration overhead
- 📦 **Zero Lock-in** — Works with any architecture or framework
- ⚙️ **YAML-Driven** — Fully customizable parsing and validation

---
## ⚖️ BPMNFlow vs Traditional BPMN Engines

| Feature                     | BPMNFlow ⚡ | Camunda / Flowable 🏗️ |
|----------------------------|------------|------------------------|
| Runtime Engine             | ❌ No       | ✅ Yes                 |
| State Management           | ❌ No       | ✅ Yes                 |
| Lightweight                | ✅ Yes      | ❌ No                  |
| Cloud-Native Friendly      | ✅ High     | ⚠️ Medium              |
| Model Parsing              | ✅ Yes      | ✅ Yes                 |
| Execution Engine           | ❌ No       | ✅ Yes                 |
| Setup Complexity           | 🟢 Low      | 🔴 High                |

👉 **Use BPMNFlow when you need interpretation, not orchestration.**

---
## 📖 Overview

The `BPMNFlow` is a high-performance Java framework designed to analyze BPMN models and extract **Extension Properties**. It enables **Lightweight Workflow Automation** by turning visual business models into actionable technical metadata without the overhead of a full BPMN engine.

---
## 🎯 Strategic Vision & Technical Value

This parser was designed to bridge the gap between business modeling and lightweight execution. While full-scale engines like Camunda or Flowable are powerful, they often introduce unnecessary infrastructure overhead for many use cases.

The `BPMNFlow` provides a lean way to "read business intent" and trigger application logic. It is the perfect fit for:

- **🔗 Decoupled Orchestration**
- **🧠 Model-as-Code**
- **⚡ Low Overhead**

---
## 🚀 Quick Start

### 1️⃣ Installation (Maven)

```xml
<dependency>
    <groupId>com.jefersonferr</groupId>
    <artifactId>bpmn-model-parser</artifactId>
    <version>1.0.0</version>
</dependency>
 ```
### 2️⃣ Basic Usage
  ```java
import static org.bpmnparser.util.ModelParser.parser;

try (InputStream is = Files.newInputStream(Path.of("model.bpmn"))) {
    Workflow workflow = parser(is, "config.yaml");
}
System.out.println("Model: "+workflow);
  ```
---
## 🧩 Using Extension Properties in BPMN Models

Extension properties in BPMN allow you to add custom attributes or metadata to BPMN elements, making your model more flexible and tailored to your specific business needs. These properties enhance workflow functionality, provide additional context, or integrate with external systems.

### 🎯 Purpose of Extension Properties
- **Custom Metadata**: Add information like process type, stage, or activity details.
- **Integration**: Store data for external system interactions (e.g., API endpoints, database queries).
- **Conditional Logic**: Use properties to define dynamic behavior in gateways or tasks.
- **Reporting and Analytics**: Capture additional data for monitoring and analysis.

### 📌 Common Use Cases
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
## 🛠️ How to Define Extension Properties

### 🧰 In BPMN Tools
- Most BPMN modeling tools (e.g., Camunda Modeler, Bizagi, Signavio) allow you to add custom properties to elements.
- Look for a **Properties** panel or **Extension Elements** section.

### 📄 In XML (BPMN 2.0)
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
## ⚙️ Dynamic Configuration (YAML-Driven Extraction)
The `BPMNFlow` is fully driven by a `config.yaml` file, granting you granular control over the data extraction process. This file acts as a schema that defines:
1. **Scope**: Which BPMN elements (Tasks, Processes, Lanes, etc.) should be parsed.
2. **Origin**: Whether a property is a standard XML attribute (extension: false) or a custom metadata within <extensionElements> (extension: true).
3. **Strictness**: By setting `required: true`, the parser will automatically validate the model's integrity, ensuring mandatory business or technical metadata is present.

### 🧾 Configuration Schema
```yaml
bpmn_model_parser:

  model_properties:

    participant:
      - name: name
        required: true
        extension: false
      - name: presence
        required: false
        extension: false
      - name: process_type
        required: true
        extension: true
      - name: process_subtype
        required: true
        extension: true

    process:
      - name: id
        required: true
        extension: false
      - name: camunda:versionTag
        required: true
        extension: false
      - name: documentation
        required: true
        extension: false
      - name: presence
        required: true
        extension: false
      - name: process_type
        required: true
        extension: true
      - name: process_subtype
        required: true
        extension: true

    lane:
      - name: name
        required: true
        extension: false
      - name: presence
        required: true
        extension: false
      - name: stage
        required: true
        extension: true

    task:
      - name: name
        required: true
        extension: false
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
        required: true
        extension: false
      - name: conclusion
        required: true
        extension: true
      - name: process_status
        required: false
        extension: true
```
---
## ⚙️ Using Extension Properties in Workflow Execution
Extension properties play a key role in enabling dynamic and context-aware workflow execution. They allow BPMN models to carry meaningful metadata that can be leveraged at runtime across different execution strategies.

### 🔄 Workflow Engines

Many workflow engines (e.g., Camunda) support extension properties and can utilize them during process execution.

For example, a `stage` property can be accessed to determine the current phase of the workflow, enabling conditional logic, routing decisions, or context-aware processing.

### 💻 Custom Code

In custom workflow implementations, extension properties can be parsed directly from the BPMN XML and used within application logic.

This approach provides full control over execution behavior, allowing developers to design lightweight and flexible solutions without relying on a full BPMN engine.

### ✅ Best Practices
- **Consistency**: Use a consistent naming convention for properties (e.g., camelCase or snake_case).
- **Documentation**: Document the purpose and usage of each extension property.
- **Validation**: Ensure properties are validated during workflow execution to avoid errors.
- **Avoid Overloading**: Only add properties that are necessary for the workflow to function or provide value.

### 🧪 Example Scenario
Let’s say you have a task for approving a purchase order. You can add the following extension properties:
- `stage`: Defines the workflow phase (e.g., "Approval").
- `activity`: Specifies the exact task action (e.g., "Review").
- `process_status`: Indicates the status of the process (e.g., "Pending").

### 📄 BPMN XML Example (Task)
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

### 🔀 BPMN XML Example (Sequence Flow)
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
## 🚧 What’s Next

We are continuously improving `BPMNFlow` with a strong focus on simplicity, performance, and real-world usability.

### 🔧 Improvements in Progress

- Support for additional BPMN extension attributes (e.g., `camunda:*`, `zeebe:*`)
- Improved mapping of extracted properties to custom Java objects
- Performance optimizations for large `.bpmn` files

### ⚡ Planned Enhancements

- Streaming-based parsing for more efficient handling of large models
- Optional caching for repeated model access

### 🧰 Developer Experience

- Spring Boot integration to simplify adoption

> 💡 Our goal is simple: keep `BPMNFlow` **lightweight, flexible, and easy to integrate** into any Java application. Want to help? Check our Contribution Guidelines

---
## 🧾 Conclusion

The `BPMNFlow` redefines how BPMN models can be leveraged in modern applications by shifting the focus from heavy workflow engines to **lightweight, model-driven execution**. Instead of managing complex runtime states, it empowers developers to **extract business intent directly from BPMN diagrams** and seamlessly integrate it into application logic.

By combining **BPMN Extension Properties** with a **YAML-driven configuration approach**, the framework provides a highly flexible and declarative way to transform visual models into actionable metadata. This makes it an ideal solution for **cloud-native architectures**, **microservices orchestration**, and **event-driven systems** where simplicity, performance, and scalability are essential.

`BPMNFlow` is not just a parser—it is a bridge between **business design and technical execution**, enabling teams to treat BPMN as a true **Model-as-Code artifact**. As the framework evolves, it aims to become a foundational component for building **adaptive, maintainable, and decoupled workflow-driven systems**.
