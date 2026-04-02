package com.jewelry.jewelryshopbackend.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Locale;
import java.util.Set;

public class PageableUtils {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "slug", "status", "createdAt", "updatedAt");

    private PageableUtils() {
    }

    public static Pageable build(int page, int size, String sortBy, String sortDir) {
        return build(page, size, sortBy, sortDir, ALLOWED_SORT_FIELDS, "createdAt");
    }

    public static Pageable build(
            int page,
            int size,
            String sortBy,
            String sortDir,
            Set<String> allowedSortFields,
            String defaultSortBy
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        String normalizedSortBy = sortBy == null ? "" : sortBy.trim();
        String safeSortBy = allowedSortFields.contains(normalizedSortBy)
                ? normalizedSortBy
                : defaultSortBy;

        Sort sort = "asc".equalsIgnoreCase(sortDir == null ? "" : sortDir.toLowerCase(Locale.ROOT))
                ? Sort.by(safeSortBy).ascending()
                : Sort.by(safeSortBy).descending();

        return PageRequest.of(safePage, safeSize, sort);
    }
}
