package org.bpmnparser.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ActivityNode extends Node {

    String stageCode;
    String activityCode;
    String abbreviation;
    List<Conclusion> conclusions = new ArrayList<>();

    public ActivityNode(String stageCode, String activityCode, String name, String documentation) {
        super(name, documentation);
        this.stageCode = stageCode;
        this.activityCode = activityCode;
        this.abbreviation = stageCode+"-"+activityCode;
    }

    @Override
    public String toString() {
        return "ActivityNode{" +
                "abbreviation='" + abbreviation + '\'' +
                ", name='" + name + '\'' +
                ", conclusions=" + conclusions +
                '}';
    }

    public void addConclusion(Conclusion conclusion) {
        conclusions.add(conclusion);
    }

}
