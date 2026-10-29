package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;

    @Override
    public Agence create(Agence agence) {
        log.info("Création d'une nouvelle agence");
        if (agence.getIdAgence() != null) {
            throw new IllegalArgumentException("L'identifiant doit être null lors de la création d'une agence");
        }
        validerAgence(agence);
        return agenceRepository.save(agence);
    }

    @Override
    @Transactional(readOnly = true)
    public Agence findById(Long id) {
        log.info("Recherche de l'agence avec l'ID : {}", id);
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Agence> findAll() {
        log.info("Récupération de toutes les agences");
        return agenceRepository.findAll();
    }

    @Override
    public Agence update(Long id, Agence agence) {
        log.info("Mise à jour de l'agence avec l'ID : {}", id);
        validerAgence(agence);
        Agence existant = findById(id);
        existant.setNom(agence.getNom());
        existant.setVille(agence.getVille());
        existant.setAdresse(agence.getAdresse());
        existant.setTelephone(agence.getTelephone());
        return agenceRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Suppression de l'agence avec l'ID : {}", id);
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", id);
        }
        agenceRepository.deleteById(id);
    }

    private void validerAgence(Agence agence) {
        if (agence.getNom() == null || agence.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de l'agence est obligatoire");
        }
        if (agence.getVille() == null || agence.getVille().isBlank()) {
            throw new IllegalArgumentException("La ville de l'agence est obligatoire");
        }
    }
}
