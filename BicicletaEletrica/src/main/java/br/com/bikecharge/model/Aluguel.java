package br.com.bikecharge.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Aluguel {

    private Integer id;
    private Integer clienteId;
    private Integer bicicletaId;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFimPrevisto;
    private LocalDateTime dataFimReal; // pode ser null enquanto ativo

    private BigDecimal valorBase;
    private BigDecimal valorMulta;
    private BigDecimal valorTotal;

    public Aluguel() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public Integer getBicicletaId() { return bicicletaId; }
    public void setBicicletaId(Integer bicicletaId) { this.bicicletaId = bicicletaId; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFimPrevisto() { return dataFimPrevisto; }
    public void setDataFimPrevisto(LocalDateTime dataFimPrevisto) { this.dataFimPrevisto = dataFimPrevisto; }

    public LocalDateTime getDataFimReal() { return dataFimReal; }
    public void setDataFimReal(LocalDateTime dataFimReal) { this.dataFimReal = dataFimReal; }

    public BigDecimal getValorBase() { return valorBase; }
    public void setValorBase(BigDecimal valorBase) { this.valorBase = valorBase; }

    public BigDecimal getValorMulta() { return valorMulta; }
    public void setValorMulta(BigDecimal valorMulta) { this.valorMulta = valorMulta; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    // Método utilitário opcional
    public boolean isAtivo() {
        return dataFimReal == null;
    }
}
