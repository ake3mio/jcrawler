package org.ake3m.jcrawler.common;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public final class URLUtils {

    private URLUtils() {
    }

    public static URL createURL(String url, URL parentURL) {
        try {
            var sanitizedURL = requireNonNull(sanitizeURL(url));
            if (!sanitizedURL.startsWith("http") && !sanitizedURL.startsWith("https")) {
                return parentURL.toURI().resolve(sanitizedURL).toURL();
            }
            var uri = new URL(sanitizedURL);
            return isNull(uri.getHost()) ? null : uri;
        } catch (MalformedURLException | URISyntaxException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static boolean hasEqualHost(URL url1, URL url2) {
        return url1.getHost().equalsIgnoreCase(url2.getHost());
    }

    public static String sanitizeURL(String url) {
        if (isNull(url)) {
            return null;
        }

        var strings = url
                .trim()
                .split("#");

        if (strings.length == 0) {
            return null;
        }

        return strings[0].replaceAll("[/=]$", "");
    }
}
