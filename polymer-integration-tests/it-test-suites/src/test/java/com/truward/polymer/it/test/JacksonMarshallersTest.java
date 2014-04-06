package com.truward.polymer.it.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truward.polymer.generated.jackson.JacksonMarshallers;
import com.truward.polymer.generated.model.UserImpl;
import com.truward.polymer.it.model.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests generated marshaller
 *
 * @author Alexander Shabanov
 */
public final class JacksonMarshallersTest {
  private final ObjectMapper mapper = new ObjectMapper();

  private final String userJson = "{ \"id\": 1, \"name\": \"bob\", \"age\": 10 }";
  private final User user = UserImpl.newBuilder()
      .setId(1L)
      .setName("bob")
      .setAge(10)
      .build();

  @Before
  public void initMapper() {
    JacksonMarshallers.attachMarshallersTo(mapper);
  }

  @Test
  public void shouldSerializeUser() {
    assertEquivalentJson(userJson, user);
  }

  @Test
  public void shouldDeserializeUser() throws IOException {
    assertEquals(user, mapper.readValue(userJson, User.class));
  }

  //
  // Private
  //

  private void assertEquivalentJson(String expected, Object actual) {
    try {
      final JsonNode expectedNode = mapper.readTree(expected);
      final String actualString = mapper.writeValueAsString(actual);
      final JsonNode actualNode = mapper.readTree(actualString);

      assertEquals("Mismatched JSON for " + expected + " and " + actual, expectedNode, actualNode);
    } catch (IOException e) {
      throw new AssertionError(e); // unlikely
    }
  }
}
