package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Equipement;
import java.util.List;

public interface IEquipementService {
    List<Equipement> getAllEquipements();
    Equipement getEquipementById(Long id);
    Equipement addEquipement(Equipement equipement);
    Equipement updateEquipement(Long id, Equipement equipement);
    void deleteEquipement(Long id);
}
