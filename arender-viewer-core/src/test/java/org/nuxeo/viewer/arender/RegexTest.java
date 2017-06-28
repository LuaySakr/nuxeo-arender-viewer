package org.nuxeo.viewer.arender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.nuxeo.viewer.arender.ArenderRedirectFilter.PDFJS_URL;

import java.util.regex.Matcher;
import java.util.stream.IntStream;

import org.junit.Test;

public class RegexTest {
    public static final String WITH_FIELD = "http://localhost:8080/nuxeo/viewers/pdfjs/web/viewer.html?file=http://localhost:8080/nuxeo/site/api/v1/repo/default/id/ebe4883d-1b11-4d50-b7f8-490a04172862/@blob/file:content/@preview/pdf";

    public static final String WITHOUT_FIELD = "http://localhost:8080/nuxeo/viewers/pdfjs/web/viewer.html?file=http://localhost:8080/nuxeo/site/api/v1/repo/default/id/ebe4883d-1b11-4d50-b7f8-490a04172862/@preview/pdf";

    public static final String WITHOUT_PORT = "http://localhost/nuxeo/viewers/pdfjs/web/viewer.html?file=http://localhost/nuxeo/site/api/v1/repo/default/id/ebe4883d-1b11-4d50-b7f8-490a04172862/@preview/pdf";

    public static final String WITHOUT_REPO = "http://localhost/nuxeo/viewers/pdfjs/web/viewer.html?file=http://localhost/nuxeo/site/api/v1/id/ebe4883d-1b11-4d50-b7f8-490a04172862/@preview/pdf";

    @Test
    public void testRegex() {
        assertMatchAndGroup(WITH_FIELD, "http://localhost:8080/nuxeo/", "default",
                "ebe4883d-1b11-4d50-b7f8-490a04172862", "file:content");
        assertMatchAndGroup(WITHOUT_FIELD, "http://localhost:8080/nuxeo/", "default",
                "ebe4883d-1b11-4d50-b7f8-490a04172862", null);
        assertMatchAndGroup(WITHOUT_PORT, "http://localhost/nuxeo/", "default", "ebe4883d-1b11-4d50-b7f8-490a04172862",
                null);
        assertMatchAndGroup(WITHOUT_REPO, "http://localhost/nuxeo/", null, "ebe4883d-1b11-4d50-b7f8-490a04172862",
                null);
    }

    protected static void assertMatchAndGroup(String input, String... matches) {
        Matcher matcher = PDFJS_URL.matcher(input);
        assertTrue(matcher.matches());
        assertEquals(matcher.groupCount(), matches.length);

        IntStream.range(0, matches.length).forEach(i -> assertEquals(matches[i], matcher.group(i + 1)));
    }
}
