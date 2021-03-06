package org.ignast.stockinvesting.quotes.api.controller.errorhandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.val;
import org.junit.jupiter.api.Test;

public final class AppBusinessErrorDTOTest {

    @Test
    public void shouldCreateForCompanyNotFound() {
        final val error = AppBusinessErrorDTO.createForCompanyNotFound();

        assertThat(error.getErrorName()).isNull();
        assertThat(error.getHttpStatus()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void shouldCreateForCompanyAlreadyExists() {
        final val error = AppBusinessErrorDTO.createForCompanyAlreadyExists();

        assertThat(error.getErrorName()).isEqualTo("companyAlreadyExists");
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void shouldCreateForListingAlreadyExists() {
        final val error = AppBusinessErrorDTO.createForListingAlreadyExists();

        assertThat(error.getErrorName()).isEqualTo("listingAlreadyExists");
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void shouldCreateForStockSymbolNotSupportedInTheMarket() {
        final val error = AppBusinessErrorDTO.createForStockSymbolNotSupportedInThisMarket();

        assertThat(error.getErrorName()).isEqualTo("stockSymbolNotSupportedInThisMarket");
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void shouldCreateForMarketNotSupported() {
        final val error = AppBusinessErrorDTO.createForMarketNotSupported();

        assertThat(error.getErrorName()).isEqualTo("marketNotSupported");
        assertThat(error.getHttpStatus()).isEqualTo(BAD_REQUEST);
    }
}
