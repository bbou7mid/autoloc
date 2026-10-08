package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.repository.IClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        log.info("Récupération de tous les clients");
        return clientRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Client getClientById(Long id) {
        log.info("Recherche du client avec l'ID : {}", id);
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable avec l'ID : " + id));
    }

    @Override
    public Client addClient(Client client) {
        log.info("Ajout d'un nouveau client : {} {}", client.getNom(), client.getPrenom());
        return clientRepository.save(client);
    }

    @Override
    public Client updateClient(Long id, Client client) {
        log.info("Mise à jour du client avec l'ID : {}", id);
        Client existingClient = getClientById(id);
        existingClient.setNom(client.getNom());
        existingClient.setPrenom(client.getPrenom());
        existingClient.setEmail(client.getEmail());
        existingClient.setTelephone(client.getTelephone());
        existingClient.setNumPermis(client.getNumPermis());
        existingClient.setDateInscription(client.getDateInscription());
        return clientRepository.save(existingClient);
    }

    @Override
    public void deleteClient(Long id) {
        log.info("Suppression du client avec l'ID : {}", id);
        if (!clientRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : client introuvable avec l'ID : " + id);
        }
        clientRepository.deleteById(id);
    }
}
