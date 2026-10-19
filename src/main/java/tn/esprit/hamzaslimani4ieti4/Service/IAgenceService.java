package tn.esprit.hamzaslimani4ieti4.Service;

import java.util.List;
import tn.esprit.hamzaslimani4ieti4.Domain.Agence;

public interface IAgenceService {
    Agence getById(Long id);
    Agence add(Agence agence);
    List<Agence> findAll();
    Agence update(Agence agence);
    void deleteById(Long id);
}
