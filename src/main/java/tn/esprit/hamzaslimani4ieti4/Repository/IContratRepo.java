package tn.esprit.hamzaslimani4ieti4.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.hamzaslimani4ieti4.Domain.Contrat;

public interface IContratRepo extends JpaRepository<Contrat, Long> {
}
