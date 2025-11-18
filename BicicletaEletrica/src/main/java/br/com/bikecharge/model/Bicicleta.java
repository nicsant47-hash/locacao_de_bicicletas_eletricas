package br.com.bikecharge.model;

public class Bicicleta {

    private Integer id;
    private String modelo;
    private Integer autonomiaKm;
    private String status; // "disponivel", "alugada", "manutencao"

    public Bicicleta() {}

    public Bicicleta(Integer id, String modelo, Integer autonomiaKm, String status) {
        this.id = id;
        this.modelo = modelo;
        this.autonomiaKm = autonomiaKm;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAutonomiaKm() { return autonomiaKm; }
    public void setAutonomiaKm(Integer autonomiaKm) { this.autonomiaKm = autonomiaKm; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // utilitário rápido
    public boolean isDisponivel() {
        return status != null && status.equalsIgnoreCase("disponivel");
    }

    @Override
    public String toString() {
        return "Bicicleta{" +
                "id=" + id +
                ", modelo='" + modelo + '\'' +
                ", autonomiaKm=" + autonomiaKm +
                ", status='" + status + '\'' +
                '}';
    }
}
