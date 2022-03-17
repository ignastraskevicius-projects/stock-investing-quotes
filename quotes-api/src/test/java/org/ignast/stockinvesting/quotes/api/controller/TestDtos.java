package org.ignast.stockinvesting.quotes.api.controller;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ignast.stockinvesting.quotes.api.testutil.DomainFactoryForTests.amazon;

public final class TestDtos {

    public static CompanyDTO amazonDto() {
        return new CompanyDTO(6, "Amazon", List.of(new ListingDTO("XNAS", "AMZN")));
    }
}

final class TestDtosTest {
    @Test
    public void shouldCreateAmazonDto() {
        assertThat(TestDtos.amazonDto().getId()).isEqualTo(amazon().getExternalId().get());
        assertThat(TestDtos.amazonDto().getName()).isEqualTo(amazon().getName().get());
        assertThat(TestDtos.amazonDto().getListings()).isNotEmpty();
        TestDtos.amazonDto().getListings().forEach(l -> {
            assertThat(l.getStockSymbol()).isEqualTo(amazon().getStockSymbol().get());
            assertThat(l.getMarketIdentifier()).isEqualTo(amazon().getStockExchange().getMarketIdentifierCode().get());
        });
    }
}
