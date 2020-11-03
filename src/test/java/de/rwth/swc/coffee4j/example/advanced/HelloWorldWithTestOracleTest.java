package de.rwth.swc.coffee4j.example.advanced;

import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipog.Ipog;
import de.rwth.swc.coffee4j.algorithmic.sequential.generator.ipogneg.algorithm.IpogNeg;
import de.rwth.swc.coffee4j.algorithmic.util.Preconditions;
import de.rwth.swc.coffee4j.engine.configuration.model.Combination;
import de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel;
import de.rwth.swc.coffee4j.junit.engine.annotation.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.characterization.EnableFaultCharacterization;
import de.rwth.swc.coffee4j.junit.engine.annotation.configuration.sequential.generation.EnableGeneration;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.combination.InputCombination;
import de.rwth.swc.coffee4j.junit.engine.annotation.parameter.parameter.InputParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.rwth.swc.coffee4j.engine.configuration.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.engine.configuration.model.Parameter.parameter;
import static de.rwth.swc.coffee4j.engine.configuration.model.constraints.ConstraintBuilder.constrain;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelloWorldWithTestOracleTest {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldWithTestOracleTest.class);

    @SuppressWarnings("unused")
    private static InputParameterModel model() {
        return inputParameterModel("hello-world-model")
                .positiveTestingStrength(2)
                .negativeTestingStrength(1)
                .parameters(
                        parameter("Title").values("Mr", "Mrs", "123"),
                        parameter("FirstName").values("John", "Jane", "123"),
                        parameter("LastName").values("Doe", "Foo", "123")
                ).errorConstraints(
                        constrain("Title")
                                .withName("INVALID_TITLE")
                                .by((String title)
                                        -> !title.equalsIgnoreCase("123")),
                        constrain("FirstName")
                                .withName("INVALID_FIRST_NAME")
                                .by((String firstName)
                                        -> !firstName.equalsIgnoreCase("123")),
                        constrain("LastName")
                                .withName("INVALID_LAST_NAME")
                                .by((String lastName)
                                        -> !lastName.equalsIgnoreCase("123")),
                        constrain("Title", "FirstName")
                                .withName("INVALID_GREETING")
                                .by((String title, String firstName)
                                        -> !(title.equals("Mr") && firstName.equals("Jane"))
                                        && !(title.equals("Mrs") && firstName.equals("John")))
                ).build();
    }

    private final ErrorConstraintNameBasedTestOracle testOracle = new ErrorConstraintNameBasedTestOracle(model());

    @CombinatorialTest(inputParameterModel = "model")
    @EnableGeneration(algorithms = {Ipog.class, IpogNeg.class})
    @EnableFaultCharacterization
    void test(
            @InputParameter("Title") String title,
            @InputParameter("FirstName") String firstName,
            @InputParameter("LastName") String lastName,
            @InputCombination Combination combination) {
        final SystemUnderTest sut = new SystemUnderTest();

        try {
            final String greeting = sut.sayHello(title, firstName, lastName);
            LOG.info("succeeded with {}", greeting);

            assertTrue(testOracle.validateNormal(combination));

        } catch (IllegalArgumentException exception) {
            final String errorCode = exception.getMessage();
            LOG.info("failed with error code {}", errorCode);

            assertTrue(testOracle.validateExceptional(combination, errorCode));
        }
    }

    private static class ErrorConstraintNameBasedTestOracle {

        private final InputParameterModel inputParameterModel;

        public ErrorConstraintNameBasedTestOracle(InputParameterModel inputParameterModel) {
            this.inputParameterModel = inputParameterModel;
        }

        public boolean validateNormal(Combination combination) {
            return inputParameterModel.getErrorConstraints()
                    .stream()
                    .allMatch(errorConstraint -> errorConstraint.checkIfValid(combination));
        }

        public boolean validateExceptional(Combination combination, String result) {
            return inputParameterModel.getErrorConstraints()
                    .stream()
                    .filter(errorConstraint -> !errorConstraint.checkIfValid(combination))
                    .anyMatch(errorConstraint -> errorConstraint.getName().equals(result));
        }
    }

    private static class SystemUnderTest {

        public String sayHello(String title, String firstName, String lastName) {
            Preconditions.check(isValidTitle(title), "INVALID_TITLE");
            Preconditions.check(isValidFirstName(firstName), "INVALID_FIRST_NAME");
            Preconditions.check(isValidLastName(lastName), "INVALID_LAST_NAME");
            Preconditions.check(isValidGreeting(title, firstName), "INVALID_GREETING");

            return "Hello " + title + " " + firstName + " " + lastName;
        }

        private boolean isValidTitle(String title) {
            return "Mr".equalsIgnoreCase(title) || "Mrs".equalsIgnoreCase(title);
        }

        private boolean isValidFirstName(String firstName) {
            return "John".equalsIgnoreCase(firstName) || "Jane".equalsIgnoreCase(firstName);
        }

        private boolean isValidLastName(String lastName) {
            return "Doe".equalsIgnoreCase(lastName) || "Foo".equalsIgnoreCase(lastName);
        }

        private boolean isValidGreeting(String title, String firstName) {
            return "Mr".equalsIgnoreCase(title) && "John".equalsIgnoreCase(firstName) ||
                    "Mrs".equalsIgnoreCase(title) // Fault, Forgotten:  && "Jane".equalsIgnoreCase(firstName)
                    ;
        }
    }
}
