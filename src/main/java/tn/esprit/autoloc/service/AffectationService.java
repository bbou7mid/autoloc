package tn.esprit.autoloc.service;

import java.time.LocalDate;
import tn.esprit.autoloc.dto.*;

/**
 * Service exposing the five affectation operations.
 */
public interface AffectationService {

    ReservationResponseDTO affecterReservationAClient(Long idReservation,
                                                      String nom,
                                                      String prenom);

    VehiculeDTO affecterVehiculeAEquipement(Long idVehicule,
                                            Long idEquipement);

    VehiculeDTO affecterMaintenanceAvehicule(String description,
                                             String immatriculation);

    ContratDTO affecterContratAReservation(LocalDate dateSignature,
                                          LocalDate dateDebut,
                                          LocalDate dateFin);

    ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO);
}
