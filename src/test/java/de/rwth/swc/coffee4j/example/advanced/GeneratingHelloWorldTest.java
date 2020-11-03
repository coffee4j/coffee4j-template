package de.rwth.swc.coffee4j.example.advanced;

import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesChecker;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import de.rwth.swc.coffee4j.junit.engine.result.DefaultResultWrapper;
import de.rwth.swc.coffee4j.junit.engine.result.ExecutionResult;
import de.rwth.swc.coffee4j.junit.engine.result.ResultValidator;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.classification.EnableClassification;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.interleaving.EnableInterleavingConstraintGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.EnableInterleavingGeneration;
import de.rwth.swc.coffee4j.algorithmic.classification.IsolatingClassificationStrategy;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.algorithmic.interleaving.feedback.DefaultFeedbackCheckingStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.aetg.AetgStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.trt.TupleRelationshipStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.DefaultGeneratingInterleavingManager;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.DefaultInterleavingManager;
import de.rwth.swc.coffee4j.engine.process.report.interleaving.LoggingInterleavingExecutionReporterForGenerationJava;

import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;

public class GeneratingHelloWorldTest {

    @SuppressWarnings("unused")
    public static InputParameterModel.Builder model() {
        return inputParameterModel("generating-hello-world-model")
                .positiveTestingStrength(2)
                .parameters(
                        parameter("Title").values("Mr", "Mrs", ""),
                        parameter("Name").values("John", "Jane", ""),
                        parameter("Family Name").values("Doe", "Foo", ""));
    }

    @SuppressWarnings("unused")
    private static InterleavingExecutionConfiguration generationConfiguration() {
        return InterleavingExecutionConfiguration.generatingExecutionConfiguration()
                .managerFactory(DefaultGeneratingInterleavingManager.managerFactory())
                .testInputGenerationStrategyFactory(AetgStrategy.aetgStrategy())
                .identificationStrategyFactory(TupleRelationshipStrategy.tupleRelationshipStrategy())
                .feedbackCheckingStrategyFactory(DefaultFeedbackCheckingStrategy.defaultCheckingStrategy())
                .classificationStrategyFactory(IsolatingClassificationStrategy.isolatingClassificationStrategy())
                .constraintCheckingFactory(MinimalForbiddenTuplesChecker.minimalForbiddenTuplesChecker())
                .executionReporter(new LoggingInterleavingExecutionReporterForGenerationJava())
                .build();
    }

    @SuppressWarnings("unused")
    private static InterleavingExecutionConfiguration testingConfiguration() {
        return InterleavingExecutionConfiguration.executionConfiguration()
                .managerFactory(DefaultInterleavingManager.managerFactory())
                .testInputGenerationStrategyFactory(AetgStrategy.aetgStrategy())
                .identificationStrategyFactory(TupleRelationshipStrategy.tupleRelationshipStrategy())
                .feedbackCheckingStrategyFactory(DefaultFeedbackCheckingStrategy.defaultCheckingStrategy())
                .constraintCheckingFactory(MinimalForbiddenTuplesChecker.minimalForbiddenTuplesChecker())
                .build();
    }

    @CombinatorialTest
    @EnableInterleavingGeneration("testingConfiguration")
    @EnableInterleavingConstraintGeneration("generationConfiguration")
    @EnableClassification
    public void checkAddress(@InputParameter("Title") String title,
                             @InputParameter("Name") String name,
                             @InputParameter("Family Name") String familyName) throws NoSuchMethodException {
        ResultValidator validator = new ResultValidator();
        DefaultResultWrapper resultMapper = new DefaultResultWrapper(this.getClass().getMethod("checkAddress", String.class, String.class, String.class));

        System system = new System();
        Oracle oracle = new Oracle();

        ExecutionResult result = resultMapper.runTestFunction(
                () -> system.check(title, name, familyName)
        );

        ExecutionResult expectedResult = resultMapper.runTestFunction(
                () -> oracle.check(title, name, familyName)
        );

        validator.check(result, expectedResult);
    }

    static class EmptyValueException extends Exception {

        EmptyValueException() {
            super("value must not be empty!");
        }
    }

    static class InvalidCombinationException1 extends Exception {

        InvalidCombinationException1() {
            super("invalid combination!");
        }
    }

    static class InvalidCombinationException2 extends Exception {

        InvalidCombinationException2() {
            super("invalid combination!");
        }
    }

    private static class System {

        public boolean check(String title, String name, String familyName) throws Exception {
            if (title.equals("") || name.equals("") || familyName.equals("")) {
                throw new EmptyValueException();
            } else if (title.equals("Mr") && name.equals("Jane")) {
                throw new InvalidCombinationException1();
            } else if (title.equals("Mrs") && name.equals("John")) {
                throw new InvalidCombinationException2();
            }

            return true;
        }
    }

    public static class Oracle {

        public boolean check(String title, String name, String familyName) throws Exception {
            if (title.equals("") || name.equals("") || familyName.equals("")) {
                throw new EmptyValueException();
            } else if (title.equals("Mr") && name.equals("Jane")) {
                throw new InvalidCombinationException1();
            } else if (title.equals("Mrs") && name.equals("John")) {
                throw new InvalidCombinationException2();
            }

            return true;
        }
    }
}
