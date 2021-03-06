package org.ignast.stockinvesting.quotes.domain;

import java.util.AbstractMap;
import java.util.Map;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class StockExchanges {

    private static final String USD = "USD";

    private final Map<MarketIdentifierCode, StockExchange> supportedStockExchanges;

    public StockExchanges(final QuotesRepository quotes) {
        supportedStockExchanges =
            Map.ofEntries(
                newStockExchange(new MarketIdentifierCode("XFRA"), new CurrencyCode("EUR"), quotes),
                newStockExchange(new MarketIdentifierCode("XNYS"), new CurrencyCode(USD), quotes),
                newStockExchange(new MarketIdentifierCode("XTSE"), new CurrencyCode("CAD"), quotes),
                newStockExchange(new MarketIdentifierCode("XHKG"), new CurrencyCode("HKD"), quotes),
                newStockExchange(new MarketIdentifierCode("XASX"), new CurrencyCode("AUD"), quotes),
                newStockExchange(new MarketIdentifierCode("XNAS"), new CurrencyCode(USD), quotes),
                newStockExchange(new MarketIdentifierCode("XLON"), new CurrencyCode("GBP"), quotes)
            );
    }

    private AbstractMap.SimpleEntry<MarketIdentifierCode, StockExchange> newStockExchange(
        final MarketIdentifierCode mic,
        final CurrencyCode currency,
        final QuotesRepository quotes
    ) {
        return new AbstractMap.SimpleEntry<>(mic, StockExchange.create(mic, currency, quotes));
    }

    public StockExchange getFor(@NonNull final MarketIdentifierCode mic) {
        if (supportedStockExchanges.containsKey(mic)) {
            return supportedStockExchanges.get(mic);
        } else {
            throw new StockExchangeNotSupported(mic);
        }
    }
}
