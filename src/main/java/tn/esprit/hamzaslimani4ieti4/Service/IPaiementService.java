package tn.esprit.hamzaslimani4ieti4.Service;

import java.util.List;
import tn.esprit.hamzaslimani4ieti4.Domain.Paiement;

public interface IPaiementService {
    Paiement getById(Long id);
    Paiement add(Paiement paiement);
    List<Paiement> findAll();
    Paiement update(Paiement paiement);
    void deleteById(Long id);
}
