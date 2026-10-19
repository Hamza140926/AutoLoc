package tn.esprit.hamzaslimani4ieti4.Service;
import java.util.List;

import tn.esprit.hamzaslimani4ieti4.Domain.Vehicule;

public interface IVehiculeService {

    Vehicule getById(Long id);
    Vehicule add(Vehicule vehicule);
    List<Vehicule> findAll();
    Vehicule update(Vehicule vehicule);
    void deleteById(Long id);
    
    

}
