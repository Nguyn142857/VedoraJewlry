package com.jewelry.jewelryshopbackend.util;


import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private SlugUtils() {
    }

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        String slug = WHITESPACE.matcher(normalized.trim()).replaceAll("-");
        slug = NONLATIN.matcher(slug).replaceAll("");
        slug = slug.replaceAll("-{2,}", "-");
        slug = slug.replaceAll("^-|-$", "");

        return slug.toLowerCase(Locale.ROOT);
    }
}