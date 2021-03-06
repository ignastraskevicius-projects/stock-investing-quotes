package org.ignast.stockinvesting.quotes.api.testutil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.api.testutil.DomainFactoryForTests.amazon;
import static org.ignast.stockinvesting.quotes.api.testutil.DomainFactoryForTests.exchangeNotSupportingAnySymbol;
import static org.ignast.stockinvesting.testutil.MockitoUtils.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import lombok.val;
import org.ignast.stockinvesting.quotes.domain.Company;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.QuotesRepository;
import org.ignast.stockinvesting.quotes.domain.StockExchange;
import org.ignast.stockinvesting.quotes.domain.StockExchanges;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.ignast.stockinvesting.quotes.domain.StockSymbolNotSupportedInThisMarket;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

public final class DomainFactoryForTests {

    private DomainFactoryForTests() {}

    @SuppressWarnings("checkstyle:magicnumber")
    public static Company amazon() {
        return Company.create(
            new CompanyExternalId(6),
            new CompanyName("Amazon"),
            new StockSymbol("AMZN"),
            new StockExchanges((s, m) -> new BigDecimal("3000")).getFor(new MarketIdentifierCode("XNAS"))
        );
    }

    public static StockExchange exchangeNotSupportingAnySymbol() {
        return mock(
            StockExchange.class,
            e -> when(e.getQuotedPrice(any())).thenThrow(StockSymbolNotSupportedInThisMarket.class)
        );
    }

    private static class StubQuotesRepository implements QuotesRepository {

        @Override
        public BigDecimal getQuotedPriceOf(final StockSymbol stockSymbol, final MarketIdentifierCode mic) {
            return null;
        }
    }
}

final class DomainFactoryForTestsTest {

    @Test
    public void shouldCreateAmazon() {
        final val externalAmazonIdForTests = 6;
        assertThat(amazon().getExternalId()).isEqualTo(new CompanyExternalId(externalAmazonIdForTests));
        assertThat(amazon().getName()).isEqualTo(new CompanyName("Amazon"));
        assertThat(amazon().getStockSymbol()).isEqualTo(new StockSymbol("AMZN"));
        assertThat(amazon().getStockExchange().getMarketIdentifierCode())
            .isEqualTo(new MarketIdentifierCode("XNAS"));
        assertThat(amazon().getQuotedPrice()).isEqualTo(Money.of(new BigDecimal("3000"), "USD"));
    }

    @Test
    public void shouldCreateStockExchangeNotSupportingSymbol() {
        assertThatExceptionOfType(StockSymbolNotSupportedInThisMarket.class)
            .isThrownBy(() -> exchangeNotSupportingAnySymbol().getQuotedPrice(new StockSymbol("AAAA")));
    }
}
