package com.ironcladbox.dao;

import com.ironcladbox.model.Atleta;
import com.ironcladbox.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AtletaDAO implements IAtletaDAO {

    @Override
    public Atleta obtenerPorId(int id) {
        String sql = "SELECT a.*, u.* FROM atletas a JOIN usuarios u ON a.id_usuario = u.id_usuario WHERE a.id_atleta = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAtleta(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Atleta obtenerPorIdUsuario(int idUsuario) {
        String sql = "SELECT a.*, u.* FROM atletas a JOIN usuarios u ON a.id_usuario = u.id_usuario WHERE a.id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAtleta(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Atleta> obtenerTodos() {
        List<Atleta> atletas = new ArrayList<>();
        String sql = "SELECT a.*, u.* FROM atletas a JOIN usuarios u ON a.id_usuario = u.id_usuario WHERE u.activo = true";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                atletas.add(mapResultSetToAtleta(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return atletas;
    }

    @Override
    public void guardar(Atleta atleta) {
        String sql = "INSERT INTO atletas (id_usuario, peso, altura) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, atleta.getIdUsuario());
            stmt.setDouble(2, atleta.getPeso());
            stmt.setDouble(3, atleta.getAltura());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    atleta.setIdAtleta(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Atleta atleta) {
        String sql = "UPDATE atletas SET peso = ?, altura = ? WHERE id_atleta = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, atleta.getPeso());
            stmt.setDouble(2, atleta.getAltura());
            stmt.setInt(3, atleta.getIdAtleta());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM atletas WHERE id_atleta = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Atleta mapResultSetToAtleta(ResultSet rs) throws SQLException {
        Atleta atleta = new Atleta();
        atleta.setIdAtleta(rs.getInt("id_atleta"));
        atleta.setIdUsuario(rs.getInt("id_usuario"));
        atleta.setEmail(rs.getString("email"));
        atleta.setNombre(rs.getString("nombre"));
        atleta.setApellido(rs.getString("apellido"));
        atleta.setTelefono(rs.getString("telefono"));
        atleta.setPeso(rs.getDouble("peso"));
        atleta.setAltura(rs.getDouble("altura"));
        atleta.setActivo(rs.getBoolean("activo"));
        return atleta;
    }
}
