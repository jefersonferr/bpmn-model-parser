package me.bpmnparser.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Workflow {

    String name;
    String id;
    String version;
    String documentation;
    String type;
    String subtype;
    List<Stage> stages = new ArrayList<>();
    List<ActivityNode> activities = new ArrayList<>();
    List<Inconsistency> inconsistencies = new ArrayList<>();
    List<WorkflowRule> rules = new ArrayList<>();

    public Workflow(String name, String id, String version, String documentation, String type, String subtype) {
        this.name = name;
        this.id = id;
        this.version = version;
        this.documentation = documentation;
        this.type = type;
        this.subtype = subtype;
    }

    @Override
    public String toString() {
        return "Workflow{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", documentation='" + documentation + '\'' +
                ", type='" + type + '\'' +
                ", subtype='" + subtype + '\'' +
                ", stages=" + stages +
                ", activities=" + activities +
                ", inconsistencies=" + inconsistencies +
                ", rules=" + rules +
                '}';
    }

    public void addStage(Stage stage) {
        stages.add(stage);
    }

    public int stagesSize() {
        return stages.size();
    }

    public void addActivity(ActivityNode activityNode) {
        activities.add(activityNode);
    }

    public int activitiesSize() {
        return activities.size();
    }

    public void addInconsistency(Inconsistency inconsistency) {
        inconsistencies.add(inconsistency);
    }

    public int inconsistenciesSize() {
        return inconsistencies.size();
    }

    public void addRule(WorkflowRule workflowRule) {
        rules.add(workflowRule);
    }

    public int rulesSize() {
        return rules.size();
    }

}