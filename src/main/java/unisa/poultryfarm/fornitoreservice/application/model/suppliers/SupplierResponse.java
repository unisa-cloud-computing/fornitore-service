package unisa.poultryfarm.fornitoreservice.application.model.suppliers;


import lombok.Builder;
import lombok.Data;
import unisa.poultryfarm.fornitoreservice.application.model.PageInfo;
import unisa.poultryfarm.fornitoreservice.persistence.entity.Fornitore;

import java.util.List;

@Builder
@Data
public class SupplierResponse {
    private List<Fornitore> supplierEntities;
    PageInfo pageInfo;
}
