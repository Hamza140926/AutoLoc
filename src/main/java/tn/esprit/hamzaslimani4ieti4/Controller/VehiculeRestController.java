package tn.esprit.hamzaslimani4ieti4.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import tn.esprit.hamzaslimani4ieti4.Service.IVehiculeService;  
import tn.esprit.hamzaslimani4ieti4.Domain.Vehicule;


@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicules")



public class VehiculeRestController {
    private final IVehiculeService vehiculeService;

    @PostMapping("add")
    Vehicule createVehicule(@RequestBody Vehicule vehicule) {
        return vehiculeService.add(vehicule);
    }


}
