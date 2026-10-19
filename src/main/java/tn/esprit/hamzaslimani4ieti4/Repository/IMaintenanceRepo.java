package tn.esprit.hamzaslimani4ieti4.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.hamzaslimani4ieti4.Domain.Maintenance;

public interface IMaintenanceRepo extends JpaRepository<Maintenance, Long> {
}
