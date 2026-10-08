package tn.esprit.autoloc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final IVehiculeRepository vehiculeRepository;

    @Override
    public void run(String... args) {
        if (vehiculeRepository.count() == 0) {
            vehiculeRepository.saveAll(List.of(
                new Vehicule(null, "200-TUN-1234", "Peugeot", "208", CategorieVehicule.CITADINE, new BigDecimal("80.00"), StatutVehicule.DISPONIBLE),
                new Vehicule(null, "210-TUN-5678", "Volkswagen", "Golf 8", CategorieVehicule.BERLINE, new BigDecimal("120.00"), StatutVehicule.DISPONIBLE),
                new Vehicule(null, "220-TUN-9012", "Toyota", "RAV4", CategorieVehicule.SUV, new BigDecimal("160.00"), StatutVehicule.MAINTENANCE)
            ));
            log.info("Données de démonstration initialisées : 3 véhicules insérés avec succès.");
        }
    }
}
