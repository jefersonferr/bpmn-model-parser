package org.bpmnparser;

import org.bpmnparser.model.Workflow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.bpmnparser.util.ModelParser.parser;
import static org.junit.jupiter.api.Assertions.*;

class ModelParserTest {
    static Workflow workflow;
    static String configPath;
    InputStream modelStream = null;

    @Test
    @DisplayName("Test 01 - Model 01: Consistent")
    void test01() {
        try {
            modelStream = Files.newInputStream(Path.of("src/test/resources/models/model_01.bpmn"));
            configPath = "src/test/resources/config/test_config_01.yaml";
            workflow = parser(modelStream,configPath);
            assertAll(
                    "Grouped Assertions of Model 01",
                    () -> assertEquals(0, workflow.stagesSize()),
                    () -> assertEquals(0, workflow.inconsistenciesSize()),
                    () -> assertEquals(2, workflow.activitiesSize()),
                    () -> assertEquals(3, workflow.rulesSize())
            );
            System.out.println("Model 01: "+workflow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test 02 - Rule 1: StartEvent -> Task = Initial rule")
    void test02() {
        // Rule 1 - StartEvent -> Task = Initial rule
        var workflowRule = workflow.getRules().get(0);
        assertAll(
                "Grouped Assertions of Rule 1",
                () -> assertNull(workflowRule.getSource()),
                () -> assertNotNull(workflowRule.getTarget()),
                () -> assertNull(workflowRule.getConclusion()),
                () -> assertNotNull(workflowRule.getProcessStatus())
        );
    }

    @Test
    @DisplayName("Test 03 - Rule 2: Task -> Task")
    void test03() {
        // Rule 7: Task -> EndEvent = Final rule B
        var workflowRule = workflow.getRules().get(workflow.getRules().size() - 1);
        assertAll(
                "Grouped Assertions of Rule 2",
                () -> assertNotNull(workflowRule.getSource()),
                () -> assertNotNull(workflowRule.getTarget()),
                () -> assertNull(workflowRule.getConclusion()),
                () -> assertNull(workflowRule.getProcessStatus())
        );
    }

    @Test
    @DisplayName("Test 04 - Rule 7: Final rule B")
    void test04() {
        // Rule 7: Task -> EndEvent = Final rule B
        var workflowRule = workflow.getRules().get(1);
        assertAll(
                "Grouped Assertions of Rule 7",
                () -> assertNotNull(workflowRule.getSource()),
                () -> assertNull(workflowRule.getTarget()),
                () -> assertNull(workflowRule.getConclusion()),
                () -> assertNotNull(workflowRule.getProcessStatus())
        );
    }

    @Test
    @DisplayName("Test 05 - Model 02: Inconsistencies 10,11, and 12")
    void test05() {
        try {
            modelStream = Files.newInputStream(Path.of("src/test/resources/models/model_02.bpmn"));
            configPath = "src/test/resources/config/test_config_01.yaml";
            workflow = parser(modelStream,configPath);
            assertAll(
                    "Grouped Assertions of Model 02",
                    () -> assertEquals(0, workflow.stagesSize()),
                    () -> assertEquals(4, workflow.inconsistenciesSize()),
                    () -> assertEquals(0, workflow.activitiesSize()),
                    () -> assertEquals(0, workflow.rulesSize())
            );
            System.out.println("Model 02: "+workflow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test 06 - Model 03: Consistent")
    void test06() {
        try {
            modelStream = Files.newInputStream(Path.of("src/test/resources/models/model_03.bpmn"));
            configPath = "src/test/resources/config/test_config_01.yaml";
            workflow = parser(modelStream,configPath);
            assertAll(
                    "Grouped Assertions of Model 03",
                    () -> assertEquals(0, workflow.stagesSize()),
                    () -> assertEquals(0, workflow.inconsistenciesSize()),
                    () -> assertEquals(2, workflow.activitiesSize()),
                    () -> assertEquals(5, workflow.rulesSize())
            );
            System.out.println("Model 03: "+workflow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test 07 - Rule 5: Split -> Task")
    void test07() {
        // Rule 5: Split -> Task
        var workflowRule = workflow.getRules().get(2);
        assertAll(
                "Grouped Assertions of Rule 5",
                () -> assertNotNull(workflowRule.getSource()),
                () -> assertNotNull(workflowRule.getTarget()),
                () -> assertNotNull(workflowRule.getConclusion()),
                () -> assertNull(workflowRule.getProcessStatus())
        );
    }

    @Test
    @DisplayName("Test 08 - Rule 8: Final rule C")
    void test08() {
        // Rule 8: Task -> Split -> EndEvent = Final rule C
        var workflowRule = workflow.getRules().get(4);
        assertAll(
                "Grouped Assertions of Rule 8",
                () -> assertNotNull(workflowRule.getSource()),
                () -> assertNull(workflowRule.getTarget()),
                () -> assertNotNull(workflowRule.getConclusion()),
                () -> assertNotNull(workflowRule.getProcessStatus())
        );
    }

    @Test
    @DisplayName("Test 09 - Model 04: Inconsistency 14")
    void test09() {
        try {
            modelStream = Files.newInputStream(Path.of("src/test/resources/models/model_04.bpmn"));
            configPath = "src/test/resources/config/test_config_01.yaml";
            workflow = parser(modelStream,configPath);
            assertAll(
                    "Grouped Assertions of Model 04",
                    () -> assertEquals(0, workflow.stagesSize()),
                    () -> assertEquals(3, workflow.inconsistenciesSize()),
                    () -> assertEquals(2, workflow.activitiesSize()),
                    () -> assertEquals(2, workflow.rulesSize())
            );
            System.out.println("Model 04: "+workflow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test 10 - Model 05: Inconsistencies 1,3,5,6,101,102")
    void test10() {
        try {
            modelStream = Files.newInputStream(Path.of("src/test/resources/models/model_05.bpmn"));
            configPath = "src/test/resources/config/test_config_02.yaml";
            workflow = parser(modelStream,configPath);
            assertAll(
                    "Grouped Assertions of Model 05",
                    () -> assertEquals(0, workflow.stagesSize()),
                    () -> assertEquals(6, workflow.inconsistenciesSize()),
                    () -> assertEquals(2, workflow.activitiesSize()),
                    () -> assertEquals(5, workflow.rulesSize())
            );
            System.out.println("Model 05: "+workflow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test 11 - Model 06: Inconsistencies 7,8,9,13")
    void test11() {
        try {
            modelStream = Files.newInputStream(Path.of("src/test/resources/models/model_06.bpmn"));
            configPath = "src/test/resources/config/test_config_02.yaml";
            workflow = parser(modelStream,configPath);
            assertAll(
                    "Grouped Assertions of Model 06",
                    () -> assertEquals(0, workflow.stagesSize()),
                    () -> assertEquals(8, workflow.inconsistenciesSize()),
                    () -> assertEquals(1, workflow.activitiesSize()),
                    () -> assertEquals(4, workflow.rulesSize())
            );
            System.out.println("Model 06: "+workflow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test 12 - Model 07: Testing the config file yaml by relaxing the requirements")
    void test12() {
        try {
            modelStream = Files.newInputStream(Path.of("src/test/resources/models/model_07.bpmn"));
            configPath = "src/test/resources/config/test_config_03.yaml";
            workflow = parser(modelStream,configPath);
            assertAll(
                    "Grouped Assertions of Model 07",
                    () -> assertEquals(0, workflow.stagesSize()),
                    () -> assertEquals(2, workflow.inconsistenciesSize()),
                    () -> assertEquals(2, workflow.activitiesSize()),
                    () -> assertEquals(5, workflow.rulesSize())
            );
            System.out.println("Model 07: "+workflow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test 13 - Model 08: Rule 04: Task -> Merge -> End, and Rule 06: Split -> Merge")
    void test13() {
        try {
            modelStream = Files.newInputStream(Path.of("src/test/resources/models/model_08.bpmn"));
            configPath = "src/test/resources/config/test_config_03.yaml";
            workflow = parser(modelStream,configPath);
            assertAll(
                    "Grouped Assertions of Model 08",
                    () -> assertEquals(7, workflow.stagesSize()),
                    () -> assertEquals(2, workflow.inconsistenciesSize()),
                    () -> assertEquals(14, workflow.activitiesSize()),
                    () -> assertEquals(32, workflow.rulesSize())
            );
            System.out.println("Model 08: "+workflow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test 14 - Model 09: Rule 03")
    void test14() {
        try {
            modelStream = Files.newInputStream(Path.of("src/test/resources/models/model_09.bpmn"));
            configPath = "src/test/resources/config/test_config_01.yaml";
            workflow = parser(modelStream,configPath);
            assertAll(
                    "Grouped Assertions of Model 09",
                    () -> assertEquals(3, workflow.stagesSize()),
                    () -> assertEquals(2, workflow.inconsistenciesSize()),
                    () -> assertEquals(4, workflow.activitiesSize()),
                    () -> assertEquals(7, workflow.rulesSize())
            );
            System.out.println("Model 09: "+workflow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}