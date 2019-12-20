package de.rwth.swc.coffee4j.example;

import de.rwth.swc.coffee4j.engine.constraint.HardConstraintCheckerFactory;
import de.rwth.swc.coffee4j.engine.generator.ipog.Ipog;
import de.rwth.swc.coffee4j.engine.generator.ipogneg.IpogNeg;
import de.rwth.swc.coffee4j.junit.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.provider.configuration.generator.Generator;
import de.rwth.swc.coffee4j.junit.provider.model.ModelFromMethod;
import de.rwth.swc.coffee4j.model.InputParameterModel;

import static de.rwth.swc.coffee4j.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.model.constraints.ConstraintBuilder.constrain;

class GreetingsTest {

    private static InputParameterModel model() {
        return inputParameterModel("example-model")
                .strength(2)
                .parameters(
                        parameter("Title").values("Mr", "Mrs"),
                        parameter("FirstName").values("John", "Jane"),
                        parameter("LastName").values("Doo", "Foo")
                ).errorConstraints(
                        constrain("Title", "FirstName").by((String title, String firstName) -> {
                            if (title.equals("Mr") && firstName.equals("Jane")) return false;
                            if (title.equals("Mrs") && firstName.equals("John")) return false;
                            return true;
                        })
                ).build();
    }

    @CombinatorialTest
    @Generator(
            algorithms = {Ipog.class},
            factories = {HardConstraintCheckerFactory.class})
    @ModelFromMethod("model")
    void testGreetingsPositive(String title, String firstName, String lastName) {
        System.out.println("Hello " + title + " " + firstName + " " + lastName);
    }
}
