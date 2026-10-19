package tn.esprit.hamzaslimani4ieti4.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.hamzaslimani4ieti4.Domain.Equipement;

public interface IEquipementRepo extends JpaRepository<Equipement, Long> {
}
