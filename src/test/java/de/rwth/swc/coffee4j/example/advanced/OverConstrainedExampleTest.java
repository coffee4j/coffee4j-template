package de.rwth.swc.coffee4j.example.advanced;

import de.rwth.swc.coffee4j.algorithmic.constraint.DiagnosticConstraintCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm.IpogNeg;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.diagnosis.EnableConflictDetection;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.configuration.ConfigureIpogNeg;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;

import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintBuilder.constrain;

public class OverConstrainedExampleTest {

    @SuppressWarnings("unused")
    private static InputParameterModel model() {
        return inputParameterModel("example-model")
                .positiveTestingStrength(1)
                .negativeTestingStrength(0)
                .parameters(
                        parameter("Packages").values(1, 3, null),
                        parameter("Country").values("UK", "USA", null),
                        parameter("Phone").values("+44", "+1", null),
                        parameter("Shipping").values("Standard", "Express")
                ).errorConstraints(
                        constrain("Packages")
                                .withName("invalid-packages")
                                .by((Integer packages) -> packages != null),
                        constrain("Country")
                                .withName("invalid-country")
                                .by((String country) -> country != null),
                        constrain("Phone")
                                .withName("invalid-phone")
                                .by((String phone) -> phone != null),
                        constrain("Country", "Phone")
                                .withName("invalid-country-phone-1")
                                .by((String country, String phone) ->
                                    !("UK".equals(country) && "+1".equals(phone)) &&
                                    !("UK".equals(country) && null == phone)
                        ),
                        constrain("Country", "Phone")
                                .withName("invalid-country-phone-2")
                                .by((String country, String phone) ->
                                    !("USA".equals(country) && "+44".equals(phone)) &&
                                    !("USA".equals(country) && null == phone)
                        )
                ).build();
    }

    @CombinatorialTest(inputParameterModel = "model")
    @EnableGeneration(algorithms = {IpogNeg.class })
    @ConfigureIpogNeg(constraintCheckerFactory = DiagnosticConstraintCheckerFactory.class)
    @EnableConflictDetection(shouldAbort = false, explainConflicts = true, diagnoseConflicts = true)
    void test(
            @InputParameter("Packages") Integer packages,
            @InputParameter("Country") String country,
            @InputParameter("Phone") String phone,
            @InputParameter("Shipping") String shipping) {

    }
}
