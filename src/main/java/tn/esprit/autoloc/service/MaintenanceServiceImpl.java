package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.repository.IMaintenanceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MaintenanceServiceImpl implements IMaintenanceService {

    private final IMaintenanceRepository maintenanceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Maintenance> getAllMaintenances() {
        log.info("Récupération de toutes les maintenances");
        return maintenanceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Maintenance getMaintenanceById(Long id) {
        log.info("Recherche de la maintenance avec l'ID : {}", id);
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance introuvable avec l'ID : " + id));
    }

    @Override
    public Maintenance addMaintenance(Maintenance maintenance) {
        log.info("Ajout d'une nouvelle maintenance débutant le : {}", maintenance.getDateDebut());
        return maintenanceRepository.save(maintenance);
    }

    @Override
    public Maintenance updateMaintenance(Long id, Maintenance maintenance) {
        log.info("Mise à jour de la maintenance avec l'ID : {}", id);
        Maintenance existingMaintenance = getMaintenanceById(id);
        existingMaintenance.setDateDebut(maintenance.getDateDebut());
        existingMaintenance.setDateFin(maintenance.getDateFin());
        existingMaintenance.setDescription(maintenance.getDescription());
        existingMaintenance.setVehicule(maintenance.getVehicule());
        return maintenanceRepository.save(existingMaintenance);
    }

    @Override
    public void deleteMaintenance(Long id) {
        log.info("Suppression de la maintenance avec l'ID : {}", id);
        if (!maintenanceRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : maintenance introuvable avec l'ID : " + id);
        }
        maintenanceRepository.deleteById(id);
    }
}
