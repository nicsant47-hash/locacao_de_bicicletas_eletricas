package br.com.bikecharge.dao;

import br.com.bikecharge.model.Bicicleta;

import java.util.List;
import java.util.Optional;

public interface BicicletaDAO {

    /**
     * Persiste uma nova bicicleta e retorna o objeto com id gerado.
     */
    Bicicleta save(Bicicleta bicicleta) throws Exception;

    /**
     * Atualiza os dados da bicicleta (modelo, autonomia, status).
     */
    void update(Bicicleta bicicleta) throws Exception;

    /**
     * Busca por id.
     */
    Optional<Bicicleta> findById(Integer id) throws Exception;

    /**
     * Lista todas as bicicletas.
     */
    List<Bicicleta> findAll() throws Exception;

    /**
     * Atualiza apenas o status da bicicleta (ex: disponivel, alugada, manutencao).
     */
    void updateStatus(Integer id, String status) throws Exception;

    /**
     * Remove a bicicleta por id.
     */
    void delete(Integer id) throws Exception;
}
