package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.repository.ReservationRepository;

/**
 * Scheduled job that runs every day at 07:00 and logs contracts (via reservations)
 * whose end date is within the next 48 hours.
 */
@Component
public class ContractExpirationSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(ContractExpirationSchedulerService.class);
    private final ReservationRepository reservationRepo;

    public ContractExpirationSchedulerService(ReservationRepository reservationRepo) {
        this.reservationRepo = reservationRepo;
    }

    // 0 0 7 * * *  -> 07:00:00 every day
    @Scheduled(cron = "0 0 7 * * *")
    public void reportExpiringContracts() {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(2); // 48h ≈ 2 days
        List<Reservation> expiring = reservationRepo.findByEndDateBetween(today, limit);
        if (expiring.isEmpty()) {
            log.info("Aucun contrat ne doit expirer dans les 48 heures.");
            return;
        }
        List<Long> ids = expiring.stream()
                .map(Reservation::getIdReservation)
                .collect(Collectors.toList());
        log.info("Contrats expirant dans les 48 heures – IDs réservation : {}", ids);
        // Vous pouvez ajouter ici l'envoi d'une notification par email ou autre.
    }
}
