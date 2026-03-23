import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Main GUI window for the Student Management System.
 * Uses Java Swing + JDBC + MySQL.
 */
public class StudentManagementSystem extends JFrame {

    // ── Form fields ──────────────────────────────────────────────────────────
    private JTextField txtId, txtFirstName, txtLastName, txtMajor, txtPhone, txtCgpa, txtDob;
    private JTextField txtSearch;

    // ── Table ────────────────────────────────────────────────────────────────
    private JTable table;
    private DefaultTableModel tableModel;

    // ── DAO ──────────────────────────────────────────────────────────────────
    private final StudentDAO dao = new StudentDAO();

    // ── Column names ─────────────────────────────────────────────────────────
    private static final String[] COLUMNS =
        {"Student ID", "First Name", "Last Name", "Major", "Phone", "CGPA", "DOB"};

    // ─────────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────────

    public StudentManagementSystem() {
        setTitle("Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Close DB on exit
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                dbConnect.closeConnection();
            }
        });

        add(buildHeaderPanel(),  BorderLayout.NORTH);
        add(buildFormPanel(),    BorderLayout.WEST);
        add(buildTablePanel(),   BorderLayout.CENTER);
        add(buildButtonPanel(),  BorderLayout.SOUTH);

        loadAllStudents();
        setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI Builders
    // ─────────────────────────────────────────────────────────────────────────

    /** Top title bar */
    private JPanel buildHeaderPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(33, 97, 140));
        p.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel title = new JLabel("🎓  Student Management System");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        p.add(title, BorderLayout.WEST);

        // Search bar (top-right)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.setOpaque(false);
        txtSearch = new JTextField(18);
        txtSearch.setToolTipText("Search by name or ID");
        JButton btnSearch = styledButton("Search", new Color(52, 152, 219));
        JButton btnHeaderClear  = styledButton("Clear",  new Color(52, 152, 219));
        btnSearch.addActionListener(e -> searchStudents());
        btnHeaderClear.addActionListener(e -> { txtSearch.setText(""); loadAllStudents(); });
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnHeaderClear);
        p.add(searchPanel, BorderLayout.EAST);

        return p;
    }

    /** Left-side input form */
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(33, 97, 140), 2),
            "Student Details", 0, 0,
            new Font("SansSerif", Font.BOLD, 13), new Color(33, 97, 140)));
        outer.setPreferredSize(new Dimension(270, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtId        = new JTextField(15);
        txtFirstName = new JTextField(15);
        txtLastName  = new JTextField(15);
        txtMajor     = new JTextField(15);
        txtPhone     = new JTextField(15);
        txtCgpa      = new JTextField(15);
        txtDob       = new JTextField(15);
        txtDob.setToolTipText("Format: YYYY-MM-DD");

        String[] labels = {"Student ID *", "First Name *", "Last Name *",
                           "Major *", "Phone", "CGPA *", "DOB (YYYY-MM-DD)"};
        JTextField[] fields = {txtId, txtFirstName, txtLastName,
                               txtMajor, txtPhone, txtCgpa, txtDob};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            form.add(fields[i], gbc);
        }

        outer.add(form, BorderLayout.NORTH);
        return outer;
    }

    /** Center table */
    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(33, 97, 140), 2),
            "Student Records", 0, 0,
            new Font("SansSerif", Font.BOLD, 13), new Color(33, 97, 140)));

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(33, 97, 140));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(174, 214, 241));
        table.setGridColor(new Color(189, 195, 199));

        // Clicking a row populates the form
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { populateFormFromTable(); }
        });

        p.add(new JScrollPane(table), BorderLayout.CENTER);

        // Sort buttons below table
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        sortPanel.add(new JLabel("Sort by:"));
        for (StudentDAO.SortBy s : StudentDAO.SortBy.values()) {
            JButton b = styledButton(s.name().replace('_', ' '),
                                     new Color(41, 128, 185));
            b.addActionListener(e -> loadSorted(s));
            sortPanel.add(b);
        }
        p.add(sortPanel, BorderLayout.SOUTH);
        return p;
    }

    /** Bottom CRUD buttons */
    private JPanel buildButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        p.setBackground(new Color(245, 246, 250));

        JButton btnAdd    = styledButton("➕  Add",    new Color(52, 152, 219));
        JButton btnUpdate = styledButton("✏️  Update", new Color(52, 152, 219));
        JButton btnDelete = styledButton("🗑️  Delete", new Color(52, 152, 219));
        JButton btnClear  = styledButton("🔄  Clear",  new Color(52, 152, 219));
        JButton btnRefresh= styledButton("↻  Refresh", new Color(52, 152, 219));

        btnAdd.addActionListener(e     -> addStudent());
        btnUpdate.addActionListener(e  -> updateStudent());
        btnDelete.addActionListener(e  -> deleteStudent());
        btnClear.addActionListener(e   -> clearForm());
        btnRefresh.addActionListener(e -> loadAllStudents());

        p.add(btnAdd);
        p.add(btnUpdate);
        p.add(btnDelete);
        p.add(btnClear);
        p.add(btnRefresh);
        return p;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CRUD Actions
    // ─────────────────────────────────────────────────────────────────────────

    private void addStudent() {
        if (!validateForm()) return;

        // Check duplicate ID
        if (dao.findById(txtId.getText().trim()) != null) {
            showError("Student ID already exists!");
            return;
        }
        boolean ok = dao.addStudent(buildStudentFromForm());
        if (ok) {
            showInfo("Student added successfully!");
            clearForm();
            loadAllStudents();
        } else {
            showError("Failed to add student.");
        }
    }

    private void updateStudent() {
        if (!validateForm()) return;
        if (dao.findById(txtId.getText().trim()) != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Update student " + txtId.getText().trim() + "?",
                "Confirm Update", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            boolean ok = dao.updateStudent(buildStudentFromForm());
            if (ok) {
                showInfo("Student updated successfully!");
                clearForm();
                loadAllStudents();
            } else {
                showError("Failed to update student.");
            }
        } else {
            showError("No student found with ID: " + txtId.getText().trim());
        }
    }

    private void deleteStudent() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            showError("Please enter/select a Student ID to delete.");
            return;
        }
        if (dao.findById(id) == null) {
            showError("No student found with ID: " + id);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete student " + id + "? This cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = dao.deleteStudent(id);
        if (ok) {
            showInfo("Student deleted.");
            clearForm();
            loadAllStudents();
        } else {
            showError("Failed to delete student.");
        }
    }

    private void searchStudents() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadAllStudents(); return; }

        // Try exact ID match first
        Student byId = dao.findById(keyword);
        if (byId != null) {
            populateTable(List.of(byId));
            return;
        }
        // Then name search
        List<Student> results = dao.searchByName(keyword);
        if (results.isEmpty()) {
            showInfo("No students found matching: " + keyword);
            loadAllStudents();
        } else {
            populateTable(results);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Table Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void loadAllStudents() {
        populateTable(dao.getAllStudents());
    }

    private void loadSorted(StudentDAO.SortBy sortBy) {
        populateTable(dao.getSortedStudents(sortBy));
    }

    private void populateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                s.getStudentId(), s.getFirstName(), s.getLastName(),
                s.getMajor(), s.getPhone(), s.getCgpa(), s.getDob()
            });
        }
    }

    /** Fills the form fields when a table row is clicked */
    private void populateFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText((String) tableModel.getValueAt(row, 0));
        txtFirstName.setText((String) tableModel.getValueAt(row, 1));
        txtLastName.setText((String) tableModel.getValueAt(row, 2));
        txtMajor.setText((String) tableModel.getValueAt(row, 3));
        txtPhone.setText((String) tableModel.getValueAt(row, 4));
        txtCgpa.setText((String) tableModel.getValueAt(row, 5));
        txtDob.setText((String) tableModel.getValueAt(row, 6));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Form Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private Student buildStudentFromForm() {
        return new Student(
            txtId.getText().trim(),
            txtFirstName.getText().trim(),
            txtLastName.getText().trim(),
            txtMajor.getText().trim(),
            txtPhone.getText().trim(),
            txtCgpa.getText().trim(),
            txtDob.getText().trim()
        );
    }

    private boolean validateForm() {
        if (txtId.getText().trim().isEmpty()) { showError("Student ID is required."); return false; }
        if (txtFirstName.getText().trim().isEmpty()) { showError("First Name is required."); return false; }
        if (txtLastName.getText().trim().isEmpty())  { showError("Last Name is required.");  return false; }
        if (txtMajor.getText().trim().isEmpty())     { showError("Major is required.");      return false; }
        if (txtCgpa.getText().trim().isEmpty())      { showError("CGPA is required.");       return false; }

        // CGPA range validation
        try {
            double cgpa = Double.parseDouble(txtCgpa.getText().trim());
            if (cgpa < 0.0 || cgpa > 10.0) { showError("CGPA must be between 0.0 and 10.0."); return false; }
        } catch (NumberFormatException e) {
            showError("CGPA must be a number (e.g. 8.5).");
            return false;
        }

        // Phone length (optional but if given must be <= 12 digits)
        String phone = txtPhone.getText().trim();
        if (!phone.isEmpty() && !phone.matches("\\d{7,12}")) {
            showError("Phone must be 7-12 digits.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtId.setText("");        txtFirstName.setText("");
        txtLastName.setText("");  txtMajor.setText("");
        txtPhone.setText("");     txtCgpa.setText("");
        txtDob.setText("");
        table.clearSelection();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utility
    // ─────────────────────────────────────────────────────────────────────────

    private JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.BLUE);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Entry Point
    // ─────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // Use system look and feel for a native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(StudentManagementSystem::new);
    }
}

