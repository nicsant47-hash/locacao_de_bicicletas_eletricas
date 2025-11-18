package br.com.bikecharge.dao;

import br.com.bikecharge.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteDAO {

    Cliente save(Cliente cliente) throws Exception;

    void update(Cliente cliente) throws Exception;

    Optional<Cliente> findById(Integer id) throws Exception;

    Optional<Cliente> findByCpf(String cpf) throws Exception;

    List<Cliente> findAll() throws Exception;

    void delete(Integer id) throws Exception;
}
