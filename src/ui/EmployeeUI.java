package ui;

import dao.EmployeeDAO;
import db.DatabaseVerifier;
import model.Employee;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class EmployeeUI {
    private static final Scanner scanner = new Scanner(System.in);
    private static final EmployeeDAO employeeDAO = new EmployeeDAO();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        showMainMenu();
    }
    public static void showMainMenu() {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("     EMPLOYEE MANAGEMENT SYSTEM");
            System.out.println("========================================");
            System.out.println("1. Add New Employee");
            System.out.println("2. View All Employees");
            System.out.println("3. Search Employee by ID");
            System.out.println("4. Update Employee");
            System.out.println("5. Delete Employee");
            System.out.println("6. View Employee Statistics");
            System.out.println("7. Verify Employee in Database");  // NEW OPTION
            System.out.println("8. Exit");
            System.out.println("========================================");
            System.out.print("Enter your choice (1-8): ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    addEmployee();
                    break;
                case 2:
                    viewAllEmployees();
                    break;
                case 3:
                    searchEmployee();
                    break;
                case 4:
                    updateEmployee();
                    break;
                case 5:
                    deleteEmployee();
                    break;
                case 6:
                    viewStatistics();
                    break;
                case 7:
                    verifyEmployeeInDatabase();  // NEW CASE
                    break;
                case 8:
                    System.out.println("\nExiting Employee Management System. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1-8.");
            }
        }
    }
    private static void addEmployee() {
        System.out.println("\n--- ADD NEW EMPLOYEE ---");

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Department: ");
        String department = scanner.nextLine();

        System.out.print("Enter Designation: ");
        String designation = scanner.nextLine();

        System.out.print("Enter Salary: ");
        double salary = getDoubleInput();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Enter Joining Date (YYYY-MM-DD): ");
        LocalDate joiningDate = getDateInput();

        Employee employee = new Employee(name, department, designation, salary, email, phone, joiningDate);
        employeeDAO.addEmployee(employee);

        System.out.println("\nEmployee added successfully.");

        // Auto-verify
        System.out.println("\nVerifying database entry...");
        List<Employee> employees = employeeDAO.getAllEmployees();
        if (!employees.isEmpty()) {
            Employee lastEmployee = employees.get(employees.size() - 1);
            int employeeId = lastEmployee.getEmployeeId();

            // Verify using DatabaseVerifier
            if (DatabaseVerifier.employeeExists(employeeId)) {
                System.out.println("Verification: Employee ID " + employeeId + " exists in database.");
            } else {
                System.out.println("Verification: Employee record not found in database.");
            }
        }
        pause();
    }
    private static void viewAllEmployees() {
        System.out.println("\n--- ALL EMPLOYEES ---");
        List<Employee> employees = employeeDAO.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found in the database.");
        } else {
            System.out.println("\nTotal Employees: " + employees.size());
            System.out.println("\n" + "-".repeat(100));
            System.out.printf("%-5s %-20s %-15s %-20s %-10s %-25s %-12s\n",
                    "ID", "Name", "Department", "Designation", "Salary", "Email", "Joining Date");
            System.out.println("-".repeat(100));

            for (Employee emp : employees) {
                System.out.printf("%-5d %-20s %-15s %-20s %-10.2f %-25s %-12s\n",
                        emp.getEmployeeId(),
                        truncateString(emp.getName(), 20),
                        truncateString(emp.getDepartment(), 15),
                        truncateString(emp.getDesignation(), 20),
                        emp.getSalary(),
                        truncateString(emp.getEmail(), 25),
                        emp.getJoiningDate()
                );
            }
            System.out.println("-".repeat(100));
        }
        pause();
    }
    private static void viewStatistics() {
        System.out.println("\n--- EMPLOYEE STATISTICS ---");
        List<Employee> employees = employeeDAO.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees in database.");
            pause();
            return;
        }

        int totalEmployees = employees.size();
        double totalSalary = 0;
        double maxSalary = 0;
        double minSalary = Double.MAX_VALUE;

        for (Employee emp : employees) {
            totalSalary += emp.getSalary();
            if (emp.getSalary() > maxSalary) {
                maxSalary = emp.getSalary();
            }
            if (emp.getSalary() < minSalary) {
                minSalary = emp.getSalary();
            }
        }

        double averageSalary = totalSalary / totalEmployees;

        System.out.println("\nSummary:");
        System.out.println("=".repeat(40));
        System.out.println("Total Employees   : " + totalEmployees);
        System.out.println("Total Salary Pool : $" + String.format("%.2f", totalSalary));
        System.out.println("Average Salary    : $" + String.format("%.2f", averageSalary));
        System.out.println("Highest Salary    : $" + String.format("%.2f", maxSalary));
        System.out.println("Lowest Salary     : $" + String.format("%.2f", minSalary));
        System.out.println("=".repeat(40));

        pause();
    }

    private static void searchEmployee() {
        System.out.println("\n--- SEARCH EMPLOYEE ---");
        System.out.print("Enter Employee ID: ");
        int id = getIntInput();

        Employee employee = employeeDAO.getEmployeeById(id);

        if (employee != null) {
            System.out.println("\nEmployee Found:");
            System.out.println("=".repeat(50));
            System.out.println("ID          : " + employee.getEmployeeId());
            System.out.println("Name        : " + employee.getName());
            System.out.println("Department  : " + employee.getDepartment());
            System.out.println("Designation : " + employee.getDesignation());
            System.out.println("Salary      : $" + employee.getSalary());
            System.out.println("Email       : " + employee.getEmail());
            System.out.println("Phone       : " + employee.getPhone());
            System.out.println("Joining Date: " + employee.getJoiningDate());
            System.out.println("=".repeat(50));
        } else {
            System.out.println("No employee found with ID: " + id);
        }
        pause();
    }

    private static void updateEmployee() {
        System.out.println("\n--- UPDATE EMPLOYEE ---");
        System.out.print("Enter Employee ID to update: ");
        int id = getIntInput();

        Employee employee = employeeDAO.getEmployeeById(id);

        if (employee == null) {
            System.out.println("No employee found with ID: " + id);
            pause();
            return;
        }

        System.out.println("\nCurrent Employee Details:");
        System.out.println("Name        : " + employee.getName());
        System.out.println("Department  : " + employee.getDepartment());
        System.out.println("Designation : " + employee.getDesignation());
        System.out.println("Salary      : $" + employee.getSalary());
        System.out.println("Email       : " + employee.getEmail());
        System.out.println("Phone       : " + employee.getPhone());
        System.out.println("Joining Date: " + employee.getJoiningDate());

        System.out.println("\nEnter New Details (Press Enter to keep current value)");

        System.out.print("Enter Name [" + employee.getName() + "]: ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) employee.setName(name);

        System.out.print("Enter Department [" + employee.getDepartment() + "]: ");
        String department = scanner.nextLine();
        if (!department.isEmpty()) employee.setDepartment(department);

        System.out.print("Enter Designation [" + employee.getDesignation() + "]: ");
        String designation = scanner.nextLine();
        if (!designation.isEmpty()) employee.setDesignation(designation);

        System.out.print("Enter Salary [$" + employee.getSalary() + "]: ");
        String salaryInput = scanner.nextLine();
        if (!salaryInput.isEmpty()) {
            try {
                employee.setSalary(Double.parseDouble(salaryInput));
            } catch (NumberFormatException e) {
                System.out.println("Invalid salary format. Keeping current value.");
            }
        }

        System.out.print("Enter Email [" + employee.getEmail() + "]: ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) employee.setEmail(email);

        System.out.print("Enter Phone [" + employee.getPhone() + "]: ");
        String phone = scanner.nextLine();
        if (!phone.isEmpty()) employee.setPhone(phone);

        System.out.print("Enter Joining Date [" + employee.getJoiningDate() + "] (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine();
        if (!dateInput.isEmpty()) {
            try {
                employee.setJoiningDate(LocalDate.parse(dateInput, dateFormatter));
            } catch (Exception e) {
                System.out.println("Invalid date format. Keeping current value.");
            }
        }

        employeeDAO.updateEmployee(employee);
        System.out.println("Employee updated successfully.");

        // ========== ADD VERIFICATION HERE ==========
        // Verify the update
        Employee verifiedEmployee = employeeDAO.getEmployeeById(id);
        if (verifiedEmployee != null) {
            System.out.println("Verification: Employee ID " + id + " updated successfully.");
            System.out.println("Updated Name: " + verifiedEmployee.getName());
            System.out.println("Updated Salary: $" + verifiedEmployee.getSalary());
        } else {
            System.out.println("Verification: Employee ID " + id + " not found after update.");
        }
        // ==========================================

        pause();
    }

    private static void deleteEmployee() {
        System.out.println("\n--- DELETE EMPLOYEE ---");
        System.out.print("Enter Employee ID to delete: ");
        int id = getIntInput();

        Employee employee = employeeDAO.getEmployeeById(id);
        if (employee == null) {
            System.out.println("No employee found with ID: " + id);
            pause();
            return;
        }

        System.out.println("\nConfirm deletion of this employee:");
        System.out.println("Name: " + employee.getName());
        System.out.println("Department: " + employee.getDepartment());
        System.out.print("Type 'YES' to confirm deletion: ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("YES")) {
            employeeDAO.deleteEmployee(id);

            // ========== ADD VERIFICATION HERE ==========
            // Verify deletion
            Employee deletedCheck = employeeDAO.getEmployeeById(id);
            if (deletedCheck == null) {
                System.out.println("Verification: Employee ID " + id + " successfully deleted from database.");
            } else {
                System.out.println("Verification: Employee ID " + id + " still exists in database.");
            }
            // ==========================================

            System.out.println("Employee deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
        pause();
    }

    private static int getIntInput() {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                return input;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                double input = Double.parseDouble(scanner.nextLine());
                return input;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid salary: ");
            }
        }
    }

    private static LocalDate getDateInput() {
        while (true) {
            try {
                String dateStr = scanner.nextLine();
                return LocalDate.parse(dateStr, dateFormatter);
            } catch (Exception e) {
                System.out.print("Invalid date format. Please use YYYY-MM-DD: ");
            }
        }
    }

    private static String truncateString(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
    private static void verifyEmployeeInDatabase() {
        System.out.println("\n--- VERIFY EMPLOYEE IN DATABASE ---");
        System.out.print("Enter Employee ID to verify: ");
        int id = getIntInput();

        // Using DatabaseVerifier
        if (DatabaseVerifier.employeeExists(id)) {
            System.out.println("Status: Employee ID " + id + " EXISTS in database.");

            // Fetch and display details
            Employee employee = employeeDAO.getEmployeeById(id);
            if (employee != null) {
                System.out.println("\nEmployee Details:");
                System.out.println("Name        : " + employee.getName());
                System.out.println("Department  : " + employee.getDepartment());
                System.out.println("Designation : " + employee.getDesignation());
                System.out.println("Salary      : $" + employee.getSalary());
                System.out.println("Email       : " + employee.getEmail());
                System.out.println("Phone       : " + employee.getPhone());
                System.out.println("Joining Date: " + employee.getJoiningDate());
            }
        } else {
            System.out.println("Status: Employee ID " + id + " does NOT exist in database.");
        }
        pause();
    }

    private static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}