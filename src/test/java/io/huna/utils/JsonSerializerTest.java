package io.huna.utils;

import io.huna.dto.Person;
import io.huna.exception.JsonSerializationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonSerializerTest {

    private final JsonSerializer serializer = new JsonSerializer();

    @Test
    public void givenObjectSerializedThenTrueReturned() throws JsonSerializationException {
        // given
        Person person = Person.builder()
                .firstName("soufiane")
                .lastName("cheouati")
                .age("34")
                .build();

        // when
        String jsonString = serializer.serialize(person);

        // then
        assertEquals(
                "{\"personAge\":\"34\",\"firstName\":\"Soufiane\",\"lastName\":\"Cheouati\"}",
                jsonString
        );

    }

}