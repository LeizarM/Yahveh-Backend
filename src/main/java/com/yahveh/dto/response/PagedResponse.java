package com.yahveh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper de respuesta paginada. La paginación se hace en BD (server-side)
 * usando los stored procedures. El total siempre refleja el total real de
 * registros que matchean los filtros, no solo los de la página actual.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> data;
    private long total;
    private int page;        // 1-based para mostrar al usuario
    private int pageSize;
    private int totalPages;

    public static <T> PagedResponse<T> of(List<T> data, long total, int page, int pageSize) {
        int totalPages = pageSize > 0
                ? (int) Math.ceil((double) total / pageSize)
                : 0;
        return PagedResponse.<T>builder()
                .data(data)
                .total(total)
                .page(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }
}
