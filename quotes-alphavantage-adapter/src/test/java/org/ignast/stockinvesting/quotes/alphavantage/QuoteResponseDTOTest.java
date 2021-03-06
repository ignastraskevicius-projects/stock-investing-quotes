package org.ignast.stockinvesting.quotes.alphavantage;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import lombok.val;
import org.junit.jupiter.api.Test;

public final class QuoteResponseDTOTest {

    @Test
    public void shouldPreserveError() {
        final val error = new QuoteResponseDTO(empty(), of("humal-readable-message"));

        assertThat(error.getError().get()).isEqualTo("humal-readable-message");
        assertThat(error.toString()).contains("error").contains("humal-readable-message");
    }

    @Test
    public void shouldPreserveAbsenceOfError() {
        assertThat(new QuoteResponseDTO(empty(), empty()).getError()).isEmpty();
    }

    @Test
    public void shouldRejectNullErrors() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> new QuoteResponseDTO(empty(), null));
    }

    @Test
    public void shouldPreserveQuote() {
        final val quote = new QuoteDTO(of(BigDecimal.ONE));
        final val response = new QuoteResponseDTO(of(quote), empty());

        assertThat(response.getQuote().get()).isEqualTo(quote);
        assertThat(response.toString()).contains("quote").contains("price");
    }

    @Test
    public void shouldPreserveAbsenceOfQuote() {
        assertThat(new QuoteResponseDTO(empty(), empty()).getQuote()).isEmpty();
    }

    @Test
    public void shouldRejectNullQuote() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> new QuoteResponseDTO(null, empty()));
    }

    @Test
    void quoteShouldPreservePrice() {
        final val quote = new QuoteDTO(of(BigDecimal.ONE));

        assertThat(quote.getPrice()).isEqualTo(of(BigDecimal.ONE));
        assertThat(quote.toString()).contains("price").contains("1");
    }

    @Test
    void quoteShouldPreserveAbsenceOfPrice() {
        assertThat(new QuoteDTO(empty()).getPrice()).isEqualTo(empty());
    }

    @Test
    public void shouldRejectNullPrice() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new QuoteDTO(null));
    }
}
