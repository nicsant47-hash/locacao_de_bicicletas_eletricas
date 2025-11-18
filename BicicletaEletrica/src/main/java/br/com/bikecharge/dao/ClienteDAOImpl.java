package br.com.bikecharge.dao;

import br.com.bikecharge.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public Cliente save(Cliente cliente) throws Exception {
        String sql = "INSERT INTO cliente (nome, cpf, data_nascimento, telefone) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());

            if (cliente.getDataNascimento() != null) {
                ps.setDate(3, Date.valueOf(cliente.getDataNascimento()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            if (cliente.getTelefone() != null) {
                ps.setString(4, cliente.getTelefone());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    cliente.setId(rs.getInt(1));
            }
        }

        return cliente;
    }

    @Override
    public void update(Cliente cliente) throws Exception {
        String sql = "UPDATE cliente SET nome = ?, cpf = ?, data_nascimento = ?, telefone = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());

            if (cliente.getDataNascimento() != null) {
                ps.setDate(3, Date.valueOf(cliente.getDataNascimento()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            if (cliente.getTelefone() != null) {
                ps.setString(4, cliente.getTelefone());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            ps.setInt(5, cliente.getId());

            int updated = ps.executeUpdate();
            if (updated == 0)
                throw new SQLException("Nenhum cliente atualizado! ID inválido: " + cliente.getId());
        }
    }

    @Override
    public Optional<Cliente> findById(Integer id) throws Exception {
        String sql = "SELECT * FROM cliente WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return Optional.of(mapRow(rs));
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Cliente> findByCpf(String cpf) throws Exception {
        String sql = "SELECT * FROM cliente WHERE cpf = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cpf);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return Optional.of(mapRow(rs));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Cliente> findAll() throws Exception {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente ORDER BY nome";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }

        return lista;
    }

    @Override
    public void delete(Integer id) throws Exception {
        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int deleted = ps.executeUpdate();
            if (deleted == 0)
                throw new SQLException("Nenhum cliente removido! ID inválido: " + id);
        }
    }

    // MAPEAMENTO — ResultSet → Cliente
    private Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();

        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));

        Date d = rs.getDate("data_nascimento");
        if (d != null) c.setDataNascimento(d.toLocalDate());

        String tel = rs.getString("telefone");
        c.setTelefone(tel);

        return c;
    }
}
