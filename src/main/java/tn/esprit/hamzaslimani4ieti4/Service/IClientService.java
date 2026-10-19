package tn.esprit.hamzaslimani4ieti4.Service;

import java.util.List;
import tn.esprit.hamzaslimani4ieti4.Domain.Client;

public interface IClientService {
    Client getById(Long id);
    Client add(Client client);
    List<Client> findAll();
    Client update(Client client);
    void deleteById(Long id);
}
