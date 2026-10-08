package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.repository.IReservationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        log.info("Récupération de toutes les réservations");
        return reservationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation getReservationById(Long id) {
        log.info("Recherche de la réservation avec l'ID : {}", id);
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable avec l'ID : " + id));
    }

    @Override
    public Reservation addReservation(Reservation reservation) {
        log.info("Ajout d'une nouvelle réservation débutant le : {}", reservation.getDateDebut());
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation updateReservation(Long id, Reservation reservation) {
        log.info("Mise à jour de la réservation avec l'ID : {}", id);
        Reservation existingReservation = getReservationById(id);
        existingReservation.setDateDebut(reservation.getDateDebut());
        existingReservation.setDateFin(reservation.getDateFin());
        existingReservation.setStatut(reservation.getStatut());
        existingReservation.setClient(reservation.getClient());
        existingReservation.setVehicule(reservation.getVehicule());
        return reservationRepository.save(existingReservation);
    }

    @Override
    public void deleteReservation(Long id) {
        log.info("Suppression de la réservation avec l'ID : {}", id);
        if (!reservationRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : réservation introuvable avec l'ID : " + id);
        }
        reservationRepository.deleteById(id);
    }
}
