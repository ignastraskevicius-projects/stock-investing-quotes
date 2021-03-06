package org.ignast.stockinvesting.testutil.api.traversor;

import static org.springframework.http.HttpMethod.GET;

import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public interface Hop {
    public abstract static class TraversableHop implements Hop {

        abstract ResponseEntity<String> traverse(ResponseEntity<String> response);
    }

    @RequiredArgsConstructor
    public static final class Factory {

        @NonNull
        private final MediaType appMediaType;

        @NonNull
        private final RestTemplate restTemplate;

        @NonNull
        private final HrefExtractor hrefExtractor;

        public TraversableHop put(final String rel, final String body) {
            return new PutHop(appMediaType, restTemplate, hrefExtractor, rel, body);
        }

        public TraversableHop get(@NonNull final String rel) {
            return new GetHop(appMediaType, restTemplate, r -> hrefExtractor.extractHref(r, rel));
        }

        public TraversableHop getDocsFor(final String rel) {
            return new GetHop(appMediaType, restTemplate, r -> hrefExtractor.extractCuriesHref(r, rel));
        }

        @AllArgsConstructor
        private static final class PutHop extends TraversableHop {

            private final MediaType appMediaType;

            private final RestTemplate restTemplate;

            private final HrefExtractor hrefExtractor;

            @NonNull
            private final String rel;

            @NonNull
            private final String body;

            @Override
            public ResponseEntity<String> traverse(@NonNull final ResponseEntity<String> response) {
                final val href = hrefExtractor.extractHref(response, rel);
                return restTemplate.exchange(href, HttpMethod.PUT, contentTypeV1(body), String.class);
            }

            private HttpEntity<String> contentTypeV1(final String content) {
                final val headers = new HttpHeaders();
                headers.add("Content-Type", appMediaType.toString());
                return new HttpEntity<>(content, headers);
            }
        }

        @AllArgsConstructor
        private static final class GetHop extends TraversableHop {

            private final MediaType appMediaType;

            private final RestTemplate restTemplate;

            private final Function<ResponseEntity<String>, String> extractorHref;

            @Override
            ResponseEntity<String> traverse(@NonNull final ResponseEntity<String> previousResponse) {
                final val href = extractorHref.apply(previousResponse);
                return restTemplate.exchange(href, GET, acceptV1(), String.class);
            }

            private HttpEntity<String> acceptV1() {
                final val headers = new HttpHeaders();
                headers.add("Accept", appMediaType.toString());
                return new HttpEntity<>(headers);
            }
        }
    }
}
