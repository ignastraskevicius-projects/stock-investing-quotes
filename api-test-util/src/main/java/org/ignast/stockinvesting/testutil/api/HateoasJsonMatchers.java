package org.ignast.stockinvesting.testutil.api;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.json.JSONObject;

public final class HateoasJsonMatchers {

    private static final String LINKS = "_links";

    private static final String HREF = "href";

    private HateoasJsonMatchers() {}

    public static HasRel hasRel(final String relName) {
        return new HasRel(relName);
    }

    public static HasCuries hasCuries() {
        return new HasCuries();
    }

    public static final class HasCuries {

        public HasCuries() {}

        public TypeSafeMatcher<String> withHref() {
            return new ExistsRelWithHref();
        }

        static final class ExistsRelWithHref extends TypeSafeMatcher<String> {

            @Override
            protected boolean matchesSafely(final String hateoasJson) {
                try {
                    new JSONObject(hateoasJson)
                        .getJSONObject(LINKS)
                        .getJSONArray("curies")
                        .getJSONObject(0)
                        .getString(HREF);
                    return true;
                } catch (JSONException e) {
                    return false;
                }
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText(
                    String.format("HATEOAS json should contain a 'curies' rel with a href")
                );
            }
        }
    }

    public static final class HasRel {

        private final String relName;

        public HasRel(final String relName) {
            this.relName = relName;
        }

        public TypeSafeMatcher<String> withHrefContaining(final String hrefSubstring) {
            return new ExistsRelWithHrefContainingString(relName, hrefSubstring);
        }

        public TypeSafeMatcher<String> withHref() {
            return new ExistsRelWithHref(relName);
        }

        static class ExistsRelWithHrefContainingString extends TypeSafeMatcher<String> {

            private final String relName;

            private final String hrefSubstring;

            public ExistsRelWithHrefContainingString(final String relName, final String hrefSubstring) {
                this.relName = relName;
                this.hrefSubstring = hrefSubstring;
            }

            @Override
            protected boolean matchesSafely(final String hateoasJson) {
                try {
                    return new JSONObject(hateoasJson)
                        .getJSONObject(LINKS)
                        .getJSONObject(relName)
                        .getString(HREF)
                        .contains(hrefSubstring);
                } catch (JSONException e) {
                    return false;
                }
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText(
                    String.format(
                        "HATEOAS json should contain a '%s' rel with a href containing substring '%s'",
                        relName,
                        hrefSubstring
                    )
                );
            }
        }

        static class ExistsRelWithHref extends TypeSafeMatcher<String> {

            private final String relName;

            public ExistsRelWithHref(final String relName) {
                this.relName = relName;
            }

            @Override
            protected boolean matchesSafely(final String hateoasJson) {
                try {
                    new JSONObject(hateoasJson).getJSONObject(LINKS).getJSONObject(relName).getString(HREF);
                    return true;
                } catch (JSONException e) {
                    return false;
                }
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText(
                    String.format("HATEOAS json should contain a '%s' rel with a href", relName)
                );
            }
        }
    }
}
