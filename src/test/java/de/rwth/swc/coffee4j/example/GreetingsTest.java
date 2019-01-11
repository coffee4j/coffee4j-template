package de.rwth.swc.coffee4j.example;

import de.rwth.swc.coffee4j.junit.CombinatorialTest;
import de.rwth.swc.coffee4j.junit.provider.model.ModelFromMethod;
import de.rwth.swc.coffee4j.model.InputParameterModel;

import static de.rwth.swc.coffee4j.model.InputParameterModel.inputParameterModel;
import static de.rwth.swc.coffee4j.model.Parameter.parameter;

class GreetingsTest {

  private static InputParameterModel model() {
    return inputParameterModel("example-model")
      .strength(2)
      .parameters(
        parameter("Title").values("Mr", "Mrs"),
        parameter("FirstName").values("John", "Jane"),
        parameter("LastName").values("Doo", "Foo")
    ).build();
  }

  @CombinatorialTest
  @ModelFromMethod("model")
  void testGreetings(String title, String firstName, String lastName) {
    System.out.println("Hello " + title + " " + firstName + " " + lastName);
  }
}
