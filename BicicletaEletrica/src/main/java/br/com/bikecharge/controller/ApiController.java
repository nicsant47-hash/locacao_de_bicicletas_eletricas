package br.com.bikecharge.controller;

import br.com.bikecharge.dao.*;
import br.com.bikecharge.model.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static spark.Spark.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class ApiController {

    public static void main(String[] args) {
        port(4567);

        // DAOs (suas implementações)
        ClienteDAO clienteDAO = new ClienteDAOImpl();
        BicicletaDAO bicicletaDAO = new BicicletaDAOImpl();
        AluguelDAO aluguelDAO = new AluguelDAOImpl();

        Gson gson = new Gson();

        // Health
        get("/health", (req, res) -> {
            res.type("text/plain");
            return "OK";
        });

        // ---------- CLIENTES ----------
        post("/api/clientes", (req, res) -> {
            try {
                JsonObject body = JsonParser.parseString(req.body()).getAsJsonObject();
                String nome = body.get("nome").getAsString();
                String cpf = body.get("cpf").getAsString();
                String dataNascimentoStr = body.get("dataNascimento").getAsString();
                String telefone = body.has("telefone") ? body.get("telefone").getAsString() : null;

                Cliente c = new Cliente();
                c.setNome(nome);
                c.setCpf(cpf);
                c.setDataNascimento(LocalDate.parse(dataNascimentoStr));
                c.setTelefone(telefone);

                Cliente saved = clienteDAO.save(c);
                res.status(201);
                res.type("application/json");
                return gson.toJson(saved);
            } catch (Exception e) {
                res.status(400);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });

        get("/api/clientes", (req, res) -> {
            res.type("application/json");
            return gson.toJson(clienteDAO.findAll());
        });

        // ---------- BICICLETAS ----------
        post("/api/bicicletas", (req, res) -> {
            try {
                JsonObject body = JsonParser.parseString(req.body()).getAsJsonObject();
                String modelo = body.get("modelo").getAsString();
                Integer autonomia = body.has("autonomiaKm") ? body.get("autonomiaKm").getAsInt() : null;
                String status = body.has("status") ? body.get("status").getAsString() : "disponivel";

                Bicicleta b = new Bicicleta();
                b.setModelo(modelo);
                b.setAutonomiaKm(autonomia);
                b.setStatus(status);

                Bicicleta saved = bicicletaDAO.save(b);
                res.status(201);
                res.type("application/json");
                return gson.toJson(saved);
            } catch (Exception e) {
                res.status(400);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });

        get("/api/bicicletas", (req, res) -> {
            res.type("application/json");
            return gson.toJson(bicicletaDAO.findAll());
        });

        put("/api/bicicletas/:id/status", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                JsonObject body = JsonParser.parseString(req.body()).getAsJsonObject();
                String status = body.get("status").getAsString();

                bicicletaDAO.updateStatus(id, status);
                return "{\"message\":\"status atualizado\"}";
            } catch (Exception e) {
                res.status(400);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });

        // ---------- ALUGUÉIS ----------
        post("/api/alugueis", (req, res) -> {
            try {
                JsonObject body = JsonParser.parseString(req.body()).getAsJsonObject();
                Integer clienteId = body.get("clienteId").getAsInt();
                Integer bicicletaId = body.get("bicicletaId").getAsInt();
                LocalDateTime dataInicio = LocalDateTime.parse(body.get("dataInicio").getAsString());
                LocalDateTime dataFimPrevisto = LocalDateTime.parse(body.get("dataFimPrevisto").getAsString());

                // valida cliente
                Optional<Cliente> oc = clienteDAO.findById(clienteId);
                if (oc.isEmpty()) {
                    res.status(400);
                    return "{\"error\":\"Cliente não encontrado\"}";
                }

                // verifica disponibilidade básica
                if (aluguelDAO.existsActiveByBicicleta(bicicletaId)) {
                    res.status(409);
                    return "{\"error\":\"Bicicleta já alugada\"}";
                }

                // calcula valor base (ex: R$15/hora)
                Duration dur = Duration.between(dataInicio, dataFimPrevisto);
                long horas = Math.max(1, dur.toHours());
                BigDecimal valorBase = BigDecimal.valueOf(horas * 15.0);

                Aluguel a = new Aluguel();
                a.setClienteId(clienteId);
                a.setBicicletaId(bicicletaId);
                a.setDataInicio(dataInicio);
                a.setDataFimPrevisto(dataFimPrevisto);
                a.setValorBase(valorBase);
                a.setValorMulta(BigDecimal.ZERO);
                a.setValorTotal(valorBase);

                Aluguel saved = aluguelDAO.save(a);

                // atualiza status da bicicleta para alugada
                bicicletaDAO.updateStatus(bicicletaId, "alugada");

                res.status(201);
                res.type("application/json");
                return gson.toJson(saved);
            } catch (Exception e) {
                res.status(400);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });

        get("/api/alugueis", (req, res) -> {
            res.type("application/json");
            return gson.toJson(aluguelDAO.findAll());
        });

        put("/api/alugueis/:id/devolucao", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                JsonObject body = JsonParser.parseString(req.body()).getAsJsonObject();
                LocalDateTime dataFimReal = LocalDateTime.parse(body.get("dataFimReal").getAsString());

                Optional<Aluguel> oa = aluguelDAO.findById(id);
                if (oa.isEmpty()) {
                    res.status(404);
                    return "{\"error\":\"Aluguel não encontrado\"}";
                }

                Aluguel aluguel = oa.get();
                if (aluguel.getDataFimReal() != null) {
                    res.status(409);
                    return "{\"error\":\"Devolução já registrada\"}";
                }

                aluguel.setDataFimReal(dataFimReal);

                if (dataFimReal.isAfter(aluguel.getDataFimPrevisto())) {
                    Duration atraso = Duration.between(aluguel.getDataFimPrevisto(), dataFimReal);
                    long horasAtraso = Math.max(1, atraso.toHours());
                    BigDecimal valorMulta = BigDecimal.valueOf(horasAtraso * 20.0);
                    aluguel.setValorMulta(valorMulta);
                } else {
                    aluguel.setValorMulta(BigDecimal.ZERO);
                }

                aluguel.setValorTotal(aluguel.getValorBase().add(aluguel.getValorMulta()));

                aluguelDAO.updateDevolucao(aluguel);

                // libera bicicleta
                bicicletaDAO.updateStatus(aluguel.getBicicletaId(), "disponivel");

                res.type("application/json");
                return gson.toJson(aluguel);
            } catch (Exception e) {
                res.status(400);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });
    }
}
