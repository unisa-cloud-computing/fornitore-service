package unisa.poultryfarm.fornitoreservice.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unisa.poultryfarm.fornitoreservice.persistence.entity.Fornitore;

@Repository
public interface FornitoreRepository extends JpaRepository<Fornitore, Long> { }
