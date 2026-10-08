package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Paiement;
import java.util.List;

public interface IPaiementService {
    List<Paiement> getAllPaiements();
    Paiement getPaiementById(Long id);
    Paiement addPaiement(Paiement paiement);
    Paiement updatePaiement(Long id, Paiement paiement);
    void deletePaiement(Long id);
}
