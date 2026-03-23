import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) – handles all SQL operations on the sdata table.
 */
public class StudentDAO {

    private final Connection conn;

    public StudentDAO() {
        this.conn = dbConnect.getConnection();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Inserts a new student record.
     * @return true if insert succeeded.
     */
    public boolean addStudent(Student s) {
        String sql = "INSERT INTO sdata (Student_ID, first_name, last_name, major, Phone,CGPA, DOB) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setString(4, s.getMajor());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getCgpa());
            ps.setString(7, s.getDob());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addStudent error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ – all students
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns all students ordered by Student_ID. */
    public List<Student> getAllStudents() {
        return queryStudents("SELECT * FROM sdata ORDER BY Student_ID");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ – search
    // ─────────────────────────────────────────────────────────────────────────

    /** Finds a student by exact Student_ID. Returns null if not found. */
    public Student findById(String id) {
        String sql = "SELECT * FROM sdata WHERE Student_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("findById error: " + e.getMessage());
        }
        return null;
    }

    /** Searches students whose first or last name contains the keyword (case-insensitive). */
    public List<Student> searchByName(String keyword) {
        String sql = "SELECT * FROM sdata WHERE first_name LIKE ? OR last_name LIKE ?";
        List<Student> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("searchByName error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ – sort
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns all students sorted by the given column. */
    public List<Student> getSortedStudents(SortBy sortBy) {
        String col;
        switch (sortBy) {
            case MAJOR:      col = "major";      break;
            case LAST_NAME:  col = "last_name";  break;
            case FIRST_NAME: col = "first_name"; break;
            default:         col = "Student_ID"; break;
        }
        return queryStudents("SELECT * FROM sdata ORDER BY " + col);
    }

    public enum SortBy { MAJOR, LAST_NAME, FIRST_NAME, ID }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Updates all fields of an existing student (matched by Student_ID).
     * @return true if at least one row was updated.
     */
    public boolean updateStudent(Student s) {
        String sql = "UPDATE sdata SET first_name=?, last_name=?, major=?, Phone=?,CGPA=?, DOB=? " +
                     "WHERE Student_ID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getMajor());
            ps.setString(4, s.getPhone());
            ps.setString(5, s.getCgpa());
            ps.setString(6, s.getDob());
            ps.setString(7, s.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateStudent error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Deletes a student by Student_ID.
     * @return true if a row was deleted.
     */
    public boolean deleteStudent(String id) {
        String sql = "DELETE FROM sdata WHERE Student_ID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteStudent error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private List<Student> queryStudents(String sql) {
        List<Student> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("queryStudents error: " + e.getMessage());
        }
        return list;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
            rs.getString("Student_ID"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("major"),
            rs.getString("Phone"),
            rs.getString("CGPA"),
            rs.getString("DOB")
        );
    }
}
