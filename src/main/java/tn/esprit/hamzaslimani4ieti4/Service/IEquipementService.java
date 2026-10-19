package tn.esprit.hamzaslimani4ieti4.Service;

import java.util.List;
import tn.esprit.hamzaslimani4ieti4.Domain.Equipement;

public interface IEquipementService {
    Equipement getById(Long id);
    Equipement add(Equipement equipement);
    List<Equipement> findAll();
    Equipement update(Equipement equipement);
    void deleteById(Long id);
}
