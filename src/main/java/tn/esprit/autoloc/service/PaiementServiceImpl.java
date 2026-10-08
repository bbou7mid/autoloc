package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.repository.IPaiementRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaiementServiceImpl implements IPaiementService {

    private final IPaiementRepository paiementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Paiement> getAllPaiements() {
        log.info("Récupération de tous les paiements");
        return paiementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Paiement getPaiementById(Long id) {
        log.info("Recherche du paiement avec l'ID : {}", id);
        return paiementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paiement introuvable avec l'ID : " + id));
    }

    @Override
    public Paiement addPaiement(Paiement paiement) {
        log.info("Enregistrement d'un nouveau paiement de montant : {}", paiement.getMontant());
        return paiementRepository.save(paiement);
    }

    @Override
    public Paiement updatePaiement(Long id, Paiement paiement) {
        log.info("Mise à jour du paiement avec l'ID : {}", id);
        Paiement existingPaiement = getPaiementById(id);
        existingPaiement.setMontant(paiement.getMontant());
        existingPaiement.setDatePaiement(paiement.getDatePaiement());
        existingPaiement.setModePaiement(paiement.getModePaiement());
        existingPaiement.setContrat(paiement.getContrat());
        return paiementRepository.save(existingPaiement);
    }

    @Override
    public void deletePaiement(Long id) {
        log.info("Suppression du paiement avec l'ID : {}", id);
        if (!paiementRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : paiement introuvable avec l'ID : " + id);
        }
        paiementRepository.deleteById(id);
    }
}
