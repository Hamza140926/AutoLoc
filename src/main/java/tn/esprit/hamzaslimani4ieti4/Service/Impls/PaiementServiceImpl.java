package tn.esprit.hamzaslimani4ieti4.Service.Impls;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.hamzaslimani4ieti4.Domain.Paiement;
import tn.esprit.hamzaslimani4ieti4.Repository.IPaiementRepo;
import tn.esprit.hamzaslimani4ieti4.Service.EntityLookup;
import tn.esprit.hamzaslimani4ieti4.Service.IContratService;
import tn.esprit.hamzaslimani4ieti4.Service.IPaiementService;

@Service
@RequiredArgsConstructor
@Transactional
public class PaiementServiceImpl implements IPaiementService {

    private final IPaiementRepo paiementRepo;
    private final IContratService contratService;

    @Override
    @Transactional(readOnly = true)
    public Paiement getById(Long id) {
        return EntityLookup.getById(paiementRepo, id, "Paiement");
    }

    @Override
    public Paiement add(Paiement paiement) {
        EntityLookup.requireNew(paiement.getIdPaiement());
        resolveContrat(paiement);
        return paiementRepo.save(paiement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paiement> findAll() {
        return paiementRepo.findAll();
    }

    @Override
    public Paiement update(Paiement paiement) {
        Paiement existing = getById(paiement.getIdPaiement());
        resolveContrat(paiement);
        existing.setMontant(paiement.getMontant());
        existing.setDatePaiement(paiement.getDatePaiement());
        existing.setModePaiement(paiement.getModePaiement());
        existing.setContrat(paiement.getContrat());
        return paiementRepo.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        paiementRepo.delete(getById(id));
    }

    private void resolveContrat(Paiement paiement) {
        if (paiement.getContrat() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A paiement must reference an existing contrat");
        }
        paiement.setContrat(contratService.getById(paiement.getContrat().getIdContrat()));
    }
}
