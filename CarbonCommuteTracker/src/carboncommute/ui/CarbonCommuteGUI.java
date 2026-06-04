package carboncommute.ui;
import carboncommute.model.CommuteRecord;
import carboncommute.model.User;
import carboncommute.report.EmissionReport;
import carboncommute.system.FileHandler;
import carboncommute.system.TransportFactory;
import carboncommute.transport.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
/**
 * CarbonCommuteGUI - Java Swing Graphical User Interface
 * Demonstrates: GUI programming with Java Swing (JFrame, JPanel, JTabbedPane)
 * Uses all the same OOP model classes: User, CommuteRecord, TransportMode hierarchy.
 * This is the GUI front-end; the business logic stays in the model/report/transport packages.
 */
public class CarbonCommuteGUI extends JFrame {

    // ── Color Palette
    private static final Color CLR_BG        = new Color(245, 248, 245);
    private static final Color CLR_PRIMARY   = new Color(29, 158, 117);
    private static final Color CLR_PRIMARY_D = new Color(15, 110, 86);
    private static final Color CLR_ACCENT    = new Color(39, 80, 53);
    private static final Color CLR_PANEL     = Color.WHITE;
    private static final Color CLR_HEADER    = new Color(30, 50, 35);
    private static final Color CLR_TEXT      = new Color(40, 40, 40);
    private static final Color CLR_MUTED     = new Color(100, 110, 100);
    private static final Color CLR_RED       = new Color(210, 55, 55);
    private static final Color CLR_AMBER     = new Color(200, 120, 20);
    private static final Color CLR_BORDER    = new Color(210, 225, 210);

    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 13);
    // ── Application State ────────────────────────────────────────────────────
    private List<User> users = new ArrayList<>();
    private User currentUser = null;
    private FileHandler fileHandler = new FileHandler("data/commute_data.txt");
    // ── UI Components ─────────────────────────────────────────────────────────
    private JTabbedPane tabbedPane;
    private JLabel statusLabel;
    private JLabel userBadgeLabel;
    // Log Trip tab
    private JComboBox<String> userSelector;
    private JTextField dateField;
    private JComboBox<String> transportCombo;
    private JSpinner carpoolSpinner;
    private JLabel carpoolLabel;
    private JTextField distanceField;
    private JTextField notesField;
    private JLabel emissionPreviewLabel;
    private JLabel suggestionLabel;
    // History tab
    private DefaultTableModel historyTableModel;
    private JTable historyTable;
    private JLabel totalEmissionLabel;
    private JLabel totalDistLabel;
    // Report tab
    private JTextArea reportArea;
    private JComboBox<String> reportTypeCombo;
    private JSpinner yearSpinner;
    private JSpinner monthSpinner;
    // ── Constructor ───────────────────────────────────────────────────────────
    public CarbonCommuteGUI() {
        setTitle("Carbon Commute Tracker & Emission Reporter");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 680);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(CLR_BG);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(CarbonCommuteGUI.this,
                        "Save data before exiting?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    fileHandler.saveToFile(users);
                    dispose();
                } else if (confirm == JOptionPane.NO_OPTION) {
                    dispose();
                }
            }
        });
        loadData();
        buildUI();
        setVisible(true);
    }
    // ── UI Build ──────────────────────────────────────────────────────────────
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(),   BorderLayout.CENTER);
        add(buildStatus(), BorderLayout.SOUTH);
    }
    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CLR_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("🌿");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        JLabel title = new JLabel("Carbon Commute Tracker");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Track your daily commute emissions");
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(new Color(180, 215, 190));
        titles.add(title);
        titles.add(subtitle);

        left.add(icon);
        left.add(titles);

        userBadgeLabel = new JLabel("No user selected");
        userBadgeLabel.setFont(FONT_SMALL);
        userBadgeLabel.setForeground(new Color(200, 235, 205));

        header.add(left, BorderLayout.WEST);
        header.add(userBadgeLabel, BorderLayout.EAST);
        return header;
    }

    // ── Tabs ──────────────────────────────────────────────────────────────────

    private JTabbedPane buildTabs() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_BODY);
        tabbedPane.setBackground(CLR_BG);

        tabbedPane.addTab("  👤  Users  ",    buildUsersTab());
        tabbedPane.addTab("  ➕  Log Trip  ", buildLogTab());
        tabbedPane.addTab("  📋  History  ",  buildHistoryTab());
        tabbedPane.addTab("  📊  Reports  ",  buildReportTab());

        return tabbedPane;
    }

    // ── Users Tab ────────────────────────────────────────────────────────────

    private JPanel buildUsersTab() {
        JPanel panel = tabPanel();

        // Register section
        JPanel registerCard = card("Register New User");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = gbc();

        JTextField nameField  = new JTextField(20);
        JTextField emailField = new JTextField(20);
        styleField(nameField); styleField(emailField);

        gc.gridx=0; gc.gridy=0; form.add(label("Full name:"), gc);
        gc.gridx=1; form.add(nameField, gc);
        gc.gridx=0; gc.gridy=1; form.add(label("Email:"), gc);
        gc.gridx=1; form.add(emailField, gc);

        JButton regBtn = primaryButton("Register User");
        regBtn.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String email = emailField.getText().trim();
            if (name.isEmpty() || email.isEmpty()) {
                showError("Please fill in name and email.");
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                showError("Please enter a valid email address.");
                return;
            }
            for (User u : users) {
                if (u.getEmail().equalsIgnoreCase(email)) {
                    showError("A user with this email already exists.");
                    return;
                }
            }
            String uid = "U" + String.format("%03d", users.size() + 1);
            User newUser = new User(uid, name, email);
            users.add(newUser);
            currentUser = newUser;
            nameField.setText(""); emailField.setText("");
            refreshUserSelector();
            refreshHistoryTable();
            updateUserBadge();
            showStatus("User '" + name + "' registered and selected.");
        });
        gc.gridx=0; gc.gridy=2; gc.gridwidth=2;
        gc.insets = new Insets(12, 4, 4, 4);
        form.add(regBtn, gc);

        registerCard.add(form, BorderLayout.CENTER);

        // Registered users table
        JPanel usersCard = card("Registered Users");
        String[] cols = {"ID", "Name", "Email", "Trips", "Total CO2 (kg)"};
        DefaultTableModel usersModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable usersTable = new JTable(usersModel);
        styleTable(usersTable);
        JScrollPane scroll = new JScrollPane(usersTable);
        scroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER));
        usersCard.add(scroll, BorderLayout.CENTER);
        JButton refreshBtn = ghostButton("Refresh List");
        refreshBtn.addActionListener(e -> {
            usersModel.setRowCount(0);
            for (User u : users) {
                usersModel.addRow(new Object[]{
                        u.getUserId(), u.getName(), u.getEmail(),
                        u.getTripCount(),
                        String.format("%.4f", u.calculateTotalEmission())
                });
            }
        });
        JButton selectBtn = primaryButton("Select highlighted user");
        selectBtn.addActionListener(e -> {
            int row = usersTable.getSelectedRow();
            if (row < 0) { showError("Please select a user from the table."); return; }
            currentUser = users.get(row);
            refreshUserSelector();
            refreshHistoryTable();
            updateUserBadge();
            showStatus("Switched to user: " + currentUser.getName());
        });
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnRow.setOpaque(false);
        btnRow.add(refreshBtn);
        btnRow.add(selectBtn);
        JPanel usersCardFull = new JPanel(new BorderLayout(0, 8));
        usersCardFull.setOpaque(false);
        usersCardFull.add(usersCard, BorderLayout.CENTER);
        usersCardFull.add(btnRow, BorderLayout.SOUTH);
        // Trigger refresh on tab select
        refreshBtn.doClick(); // populate table if data was loaded
        panel.add(registerCard, BorderLayout.NORTH);
        panel.add(usersCardFull, BorderLayout.CENTER);
        return panel;
    }
    // ── Log Trip Tab 
    private JPanel buildLogTab() {
        JPanel panel = tabPanel();
        JPanel card = card("New Commute Entry");
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = gbc();
        // User selector
        userSelector = new JComboBox<>();
        styleCombo(userSelector);
        refreshUserSelector();
        // Date
        dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 15);
        styleField(dateField);
        // Transport
        String[] modes = {"1 – Car (Solo)  0.210 kg/km", "2 – Motorbike  0.100 kg/km",
                "3 – Bus  0.080 kg/km", "4 – Carpool  0.21÷pax kg/km",
                "5 – Bicycle  0.000 kg/km", "6 – Walking  0.000 kg/km"};
        transportCombo = new JComboBox<>(modes);
        styleCombo(transportCombo);
        // Carpool passengers
        carpoolLabel = label("Passengers:");
        carpoolSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 8, 1));
        carpoolSpinner.setFont(FONT_BODY);
        carpoolLabel.setVisible(false);
        carpoolSpinner.setVisible(false);
        transportCombo.addActionListener(e -> {
            boolean isCarpool = transportCombo.getSelectedIndex() == 3;
            carpoolLabel.setVisible(isCarpool);
            carpoolSpinner.setVisible(isCarpool);
            updateEmissionPreview();
        });
        // Distance
        distanceField = new JTextField("10", 10);
        styleField(distanceField);
        distanceField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { updateEmissionPreview(); }
        });
        transportCombo.addActionListener(e -> updateEmissionPreview());
        // Notes
        notesField = new JTextField(25);
        styleField(notesField);
        // Emission preview
        emissionPreviewLabel = new JLabel("  CO2 Emission: —");
        emissionPreviewLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        emissionPreviewLabel.setForeground(CLR_PRIMARY);
        emissionPreviewLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        emissionPreviewLabel.setOpaque(true);
        emissionPreviewLabel.setBackground(new Color(235, 250, 242));

        suggestionLabel = new JLabel(" ");
        suggestionLabel.setFont(FONT_SMALL);
        suggestionLabel.setForeground(CLR_MUTED);
        int r = 0;
        gc.gridx=0; gc.gridy=r;   form.add(label("Active user:"),  gc);
        gc.gridx=1; gc.gridwidth=2; form.add(userSelector, gc); gc.gridwidth=1;
        gc.gridx=0; gc.gridy=++r; form.add(label("Date (YYYY-MM-DD):"), gc);
        gc.gridx=1; form.add(dateField, gc);
        gc.gridx=0; gc.gridy=++r; form.add(label("Transport mode:"), gc);
        gc.gridx=1; gc.gridwidth=2; form.add(transportCombo, gc); gc.gridwidth=1;
        gc.gridx=0; gc.gridy=++r; form.add(carpoolLabel, gc);
        gc.gridx=1; form.add(carpoolSpinner, gc);
        gc.gridx=0; gc.gridy=++r; form.add(label("Distance (km):"), gc);
        gc.gridx=1; form.add(distanceField, gc);
        gc.gridx=0; gc.gridy=++r; form.add(label("Notes:"), gc);
        gc.gridx=1; gc.gridwidth=2; form.add(notesField, gc); gc.gridwidth=1;
        gc.gridx=0; gc.gridy=++r; gc.gridwidth=3;
        gc.insets = new Insets(10, 4, 4, 4);
        form.add(emissionPreviewLabel, gc); gc.gridwidth=1;
        gc.insets = new Insets(4, 4, 4, 4);
        gc.gridx=0; gc.gridy=++r; gc.gridwidth=3;
        form.add(suggestionLabel, gc); gc.gridwidth=1;

        JButton logBtn = primaryButton("✔  Log This Trip");
        logBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logBtn.addActionListener(e -> logTrip());

        gc.gridx=0; gc.gridy=++r; gc.gridwidth=3;
        gc.insets = new Insets(14, 4, 4, 4);
        form.add(logBtn, gc);

        card.add(form, BorderLayout.CENTER);
        panel.add(card, BorderLayout.CENTER);
        updateEmissionPreview();
        return panel;
    }
    // ── History Tab 
    private JPanel buildHistoryTab() {
        JPanel panel = tabPanel();
        JPanel card = card("Commute History");
        String[] cols = {"#", "Date", "Transport Mode", "Distance (km)", "CO2 Emitted (kg)", "Notes"};
        historyTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        historyTable = new JTable(historyTableModel);
        styleTable(historyTable);
        // Color-code rows by emission level
        historyTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    try {
                        double co2 = Double.parseDouble(historyTableModel.getValueAt(row, 4).toString());
                        if (co2 == 0) c.setBackground(new Color(235, 250, 240));
                        else if (co2 < 2) c.setBackground(new Color(245, 255, 245));
                        else if (co2 < 5) c.setBackground(new Color(255, 252, 235));
                        else c.setBackground(new Color(255, 242, 242));
                    } catch (Exception ex) { c.setBackground(Color.WHITE); }
                }
                return c;
            }
        });
        JScrollPane scroll = new JScrollPane(historyTable);
        scroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER));
        card.add(scroll, BorderLayout.CENTER);
        // Summary strip
        JPanel summary = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        summary.setOpaque(false);
        totalEmissionLabel = new JLabel("Total CO2: 0.0000 kg");
        totalEmissionLabel.setFont(FONT_BODY);
        totalEmissionLabel.setForeground(CLR_PRIMARY_D);
        totalDistLabel = new JLabel("Total distance: 0.0 km");
        totalDistLabel.setFont(FONT_BODY);
        totalDistLabel.setForeground(CLR_MUTED);
        summary.add(totalEmissionLabel);
        summary.add(new JSeparator(JSeparator.VERTICAL));
        summary.add(totalDistLabel);
        JButton refreshBtn = ghostButton("Refresh");
        refreshBtn.addActionListener(e -> refreshHistoryTable());
        JButton clearBtn = ghostButton("Clear All Trips");
        clearBtn.setForeground(CLR_RED);
        clearBtn.addActionListener(e -> {
            if (currentUser == null) { showError("No user selected."); return; }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete ALL trips for " + currentUser.getName() + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Re-create user with no history
                User fresh = new User(currentUser.getUserId(), currentUser.getName(), currentUser.getEmail());
                users.set(users.indexOf(currentUser), fresh);
                currentUser = fresh;
                refreshHistoryTable();
                showStatus("All trips cleared for " + currentUser.getName());
            }
        });
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(summary, BorderLayout.WEST);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        btnRow.setOpaque(false);
        btnRow.add(refreshBtn); btnRow.add(clearBtn);
        bottom.add(btnRow, BorderLayout.EAST);
        panel.add(card, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }
    // ── Report Tab 
    private JPanel buildReportTab() {
        JPanel panel = tabPanel();
        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        controls.setOpaque(false);
        controls.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        reportTypeCombo = new JComboBox<>(new String[]{
                "Full Summary", "Monthly Report", "Eco Score"});
        styleCombo(reportTypeCombo);
        yearSpinner  = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2020, 2030, 1));
        monthSpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        yearSpinner.setFont(FONT_BODY);
        monthSpinner.setFont(FONT_BODY);
        JLabel yearLabel  = label("Year:");
        JLabel monthLabel = label("Month:");
        yearLabel.setVisible(false); monthLabel.setVisible(false);
        yearSpinner.setVisible(false); monthSpinner.setVisible(false);
        reportTypeCombo.addActionListener(e -> {
            boolean monthly = reportTypeCombo.getSelectedIndex() == 1;
            yearLabel.setVisible(monthly); monthLabel.setVisible(monthly);
            yearSpinner.setVisible(monthly); monthSpinner.setVisible(monthly);
        });
        JButton generateBtn = primaryButton("Generate Report");
        generateBtn.addActionListener(e -> generateReport());
        controls.add(label("Report type:")); controls.add(reportTypeCombo);
        controls.add(yearLabel); controls.add(yearSpinner);
        controls.add(monthLabel); controls.add(monthSpinner);
        controls.add(generateBtn);
        // Report text area
        reportArea = new JTextArea();
        reportArea.setFont(FONT_MONO);
        reportArea.setEditable(false);
        reportArea.setBackground(new Color(250, 252, 250));
        reportArea.setForeground(CLR_TEXT);
        reportArea.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        reportArea.setText(
                "  Carbon Commute Tracker - Emission Reporter\n" +
                "  ════════════════════════════════════════════\n\n" +
                "  Select a report type above and click 'Generate Report'.\n\n" +
                "  Available reports:\n" +
                "  • Full Summary  - All-time emission totals and breakdown\n" +
                "  • Monthly Report - Emissions for a specific month\n" +
                "  • Eco Score     - Your green rating based on avg weekly CO2\n"
        );
        JScrollPane scroll = new JScrollPane(reportArea);
        scroll.setBorder(BorderFactory.createLineBorder(CLR_BORDER));
        panel.add(controls, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
    // ── Actions ───────────────────────────────────────────────────────────────
    private void logTrip() {
        // Resolve active user
        if (userSelector.getSelectedIndex() >= 0 && !users.isEmpty()) {
            currentUser = users.get(userSelector.getSelectedIndex());
        }
        if (currentUser == null) { showError("Please register or select a user first."); return; }
        // Parse date
        LocalDate date;
        try {
            date = LocalDate.parse(dateField.getText().trim());
        } catch (Exception e) {
            showError("Invalid date. Use format YYYY-MM-DD.");
            return;
        }
        // Parse distance
        double distance;
        try {
            distance = Double.parseDouble(distanceField.getText().trim());
            if (distance <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Enter a valid positive distance in km.");
            return;
        }
        // Create transport object via factory (Polymorphism)
        int modeIdx = transportCombo.getSelectedIndex() + 1;
        int pax = (Integer) carpoolSpinner.getValue();
        TransportMode mode = TransportFactory.create(modeIdx, pax);
        if (mode == null) { showError("Invalid transport selection."); return; }
        // Build CommuteRecord (Composition: HAS-A TransportMode)
        String notes = notesField.getText().trim();
        CommuteRecord record = new CommuteRecord(date, distance, mode,
                notes.isEmpty() ? "N/A" : notes);
        currentUser.addCommute(record);
        notesField.setText("");
        refreshHistoryTable();
        updateUserBadge();
        String msg = String.format(
                "Trip logged!\n\nDate: %s\nTransport: %s\nDistance: %.1f km\nCO2 Emitted: %.4f kg CO2\n\n%s",
                date, mode.getModeName(), distance, record.getEmissionKg(),
                mode.getGreenAlternative());
        JOptionPane.showMessageDialog(this, msg, "Trip Logged Successfully",
                JOptionPane.INFORMATION_MESSAGE);
        showStatus("Trip logged for " + currentUser.getName() + " — " +
                String.format("%.4f kg CO2", record.getEmissionKg()));
        tabbedPane.setSelectedIndex(2); // Switch to history
    }
    private void updateEmissionPreview() {
        try {
            double dist = Double.parseDouble(distanceField.getText().trim());
            int idx = transportCombo.getSelectedIndex() + 1;
            int pax = (Integer) carpoolSpinner.getValue();
            TransportMode mode = TransportFactory.create(idx, pax);
            if (mode == null) return;
            double co2 = mode.calculateEmission(dist);
            emissionPreviewLabel.setText(String.format("  CO2 Emission: %.4f kg CO2  |  %.1f km via %s",
                    co2, dist, mode.getModeName()));
            Color c = co2 == 0 ? CLR_PRIMARY : co2 < 2 ? CLR_ACCENT : co2 < 5 ? CLR_AMBER : CLR_RED;
            emissionPreviewLabel.setForeground(c);
            suggestionLabel.setText("Suggestion: " + mode.getGreenAlternative());
        } catch (NumberFormatException ignored) {
            emissionPreviewLabel.setText("  CO2 Emission: enter a valid distance above");
            emissionPreviewLabel.setForeground(CLR_MUTED);
        }
    }
    private void generateReport() {
        if (currentUser == null) { showError("Please select a user first."); return; }
        // Redirect System.out to the text area
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream old = System.out;
        System.setOut(new java.io.PrintStream(baos));
        EmissionReport report = new EmissionReport(currentUser);
        int type = reportTypeCombo.getSelectedIndex();
        if (type == 0) {
            report.generateSummaryReport();
        } else if (type == 1) {
            int year  = (Integer) yearSpinner.getValue();
            int month = (Integer) monthSpinner.getValue();
            report.generateMonthlyReport(year, month);
        } else {
            report.showEcoScore();
        }
        System.out.flush();
        System.setOut(old);
        reportArea.setText(baos.toString());
        reportArea.setCaretPosition(0);
    }
    private void refreshHistoryTable() {
        historyTableModel.setRowCount(0);
        if (currentUser == null) return;
        List<CommuteRecord> records = currentUser.getCommuteHistory();
        int i = 1;
        for (CommuteRecord r : records) {
            historyTableModel.addRow(new Object[]{
                    i++,
                    r.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    r.getTransportMode().getModeName(),
                    String.format("%.1f", r.getDistanceKm()),
                    String.format("%.4f", r.getEmissionKg()),
                    r.getNotes()
            });
        }
        totalEmissionLabel.setText(String.format("Total CO2: %.4f kg", currentUser.calculateTotalEmission()));
        totalDistLabel.setText(String.format("Total distance: %.1f km", currentUser.calculateTotalDistance()));
    }
    private void refreshUserSelector() {
        if (userSelector == null) return;
        userSelector.removeAllItems();
        for (User u : users) {
            userSelector.addItem(u.getName() + "  (" + u.getEmail() + ")");
        }
        if (currentUser != null) {
            int idx = users.indexOf(currentUser);
            if (idx >= 0) userSelector.setSelectedIndex(idx);
        }
    }
    private void updateUserBadge() {
        if (currentUser != null) {
            userBadgeLabel.setText("Active: " + currentUser.getName() +
                    "  |  " + currentUser.getTripCount() + " trips  |  " +
                    String.format("%.4f kg CO2", currentUser.calculateTotalEmission()));
        } else {
            userBadgeLabel.setText("No user selected");
        }
    }
    private void loadData() {
        new java.io.File("data").mkdirs();
        users = fileHandler.loadFromFile();
        if (!users.isEmpty()) currentUser = users.get(0);
    }
    // ── UI Helper Factories ───────────────────────────────────────────────────
    private JPanel tabPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(CLR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return p;
    }
    private JPanel card(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(CLR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_HEADING);
        titleLabel.setForeground(CLR_HEADER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(titleLabel, BorderLayout.NORTH);
        return card;
    }
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(CLR_TEXT);
        return l;
    }
    private void styleField(JTextField f) {
        f.setFont(FONT_BODY);
        f.setForeground(CLR_TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }
    private void styleCombo(JComboBox<?> c) {
        c.setFont(FONT_BODY);
        c.setBackground(Color.WHITE);
    }
    private void styleTable(JTable t) {
        t.setFont(FONT_BODY);
        t.setRowHeight(28);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.getTableHeader().setBackground(new Color(230, 245, 235));
        t.getTableHeader().setForeground(CLR_HEADER);
        t.setGridColor(CLR_BORDER);
        t.setSelectionBackground(new Color(200, 235, 215));
    }
    private JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(CLR_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(CLR_PRIMARY_D); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(CLR_PRIMARY); }
        });
        return btn;
    }
    private JButton ghostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_SMALL);
        btn.setForeground(CLR_MUTED);
        btn.setBackground(CLR_PANEL);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
    private GridBagConstraints gbc() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 4, 5, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        return gc;
    }
    private JPanel buildStatus() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(235, 242, 235));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, CLR_BORDER));
        statusLabel = new JLabel("  Ready. Register or select a user to begin.");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(CLR_MUTED);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        bar.add(statusLabel, BorderLayout.WEST);
        JButton saveBtn = ghostButton("💾  Save Now");
        saveBtn.setFont(FONT_SMALL);
        saveBtn.addActionListener(e -> {
            fileHandler.saveToFile(users);
            showStatus("Data saved to disk.");
        });
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 3));
        right.setOpaque(false);
        right.add(saveBtn);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }
    private void showStatus(String msg) {
        statusLabel.setText("  " + msg);
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
    // ── main ──────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(CarbonCommuteGUI::new);
    }
}