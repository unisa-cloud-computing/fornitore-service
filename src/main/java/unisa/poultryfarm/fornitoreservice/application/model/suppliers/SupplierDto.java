package unisa.poultryfarm.fornitoreservice.application.model.suppliers;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDto {
    @NotBlank
    private String codiceProvenienza;
    @NotBlank
    private String partitaIva;
    private String telefono;
    private String indirizzo;
}
