import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {

    // 1. Guardar turno incluyendo la fecha
    public boolean guardarTurno(String cliente, String telefono, String barbero, String fecha, String horario, String estado) {
        String sql = "INSERT INTO turnos (cliente, telefono, barbero, fecha, horario, estado) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cliente);
            pstmt.setString(2, telefono);
            pstmt.setString(3, barbero);
            pstmt.setString(4, fecha);
            pstmt.setString(5, horario);
            pstmt.setString(6, estado);
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Obtener todos los turnos ordenados por fecha y horario (Para el Administrador)
    public List<String[]> obtenerTodosLosTurnos() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, cliente, telefono, barbero, fecha, horario, estado FROM turnos ORDER BY fecha ASC, horario ASC";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String[] turno = new String[7];
                turno[0] = String.valueOf(rs.getInt("id"));
                turno[1] = rs.getString("cliente");
                turno[2] = rs.getString("telefono");
                turno[3] = rs.getString("barbero");
                turno[4] = rs.getString("fecha");
                turno[5] = rs.getString("horario");
                turno[6] = rs.getString("estado");
                lista.add(turno);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 3. Obtener turnos filtrados por fecha específica (Para el buscador del Admin)
    public List<String[]> obtenerTurnosPorFecha(String fechaBusqueda) {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, cliente, telefono, barbero, fecha, horario, estado FROM turnos WHERE fecha = ? ORDER BY horario ASC";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fechaBusqueda);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] turno = new String[7];
                    turno[0] = String.valueOf(rs.getInt("id"));
                    turno[1] = rs.getString("cliente");
                    turno[2] = rs.getString("telefono");
                    turno[3] = rs.getString("barbero");
                    turno[4] = rs.getString("fecha");
                    turno[5] = rs.getString("horario");
                    turno[6] = rs.getString("estado");
                    lista.add(turno);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 4. Obtener turnos filtrados por barbero (insensible a mayúsculas/minúsculas) ordenados cronológicamente
    public List<String[]> obtenerTurnosPorBarbero(String nombreBarbero) {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, cliente, telefono, barbero, fecha, horario, estado FROM turnos WHERE LOWER(barbero) = LOWER(?) ORDER BY fecha ASC, horario ASC";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreBarbero);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] turno = new String[7];
                    turno[0] = String.valueOf(rs.getInt("id"));
                    turno[1] = rs.getString("cliente");
                    turno[2] = rs.getString("telefono");
                    turno[3] = rs.getString("barbero");
                    turno[4] = rs.getString("fecha");
                    turno[5] = rs.getString("horario");
                    turno[6] = rs.getString("estado");
                    lista.add(turno);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 5. Actualizar el estado de un turno
    public boolean actualizarEstado(int idTurno, String nuevoEstado) {
        String sql = "UPDATE turnos SET estado = ? WHERE id = ?";
        
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