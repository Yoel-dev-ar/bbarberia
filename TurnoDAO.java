import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {

    // Guardar turno incluyendo el servicio y el costo
    public boolean guardarTurno(String cliente, String telefono, String barbero, String fecha, String horario, String estado, String servicio, double costo) {
        String sql = "INSERT INTO turnos (cliente, telefono, barbero, fecha, horario, estado, servicio, costo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cliente);
            pstmt.setString(2, telefono);
            pstmt.setString(3, barbero);
            pstmt.setString(4, fecha);
            pstmt.setString(5, horario);
            pstmt.setString(6, estado);
            pstmt.setString(7, servicio);
            pstmt.setDouble(8, costo);
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener todos los turnos
    public List<String[]> obtenerTodosLosTurnos() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, cliente, telefono, barbero, fecha, horario, estado, servicio, costo FROM turnos ORDER BY fecha ASC, horario ASC";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String[] turno = new String[9];
                turno[0] = String.valueOf(rs.getInt("id"));
                turno[1] = rs.getString("cliente");
                turno[2] = rs.getString("telefono");
                turno[3] = rs.getString("barbero");
                turno[4] = rs.getString("fecha");
                turno[5] = rs.getString("horario");
                turno[6] = rs.getString("estado");
                turno[7] = rs.getString("servicio") != null ? rs.getString("servicio") : "Sin servicio (No asistió/Cancelado)";
                turno[8] = rs.getString("servicio") != null ? "$" + rs.getDouble("costo") : "$0";
                lista.add(turno);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Obtener turnos por fecha específica
    public List<String[]> obtenerTurnosPorFecha(String fechaBusqueda) {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, cliente, telefono, barbero, fecha, horario, estado, servicio, costo FROM turnos WHERE fecha = ? ORDER BY horario ASC";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fechaBusqueda);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] turno = new String[9];
                    turno[0] = String.valueOf(rs.getInt("id"));
                    turno[1] = rs.getString("cliente");
                    turno[2] = rs.getString("telefono");
                    turno[3] = rs.getString("barbero");
                    turno[4] = rs.getString("fecha");
                    turno[5] = rs.getString("horario");
                    turno[6] = rs.getString("estado");
                    turno[7] = rs.getString("servicio") != null ? rs.getString("servicio") : "Sin servicio (No asistió/Cancelado)";
                    turno[8] = rs.getString("servicio") != null ? "$" + rs.getDouble("costo") : "$0";
                    lista.add(turno);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Obtener turnos por barbero
    public List<String[]> obtenerTurnosPorBarbero(String nombreBarbero) {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, cliente, telefono, barbero, fecha, horario, estado, servicio, costo FROM turnos WHERE LOWER(barbero) = LOWER(?) ORDER BY fecha ASC, horario ASC";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreBarbero);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] turno = new String[9];
                    turno[0] = String.valueOf(rs.getInt("id"));
                    turno[1] = rs.getString("cliente");
                    turno[2] = rs.getString("telefono");
                    turno[3] = rs.getString("barbero");
                    turno[4] = rs.getString("fecha");
                    turno[5] = rs.getString("horario");
                    turno[6] = rs.getString("estado");
                    turno[7] = rs.getString("servicio") != null ? rs.getString("servicio") : "Sin servicio (No asistió/Cancelado)";
                    turno[8] = rs.getString("servicio") != null ? "$" + rs.getDouble("costo") : "$0";
                    lista.add(turno);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Actualizar estado y limpiar servicio/costo si es Ausente o Cancelado
    public boolean actualizarEstado(int idTurno, String nuevoEstado) {
        String sql;
        boolean limpiar = nuevoEstado.equalsIgnoreCase("Cancelado") || nuevoEstado.equalsIgnoreCase("Ausente");
        
        if (limpiar) {
            sql = "UPDATE turnos SET estado = ?, servicio = NULL, costo = 0 WHERE id = ?";
        } else {
            sql = "UPDATE turnos SET estado = ? WHERE id = ?";
        }
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idTurno);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}