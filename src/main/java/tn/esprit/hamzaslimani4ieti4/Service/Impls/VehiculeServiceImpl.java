package tn.esprit.hamzaslimani4ieti4.Service.Impls;
import tn.esprit.hamzaslimani4ieti4.Domain.Vehicule;
import tn.esprit.hamzaslimani4ieti4.Repository.IVehiculeRepo;       
import tn.esprit.hamzaslimani4ieti4.Service.IVehiculeService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {


    private final IVehiculeRepo iVehiculeRepo;
    @Override
    public Vehicule getById(Long id) {
        return iVehiculeRepo.findById(id)
                            .orElseThrow(() -> new RuntimeException("Vehicule not found"));
    }

    @Override
    public Vehicule add(Vehicule vehicule) {
        return iVehiculeRepo.save(vehicule);
    }

    @Override
    public List<Vehicule> findAll() {
        return iVehiculeRepo.findAll();
    }

    @Override
    public Vehicule update(Vehicule vehicule) {
        return iVehiculeRepo.save(vehicule);
    }

    @Override
    public void deleteById(Long id) {
        iVehiculeRepo.deleteById(id);
    }

 



}
