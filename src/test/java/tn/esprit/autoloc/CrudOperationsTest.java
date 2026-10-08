package tn.esprit.autoloc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.service.IVehiculeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CrudOperationsTest {

    @Autowired
    private IVehiculeService vehiculeService;

    @Autowired
    private IContratService contratService;

    @Test
    void testVehiculeCrudOperations() {
        // 1. CREATE
        Vehicule v = new Vehicule(null, "999-TUN-9999", "Renault", "Clio 5",
                CategorieVehicule.CITADINE, new BigDecimal("95.00"), StatutVehicule.DISPONIBLE);
        Vehicule saved = vehiculeService.addVehicule(v);
        assertNotNull(saved.getIdVehicule());

        // 2. READ (findById & findAll)
        Vehicule retrieved = vehiculeService.getVehiculeById(saved.getIdVehicule());
        assertEquals("Renault", retrieved.getMarque());
        List<Vehicule> all = vehiculeService.getAllVehicules();
        assertFalse(all.isEmpty());

        // 3. UPDATE
        retrieved.setMarque("Renault Sport");
        Vehicule updated = vehiculeService.updateVehicule(retrieved.getIdVehicule(), retrieved);
        assertEquals("Renault Sport", updated.getMarque());

        // 4. DELETE
        Long idToDelete = saved.getIdVehicule();
        vehiculeService.deleteVehicule(idToDelete);
        assertThrows(IllegalArgumentException.class, () -> vehiculeService.getVehiculeById(idToDelete));
    }

    @Test
    void testContratCrudOperations() {
        // 1. CREATE
        Contrat c = new Contrat(null, LocalDate.now(), new BigDecimal("450.00"), true, null, null);
        Contrat saved = contratService.addContrat(c);
        assertNotNull(saved.getIdContrat());

        // 2. READ (findById & findAll)
        Contrat retrieved = contratService.getContratById(saved.getIdContrat());
        assertEquals(new BigDecimal("450.00"), retrieved.getMontantTotal());
        List<Contrat> all = contratService.getAllContrats();
        assertFalse(all.isEmpty());

        // 3. UPDATE
        retrieved.setMontantTotal(new BigDecimal("500.00"));
        Contrat updated = contratService.updateContrat(retrieved.getIdContrat(), retrieved);
        assertEquals(new BigDecimal("500.00"), updated.getMontantTotal());

        // 4. DELETE
        Long idToDelete = saved.getIdContrat();
        contratService.deleteContrat(idToDelete);
        assertThrows(IllegalArgumentException.class, () -> contratService.getContratById(idToDelete));
    }
}
