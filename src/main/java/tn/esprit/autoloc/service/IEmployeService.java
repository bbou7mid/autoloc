package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Employe;
import java.util.List;

public interface IEmployeService {
    List<Employe> getAllEmployes();
    Employe getEmployeById(Long id);
    Employe addEmploye(Employe employe);
    Employe updateEmploye(Long id, Employe employe);
    void deleteEmploye(Long id);
}
