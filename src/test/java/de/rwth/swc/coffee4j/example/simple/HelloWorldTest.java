package de.rwth.swc.coffee4j.example.simple;

import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintBuilder.constrain;

import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.Ipog;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HelloWorldTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldTest.class);

    @SuppressWarnings("unused")
    private static InputParameterModel model() {
        return inputParameterModel("hello-world-model")
                .positiveTestingStrength(1)
                .parameters(
                        parameter("Title").values("Mr", "Mrs"),
                        parameter("FirstName").values("John", "Jane"),
                        parameter("LastName").values("Doo", "Foo")
                ).build();
    }

    @CombinatorialTest(inputParameterModel = "model")
    @EnableGeneration(algorithms = { Ipog.class })
    void test(
            @InputParameter("Title") String title,
            @InputParameter("FirstName") String firstName,
            @InputParameter("LastName") String lastName) {
        LOG.info("Hello {} {} {}", title, firstName, lastName);
    }
}
