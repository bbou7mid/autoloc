package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.repository.IEquipementRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EquipementServiceImpl implements IEquipementService {

    private final IEquipementRepository equipementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Equipement> getAllEquipements() {
        log.info("Récupération de tous les équipements");
        return equipementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Equipement getEquipementById(Long id) {
        log.info("Recherche de l'équipement avec l'ID : {}", id);
        return equipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Équipement introuvable avec l'ID : " + id));
    }

    @Override
    public Equipement addEquipement(Equipement equipement) {
        log.info("Ajout d'un nouvel équipement : {}", equipement.getLibelle());
        return equipementRepository.save(equipement);
    }

    @Override
    public Equipement updateEquipement(Long id, Equipement equipement) {
        log.info("Mise à jour de l'équipement avec l'ID : {}", id);
        Equipement existingEquipement = getEquipementById(id);
        existingEquipement.setLibelle(equipement.getLibelle());
        return equipementRepository.save(existingEquipement);
    }

    @Override
    public void deleteEquipement(Long id) {
        log.info("Suppression de l'équipement avec l'ID : {}", id);
        if (!equipementRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : équipement introuvable avec l'ID : " + id);
        }
        equipementRepository.deleteById(id);
    }
}
