package com.ironcladbox.dao;

import com.ironcladbox.model.Membresia;
import com.ironcladbox.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembresiaDAO implements IMembresiaDAO {

    @Override
    public Membresia obtenerPorId(int id) {
        String sql = "SELECT * FROM membresias WHERE id_membresia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMembresia(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Membresia> obtenerTodas() {
        List<Membresia> membresias = new ArrayList<>();
        String sql = "SELECT * FROM membresias ORDER BY precio";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                membresias.add(mapResultSetToMembresia(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return membresias;
    }

    @Override
    public List<Membresia> obtenerActivas() {
        List<Membresia> membresias = new ArrayList<>();
        String sql = "SELECT * FROM membresias WHERE activa = true ORDER BY precio";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                membresias.add(mapResultSetToMembresia(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return membresias;
    }

    @Override
    public void guardar(Membresia membresia) {
        String sql = "INSERT INTO membresias (nombre, descripcion, precio, duracion_dias, beneficios, activa) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, membresia.getNombre());
            stmt.setString(2, membresia.getDescripcion());
            stmt.setDouble(3, membresia.getPrecio());
            stmt.setInt(4, membresia.getDuracionDias());
            stmt.setString(5, membresia.getBeneficios());
            stmt.setBoolean(6, membresia.isActiva());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    membresia.setIdMembresia(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Membresia membresia) {
        String sql = "UPDATE membresias SET nombre = ?, descripcion = ?, precio = ?, " +
                    "duracion_dias = ?, beneficios = ?, activa = ? WHERE id_membresia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, membresia.getNombre());
            stmt.setString(2, membresia.getDescripcion());
            stmt.setDouble(3, membresia.getPrecio());
            stmt.setInt(4, membresia.getDuracionDias());
            stmt.setString(5, membresia.getBeneficios());
            stmt.setBoolean(6, membresia.isActiva());
            stmt.setInt(7, membresia.getIdMembresia());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "UPDATE membresias SET activa = false WHERE id_membresia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Membresia mapResultSetToMembresia(ResultSet rs) throws SQLException {
        Membresia membresia = new Membresia();
        membresia.setIdMembresia(rs.getInt("id_membresia"));
        membresia.setNombre(rs.getString("nombre"));
        membresia.setDescripcion(rs.getString("descripcion"));
        membresia.setPrecio(rs.getDouble("precio"));
        membresia.setDuracionDias(rs.getInt("duracion_dias"));
        membresia.setBeneficios(rs.getString("beneficios"));
        membresia.setActiva(rs.getBoolean("activa"));
        return membresia;
    }
}
