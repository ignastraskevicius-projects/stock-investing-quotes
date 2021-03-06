package org.ignast.stockinvesting.quotes.domain;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public final class MarketIdentifierCode {

    private static final int ISO_10383_LENGTH = 4;

    private final String code;

    public MarketIdentifierCode(@NonNull final String code) {
        if (code.length() != ISO_10383_LENGTH) {
            throw new IllegalArgumentException(
                "Market Identifier is not 4 characters long (ISO 10383 standard)"
            );
        }
        if (!code.matches("^[A-Z]*$")) {
            throw new IllegalArgumentException(
                "Market Identifier must contain only latin uppercase alphanumeric characters (ISO 10383 standard)"
            );
        }
        this.code = code;
    }

    public String get() {
        return code;
    }
}
