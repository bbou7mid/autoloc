package tn.esprit.autoloc.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.dto.*;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.mapper.AffectationMapper;
import tn.esprit.autoloc.repository.*;

@Service
@Transactional
public class AffectationServiceImpl implements AffectationService {

    private final ReservationRepository reservationRepo;
    private final VehiculeRepository vehiculeRepo;
    private final EquipementRepository equipementRepo;
    private final MaintenanceRepository maintenanceRepo;
    private final ContratRepository contratRepo;
    private final ClientRepository clientRepo;
    private final AffectationMapper mapper;

    public AffectationServiceImpl(ReservationRepository reservationRepo,
                                 VehiculeRepository vehiculeRepo,
                                 EquipementRepository equipementRepo,
                                 MaintenanceRepository maintenanceRepo,
                                 ContratRepository contratRepo,
                                 ClientRepository clientRepo,
                                 AffectationMapper mapper) {
        this.reservationRepo = reservationRepo;
        this.vehiculeRepo = vehiculeRepo;
        this.equipementRepo = equipementRepo;
        this.maintenanceRepo = maintenanceRepo;
        this.contratRepo = contratRepo;
        this.clientRepo = clientRepo;
        this.mapper = mapper;
    }

    @Override
    public ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom) {
        Reservation reservation = reservationRepo.findById(idReservation)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        Client client = clientRepo.findAll()
                .stream()
                .filter(c -> c.getNom().equalsIgnoreCase(nom) && c.getPrenom().equalsIgnoreCase(prenom))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        reservation.setClient(client);
        reservationRepo.save(reservation);
        return mapper.toReservationResponseDTO(reservation, client);
    }

    @Override
    public VehiculeDTO affecterVehiculeAEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = vehiculeRepo.findById(idVehicule)
                .orElseThrow(() -> new IllegalArgumentException("Vehicule not found"));
        Equipement equipement = equipementRepo.findById(idEquipement)
                .orElseThrow(() -> new IllegalArgumentException("Equipement not found"));
        vehicule.addEquipement(equipement);
        vehiculeRepo.save(vehicule);
        return mapper.toVehiculeDTO(vehicule);
    }

    @Override
    public VehiculeDTO affecterMaintenanceAvehicule(String description, String immatriculation) {
        Vehicule vehicule = vehiculeRepo.findAll()
                .stream()
                .filter(v -> v.getImmatriculation().equalsIgnoreCase(immatriculation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Vehicule not found"));
        Maintenance maintenance = new Maintenance();
        maintenance.setDescription(description);
        maintenance.setDate(LocalDate.now());
        vehicule.addMaintenance(maintenance);
        vehiculeRepo.save(vehicule);
        return mapper.toVehiculeDTO(vehicule);
    }

    @Override
    public ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin) {
        Reservation reservation = reservationRepo.findAll()
                .stream()
                .filter(r -> r.getDateDebut().equals(dateDebut) && r.getDateFin().equals(dateFin))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        Contrat contrat = new Contrat();
        contrat.setDateSignature(dateSignature);
        contrat.setReservation(reservation);
        contratRepo.save(contrat);
        return mapper.toContratDTO(contrat);
    }

    @Override
    public ReservationResponseDTO creerReservationEtContratAssocie(@Valid ReservationWithContratRequestDTO requestDTO) {
        // Find or create client
        Client client = clientRepo.findAll()
                .stream()
                .filter(c -> c.getNom().equalsIgnoreCase(requestDTO.getNom()) && c.getPrenom().equalsIgnoreCase(requestDTO.getPrenom()))
                .findFirst()
                .orElseGet(() -> {
                    Client newClient = new Client();
                    newClient.setNom(requestDTO.getNom());
                    newClient.setPrenom(requestDTO.getPrenom());
                    newClient.setDateInscription(LocalDate.now());
                    return clientRepo.save(newClient);
                });
        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setDateDebut(requestDTO.getDateDebut());
        reservation.setDateFin(requestDTO.getDateFin());
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservationRepo.save(reservation);
        // Create contract linked to reservation
        Contrat contrat = new Contrat();
        contrat.setDateSignature(requestDTO.getDateSignature());
        contrat.setReservation(reservation);
        contratRepo.save(contrat);
        return mapper.toReservationResponseDTO(reservation, client);
    }
}
