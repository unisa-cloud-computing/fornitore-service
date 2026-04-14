package unisa.poultryfarm.fornitoreservice.application.model.suppliers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DropdownFornitoreDto {
    private Long id;
    private String codiceProvenienza;
}
