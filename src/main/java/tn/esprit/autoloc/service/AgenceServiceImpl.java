package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Agence> getAllAgences() {
        log.info("Récupération de toutes les agences");
        return agenceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Agence getAgenceById(Long id) {
        log.info("Recherche de l'agence avec l'ID : {}", id);
        return agenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agence introuvable avec l'ID : " + id));
    }

    @Override
    public Agence addAgence(Agence agence) {
        log.info("Ajout d'une nouvelle agence : {}", agence.getNom());
        return agenceRepository.save(agence);
    }

    @Override
    public Agence updateAgence(Long id, Agence agence) {
        log.info("Mise à jour de l'agence avec l'ID : {}", id);
        Agence existingAgence = getAgenceById(id);
        existingAgence.setNom(agence.getNom());
        existingAgence.setVille(agence.getVille());
        existingAgence.setAdresse(agence.getAdresse());
        existingAgence.setTelephone(agence.getTelephone());
        return agenceRepository.save(existingAgence);
    }

    @Override
    public void deleteAgence(Long id) {
        log.info("Suppression de l'agence avec l'ID : {}", id);
        if (!agenceRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : agence introuvable avec l'ID : " + id);
        }
        agenceRepository.deleteById(id);
    }
}
