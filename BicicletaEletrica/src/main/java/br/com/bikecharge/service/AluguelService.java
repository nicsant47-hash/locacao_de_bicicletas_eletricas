package br.com.bikecharge.service;

import br.com.bikecharge.dao.AluguelDAO;
import br.com.bikecharge.dao.BicicletaDAO;
import br.com.bikecharge.dao.ClienteDAO;
import br.com.bikecharge.model.Aluguel;
import br.com.bikecharge.model.Bicicleta;
import br.com.bikecharge.model.Cliente;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class AluguelService {

    private final AluguelDAO aluguelDAO;
    private final ClienteDAO clienteDAO;
    private final BicicletaDAO bicicletaDAO;

    // Preços fixos do sistema
    private static final double VALOR_HORA = 15.0;
    private static final double MULTA_HORA = 20.0;

    public AluguelService(AluguelDAO aluguelDAO, ClienteDAO clienteDAO, BicicletaDAO bicicletaDAO) {
        this.aluguelDAO = aluguelDAO;
        this.clienteDAO = clienteDAO;
        this.bicicletaDAO = bicicletaDAO;
    }

    //--------------------------------------
    // CRIAÇÃO DO ALUGUEL (RETIRADA)
    //--------------------------------------
    public Aluguel criarAluguel(Integer clienteId, Integer bicicletaId,
                                LocalDateTime inicio, LocalDateTime fimPrevisto) throws Exception {

        // 1) Valida cliente
        Optional<Cliente> clienteOpt = clienteDAO.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new Exception("Cliente não encontrado.");
        }

        // 2) Valida bicicleta
        Optional<Bicicleta> bikeOpt = bicicletaDAO.findById(bicicletaId);
        if (bikeOpt.isEmpty()) {
            throw new Exception("Bicicleta não encontrada.");
        }

        // 3) Verifica disponibilidade
        if (aluguelDAO.existsActiveByBicicleta(bicicletaId)) {
            throw new Exception("A bicicleta já está alugada.");
        }

        // 4) Calcula valor-base
        Duration duracao = Duration.between(inicio, fimPrevisto);
        long horas = Math.max(1, duracao.toHours());   // sempre ao menos 1 hora

        BigDecimal valorBase = BigDecimal.valueOf(horas * VALOR_HORA);

        // 5) Cria aluguel pronto
        Aluguel aluguel = new Aluguel();
        aluguel.setClienteId(clienteId);
        aluguel.setBicicletaId(bicicletaId);
        aluguel.setDataInicio(inicio);
        aluguel.setDataFimPrevisto(fimPrevisto);
        aluguel.setValorBase(valorBase);
        aluguel.setValorMulta(BigDecimal.ZERO);
        aluguel.setValorTotal(valorBase);

        // 6) Salva no DAO
        Aluguel salvo = aluguelDAO.save(aluguel);

        // 7) Atualiza status da bicicleta
        bicicletaDAO.updateStatus(bicicletaId, "alugada");

        return salvo;
    }


    //--------------------------------------
    // DEVOLUÇÃO DO ALUGUEL
    //--------------------------------------
    public Aluguel registrarDevolucao(Integer aluguelId, LocalDateTime fimReal) throws Exception {

        Optional<Aluguel> aluguelOpt = aluguelDAO.findById(aluguelId);

        if (aluguelOpt.isEmpty()) {
            throw new Exception("Aluguel não encontrado.");
        }

        Aluguel aluguel = aluguelOpt.get();

        if (aluguel.getDataFimReal() != null) {
            throw new Exception("Este aluguel já foi finalizado.");
        }

        aluguel.setDataFimReal(fimReal);

        // 1) Cálculo da multa
        BigDecimal multa = BigDecimal.ZERO;

        if (fimReal.isAfter(aluguel.getDataFimPrevisto())) {
            Duration atraso = Duration.between(aluguel.getDataFimPrevisto(), fimReal);
            long horasAtraso = Math.max(1, atraso.toHours());
            multa = BigDecimal.valueOf(horasAtraso * MULTA_HORA);
        }

        aluguel.setValorMulta(multa);
        aluguel.setValorTotal(aluguel.getValorBase().add(multa));

        // 2) Atualiza no banco
        aluguelDAO.updateDevolucao(aluguel);

        // 3) Libera bicicleta
        bicicletaDAO.updateStatus(aluguel.getBicicletaId(), "disponivel");

        return aluguel;
    }
}
