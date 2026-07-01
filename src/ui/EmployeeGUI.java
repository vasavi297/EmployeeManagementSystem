package ui;

import dao.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class EmployeeGUI extends JFrame {
    // Database and utility objects
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // UI Components
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, emailField, departmentField, designationField, phoneField, salaryField, searchField;
    private JButton addButton, updateButton, deleteButton, clearButton, exportButton, refreshButton, statsButton;
    private JLabel statusLabel;
    private JLabel recordCountLabel;
    private JPanel formPanel;
    private int selectedEmployeeId = -1;

    // Color scheme - Professional Corporate Theme
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
    private static final Color HEADER_BG = new Color(40, 53, 147);
    private static final Color TABLE_HEADER_BG = new Color(33, 150, 243);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color DANGER_COLOR = new Color(244, 67, 54);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color SECONDARY_TEXT = new Color(117, 117, 117);

    public EmployeeGUI() {
        initUI();
        loadEmployeeData();
    }

    private void initUI() {
        setTitle("Employee Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND_COLOR);

        createMenuBar();
        createHeaderPanel();
        createMainContent();
        createStatusBar();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(20, 30, 60));

        Color textColor = new Color(0, 89, 255);
        Font menuFont = new Font("Segoe UI", Font.BOLD, 14);
        Font itemFont = new Font("Segoe UI", Font.PLAIN, 13);

        // ========== FILE MENU ==========
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(textColor);
        fileMenu.setFont(menuFont);

        JMenuItem exportCSVItem = new JMenuItem("Export to CSV");
        exportCSVItem.setFont(itemFont);
        exportCSVItem.setForeground(textColor);
        exportCSVItem.setBackground(new Color(20, 30, 60));
        exportCSVItem.addActionListener(e -> exportData());
        fileMenu.add(exportCSVItem);

        fileMenu.addSeparator();

        JMenuItem printItem = new JMenuItem("Print");
        printItem.setFont(itemFont);
        printItem.setForeground(textColor);
        printItem.setBackground(new Color(20, 30, 60));
        printItem.addActionListener(e -> printTable());
        fileMenu.add(printItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(itemFont);
        exitItem.setForeground(textColor);
        exitItem.setBackground(new Color(20, 30, 60));
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // ========== EMPLOYEE MENU ==========
        JMenu employeeMenu = new JMenu("Employee");
        employeeMenu.setForeground(textColor);
        employeeMenu.setFont(menuFont);

        JMenuItem addMenuItem = new JMenuItem("Add New Employee");
        addMenuItem.setFont(itemFont);
        addMenuItem.setForeground(textColor);
        addMenuItem.setBackground(new Color(20, 30, 60));
        addMenuItem.addActionListener(e -> clearAndFocus());
        employeeMenu.add(addMenuItem);

        JMenuItem viewAllItem = new JMenuItem("View All");
        viewAllItem.setFont(itemFont);
        viewAllItem.setForeground(textColor);
        viewAllItem.setBackground(new Color(20, 30, 60));
        viewAllItem.addActionListener(e -> loadEmployeeData());
        employeeMenu.add(viewAllItem);

        employeeMenu.addSeparator();

        JMenuItem searchItem = new JMenuItem("Search");
        searchItem.setFont(itemFont);
        searchItem.setForeground(textColor);
        searchItem.setBackground(new Color(20, 30, 60));
        searchItem.addActionListener(e -> searchField.requestFocus());
        employeeMenu.add(searchItem);

        // ========== REPORTS MENU ==========
        JMenu reportsMenu = new JMenu("Reports");
        reportsMenu.setForeground(textColor);
        reportsMenu.setFont(menuFont);

        JMenuItem statsItem = new JMenuItem("Statistics");
        statsItem.setFont(itemFont);
        statsItem.setForeground(textColor);
        statsItem.setBackground(new Color(20, 30, 60));
        statsItem.addActionListener(e -> showStatistics());
        reportsMenu.add(statsItem);

        JMenuItem chartItem = new JMenuItem("Charts Dashboard");
        chartItem.setFont(itemFont);
        chartItem.setForeground(textColor);
        chartItem.setBackground(new Color(20, 30, 60));
        chartItem.addActionListener(e -> showCharts());
        reportsMenu.add(chartItem);

        JMenuItem deptStatsItem = new JMenuItem("Department Report");
        deptStatsItem.setFont(itemFont);
        deptStatsItem.setForeground(textColor);
        deptStatsItem.setBackground(new Color(20, 30, 60));
        deptStatsItem.addActionListener(e -> showDepartmentReport());
        reportsMenu.add(deptStatsItem);

        // ========== HELP MENU ==========
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(textColor);
        helpMenu.setFont(menuFont);

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setFont(itemFont);
        aboutItem.setForeground(textColor);
        aboutItem.setBackground(new Color(20, 30, 60));
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        // Set hover/selection colors
        UIManager.put("MenuItem.selectionBackground", new Color(0, 13, 255));
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);
        UIManager.put("Menu.selectionBackground", new Color(0, 72, 255));
        UIManager.put("Menu.selectionForeground", Color.WHITE);

        // ========== ADD TO MENU BAR ==========
        menuBar.add(fileMenu);
        menuBar.add(employeeMenu);
        menuBar.add(reportsMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }
    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);

        JLabel titleIcon = new JLabel("👥");
        titleIcon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        titlePanel.add(titleIcon);

        JLabel titleLabel = new JLabel("Employee Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("v2.0 | Enterprise Edition");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 255));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        statsPanel.setOpaque(false);

        JLabel totalLabel = new JLabel("Total Employees: " + employeeDAO.getEmployeeCount());
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(Color.WHITE);
        statsPanel.add(totalLabel);

        headerPanel.add(statsPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(createToolbar(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerSize(5);
        splitPane.setBackground(BACKGROUND_COLOR);
        splitPane.setBorder(null);

        splitPane.setLeftComponent(createTablePanel());

        formPanel = createFormPanel();
        splitPane.setRightComponent(formPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

    }
 

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        // Professional Dark Blue Toolbar
        toolbar.setBackground(new Color(40, 53, 147));
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(30, 40, 100)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // ===== Refresh Button - Green =====
        refreshButton = createStyledButton("🔄 Refresh", new Color(76, 175, 80), Color.WHITE);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshButton.addActionListener(e -> loadEmployeeData());
        toolbar.add(refreshButton);

        toolbar.add(createSeparator());

        // ===== Search Section =====
        JLabel searchLabel = new JLabel("🔍");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchLabel.setForeground(Color.WHITE);
        toolbar.add(searchLabel);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(220, 35));
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(new Color(33, 33, 33));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        toolbar.add(searchField);

        // ===== Search Button - Orange =====
        JButton searchBtn = createStyledButton("Search", new Color(255, 152, 0), Color.WHITE);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBtn.addActionListener(e -> searchEmployees(searchField.getText()));
        toolbar.add(searchBtn);

        // ===== Clear Search Button - Gray =====
        JButton clearSearchBtn = createStyledButton("✕", new Color(158, 158, 158), Color.WHITE);
        clearSearchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearSearchBtn.setPreferredSize(new Dimension(40, 35));
        clearSearchBtn.addActionListener(e -> {
            searchField.setText("");
            loadEmployeeData();
        });
        toolbar.add(clearSearchBtn);

        toolbar.add(createSeparator());

        // ===== Statistics Button - Purple =====
        statsButton = createStyledButton("📊 Statistics", new Color(156, 39, 176), Color.WHITE);
        statsButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statsButton.addActionListener(e -> showStatistics());
        toolbar.add(statsButton);

        // ===== Charts Dashboard Button - Cyan =====
        JButton chartBtn = createStyledButton("📈 Charts", new Color(0, 188, 212), Color.WHITE);
        chartBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        chartBtn.addActionListener(e -> showCharts());
        toolbar.add(chartBtn);

        // ===== Export CSV Button - Teal =====
        exportButton = createStyledButton("📤 Export CSV", new Color(0, 150, 136), Color.WHITE);
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        exportButton.addActionListener(e -> exportData());
        toolbar.add(exportButton);

        toolbar.add(createSeparator());

        // ===== Department Report Button - Red =====
        JButton deptBtn = createStyledButton("🏢 Departments", new Color(244, 67, 54), Color.WHITE);
        deptBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deptBtn.addActionListener(e -> showDepartmentReport());
        toolbar.add(deptBtn);

        // ===== Add Spacer to push right side items =====
        toolbar.add(Box.createHorizontalGlue());

        // ===== Right side - Employee Count =====
        JLabel countLabel = new JLabel("👥 " + employeeDAO.getEmployeeCount() + " Employees");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countLabel.setForeground(Color.WHITE);
        toolbar.add(countLabel);

        return toolbar;
    }
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel tableTitle = new JLabel("Employee Records");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(PRIMARY_COLOR);
        panel.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Department", "Designation", "Salary", "Email", "Phone", "Joining Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setRowHeight(30);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        employeeTable.setSelectionBackground(new Color(200, 220, 255));
        employeeTable.setSelectionForeground(Color.BLACK);
        employeeTable.setShowGrid(true);
        employeeTable.setGridColor(new Color(240, 240, 240));

        // Table Header Styling - VISIBLE
        JTableHeader header = employeeTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 35));

        // Header Renderer to ensure visibility
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(TABLE_HEADER_BG);
                c.setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        // Column widths
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        employeeTable.getColumnModel().getColumn(5).setPreferredWidth(180);
        employeeTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        employeeTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow != -1) {
                    populateForm(selectedRow);
                }
            }
        });

        employeeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = employeeTable.getSelectedRow();
                    if (row != -1) {
                        populateForm(row);
                        formPanel.setBorder(BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                                "✏️ Editing Employee",
                                TitledBorder.LEFT,
                                TitledBorder.TOP,
                                new Font("Segoe UI", Font.BOLD, 14),
                                PRIMARY_COLOR
                        ));
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224)));
        panel.add(scrollPane, BorderLayout.CENTER);

        recordCountLabel = new JLabel("Showing 0 records");
        recordCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        recordCountLabel.setForeground(SECONDARY_TEXT);
        panel.add(recordCountLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                "Employee Details",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                PRIMARY_COLOR
        ));
        panel.setPreferredSize(new Dimension(400, 0));

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setBackground(CARD_COLOR);
        formFields.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Name", "Department", "Designation", "Salary", "Email", "Phone"};
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setForeground(TEXT_COLOR);
            formFields.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            fields[i] = new JTextField(15);
            fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            fields[i].setBackground(Color.WHITE);
            formFields.add(fields[i], gbc);
        }

        nameField = fields[0];
        departmentField = fields[1];
        designationField = fields[2];
        salaryField = fields[3];
        emailField = fields[4];
        phoneField = fields[5];

        panel.add(formFields, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        addButton = createStyledButton("Add New", SUCCESS_COLOR, Color.WHITE);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setPreferredSize(new Dimension(0, 40));
        addButton.addActionListener(e -> addEmployee());

        updateButton = createStyledButton("Update", PRIMARY_COLOR, Color.WHITE);
        updateButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateButton.setPreferredSize(new Dimension(0, 40));
        updateButton.addActionListener(e -> updateEmployee());

        deleteButton = createStyledButton("Delete", DANGER_COLOR, Color.WHITE);
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteButton.setPreferredSize(new Dimension(0, 40));
        deleteButton.addActionListener(e -> deleteEmployee());

        clearButton = createStyledButton("Clear", new Color(158, 158, 158), Color.WHITE);
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearButton.setPreferredSize(new Dimension(0, 40));
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout(10, 0));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        statusPanel.setPreferredSize(new Dimension(0, 35));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(TEXT_COLOR);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        JLabel systemInfo = new JLabel("Java " + System.getProperty("java.version") + " | MySQL Connected");
        systemInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        systemInfo.setForeground(SECONDARY_TEXT);
        statusPanel.add(systemInfo, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    // ============ BUSINESS LOGIC METHODS ============

    private void loadEmployeeData() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.getAllEmployees();

        for (Employee emp : employees) {
            Object[] row = {
                    emp.getEmployeeId(),
                    emp.getName(),
                    emp.getDepartment(),
                    emp.getDesignation(),
                    String.format("$%.2f", emp.getSalary()),
                    emp.getEmail(),
                    emp.getPhone(),
                    emp.getJoiningDate()
            };
            tableModel.addRow(row);
        }
        updateStatus("Loaded " + employees.size() + " employees", true);
        updateRecordCount(employees.size());
    }

    private void updateRecordCount(int count) {
        if (recordCountLabel != null) {
            recordCountLabel.setText("Showing " + count + " records");
        }
    }

    private void addEmployee() {
        try {
            validateForm();
            Employee employee = getEmployeeFromForm();
            employeeDAO.addEmployee(employee);
            loadEmployeeData();
            clearForm();
            updateStatus("Employee added successfully!", true);
            JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            updateStatus("Error: " + ex.getMessage(), false);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            validateForm();
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Employee employee = getEmployeeFromForm();
            employee.setEmployeeId(id);
            employeeDAO.updateEmployee(employee);
            loadEmployeeData();
            clearForm();
            updateStatus("Employee updated successfully!", true);
            JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            updateStatus("Error: " + ex.getMessage(), false);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete employee: " + name + "?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            employeeDAO.deleteEmployee(id);
            loadEmployeeData();
            clearForm();
            updateStatus("Employee deleted successfully!", true);
            JOptionPane.showMessageDialog(this, "Employee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchEmployees(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadEmployeeData();
            return;
        }

        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.getAllEmployees();
        int count = 0;

        for (Employee emp : employees) {
            if (emp.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    emp.getDepartment().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    emp.getEmail().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    emp.getDesignation().toLowerCase().contains(searchTerm.toLowerCase())) {
                Object[] row = {
                        emp.getEmployeeId(),
                        emp.getName(),
                        emp.getDepartment(),
                        emp.getDesignation(),
                        String.format("$%.2f", emp.getSalary()),
                        emp.getEmail(),
                        emp.getPhone(),
                        emp.getJoiningDate()
                };
                tableModel.addRow(row);
                count++;
            }
        }
        updateStatus("Found " + count + " employees matching '" + searchTerm + "'", true);
        updateRecordCount(count);
    }

    private void populateForm(int row) {
        try {
            selectedEmployeeId = (int) tableModel.getValueAt(row, 0);
            nameField.setText((String) tableModel.getValueAt(row, 1));
            departmentField.setText((String) tableModel.getValueAt(row, 2));
            designationField.setText((String) tableModel.getValueAt(row, 3));
            String salaryStr = ((String) tableModel.getValueAt(row, 4)).replace("$", "");
            salaryField.setText(salaryStr);
            emailField.setText((String) tableModel.getValueAt(row, 5));
            phoneField.setText((String) tableModel.getValueAt(row, 6));

            formPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    "Editing Employee ID: " + selectedEmployeeId,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Segoe UI", Font.BOLD, 14),
                    PRIMARY_COLOR
            ));

            updateStatus("Editing employee: " + nameField.getText(), true);
        } catch (Exception e) {
            // Handle any errors
        }
    }

    private Employee getEmployeeFromForm() {
        String name = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String designation = designationField.getText().trim();
        double salary = Double.parseDouble(salaryField.getText().trim());
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        LocalDate joiningDate = LocalDate.now();

        return new Employee(name, department, designation, salary, email, phone, joiningDate);
    }

    private void validateForm() throws Exception {
        if (nameField.getText().trim().isEmpty()) {
            throw new Exception("Name is required");
        }
        if (departmentField.getText().trim().isEmpty()) {
            throw new Exception("Department is required");
        }
        if (designationField.getText().trim().isEmpty()) {
            throw new Exception("Designation is required");
        }
        if (salaryField.getText().trim().isEmpty()) {
            throw new Exception("Salary is required");
        }
        try {
            Double.parseDouble(salaryField.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Invalid salary format");
        }
        if (emailField.getText().trim().isEmpty()) {
            throw new Exception("Email is required");
        }
        if (!emailField.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("Invalid email format");
        }
        if (phoneField.getText().trim().isEmpty()) {
            throw new Exception("Phone is required");
        }
        if (!phoneField.getText().trim().matches("^[0-9]{10}$")) {
            throw new Exception("Phone must be 10 digits");
        }
    }

    private void clearForm() {
        nameField.setText("");
        departmentField.setText("");
        designationField.setText("");
        salaryField.setText("");
        emailField.setText("");
        phoneField.setText("");
        employeeTable.clearSelection();
        selectedEmployeeId = -1;
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                "Employee Details",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                PRIMARY_COLOR
        ));
        updateStatus("Form cleared", true);
    }

    private void clearAndFocus() {
        clearForm();
        nameField.requestFocus();
    }

    // ============ STATISTICS METHODS ============

    private void showStatistics() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No employees in database", "Statistics", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        double totalSalary = 0;
        double maxSalary = 0;
        double minSalary = Double.MAX_VALUE;

        for (Employee emp : employees) {
            totalSalary += emp.getSalary();
            if (emp.getSalary() > maxSalary) maxSalary = emp.getSalary();
            if (emp.getSalary() < minSalary) minSalary = emp.getSalary();
        }

        double avgSalary = totalSalary / employees.size();

        JPanel statsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addStatRow(statsPanel, "Total Employees:", String.valueOf(employees.size()));
        addStatRow(statsPanel, "Total Salary Pool:", String.format("$%.2f", totalSalary));
        addStatRow(statsPanel, "Average Salary:", String.format("$%.2f", avgSalary));
        addStatRow(statsPanel, "Highest Salary:", String.format("$%.2f", maxSalary));
        addStatRow(statsPanel, "Lowest Salary:", String.format("$%.2f", minSalary));

        JOptionPane.showMessageDialog(this, statsPanel, "Employee Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addStatRow(JPanel panel, String label, String value) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(labelComp);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComp.setForeground(PRIMARY_COLOR);
        panel.add(valueComp);
    }

    // ============ EXPORT METHODS ============

    private void exportData() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        fileChooser.setSelectedFile(new File("employees_export_" + LocalDate.now() + ".csv"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();

                if (!filePath.toLowerCase().endsWith(".csv")) {
                    filePath = filePath + ".csv";
                }

                FileWriter writer = new FileWriter(filePath);
                writer.write("ID,Name,Department,Designation,Salary,Email,Phone,Joining Date\n");

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        if (j > 0) line.append(",");
                        Object value = tableModel.getValueAt(i, j);
                        if (value != null) {
                            String stringValue = value.toString();
                            if (stringValue.contains(",")) {
                                line.append("\"").append(stringValue).append("\"");
                            } else {
                                line.append(stringValue);
                            }
                        }
                    }
                    writer.write(line.toString() + "\n");
                }
                writer.close();

                JOptionPane.showMessageDialog(this, "Data exported successfully to:\n" + filePath, "Success", JOptionPane.INFORMATION_MESSAGE);
                updateStatus("Exported to: " + filePath, true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                updateStatus("Export failed: " + ex.getMessage(), false);
            }
        }
    }

    private void exportToPDF() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "PDF Export requires iText library.\n" +
                        "Add to pom.xml:\n" +
                        "<dependency>\n" +
                        "    <groupId>com.itextpdf</groupId>\n" +
                        "    <artifactId>itext7-core</artifactId>\n" +
                        "    <version>7.2.5</version>\n" +
                        "</dependency>\n\n" +
                        "Or download from: https://repo1.maven.org/maven2/com/itextpdf/itext7-core/7.2.5/",
                "PDF Export",
                JOptionPane.INFORMATION_MESSAGE);

        // Simple CSV fallback
        exportData();
    }

    private void printTable() {
        try {
            boolean complete = employeeTable.print(JTable.PrintMode.FIT_WIDTH);
            if (complete) {
                updateStatus("Printing completed", true);
            } else {
                updateStatus("Printing cancelled", false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Printing error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============ CHART METHODS ============

    private void showCharts() {
        JFrame chartFrame = new JFrame("Charts Dashboard");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setSize(900, 700);
        chartFrame.setLocationRelativeTo(this);
        chartFrame.add(createChartPanel());
        chartFrame.setVisible(true);
    }

    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<Employee> employees = employeeDAO.getAllEmployees();
        Map<String, Integer> deptCount = new HashMap<>();
        Map<String, Double> deptSalary = new HashMap<>();
        Map<String, Integer> deptEmpCount = new HashMap<>();

        for (Employee emp : employees) {
            deptCount.put(emp.getDepartment(), deptCount.getOrDefault(emp.getDepartment(), 0) + 1);
            deptSalary.put(emp.getDepartment(), deptSalary.getOrDefault(emp.getDepartment(), 0.0) + emp.getSalary());
            deptEmpCount.put(emp.getDepartment(), deptEmpCount.getOrDefault(emp.getDepartment(), 0) + 1);
        }

        panel.add(createPieChartPanel(deptCount));
        panel.add(createBarChartPanel(deptSalary, deptEmpCount));
        panel.add(createCountChartPanel(deptCount));
        panel.add(createSummaryPanel(employees));

        return panel;
    }

    private JPanel createPieChartPanel(Map<String, Integer> data) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Department Distribution"));
        panel.setBackground(Color.WHITE);

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int total = data.values().stream().mapToInt(Integer::intValue).sum();
                if (total == 0) {
                    g.drawString("No data available", getWidth()/2 - 50, getHeight()/2);
                    return;
                }
                int startAngle = 0;
                Color[] colors = {new Color(33, 150, 243), new Color(76, 175, 80),
                        new Color(255, 193, 7), new Color(156, 39, 176),
                        new Color(244, 67, 54), new Color(0, 188, 212)};
                int colorIndex = 0;

                int centerX = getWidth() / 2 - 20;
                int centerY = getHeight() / 2;
                int radius = Math.min(getWidth(), getHeight()) / 3;

                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    int angle = (int) ((entry.getValue() * 360.0) / total);
                    g.setColor(colors[colorIndex % colors.length]);
                    g.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, startAngle, angle);
                    startAngle += angle;
                    colorIndex++;
                }

                // Legend
                int legendY = 20;
                colorIndex = 0;
                g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    g.setColor(colors[colorIndex % colors.length]);
                    g.fillRect(getWidth() - 130, legendY, 15, 15);
                    g.setColor(Color.BLACK);
                    g.drawString(entry.getKey() + " (" + entry.getValue() + ")", getWidth() - 110, legendY + 12);
                    legendY += 22;
                    colorIndex++;
                }
            }
        };
        chartPanel.setBackground(Color.WHITE);
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBarChartPanel(Map<String, Double> salaryData, Map<String, Integer> countData) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Average Salary by Department"));
        panel.setBackground(Color.WHITE);

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (salaryData.isEmpty()) {
                    g.drawString("No data available", getWidth()/2 - 50, getHeight()/2);
                    return;
                }
                int x = 30;
                int y = getHeight() - 40;
                int barWidth = 60;
                int maxHeight = getHeight() - 80;
                double maxSalary = salaryData.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);

                g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                int index = 0;
                Color[] colors = {new Color(33, 150, 243), new Color(76, 175, 80),
                        new Color(255, 193, 7), new Color(156, 39, 176),
                        new Color(244, 67, 54)};

                for (Map.Entry<String, Double> entry : salaryData.entrySet()) {
                    double avgSalary = entry.getValue() / countData.get(entry.getKey());
                    int height = (int) ((avgSalary / maxSalary) * maxHeight);
                    g.setColor(colors[index % colors.length]);
                    g.fillRect(x, y - height, barWidth, height);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y - height, barWidth, height);

                    String label = entry.getKey().length() > 8 ? entry.getKey().substring(0, 8) + ".." : entry.getKey();
                    g.drawString(label, x + 5, y + 20);
                    g.drawString("$" + String.format("%.0f", avgSalary), x + 5, y - height - 5);
                    x += barWidth + 15;
                    index++;
                }
            }
        };
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCountChartPanel(Map<String, Integer> data) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Employee Count by Department"));
        panel.setBackground(Color.WHITE);

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (data.isEmpty()) {
                    g.drawString("No data available", getWidth()/2 - 50, getHeight()/2);
                    return;
                }
                int x = 30;
                int y = getHeight() - 40;
                int barWidth = 60;
                int maxHeight = getHeight() - 80;
                int maxCount = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);

                g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                int index = 0;
                Color[] colors = {new Color(76, 175, 80), new Color(33, 150, 243),
                        new Color(255, 193, 7), new Color(156, 39, 176),
                        new Color(244, 67, 54)};

                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    int height = (entry.getValue() * maxHeight) / maxCount;
                    g.setColor(colors[index % colors.length]);
                    g.fillRect(x, y - height, barWidth, height);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y - height, barWidth, height);

                    String label = entry.getKey().length() > 8 ? entry.getKey().substring(0, 8) + ".." : entry.getKey();
                    g.drawString(label, x + 5, y + 20);
                    g.drawString(String.valueOf(entry.getValue()), x + 20, y - height - 5);
                    x += barWidth + 15;
                    index++;
                }
            }
        };
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSummaryPanel(List<Employee> employees) {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Quick Summary"));
        panel.setBackground(Color.WHITE);

        double totalSalary = employees.stream().mapToDouble(Employee::getSalary).sum();
        double avgSalary = employees.isEmpty() ? 0 : totalSalary / employees.size();

        panel.add(createSummaryRow("Total Employees", String.valueOf(employees.size()), new Color(33, 150, 243)));
        panel.add(createSummaryRow("Total Salary", String.format("$%.2f", totalSalary), new Color(76, 175, 80)));
        panel.add(createSummaryRow("Average Salary", String.format("$%.2f", avgSalary), new Color(255, 193, 7)));
        panel.add(createSummaryRow("Departments", String.valueOf(getDepartmentCount(employees)), new Color(156, 39, 176)));

        return panel;
    }

    private JPanel createSummaryRow(String label, String value, Color color) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        row.add(labelComp, BorderLayout.WEST);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueComp.setForeground(color);
        row.add(valueComp, BorderLayout.EAST);

        return row;
    }

    private int getDepartmentCount(List<Employee> employees) {
        Map<String, Integer> deptMap = new HashMap<>();
        for (Employee emp : employees) {
            deptMap.put(emp.getDepartment(), deptMap.getOrDefault(emp.getDepartment(), 0) + 1);
        }
        return deptMap.size();
    }

    private void showDepartmentReport() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No employees in database", "Department Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Map<String, Integer> deptCount = new HashMap<>();
        Map<String, Double> deptSalary = new HashMap<>();

        for (Employee emp : employees) {
            deptCount.put(emp.getDepartment(), deptCount.getOrDefault(emp.getDepartment(), 0) + 1);
            deptSalary.put(emp.getDepartment(), deptSalary.getOrDefault(emp.getDepartment(), 0.0) + emp.getSalary());
        }

        StringBuilder report = new StringBuilder();
        report.append("Department Report\n");
        report.append("=".repeat(60)).append("\n");
        report.append(String.format("%-20s %-15s %-15s %-15s\n", "Department", "Employees", "Total Salary", "Avg Salary"));
        report.append("-".repeat(60)).append("\n");

        for (Map.Entry<String, Integer> entry : deptCount.entrySet()) {
            String dept = entry.getKey();
            int count = entry.getValue();
            double total = deptSalary.get(dept);
            double avg = total / count;
            report.append(String.format("%-20s %-15d %-15.2f %-15.2f\n", dept, count, total, avg));
        }
        report.append("=".repeat(60));

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Department Report", JOptionPane.INFORMATION_MESSAGE);
    }

    // ============ UI UTILITY METHODS ============

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darker(bgColor));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private Color darker(Color color) {
        return new Color(Math.max(0, color.getRed() - 30),
                Math.max(0, color.getGreen() - 30),
                Math.max(0, color.getBlue() - 30));
    }

    private JLabel createSeparator() {
        JLabel separator = new JLabel("|");
        separator.setFont(new Font("Segoe UI", Font.BOLD, 20));
        separator.setForeground(new Color(200, 200, 200));
        return separator;
    }

    private void updateStatus(String message, boolean isSuccess) {
        statusLabel.setText((isSuccess ? "✅ " : "❌ ") + message);
        statusLabel.setForeground(isSuccess ? SUCCESS_COLOR : DANGER_COLOR);
    }

    private void showAboutDialog() {
        String about = """
            Employee Management System
            Version 2.0 Enterprise Edition
            
            Developed with:
            • Java 17+
            • Swing UI Framework
            • JDBC with MySQL
            • MVC Architecture
            
            Features:
            • Full CRUD Operations
            • Secure Database Access
            • Advanced Search
            • Data Export (CSV)
            • Charts Dashboard
            • Statistics Dashboard
            • Department Reports
            
            © 2026 All Rights Reserved
            """;
        JOptionPane.showMessageDialog(this, about, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    // ============ MAIN METHOD ============

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new EmployeeGUI().setVisible(true);
        });
    }
}