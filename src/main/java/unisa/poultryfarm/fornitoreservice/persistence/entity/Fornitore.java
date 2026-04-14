package unisa.poultryfarm.fornitoreservice.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "FORNITORE", schema = "dbo")
@Data
public class Fornitore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "CODICE_PROVENIENZA")
    private String codiceProvenienza;
    @Column(name = "PARTITA_IVA", nullable = false, unique = true)
    private String partitaIva;
    private String telefono;
    private String indirizzo;

    public Fornitore() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Fornitore that = (Fornitore) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
