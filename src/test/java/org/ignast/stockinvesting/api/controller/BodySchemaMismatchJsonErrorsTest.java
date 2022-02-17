package org.ignast.stockinvesting.api.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BodySchemaMismatchJsonErrorsTest {
    @Test
    public void shouldCreateErrorJsonForMissingField() {
        assertThat(BodySchemaMismatchJsonErrors.forMissingFieldAt("someJsonPath")).isEqualTo(
                "{\"errorName\":\"bodyDoesNotMatchSchema\",\"validationErrors\":[{\"errorName\":\"fieldIsMissing\",\"jsonPath\":\"someJsonPath\"}]}");
    }

    @Test
    public void shouldCreateErrorJsonForStringRequiredField() {
        assertThat(BodySchemaMismatchJsonErrors.forStringRequiredAt("someJsonPath"))
                .isEqualTo("{\"errorName\":\"fieldMustBeString\",\"jsonPath\":\"someJsonPath\"}");
    }
}