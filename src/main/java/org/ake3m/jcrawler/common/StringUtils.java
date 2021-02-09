package org.ake3m.jcrawler.common;

import java.util.Collection;
import java.util.stream.Collectors;

public final class StringUtils {
    private StringUtils() {
    }

    public static String asBulletedList(Collection<String> collection) {
        var result = collection
                .stream()
                .map(s -> "> " + s)
                .collect(Collectors.joining("\n"));
        return result.length() > 0 ? result : "NONE";
    }
}
