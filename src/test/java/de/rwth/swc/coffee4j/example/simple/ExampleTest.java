package de.rwth.swc.coffee4j.example.simple;

import de.rwth.swc.coffee4j.algorithmic.constraint.MinimalForbiddenTuplesCheckerFactory;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.Ipog;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm.IpogNeg;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.configuration.ConfigureIpogNeg;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;

import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintBuilder.constrain;

public class ExampleTest {

    @SuppressWarnings("unused")
    private static InputParameterModel model() {
        return inputParameterModel("example-model")
                .positiveTestingStrength(1)
                .negativeTestingStrength(0)
                .parameters(
                        parameter("Packages").values(1, 3, null),
                        parameter("Country").values("UK", "USA", "GER", null),
                        parameter("Phone").values("+49", "+44", "+1", null),
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
                                .withName("invalid-country-phone")
                                .by((String country, String phone) -> {
                                    if("UK".equals(country) && "+1".equals(phone)) return false;
                                    if("UK".equals(country) && "+49".equals(phone)) return false;
                                    if("USA".equals(country) && "+49".equals(phone)) return false;
                                    if("USA".equals(country) && "+44".equals(phone)) return false;
                                    if("GER".equals(country) && "+1".equals(phone)) return false;
                                    if("GER".equals(country) && "+44".equals(phone)) return false;

                                    return true;
                                })
                ).build();
    }

    @CombinatorialTest(inputParameterModel = "model")
    @EnableGeneration(algorithms = { Ipog.class, IpogNeg.class })
    @ConfigureIpogNeg(constraintCheckerFactory = MinimalForbiddenTuplesCheckerFactory.class)
    void test(
            @InputParameter("Packages") Integer packages,
            @InputParameter("Country") String country,
            @InputParameter("Phone") String phone,
            @InputParameter("Shipping") String shipping) {
        // stimulate the SUT and check its behavior
    }
}
