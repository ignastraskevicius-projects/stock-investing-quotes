package org.ignast.stockinvesting.quotes.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.ignast.stockinvesting.quotes.domain.CompanyExternalId;
import org.ignast.stockinvesting.quotes.domain.CompanyName;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.annotation.DomainClassConstraint;

@EqualsAndHashCode
@Getter
public final class CompanyDTO {

    @NotNull
    @DomainClassConstraint(domainClass = CompanyExternalId.class)
    private final Integer id;

    @NotNull
    @DomainClassConstraint(domainClass = CompanyName.class)
    private final String name;

    @NotNull
    @Size(min = 1, message = "Company must be listed on at least 1 stock exchange")
    @Size(max = 1, message = "Multiple listings are not supported")
    @Valid
    private final List<ListingDTO> listings;

    public CompanyDTO(
        @JsonProperty(value = "id") final Integer id,
        @JsonProperty(value = "name") final String name,
        @JsonProperty(value = "listings") final List<ListingDTO> listings
    ) {
        this.id = id;
        this.name = name;
        this.listings = withoutNullElements(listings);
    }

    private List<ListingDTO> withoutNullElements(final List<ListingDTO> listingsContainingNulls) {
        if (listingsContainingNulls != null) {
            return listingsContainingNulls.stream().filter(Objects::nonNull).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
