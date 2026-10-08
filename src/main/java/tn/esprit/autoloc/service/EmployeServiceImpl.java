package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.repository.IEmployeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Employe> getAllEmployes() {
        log.info("Récupération de tous les employés");
        return employeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Employe getEmployeById(Long id) {
        log.info("Recherche de l'employé avec l'ID : {}", id);
        return employeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employé introuvable avec l'ID : " + id));
    }

    @Override
    public Employe addEmploye(Employe employe) {
        log.info("Ajout d'un nouvel employé : {} {}", employe.getNom(), employe.getPrenom());
        return employeRepository.save(employe);
    }

    @Override
    public Employe updateEmploye(Long id, Employe employe) {
        log.info("Mise à jour de l'employé avec l'ID : {}", id);
        Employe existingEmploye = getEmployeById(id);
        existingEmploye.setNom(employe.getNom());
        existingEmploye.setPrenom(employe.getPrenom());
        existingEmploye.setRole(employe.getRole());
        existingEmploye.setAgence(employe.getAgence());
        return employeRepository.save(existingEmploye);
    }

    @Override
    public void deleteEmploye(Long id) {
        log.info("Suppression de l'employé avec l'ID : {}", id);
        if (!employeRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : employé introuvable avec l'ID : " + id);
        }
        employeRepository.deleteById(id);
    }
}
