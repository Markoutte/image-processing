package me.markoutte.benchmark;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Strings {

    private static final Pattern CURLY_BRACES = Pattern.compile("(\\{\\d+\\})");

    public static String $(String message, Object... params) {
        if (params.length == 0) {
            return message;
        }
        Matcher m = CURLY_BRACES.matcher(message);
        while (m.find()) {
            int idx = Integer.parseInt(m.group().replaceAll("\\{|\\}", ""));
            if (idx >= params.length) continue;
            Object value = params[idx];
            message = message.replace(m.group(), value == null ? "null" : value.toString());
        }
        return message;
    }
}
