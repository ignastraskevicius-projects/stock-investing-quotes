package org.ignast.stockinvesting.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StockSymbolTest {

    @Test
    public void shouldNotBeNull() {
        Assertions.assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new StockSymbol(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "AMZN", "MSFT" })
    public void shouldPreserveCode(String symbol) {
        assertThat(new StockSymbol(symbol).get()).isEqualTo(symbol);
    }

    @Test
    public void shouldNotBeEmpty() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new StockSymbol(""))
                .withMessage("Stock Symbol must contain between 1-5 characters");
        assertThat(new StockSymbol("A").get()).isEqualTo("A");
    }

    @Test
    public void shouldRejectLongerThan5() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new StockSymbol("ABCDEF"))
                .withMessage("Stock Symbol must contain between 1-5 characters");
        assertThat(new StockSymbol("ABCDE").get()).isEqualTo("ABCDE");
    }

    @ParameterizedTest
    @ValueSource(strings = { "1Aa", "Ñ1A" })
    public void shouldRejectNonAlphanumericUppercaseCharacters(String symbol) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new StockSymbol(symbol))
                .withMessage("Stock Symbol must contain only uppercase alphanumeric characters");
    }

    @ParameterizedTest
    @ValueSource(strings = { "11", "AA", "2B", "C3" })
    public void shouldAcceptUppercaseAlphanumeric(String symbol) {
        assertThat(new StockSymbol(symbol).get()).isEqualTo(symbol);
    }
}