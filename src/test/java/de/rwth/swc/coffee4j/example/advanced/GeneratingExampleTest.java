package de.rwth.swc.coffee4j.example.advanced;

import de.rwth.swc.coffee4j.algorithmic.ErrorConstraintException;
import de.rwth.swc.coffee4j.algorithmic.classification.IsolatingClassificationStrategy;
import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesChecker;
import de.rwth.swc.coffee4j.algorithmic.interleaving.feedback.DefaultFeedbackCheckingStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.generator.aetg.AetgStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.identification.trt.TupleRelationshipStrategy;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.DefaultGeneratingInterleavingManager;
import de.rwth.swc.coffee4j.algorithmic.interleaving.manager.DefaultInterleavingManager;
import de.rwth.swc.coffee4j.engine.configuration.execution.InterleavingExecutionConfiguration;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.engine.process.report.interleaving.LoggingInterleavingExecutionReporterForGenerationJava;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.constraintgenerator.interleaving.EnableInterleavingConstraintGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.interleaving.EnableInterleavingGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import de.rwth.swc.coffee4j.junit.engine.result.ExceptionalValueResult;

import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.example.advanced.GeneratingExampleTest.SystemUnderTest.processOrder;

public class GeneratingExampleTest {

    private static InputParameterModel model() {
        return inputParameterModel("ordering-web-service-model")
                .positiveTestingStrength(2)
                .negativeTestingStrength(0)
                .parameters(
                        parameter("Packages").values(1, 3, null),
                        parameter("Country").values("UK", "USA", null),
                        parameter("Phone").values("+44", "+1", null),
                        parameter("Shipping").values("Standard", "Express")
//
// These error-constraints will be automaticaly generated:
//
//                ).errorConstraints(
//                        constrain("Packages")
//                                .withName("invalid-packages")
//                                .by((Integer packages) -> packages != null),
//                        constrain("Country")
//                                .withName("invalid-country")
//                                .by((String country) -> country != null),
//                        constrain("Phone")
//                                .withName("invalid-phone")
//                                .by((String phone) -> phone != null),
//                        constrain("Country", "Phone")
//                                .withName("invalid-country-phone")
//                                .by((String country, String phone) -> {
//                                    if ("UK".equals(country) && "+1".equals(phone)) return false;
//                                    if ("UK".equals(country) && "+49".equals(phone)) return false;
//                                    if ("USA".equals(country) && "+49".equals(phone)) return false;
//                                    if ("USA".equals(country) && "+44".equals(phone)) return false;
//                                    if ("GER".equals(country) && "+1".equals(phone)) return false;
//                                    if ("GER".equals(country) && "+44".equals(phone)) return false;
//
//                                    return true;
//                                })
//
                ).build();
    }

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

    private static InterleavingExecutionConfiguration testingConfiguration() {
        return InterleavingExecutionConfiguration.executionConfiguration()
                .managerFactory(DefaultInterleavingManager.managerFactory())
                .testInputGenerationStrategyFactory(AetgStrategy.aetgStrategy())
                .identificationStrategyFactory(TupleRelationshipStrategy.tupleRelationshipStrategy())
                .feedbackCheckingStrategyFactory(DefaultFeedbackCheckingStrategy.defaultCheckingStrategy())
                .constraintCheckingFactory(MinimalForbiddenTuplesChecker.minimalForbiddenTuplesChecker())
                .build();
    }

    @CombinatorialTest(inputParameterModel = "model")
    @EnableGeneration
    @EnableInterleavingGeneration("testingConfiguration")
    @EnableInterleavingConstraintGeneration("generationConfiguration")
    void test(
            @InputParameter("Packages") Integer packages,
            @InputParameter("Country") String country,
            @InputParameter("Phone") String phone,
            @InputParameter("Shipping") String shipping) {

        final String result = processOrder(packages, country, phone, ShippingType.valueOf(shipping));

        System.out.println(result);

        if (!"SUCCESS".equals(result)) {
            throw new ErrorConstraintException(new ExceptionalValueResult.ExceptionalValueException(result));
        }
    }

    enum ShippingType {Standard, Express}

    static class SystemUnderTest {

        static String processOrder(Integer packages, String country, String phone, ShippingType shippingType) {
            if (isInvalidNumberofPackages(packages)) return "invalid-packages";
            if (isInvalidCountry(country)) return "invalid-country";
            if (isInvalidPhoneNumber(phone)) return "invalid-phone";
            if (isInvalidCountryCode(country, phone)) return "invalid-country-phone";

            return "SUCCESS";
        }

        private static boolean isInvalidNumberofPackages(Integer packages) {
            return packages == null;
        }

        private static boolean isInvalidCountry(String country) {
            return country == null;
        }

        private static boolean isInvalidPhoneNumber(String phone) {
            return phone == null;
        }

        private static boolean isInvalidCountryCode(String country, String phone) {
            return !("GER".equals(country) && "+49".equals(phone))
                    && !("USA".equals(country) && "+1".equals(phone))
                    && !("UK".equals(country) && "+44".equals(phone));
        }
    }
}
