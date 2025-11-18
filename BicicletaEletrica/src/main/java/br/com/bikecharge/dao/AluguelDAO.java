package br.com.bikecharge.dao;

import br.com.bikecharge.model.Aluguel;
import java.util.List;
import java.util.Optional;

public interface AluguelDAO {

    Aluguel save(Aluguel aluguel) throws Exception;

    Optional<Aluguel> findById(Integer id) throws Exception;

    List<Aluguel> findAll() throws Exception;

    boolean existsActiveByBicicleta(Integer bicicletaId) throws Exception;

    void updateDevolucao(Aluguel aluguel) throws Exception;
}
