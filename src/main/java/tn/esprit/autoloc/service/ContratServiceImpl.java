package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.repository.IContratRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> getAllContrats() {
        log.info("Récupération de tous les contrats");
        return contratRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat getContratById(Long id) {
        log.info("Recherche du contrat avec l'ID : {}", id);
        return contratRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contrat introuvable avec l'ID : " + id));
    }

    @Override
    public Contrat addContrat(Contrat contrat) {
        log.info("Création d'un nouveau contrat signé le : {}", contrat.getDateSignature());
        return contratRepository.save(contrat);
    }

    @Override
    public Contrat updateContrat(Long id, Contrat contrat) {
        log.info("Mise à jour du contrat avec l'ID : {}", id);
        Contrat existingContrat = getContratById(id);
        existingContrat.setDateSignature(contrat.getDateSignature());
        existingContrat.setMontantTotal(contrat.getMontantTotal());
        existingContrat.setValide(contrat.isValide());
        existingContrat.setReservation(contrat.getReservation());
        return contratRepository.save(existingContrat);
    }

    @Override
    public void deleteContrat(Long id) {
        log.info("Suppression du contrat avec l'ID : {}", id);
        if (!contratRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : contrat introuvable avec l'ID : " + id);
        }
        contratRepository.deleteById(id);
    }
}
