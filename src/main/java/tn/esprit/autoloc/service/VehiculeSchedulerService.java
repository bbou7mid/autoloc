package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;

/**
 * Scheduler that runs every day at 01:00.
 * It sets the vehicle status to DISPONIBLE for reservations that have ended
 * and marks the reservation status as TERMINEE.
 */
@Component
public class VehiculeSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(VehiculeSchedulerService.class);
    private final ReservationRepository reservationRepo;
    private final VehiculeRepository vehiculeRepo;

    public VehiculeSchedulerService(ReservationRepository reservationRepo,
                                    VehiculeRepository vehiculeRepo) {
        this.reservationRepo = reservationRepo;
        this.vehiculeRepo = vehiculeRepo;
    }

    // 0 0 1 * * * -> 01:00:00 every day
    @Scheduled(cron = "0 0 1 * * *")
    public void releaseVehiclesAndCloseReservations() {
        LocalDate today = LocalDate.now();
        List<Reservation> toClose = reservationRepo.findAll()
                .stream()
                .filter(r -> r.getDateFin() != null && r.getDateFin().isBefore(today)
                        && r.getStatut() != StatutReservation.TERMINEE)
                .toList();
        if (toClose.isEmpty()) {
            log.info("Aucune reservation à clôturer aujourd'hui.");
            return;
        }
        for (Reservation reservation : toClose) {
            Vehicule vehicule = reservation.getVehicule();
            if (vehicule != null) {
                vehicule.setStatut(StatutVehicule.DISPONIBLE);
                vehiculeRepo.save(vehicule);
                log.debug("Véhicule {} mis à DISPONIBLE.", vehicule.getIdVehicule());
            }
            reservation.setStatut(StatutReservation.TERMINEE);
            reservationRepo.save(reservation);
            log.info("Reservation {} marquée comme TERMINEE.", reservation.getIdReservation());
        }
    }
}
