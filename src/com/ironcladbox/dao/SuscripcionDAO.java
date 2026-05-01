package com.ironcladbox.dao;

import com.ironcladbox.model.Suscripcion;
import com.ironcladbox.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuscripcionDAO implements ISuscripcionDAO {

    @Override
    public Suscripcion obtenerPorId(int id) {
        String sql = "SELECT s.*, m.nombre as nombre_membresia, m.precio FROM suscripciones s " +
                    "JOIN membresias m ON s.id_membresia = m.id_membresia WHERE s.id_suscripcion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSuscripcion(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Suscripcion obtenerActivaDeAtleta(int idAtleta) {
        String sql = "SELECT s.*, m.nombre as nombre_membresia, m.precio FROM suscripciones s " +
                    "JOIN membresias m ON s.id_membresia = m.id_membresia " +
                    "WHERE s.id_atleta = ? AND s.activa = true AND s.fecha_fin >= CURRENT_DATE ORDER BY s.fecha_fin DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAtleta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSuscripcion(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Suscripcion> obtenerPorAtleta(int idAtleta) {
        List<Suscripcion> suscripciones = new ArrayList<>();
        String sql = "SELECT s.*, m.nombre as nombre_membresia, m.precio FROM suscripciones s " +
                    "JOIN membresias m ON s.id_membresia = m.id_membresia " +
                    "WHERE s.id_atleta = ? ORDER BY s.fecha_inicio DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAtleta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    suscripciones.add(mapResultSetToSuscripcion(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suscripciones;
    }

    @Override
    public List<Suscripcion> obtenerTodas() {
        List<Suscripcion> suscripciones = new ArrayList<>();
        String sql = "SELECT s.*, m.nombre as nombre_membresia, m.precio FROM suscripciones s " +
                    "JOIN membresias m ON s.id_membresia = m.id_membresia ORDER BY s.fecha_inicio DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                suscripciones.add(mapResultSetToSuscripcion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suscripciones;
    }

    @Override
    public void guardar(Suscripcion suscripcion) {
        String sql = "INSERT INTO suscripciones (id_atleta, id_membresia, fecha_inicio, fecha_fin, activa) " +
                    "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, suscripcion.getIdAtleta());
            stmt.setInt(2, suscripcion.getIdMembresia());
            stmt.setDate(3, java.sql.Date.valueOf(suscripcion.getFechaInicio()));
            stmt.setDate(4, java.sql.Date.valueOf(suscripcion.getFechaFin()));
            stmt.setBoolean(5, suscripcion.isActiva());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    suscripcion.setIdSuscripcion(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Suscripcion suscripcion) {
        String sql = "UPDATE suscripciones SET id_membresia = ?, fecha_inicio = ?, fecha_fin = ?, activa = ? WHERE id_suscripcion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, suscripcion.getIdMembresia());
            stmt.setDate(2, java.sql.Date.valueOf(suscripcion.getFechaInicio()));
            stmt.setDate(3, java.sql.Date.valueOf(suscripcion.getFechaFin()));
            stmt.setBoolean(4, suscripcion.isActiva());
            stmt.setInt(5, suscripcion.getIdSuscripcion());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "UPDATE suscripciones SET activa = false WHERE id_suscripcion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Suscripcion mapResultSetToSuscripcion(ResultSet rs) throws SQLException {
        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setIdSuscripcion(rs.getInt("id_suscripcion"));
        suscripcion.setIdAtleta(rs.getInt("id_atleta"));
        suscripcion.setIdMembresia(rs.getInt("id_membresia"));
        suscripcion.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        suscripcion.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
        suscripcion.setActiva(rs.getBoolean("activa"));
        suscripcion.setNombreMembresia(rs.getString("nombre_membresia"));
        suscripcion.setPrecioMembresia(rs.getDouble("precio"));
        return suscripcion;
    }
}
