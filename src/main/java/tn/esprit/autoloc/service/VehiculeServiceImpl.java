package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> getAllVehicules() {
        log.info("Récupération de tous les véhicules");
        return vehiculeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicule getVehiculeById(Long id) {
        log.info("Recherche du véhicule avec l'ID : {}", id);
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule introuvable avec l'ID : " + id));
    }

    @Override
    public Vehicule addVehicule(Vehicule vehicule) {
        log.info("Ajout d'un nouveau véhicule : immatriculation {}", vehicule.getImmatriculation());
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule updateVehicule(Long id, Vehicule vehicule) {
        log.info("Mise à jour du véhicule avec l'ID : {}", id);
        Vehicule existingVehicule = getVehiculeById(id);
        existingVehicule.setImmatriculation(vehicule.getImmatriculation());
        existingVehicule.setMarque(vehicule.getMarque());
        existingVehicule.setModele(vehicule.getModele());
        existingVehicule.setCategorie(vehicule.getCategorie());
        existingVehicule.setTarifJournalier(vehicule.getTarifJournalier());
        existingVehicule.setStatut(vehicule.getStatut());
        existingVehicule.setAgence(vehicule.getAgence());
        return vehiculeRepository.save(existingVehicule);
    }

    @Override
    public void deleteVehicule(Long id) {
        log.info("Suppression du véhicule avec l'ID : {}", id);
        if (!vehiculeRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : véhicule introuvable avec l'ID : " + id);
        }
        vehiculeRepository.deleteById(id);
    }
}
