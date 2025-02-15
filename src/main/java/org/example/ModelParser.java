package org.example;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.example.model.*;

import java.io.File;
import java.util.*;

import static java.util.Objects.nonNull;

public class ModelParser {

    public static Workflow parser(File model) {

        Workflow workflow = null;
        String workflowName = null;
        String worflowId = null;
        String workflowVersion = null;
        String workflowDocumentation = null;
        String processType = null;
        String processSubtype = null;
        List<Stage> stages = new ArrayList<Stage>();
        List<Inconsistency> inconsistencies = new ArrayList<>();
        Map<String,Node> nodeMap = new HashMap<>();
        Map<SequenceFlow,Conclusion> conclusionMap = new HashMap<>();

        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(model);
        // find all elements of the type Participant
        ModelElementType participantType = modelInstance.getModel().getType(Participant.class);
        Collection<ModelElementInstance> participants = modelInstance.getModelElementsByType(participantType);

        for (ModelElementInstance p : participants) {
            Participant participant = (Participant) p;
            workflowName = participant.getName();

            // inconsistency: 1
            if (workflowName == null || workflowName.isEmpty())
                inconsistencies.add(new Inconsistency(1,"Missing attribute 'Name' in element " + participant.getId()));

            Process process = participant.getProcess();
            if (nonNull(process)) {
                worflowId = process.getId();

                // inconsistency: 2
                if (worflowId == null || worflowId.isEmpty())
                    inconsistencies.add(new Inconsistency(2,"Missing attribute 'Process ID' in element " + participant.getId()));

                workflowVersion = process.getCamundaVersionTag();

                // inconsistency: 3
                if (workflowVersion == null || workflowVersion.isEmpty())
                    inconsistencies.add(new Inconsistency(3,"Missing attribute 'Version tag' in element " + participant.getId()));

                Collection<Documentation> documentations = process.getDocumentations();
                for (Documentation doc : documentations) {
                    workflowDocumentation = doc.getTextContent();

                    // inconsistency: 4
                    if (workflowDocumentation == null || workflowDocumentation.isEmpty())
                        inconsistencies.add(new Inconsistency(4,"Missing attribute 'Process Documentation' in element " + participant.getId()));

                }

                Map<String, String> processAttributes = getAttributes(process);
                processType = processAttributes.get("process_type");

                // inconsistency: 5
                if (processType == null || processType.isEmpty())
                    inconsistencies.add(new Inconsistency(5, "Missing extension property 'process_type' in element " + participant.getId()));

                processSubtype = processAttributes.get("process_subtype");

                // inconsistency: 6
                if (processSubtype == null || processSubtype.isEmpty())
                    inconsistencies.add(new Inconsistency(6, "Missing extension property 'process_subtype' in element " + participant.getId()));

                Collection<LaneSet> laneSets = process.getLaneSets();
                for (LaneSet laneSet : laneSets) {
                    Collection<Lane> lanes = laneSet.getLanes();
                    for (Lane lane : lanes) {
                        Map<String, String> laneAttributes = getAttributes(lane);
                        String stageCode = laneAttributes.get("stage");

                        // inconsistency: 7
                        if (stageCode == null || stageCode.isEmpty())
                            inconsistencies.add(new Inconsistency(7, "Missing extension property 'stage' in element " + lane.getId()));

                        String laneName = lane.getName();
                        if (laneName != null && !laneName.isEmpty()) {
                            Stage stage = new Stage(laneName,stageCode);
                            stages.add(stage);
                        } else {
                            // inconsistency: 8
                            inconsistencies.add(new Inconsistency(8,"Missing attribute 'name' in element " + lane.getId()));
                        }
                    }
                }
            }
        }
        workflow = new Workflow(workflowName,worflowId,workflowVersion,workflowDocumentation,processType,processSubtype);
        workflow.setStages(stages);

        // find all elements of the type FlowNode
        ModelElementType flowNodeType = modelInstance.getModel().getType(FlowNode.class);
        Collection<ModelElementInstance> nodes = modelInstance.getModelElementsByType(flowNodeType);

        for (ModelElementInstance node : nodes) {

            FlowNode flowNode = (FlowNode) node;
            String id = flowNode.getAttributeValue("id");
            String name = flowNode.getAttributeValue("name");
            String documentation = flowNode.getAttributeValue("documentation");
            Map<String, String> flowNodeAttributes = getAttributes(flowNode);

            if (flowNode instanceof Task) {
                boolean ok = true;

                // inconsistency: 9
                if (name == null || name.isEmpty()) {
                    inconsistencies.add(new Inconsistency(9, "Missing attribute 'name' in element " + flowNode.getId()));
                    ok = false;
                }

                String stageCode = flowNodeAttributes.get("stage");
                String activityCode = flowNodeAttributes.get("activity");

                // inconsistency: 10
                if (stageCode == null || stageCode.isEmpty()) {
                    inconsistencies.add(new Inconsistency(10, "Missing extension property 'stage' in element " + flowNode.getId()));
                    ok = false;
                }

                // inconsistency: 11
                if (activityCode == null || activityCode.isEmpty()) {
                    inconsistencies.add(new Inconsistency(11, "Missing extension property 'activity' in element " + flowNode.getId()));
                    ok = false;
                }

                if (ok) {
                    ActivityNode activity = new ActivityNode(stageCode, activityCode, name, documentation);
                    workflow.addActivity(activity);
                    nodeMap.put(id, activity);
                }
            }

            if (flowNode instanceof EndEvent || flowNode instanceof StartEvent) {
                String processStatus = flowNodeAttributes.get("process_status");

                // inconsistency: 12
                if (processStatus == null || processStatus.isEmpty())
                    inconsistencies.add(new Inconsistency(12, "Missing extension property 'process_status' in element " + flowNode.getId()));

                StartEndNode startEndNode = new StartEndNode(name,documentation,processStatus);
                nodeMap.put(id,startEndNode);
            }
        }

        // find all elements of the type ExclusiveGateway
        ModelElementType exclusiveGatewayType = modelInstance.getModel().getType(ExclusiveGateway.class);
        Collection<ModelElementInstance> exclusiveNodes = modelInstance.getModelElementsByType(exclusiveGatewayType);

        for (ModelElementInstance node : exclusiveNodes) {

            FlowNode flowNode = (FlowNode) node;

            if (flowNode instanceof ExclusiveGateway exclusiveGateway) {
                var incomings = exclusiveGateway.getIncoming();
                int totalIncomings = incomings.size();

                // ExclusiveGateway - Split
                if (totalIncomings == 1) {
                    SequenceFlow incomingEdge = incomings.iterator().next();
                    FlowNode sourceNode = incomingEdge.getSource();
                    String idSource = sourceNode.getAttributeValue("id");
                    ActivityNode activityNode = (ActivityNode)nodeMap.get(idSource);
                    if (activityNode != null) {
                        List<Conclusion> conclusions = new ArrayList<>();
                        for (SequenceFlow sequenceFlow : exclusiveGateway.getOutgoing()) {
                            String conclusionName = sequenceFlow.getName();
                            Map<String, String> sequenceFlowAttributes = getAttributes(sequenceFlow);
                            String conclusionCode = sequenceFlowAttributes.get("conclusion");
                            boolean ok = true;

                            // inconsistency: 13
                            if (conclusionName == null || conclusionName.isEmpty()) {
                                inconsistencies.add(new Inconsistency(13, "Missing attribute 'name' in element " + sequenceFlow.getId()));
                                ok = false;
                            }

                            // inconsistency: 14
                            if (conclusionCode == null || conclusionCode.isEmpty()) {
                                inconsistencies.add(new Inconsistency(14, "Missing extension property 'conclusion' in element " + sequenceFlow.getId()));
                                ok = false;
                            }

                            if (ok) {
                                Conclusion conclusion = new Conclusion(conclusionCode, conclusionName);
                                activityNode.addConclusion(conclusion);
                                conclusionMap.put(sequenceFlow, conclusion);
                            }
                        }
                    }
                }
            }
        }

        workflow.setInconsistencies(inconsistencies);

        // find all elements of the type SequenceFlow
        ModelElementType sequenceFlowType = modelInstance.getModel().getType(SequenceFlow.class);
        Collection<ModelElementInstance> edges = modelInstance.getModelElementsByType(sequenceFlowType);

        for (ModelElementInstance edge : edges) {
            var sequenceFlow = (SequenceFlow) edge;

            // get source and target
            var source = sequenceFlow.getSource();
            var target = sequenceFlow.getTarget();
            var conclusion = conclusionMap.get(sequenceFlow);

            Map<String, String> sequenceFlowAttributes = getAttributes(sequenceFlow);
            var processStatus = sequenceFlowAttributes.get("process_status");

            // Rule 1: StartEvent -> Task = Initial rule
            if (source instanceof StartEvent && target instanceof Task) {
                Map<String, String> startEventAttributes = getAttributes(source);
                String startProcessStatus = startEventAttributes.get("process_status");
                ActivityNode targetNode = (ActivityNode) nodeMap.get(target.getAttributeValue("id"));
                if (startProcessStatus != null && !startProcessStatus.isEmpty())
                    workflow.addRule(new WorkflowRule(1,null, targetNode, conclusion, startProcessStatus));
            }

            // Rule 2: Task -> Task
            if ((source instanceof Task) && (target instanceof Task)) {
                ActivityNode sourceNode = (ActivityNode) nodeMap.get(source.getAttributeValue("id"));
                ActivityNode targetNode = (ActivityNode) nodeMap.get(target.getAttributeValue("id"));
                workflow.addRule(new WorkflowRule(2, sourceNode, targetNode, conclusion, processStatus));
            }

            // Task -> ExclusiveGateway
            if ((source instanceof Task) && (target instanceof ExclusiveGateway exclusiveGateway)) {
                var incomings = exclusiveGateway.getIncoming();
                var outgoings = exclusiveGateway.getOutgoing();
                int totalIncomings = incomings.size();
                int totalOutgoings = outgoings.size();
                if (totalIncomings == 1) {
                    // Task -> Split: ignore
                }
                if (totalOutgoings == 1) {
                    // Merge
                    SequenceFlow outgoingEdge = outgoings.iterator().next();
                    FlowNode ruleTarget = outgoingEdge.getTarget();
                    ActivityNode sourceNode = (ActivityNode) nodeMap.get(source.getAttributeValue("id"));
                    if (ruleTarget instanceof EndEvent) {
                        // Rule 3: Task -> Merge -> EndEvent = Final rule A
                        Map<String, String> endEventAttributes = getAttributes(ruleTarget);
                        String endProcessStatus = endEventAttributes.get("process_status");
                        if (endProcessStatus != null && !endProcessStatus.isEmpty())
                            workflow.addRule(new WorkflowRule(3, sourceNode, null, conclusion, endProcessStatus));
                    }
                    if (ruleTarget instanceof Task) {
                        ActivityNode targetNode = (ActivityNode) nodeMap.get(ruleTarget.getAttributeValue("id"));
                        // Rule 4: Task -> Merge -> Task
                        workflow.addRule(new WorkflowRule(4, sourceNode, targetNode, conclusion, processStatus));
                    }
                }
            }

            // Rule 5: ExclusiveGateway -> Task
            if ((source instanceof ExclusiveGateway exclusiveGateway) && (target instanceof Task)) {
                var incomings = exclusiveGateway.getIncoming();
                int totalIncomings = incomings.size();
                if (totalIncomings == 1) {
                    // Split -> Task
                    SequenceFlow incomingEdge = incomings.iterator().next();
                    FlowNode ruleSource = incomingEdge.getSource();
                    ActivityNode sourceNode = (ActivityNode) nodeMap.get(ruleSource.getAttributeValue("id"));
                    ActivityNode targetNode = (ActivityNode) nodeMap.get(target.getAttributeValue("id"));
                    if (conclusion != null) {
                        workflow.addRule(new WorkflowRule(5, sourceNode, targetNode, conclusion, processStatus));
                    }
                }
                // Merge -> Task: ignore
            }
            // Rule 6: Split -> Merge
            if ((source instanceof ExclusiveGateway exclusiveGatewaySource) && (target instanceof ExclusiveGateway exclusiveGatewayTarget)) {
                var incomings = exclusiveGatewaySource.getIncoming();
                int totalIncomings = incomings.size();
                var outgoings = exclusiveGatewayTarget.getOutgoing();
                int totalOutgoings = outgoings.size();
                //  Split -> Merge
                if ((totalIncomings == 1) && (totalOutgoings == 1)) {
                    SequenceFlow incomingEdge = incomings.iterator().next();
                    FlowNode ruleSource = incomingEdge.getSource();
                    SequenceFlow outgoingEdge = outgoings.iterator().next();
                    FlowNode ruleTarget = outgoingEdge.getTarget();
                    ActivityNode sourceNode = (ActivityNode) nodeMap.get(ruleSource.getAttributeValue("id"));
                    ActivityNode targetNode = (ActivityNode) nodeMap.get(ruleTarget.getAttributeValue("id"));
                    workflow.addRule(new WorkflowRule(6, sourceNode, targetNode, conclusion, processStatus));
                }
            }
            // Rule 7: Task -> EndEvent = Final rule B
            if ((source instanceof Task) && (target instanceof EndEvent)) {
                Map<String, String> endEventAttributes = getAttributes(target);
                String endProcessStatus = endEventAttributes.get("process_status");
                ActivityNode sourceNode = (ActivityNode) nodeMap.get(source.getAttributeValue("id"));
                if (endProcessStatus != null && !endProcessStatus.isEmpty())
                    workflow.addRule(new WorkflowRule(7, sourceNode, null, conclusion, endProcessStatus));
            }
            // Rule 8: Task -> Split -> EndEvent = Final rule C
            if ((source instanceof ExclusiveGateway exclusiveGateway) && (target instanceof EndEvent)) {
                var incomings = exclusiveGateway.getIncoming();
                int totalIncomings = incomings.size();
                if (totalIncomings == 1) {
                    // Split
                    SequenceFlow incomingEdge = incomings.iterator().next();
                    FlowNode ruleSource = incomingEdge.getSource();
                    ActivityNode sourceNode = (ActivityNode) nodeMap.get(ruleSource.getAttributeValue("id"));
                    if (ruleSource instanceof Task) {
                        // Task -> Split -> End
                        Map<String, String> endEventAttributes = getAttributes(target);
                        String endProcessStatus = endEventAttributes.get("process_status");
                        if ((endProcessStatus != null && !endProcessStatus.isEmpty()) && (conclusion != null)) {
                            workflow.addRule(new WorkflowRule(8, sourceNode, null, conclusion, endProcessStatus));
                        }
                    }
                }
            }
        }
        return workflow;
    }

    private static Map<String, String> getAttributes(BaseElement baseElement) {
        Map<String, String> attributes = new HashMap<String, String>();
        ExtensionElements extensionElements = baseElement.getExtensionElements();
        if (nonNull(extensionElements)) for (ModelElementInstance elementInstance : extensionElements.getElements())
            if (elementInstance instanceof CamundaProperties camundaProperties)
                for (CamundaProperty camundaProperty : camundaProperties.getCamundaProperties())
                    attributes.put(camundaProperty.getCamundaName(), camundaProperty.getCamundaValue());
        return attributes;
    }
}
