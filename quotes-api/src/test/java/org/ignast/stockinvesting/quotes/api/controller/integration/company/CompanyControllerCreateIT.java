package org.ignast.stockinvesting.quotes.api.controller.integration.company;

import static java.lang.String.format;
import static org.ignast.stockinvesting.quotes.api.controller.integration.company.CompanyControllerRetrievalByIdTest.DOCS_URL;
import static org.ignast.stockinvesting.quotes.api.testutil.DomainFactoryForTests.exchangeNotSupportingAnySymbol;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forArrayRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forIntegerRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forInvalidValueAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forMissingFieldAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forObjectRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forStringRequiredAt;
import static org.ignast.stockinvesting.testutil.api.BodySchemaMismatchJsonErrors.forTwoMissingFieldsAt;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.bodyMatchesJson;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.resourceContentMatchesJson;
import static org.ignast.stockinvesting.testutil.api.NonExtensibleContentMatchers.resourceLinksMatchesJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.val;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository.CompanyAlreadyExists;
import org.ignast.stockinvesting.quotes.domain.CompanyRepository.ListingAlreadyExists;
import org.ignast.stockinvesting.quotes.domain.MarketIdentifierCode;
import org.ignast.stockinvesting.quotes.domain.StockExchange;
import org.ignast.stockinvesting.quotes.domain.StockExchangeNotSupported;
import org.ignast.stockinvesting.quotes.domain.StockSymbol;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = { "documentation.url=" + DOCS_URL })
public final class CompanyControllerCreateIT extends CompanyControllerITBase {

    static final String DOCS_URL = "http://documentation:8081";

    @Test
    public void shouldRejectCompaniesBeingDefinedViaBlankBody() throws Exception {
        mockMvc
            .perform(put("/companies/").contentType(APP_V1))
            .andExpect(status().isBadRequest())
            .andExpect(bodyMatchesJson("{\"httpStatus\":400,\"errorName\":\"bodyNotParsable\"}"));
    }

    @Test
    public void shouldRejectCompaniesNotBeingDefinedInJson() throws Exception {
        assertThatRequest("not-a-json-object")
            .failsValidation("{\"httpStatus\":400,\"errorName\":\"bodyNotParsable\"}");
    }

    @Test
    public void shouldCreateCompany() throws Exception {
        when(stockExchanges.getFor(any())).thenReturn(mock(StockExchange.class));
        mockMvc
            .perform(put("/companies/").contentType(APP_V1).content(bodyFactory.createAmazon()))
            .andExpect(status().isCreated())
            .andExpect(header().string("Content-Type", APP_V1))
            .andExpect(resourceContentMatchesJson(bodyFactory.createAmazon()));
    }

    @Test
    public void createdCompanyShouldContainLinks() throws Exception {
        when(stockExchanges.getFor(any())).thenReturn(mock(StockExchange.class));
        mockMvc
            .perform(put("/companies/").contentType(APP_V1).content(bodyFactory.createAmazon()))
            .andExpect(status().isCreated())
            .andExpect(header().string("Content-Type", APP_V1))
            .andExpect(
                resourceLinksMatchesJson(
                    format(
                        """
                           {
                                "_links":{
                                    "self":{"href":"http://localhost/companies/6"},
                                    "quotes:queryQuotedPrice":{"href":"http://localhost/companies/6/price"},
                                    "curies":[{
                                        "name":"quotes",
                                        "href":"%s/rels/quotes/{rel}",
                                        "templated":true
                                    }]
                                }
                            }""",
                        DOCS_URL
                    )
                )
            );
    }

    @Test
    public void shouldRejectDuplicateCompany() throws Exception {
        when(stockExchanges.getFor(any())).thenReturn(mock(StockExchange.class));
        final val alreadyExists = new CompanyAlreadyExists(
            mock(CompanyExternalId.class),
            new RuntimeException()
        );
        doThrow(alreadyExists).when(companies).create(any());

        assertThatRequest(bodyFactory.createAmazon())
            .failsValidation(
                """
                        {"httpStatus":400,"errorName":"companyAlreadyExists"}"""
            );
    }

    @Test
    public void shouldRejectCompaniesListedInUnsupportedExchanges() throws Exception {
        final val notSnsuppoted = new StockExchangeNotSupported(mock(MarketIdentifierCode.class));
        when(stockExchanges.getFor(any())).thenThrow(notSnsuppoted);

        assertThatRequest(bodyFactory.createAmazon())
            .failsValidation(
                """
                                {"httpStatus":400,"errorName":"marketNotSupported"}"""
            );
    }

    @Test
    public void shouldRejectCompanyWithExistingListing() throws Exception {
        when(stockExchanges.getFor(any())).thenReturn(mock(StockExchange.class));
        final val listingAlreadyExists = new ListingAlreadyExists(
            mock(StockSymbol.class),
            mock(MarketIdentifierCode.class),
            new RuntimeException()
        );
        doThrow(listingAlreadyExists).when(companies).create(any());

        assertThatRequest(bodyFactory.createAmazon())
            .failsValidation(
                """
                        {"httpStatus":400,"errorName":"listingAlreadyExists"}"""
            );
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc
            .perform(put("/companies/").contentType("application/json"))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(bodyMatchesJson("{\"httpStatus\":415,\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc
            .perform(put("/companies/").contentType("application/hal+json"))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(bodyMatchesJson("{\"httpStatus\":415,\"errorName\":\"unsupportedContentType\"}"));
    }

    @Test
    public void shouldIndicateResourceNotReadable() throws Exception {
        mockMvc
            .perform(get("/companies/").contentType(HAL_JSON))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(bodyMatchesJson("{\"httpStatus\":405,\"errorName\":\"methodNotAllowed\"}"));
    }

    @Test
    public void shouldAbleToPreserveErrorsFromMultipleFields() throws Exception {
        assertThatRequest(bodyFactory.createWithoutNameAndId())
            .failsValidation(forTwoMissingFieldsAt("$.name", "$.id"));
    }
}

final class CompanyControllerIdParsingIT extends CompanyControllerITBase {

    @Test
    public void shouldRejectCompanyWithoutIdIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithIdJsonPair("")).failsValidation(forMissingFieldAt("$.id"));
    }

    @Test
    public void shouldRejectCompanyWithNonIntegerIdIndicatingWrongType() throws Exception {
        assertThatRequest(bodyFactory.createWithIdJsonPair("\"id\":\"nonInteger\""))
            .failsValidation(forIntegerRequiredAt("$.id"));
    }

    @Test
    public void shouldRejectCompanyWithInvalidId() throws Exception {
        assertThatRequest(bodyFactory.createWithIdJsonPair("\"id\":-5"))
            .failsValidation(forInvalidValueAt("$.id", "Must be positive"));
    }
}

final class CompanyControllerNameParsingIT extends CompanyControllerITBase {

    @Test
    public void shouldRejectCompanyWithoutNameIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithNameJsonPair(""))
            .failsValidation(forMissingFieldAt("$.name"));
    }

    @Test
    public void shouldRejectCompanyWithNameAsNonJsonStringIndicatingWrongType() throws Exception {
        assertThatRequest(bodyFactory.createWithNameJsonPair("\"name\":false"))
            .failsValidation(forStringRequiredAt("$.name"));
    }

    @Test
    public void shouldRejectCompanyWithInvalidName() throws Exception {
        when(stockExchanges.getFor(any())).thenReturn(mock(StockExchange.class));
        assertThatRequest(bodyFactory.createWithNameJsonPair("\"name\":\"\""))
            .failsValidation(forInvalidValueAt("$.name", "Company name must be between 1-160 characters"));
    }
}

final class CompanyControllerListingsParsingIT extends CompanyControllerITBase {

    @Test
    public void companyWithoutListingShouldBeRejectedIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithListingsJsonPair(""))
            .failsValidation(forMissingFieldAt("$.listings"));
    }

    @Test
    public void companyWithNonArrayListingShouldBeRejectedIndicatingWrongType() throws Exception {
        assertThatRequest(bodyFactory.createWithListingsJsonPair("\"listings\":3"))
            .failsValidation(forArrayRequiredAt("$.listings"));
    }

    @Test
    public void companyWithZeroListingsShouldBeRejected() throws Exception {
        assertThatRequest(bodyFactory.createWithListingsJsonPair("\"listings\":[]"))
            .failsValidation(
                forInvalidValueAt("$.listings", "Company must be listed on at least 1 stock exchange")
            );
    }

    @Test
    public void companyWithNullListingShouldBeRejected() throws Exception {
        assertThatRequest(bodyFactory.createWithListingsJsonPair("\"listings\":[null]"))
            .failsValidation(
                forInvalidValueAt("$.listings", "Company must be listed on at least 1 stock exchange")
            );
    }

    @Test
    public void companyWithIndividualListingAsNonObjectShouldBeRejectedIndicatedWrongType() throws Exception {
        assertThatRequest(bodyFactory.createWithListingsJsonPair("\"listings\":[3.3]"))
            .failsValidation(forObjectRequiredAt("$.listings[0]"));
    }

    @Test
    public void shouldNotSupportMultipleListings() throws Exception {
        assertThatRequest(bodyFactory.createWithMultipleListings())
            .failsValidation(forInvalidValueAt("$.listings", "Multiple listings are not supported"));
    }
}

final class CompanyControllerTestIndividualListingParsingIT extends CompanyControllerITBase {

    @Test
    public void shouldRejectCompanyListedWithoutMarketIdIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithMarketIdJsonPair(""))
            .failsValidation(forMissingFieldAt("$.listings[0].marketIdentifier"));
    }

    @Test
    public void shouldRejectCompanyListedWithNonStringMarketIdIndicatingTypeIsWrong() throws Exception {
        assertThatRequest(bodyFactory.createWithMarketIdJsonPair("\"marketIdentifier\":true"))
            .failsValidation(forStringRequiredAt("$.listings[0].marketIdentifier"));
    }

    @Test
    public void shouldRejectCompanyListedWithInvalidMarketId() throws Exception {
        assertThatRequest(bodyFactory.createWithMarketIdJsonPair("\"marketIdentifier\":\"invalid\""))
            .failsValidation(
                forInvalidValueAt(
                    "$.listings[0].marketIdentifier",
                    "Market Identifier is not 4 characters long (ISO 10383 standard)"
                )
            );
    }

    @Test
    public void shouldRejectCompanyWithoutSymbolIndicatingFieldIsMandatory() throws Exception {
        assertThatRequest(bodyFactory.createWithSymbolJsonPair(""))
            .failsValidation(forMissingFieldAt("$.listings[0].stockSymbol"));
    }

    @Test
    public void shouldRejectCompanyWithNonStringSymbolIndicatingTypeIsWrong() throws Exception {
        assertThatRequest(bodyFactory.createWithSymbolJsonPair("\"stockSymbol\":3"))
            .failsValidation(forStringRequiredAt("$.listings[0].stockSymbol"));
    }

    @Test
    public void shouldRejectCompanyWithInvalidSymbol() throws Exception {
        assertThatRequest(bodyFactory.createWithSymbolJsonPair("\"stockSymbol\":\"TOOLONG\""))
            .failsValidation(
                forInvalidValueAt(
                    "$.listings[0].stockSymbol",
                    "Stock Symbol must contain between 1-6 characters"
                )
            );
    }

    @Test
    public void shouldRejectCompanyWithUnsupportedSymbol() throws Exception {
        final val exchange = exchangeNotSupportingAnySymbol();
        when(stockExchanges.getFor(any())).thenReturn(exchange);
        assertThatRequest(bodyFactory.createAmazon())
            .failsValidation("{\"httpStatus\":400, \"errorName\":\"stockSymbolNotSupportedInThisMarket\"}");
    }
}
