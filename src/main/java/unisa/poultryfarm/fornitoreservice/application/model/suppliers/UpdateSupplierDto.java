package unisa.poultryfarm.fornitoreservice.application.model.suppliers;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateSupplierDto extends SupplierDto {
    private Long id;
}
