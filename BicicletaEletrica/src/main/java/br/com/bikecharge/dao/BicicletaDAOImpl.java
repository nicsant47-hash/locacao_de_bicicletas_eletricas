package br.com.bikecharge.dao;

import br.com.bikecharge.model.Bicicleta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BicicletaDAOImpl implements BicicletaDAO {

    @Override
    public Bicicleta save(Bicicleta bicicleta) throws Exception {
        String sql = "INSERT INTO bicicleta (modelo, autonomia_km, status) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, bicicleta.getModelo());
            if (bicicleta.getAutonomiaKm() != null) {
                ps.setInt(2, bicicleta.getAutonomiaKm());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, bicicleta.getStatus());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    bicicleta.setId(rs.getInt(1));
                }
            }
        }
        return bicicleta;
    }

    @Override
    public void update(Bicicleta bicicleta) throws Exception {
        String sql = "UPDATE bicicleta SET modelo = ?, autonomia_km = ?, status = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bicicleta.getModelo());
            if (bicicleta.getAutonomiaKm() != null) {
                ps.setInt(2, bicicleta.getAutonomiaKm());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, bicicleta.getStatus());
            ps.setInt(4, bicicleta.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Nenhuma bicicleta atualizada para id=" + bicicleta.getId());
            }
        }
    }

    @Override
    public Optional<Bicicleta> findById(Integer id) throws Exception {
        String sql = "SELECT * FROM bicicleta WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToBicicleta(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Bicicleta> findAll() throws Exception {
        List<Bicicleta> lista = new ArrayList<>();
        String sql = "SELECT * FROM bicicleta ORDER BY id";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRowToBicicleta(rs));
            }
        }
        return lista;
    }

    @Override
    public void updateStatus(Integer id, String status) throws Exception {
        String sql = "UPDATE bicicleta SET status = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Nenhuma bicicleta atualizada para id=" + id);
            }
        }
    }

    @Override
    public void delete(Integer id) throws Exception {
        String sql = "DELETE FROM bicicleta WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new SQLException("Nenhuma bicicleta removida para id=" + id);
            }
        }
    }

    /**
     * Mapeia o ResultSet para o modelo Bicicleta.
     */
    private Bicicleta mapRowToBicicleta(ResultSet rs) throws SQLException {
        Bicicleta b = new Bicicleta();
        b.setId(rs.getInt("id"));
        b.setModelo(rs.getString("modelo"));

        int autonomia = rs.getInt("autonomia_km");
        if (!rs.wasNull()) {
            b.setAutonomiaKm(autonomia);
        } else {
            b.setAutonomiaKm(null);
        }

        b.setStatus(rs.getString("status"));
        return b;
    }
}
