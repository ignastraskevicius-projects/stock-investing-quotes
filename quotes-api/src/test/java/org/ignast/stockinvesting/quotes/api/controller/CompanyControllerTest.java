package org.ignast.stockinvesting.quotes.api.controller;

import lombok.val;
import org.ignast.stockinvesting.quotes.domain.Companies;
import org.ignast.stockinvesting.quotes.domain.Company;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockExchange;
import org.ignast.stockinvesting.quotes.domain.StockExchanges;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.ignast.stockinvesting.quotes.api.controller.TestDtos.amazonDto;
import static org.ignast.stockinvesting.quotes.api.testutil.DomainFactoryForTests.amazon;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class CompanyControllerTest {

    private final StockExchanges stockExchanges = mock(StockExchanges.class);

    private final Companies companies = mock(Companies.class);

    private final CompanyController controller = new CompanyController(companies, stockExchanges);

    @Test
    public void shouldCreateCompany() {
        final val stockExchange = mock(StockExchange.class);
        when(stockExchanges.getFor(new MarketIdentifierCode("XNAS"))).thenReturn(stockExchange);
        final val companyDto = new CompanyDTO(5, "Microsoft", List.of(new ListingDTO("XNAS", "MSFT")));
        final val captor = ArgumentCaptor.forClass(Company.class);

        final val createdCompanyDto = controller.createCompany(companyDto);

        verify(companies).create(captor.capture());
        final val company = captor.getValue();
        assertThat(company.getExternalId()).isEqualTo(new CompanyExternalId(5));
        assertThat(company.getName()).isEqualTo(new CompanyName("Microsoft"));
        assertThat(company.getStockSymbol()).isEqualTo(new StockSymbol("MSFT"));
        assertThat(company.getStockExchange()).isEqualTo(stockExchange);

        assertThat(createdCompanyDto.getContent()).isEqualTo(companyDto);
        assertThat(createdCompanyDto.getLink("self").isPresent());
        createdCompanyDto.getLink("self").ifPresent(c ->
                assertThat(c.getHref()).endsWith("/companies/5"));
    }

    @Test
    public void companyShouldLinkToItself() {
        when(stockExchanges.getFor(new MarketIdentifierCode("XNAS"))).thenReturn(mock(StockExchange.class));
        final val companyDto = new CompanyDTO(5, "Microsoft", List.of(new ListingDTO("XNAS", "MSFT")));

        final val createdCompanyDto = controller.createCompany(companyDto);

        assertThat(createdCompanyDto.getContent()).isEqualTo(companyDto);
        assertThat(createdCompanyDto.getRequiredLink("self").getHref()).endsWith(format("/companies/%d", 5));
    }

    @Test
    public void shouldRejectDtoWithoutListings() {
        final val company = new CompanyDTO(1, "Microsoft", List.of());

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> controller.createCompany(company)).withMessage("Company to be created was expected to have one listing, but zero was found");
    }

    @Test
    public void shouldRetrieveCompany() {
        final val amazonExternalId = amazon().getExternalId().get();
        when(companies.findByExternalId(new CompanyExternalId(amazonExternalId))).thenReturn(amazon());

        final val retrievedCompany = controller.retrieveCompanyById(amazonExternalId);

        assertThat(retrievedCompany.getContent()).isEqualTo(amazonDto());
    }

    @Test
    public void retrievedCompanyShouldLinkToItself() {
        final val amazonExternalId = amazon().getExternalId().get();
        when(companies.findByExternalId(new CompanyExternalId(amazonExternalId))).thenReturn(amazon());

        final val retrievedCompany = controller.retrieveCompanyById(amazonExternalId);

        assertThat(retrievedCompany.getRequiredLink("self").getHref()).endsWith(format("/companies/%d", amazonExternalId));
    }
}