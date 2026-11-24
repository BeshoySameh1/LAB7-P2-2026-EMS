package ems.manager;

import ems.persistence.FileHandler;
import ems.validation.ValidationException;
import model.Department;
import model.Employee;
import model.EmployeeType;
import model.FullTimeEmployee;
import model.PartTimeEmployee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Handles employee records, input checks, and saving employee changes. */
public class EmployeeManager {
    private final DepartmentManager departmentManager;
    private final FileHandler fileHandler;
    private final ArrayList<Employee> employees = new ArrayList<>();

    public EmployeeManager(DepartmentManager departmentManager) {
        this(departmentManager, new FileHandler());
    }

    public EmployeeManager(DepartmentManager departmentManager, FileHandler fileHandler) {
        if (departmentManager == null) {
            throw new IllegalArgumentException("Department manager cannot be null.");
        }
        if (fileHandler == null) {
            throw new IllegalArgumentException("File handler cannot be null.");
        }
        this.departmentManager = departmentManager;
        this.fileHandler = fileHandler;
        departmentManager.registerEmployeeManager(this);
    }

    public void addEmployee(String idText, String name, String ageText,
                            String departmentIdText, String position, String employeeType,
                            String basicSalaryText, String bonusText,
                            String hourlyRateText, String hoursWorkedText)
            throws ValidationException {
        EmployeeData data = validateEmployeeData(null, idText, name, ageText, departmentIdText,
                position, employeeType, basicSalaryText, bonusText, hourlyRateText, hoursWorkedText);
        Employee employee = createEmployee(data);
        ArrayList<Employee> updated = new ArrayList<>(employees);
        updated.add(employee);
        save(updated);
        employees.add(employee);
    }

    public void updateEmployee(Employee selectedEmployee, String idText, String name, String ageText,
                               String departmentIdText, String position, String employeeType,
                               String basicSalaryText, String bonusText,
                               String hourlyRateText, String hoursWorkedText)
            throws ValidationException {
        validateSelectedEmployee(selectedEmployee, "update");
        EmployeeData data = validateEmployeeData(selectedEmployee, idText, name, ageText,
                departmentIdText, position, employeeType, basicSalaryText, bonusText,
                hourlyRateText, hoursWorkedText);

        Employee updatedEmployee = createEmployee(data);
        ArrayList<Employee> updated = new ArrayList<>(employees);
        int index = updated.indexOf(selectedEmployee);
        updated.set(index, updatedEmployee);
        // Persist the complete candidate state before changing the managed list.
        save(updated);

        // Keep the same object when its employee type stays the same so callers
        // holding the selected row continue to refer to the managed employee.
        if (selectedEmployee.getEmployeeType() == data.type) {
            selectedEmployee.setId(data.id);
            selectedEmployee.setFullName(data.name);
            selectedEmployee.setAge(data.age);
            selectedEmployee.setDepartment(data.department);
            selectedEmployee.setPosition(data.position);
            if (selectedEmployee instanceof FullTimeEmployee) {
                FullTimeEmployee fullTime = (FullTimeEmployee) selectedEmployee;
                fullTime.setBasicSalary(data.basicSalary);
                fullTime.setBonus(data.bonus);
            } else {
                PartTimeEmployee partTime = (PartTimeEmployee) selectedEmployee;
                partTime.setHourlyRate(data.hourlyRate);
                partTime.setHoursWorked(data.hoursWorked);
            }
        } else {
            // A type change needs a different employee class.
            employees.set(index, updatedEmployee);
        }
    }

    public void deleteEmployee(Employee selectedEmployee) throws ValidationException {
        validateSelectedEmployee(selectedEmployee, "delete");
        int index = employees.indexOf(selectedEmployee);
        ArrayList<Employee> updated = new ArrayList<>(employees);
        updated.remove(index);
        save(updated);
        employees.remove(index);
    }

    public Employee searchById(String idText) throws ValidationException {
        int id = parsePositiveInt(idText, "Employee ID");
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    public ArrayList<Employee> filterByDepartment(String departmentName) {
        ArrayList<Employee> filtered = new ArrayList<>();
        if (departmentName == null || departmentName.trim().isEmpty()) {
            return filtered;
        }
        String filter = departmentName.trim();
        for (Employee employee : employees) {
            if (employee.getDepartment().getName().equalsIgnoreCase(filter)) {
                filtered.add(employee);
            }
        }
        return filtered;
    }

    /** Returns employees assigned to the department with the given ID. */
    public ArrayList<Employee> filterByDepartment(int departmentId) {
        ArrayList<Employee> filtered = new ArrayList<>();
        if (departmentId <= 0) {
            return filtered;
        }
        for (Employee employee : employees) {
            if (employee.getDepartment().getId() == departmentId) {
                filtered.add(employee);
            }
        }
        return filtered;
    }

    public boolean isIdUnique(int employeeId) {
        return employeeId > 0 && findEmployeeById(employeeId) == null;
    }

    /** GUI-friendly uniqueness check for an ID field that has not yet been parsed. */
    public boolean isIdUnique(String employeeIdText) {
        try {
            return isIdUnique(parsePositiveInt(employeeIdText, "Employee ID"));
        } catch (ValidationException e) {
            return false;
        }
    }

    /** Checks required text fields before the GUI submits a complete operation. */
    public void validateRequiredField(String value, String field) throws ValidationException {
        requireText(value, field);
    }

    /** Validates that an employee ID is a positive integer and is not already in use. */
    public int validateEmployeeId(String employeeIdText) throws ValidationException {
        int id = parsePositiveInt(employeeIdText, "Employee ID");
        if (!isIdUnique(id)) {
            throw new ValidationException("An employee with ID " + id + " already exists.");
        }
        return id;
    }

    /** Validates an edited ID while allowing the selected employee to keep its own ID. */
    public int validateEmployeeId(String employeeIdText, Employee selectedEmployee)
            throws ValidationException {
        int id = parsePositiveInt(employeeIdText, "Employee ID");
        if (selectedEmployee != null && !employees.contains(selectedEmployee)) {
            throw new ValidationException("Select an employee that is currently managed.");
        }
        if (!isIdUniqueFor(id, selectedEmployee)) {
            throw new ValidationException("An employee with ID " + id + " already exists.");
        }
        return id;
    }

    public int validateAge(String ageText) throws ValidationException {
        return parsePositiveInt(ageText, "Age");
    }

    public double validateNonNegativeNumber(String value, String field) throws ValidationException {
        return parseNonNegativeDouble(value, field);
    }

    public double validateWorkingHours(String hoursText) throws ValidationException {
        return parseNonNegativeDouble(hoursText, "Working hours");
    }

    public Department validateDepartment(String departmentIdText) throws ValidationException {
        int id = parsePositiveInt(departmentIdText, "Department ID");
        Department department = departmentManager.findDepartmentById(id);
        if (department == null) {
            throw new ValidationException("No department exists with ID " + id + ".");
        }
        return department;
    }

    public EmployeeType validateEmployeeType(String employeeType) throws ValidationException {
        return parseEmployeeType(employeeType);
    }

    /** Ensures an update/delete action has a currently managed employee selected. */
    public Employee validateSelectedEmployee(Employee selectedEmployee, String operation)
            throws ValidationException {
        if (selectedEmployee == null || !employees.contains(selectedEmployee)) {
            String action = operation == null || operation.trim().isEmpty()
                    ? "modify" : operation.trim().toLowerCase();
            throw new ValidationException("Select an employee to " + action + ".");
        }
        return selectedEmployee;
    }

    public ArrayList<Employee> getEmployees() {
        return new ArrayList<>(employees);
    }

    boolean hasEmployeesInDepartment(int departmentId) {
        for (Employee employee : employees) {
            if (employee.getDepartment().getId() == departmentId) {
                return true;
            }
        }
        return false;
    }

    private EmployeeData validateEmployeeData(Employee selected, String idText, String name,
                                             String ageText, String departmentIdText,
                                             String position, String employeeType,
                                             String basicSalaryText, String bonusText,
                                             String hourlyRateText, String hoursWorkedText)
            throws ValidationException {
        // Check common fields first, then only the pay fields for this employee type.
        int id = parsePositiveInt(idText, "Employee ID");
        if (!isIdUniqueFor(id, selected)) {
            throw new ValidationException("An employee with ID " + id + " already exists.");
        }
        String validName = requireText(name, "Employee name");
        int age = parsePositiveInt(ageText, "Age");
        int departmentId = parsePositiveInt(departmentIdText, "Department ID");
        Department department = departmentManager.findDepartmentById(departmentId);
        if (department == null) {
            throw new ValidationException("No department exists with ID " + departmentId + ".");
        }
        String validPosition = requireText(position, "Position");
        EmployeeType type = parseEmployeeType(employeeType);

        double basicSalary = 0;
        double bonus = 0;
        double hourlyRate = 0;
        double hoursWorked = 0;
        if (type == EmployeeType.FULL_TIME) {
            basicSalary = parseNonNegativeDouble(basicSalaryText, "Basic salary");
            bonus = parseNonNegativeDouble(bonusText, "Bonus");
        } else {
            hourlyRate = parseNonNegativeDouble(hourlyRateText, "Hourly rate");
            hoursWorked = parseNonNegativeDouble(hoursWorkedText, "Hours worked");
        }
        return new EmployeeData(id, validName, age, department, validPosition, type,
                basicSalary, bonus, hourlyRate, hoursWorked);
    }

    private boolean isIdUniqueFor(int id, Employee selected) {
        for (Employee employee : employees) {
            if (employee.getId() == id && employee != selected) {
                return false;
            }
        }
        return true;
    }

    private Employee findEmployeeById(int id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    private static Employee createEmployee(EmployeeData data) {
        if (data.type == EmployeeType.FULL_TIME) {
            return new FullTimeEmployee(data.id, data.name, data.age, data.department,
                    data.position, data.basicSalary, data.bonus);
        }
        return new PartTimeEmployee(data.id, data.name, data.age, data.department,
                data.position, data.hourlyRate, data.hoursWorked);
    }

    private void save(List<Employee> state) throws ValidationException {
        try {
            fileHandler.saveEmployees(state);
        } catch (IOException e) {
            throw new ValidationException("Could not save employees: " + e.getMessage(), e);
        }
    }

    private static EmployeeType parseEmployeeType(String value) throws ValidationException {
        String text = requireText(value, "Employee type");
        for (EmployeeType type : EmployeeType.values()) {
            if (type.name().equalsIgnoreCase(text) || type.getDisplayName().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new ValidationException("Employee type must be Full-Time or Part-Time.");
    }

    private static String requireText(String value, String field) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(field + " is required.");
        }
        return value.trim();
    }

    private static int parsePositiveInt(String value, String field) throws ValidationException {
        String text = requireText(value, field);
        try {
            int parsed = Integer.parseInt(text);
            if (parsed <= 0) {
                throw new ValidationException(field + " must be a positive integer.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new ValidationException(field + " must be a positive integer.", e);
        }
    }

    private static double parseNonNegativeDouble(String value, String field) throws ValidationException {
        String text = requireText(value, field);
        try {
            double parsed = Double.parseDouble(text);
            if (!Double.isFinite(parsed) || parsed < 0) {
                throw new ValidationException(field + " must be a non-negative number.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new ValidationException(field + " must be a non-negative number.", e);
        }
    }

    private static final class EmployeeData {
        private final int id;
        private final String name;
        private final int age;
        private final Department department;
        private final String position;
        private final EmployeeType type;
        private final double basicSalary;
        private final double bonus;
        private final double hourlyRate;
        private final double hoursWorked;

        private EmployeeData(int id, String name, int age, Department department, String position,
                             EmployeeType type, double basicSalary, double bonus,
                             double hourlyRate, double hoursWorked) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.department = department;
            this.position = position;
            this.type = type;
            this.basicSalary = basicSalary;
            this.bonus = bonus;
            this.hourlyRate = hourlyRate;
            this.hoursWorked = hoursWorked;
        }
    }
}
