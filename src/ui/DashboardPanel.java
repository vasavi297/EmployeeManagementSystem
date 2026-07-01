package ui;

import dao.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private EmployeeDAO employeeDAO = new EmployeeDAO();

    public DashboardPanel() {
        setLayout(new GridLayout(2, 2, 20, 20));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createStatCard("Total Employees", String.valueOf(employeeDAO.getEmployeeCount()), "👥", new Color(33, 150, 243)));
        add(createStatCard("Departments", getDepartmentCount(), "🏢", new Color(76, 175, 80)));
        add(createStatCard("Total Salary", getTotalSalary(), "💰", new Color(255, 193, 7)));
        add(createStatCard("Average Salary", getAverageSalary(), "📊", new Color(156, 39, 176)));
    }

    private JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        card.add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(117, 117, 117));
        textPanel.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private String getDepartmentCount() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        Map<String, Integer> deptMap = new HashMap<>();
        for (Employee emp : employees) {
            deptMap.put(emp.getDepartment(), deptMap.getOrDefault(emp.getDepartment(), 0) + 1);
        }
        return String.valueOf(deptMap.size());
    }

    private String getTotalSalary() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        double total = employees.stream().mapToDouble(Employee::getSalary).sum();
        return String.format("$%.2f", total);
    }

    private String getAverageSalary() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        double avg = employees.stream().mapToDouble(Employee::getSalary).average().orElse(0);
        return String.format("$%.2f", avg);
    }
}