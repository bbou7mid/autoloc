package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Maintenance;
import java.util.List;

public interface IMaintenanceService {
    List<Maintenance> getAllMaintenances();
    Maintenance getMaintenanceById(Long id);
    Maintenance addMaintenance(Maintenance maintenance);
    Maintenance updateMaintenance(Long id, Maintenance maintenance);
    void deleteMaintenance(Long id);
}
