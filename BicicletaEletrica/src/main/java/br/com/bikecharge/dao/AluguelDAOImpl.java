package br.com.bikecharge.dao;

import br.com.bikecharge.model.Aluguel;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AluguelDAOImpl implements AluguelDAO {

    @Override
    public Aluguel save(Aluguel aluguel) throws Exception {
        String sql = "INSERT INTO aluguel (cliente_id, bicicleta_id, data_inicio, data_fim_previsto, data_fim_real, valor_base, valor_multa, valor_total) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, aluguel.getClienteId());
            ps.setInt(2, aluguel.getBicicletaId());
            ps.setTimestamp(3, Timestamp.valueOf(aluguel.getDataInicio()));
            ps.setTimestamp(4, Timestamp.valueOf(aluguel.getDataFimPrevisto()));

            if (aluguel.getDataFimReal() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(aluguel.getDataFimReal()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            ps.setBigDecimal(6, aluguel.getValorBase());
            ps.setBigDecimal(7, aluguel.getValorMulta());
            ps.setBigDecimal(8, aluguel.getValorTotal());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    aluguel.setId(rs.getInt(1));
            }
        }

        return aluguel;
    }

    @Override
    public Optional<Aluguel> findById(Integer id) throws Exception {
        String sql = "SELECT * FROM aluguel WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Aluguel> findAll() throws Exception {
        List<Aluguel> lista = new ArrayList<>();
        String sql = "SELECT * FROM aluguel ORDER BY id DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapRow(rs));
        }

        return lista;
    }

    @Override
    public boolean existsActiveByBicicleta(Integer bicicletaId) throws Exception {
        String sql = "SELECT COUNT(*) AS qtd FROM aluguel WHERE bicicleta_id = ? AND data_fim_real IS NULL";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bicicletaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("qtd") > 0;
                }
            }
        }
        return false;
    }

    @Override
    public void updateDevolucao(Aluguel aluguel) throws Exception {
        String sql = "UPDATE aluguel SET data_fim_real = ?, valor_multa = ?, valor_total = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(aluguel.getDataFimReal()));
            ps.setBigDecimal(2, aluguel.getValorMulta());
            ps.setBigDecimal(3, aluguel.getValorTotal());
            ps.setInt(4, aluguel.getId());

            ps.executeUpdate();
        }
    }

    // ========== MAPEAMENTO DO RESULTSET PARA OBJETO ==========
    private Aluguel mapRow(ResultSet rs) throws SQLException {
        Aluguel a = new Aluguel();

        a.setId(rs.getInt("id"));
        a.setClienteId(rs.getInt("cliente_id"));
        a.setBicicletaId(rs.getInt("bicicleta_id"));

        Timestamp inicio = rs.getTimestamp("data_inicio");
        if (inicio != null) a.setDataInicio(inicio.toLocalDateTime());

        Timestamp fimPrev = rs.getTimestamp("data_fim_previsto");
        if (fimPrev != null) a.setDataFimPrevisto(fimPrev.toLocalDateTime());

        Timestamp fimReal = rs.getTimestamp("data_fim_real");
        if (fimReal != null) a.setDataFimReal(fimReal.toLocalDateTime());

        BigDecimal vb = rs.getBigDecimal("valor_base");
        a.setValorBase(vb != null ? vb : BigDecimal.ZERO);

        BigDecimal vm = rs.getBigDecimal("valor_multa");
        a.setValorMulta(vm != null ? vm : BigDecimal.ZERO);

        BigDecimal vt = rs.getBigDecimal("valor_total");
        a.setValorTotal(vt != null ? vt : BigDecimal.ZERO);

        return a;
    }
}
