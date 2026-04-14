package unisa.poultryfarm.fornitoreservice.application.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PageInfo {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
}
