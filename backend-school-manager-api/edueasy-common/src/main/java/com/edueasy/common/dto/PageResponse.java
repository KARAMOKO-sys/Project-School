package com.edueasy.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    /**
     * Crée une PageResponse à partir d'une Page Spring
     */
    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    /**
     * Crée une PageResponse avec une liste vide
     */
    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
                .content(List.of())
                .pageNumber(0)
                .pageSize(0)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .build();
    }

    /**
     * Vérifie si la page a du contenu
     */
    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

    /**
     * Vérifie si la page est vide
     */
    public boolean isEmpty() {
        return !hasContent();
    }

    /**
     * Retourne le nombre d'éléments dans la page
     */
    public int getNumberOfElements() {
        return content != null ? content.size() : 0;
    }
}