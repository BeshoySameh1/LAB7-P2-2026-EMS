package ems.manager;

import ems.persistence.FileHandler;
import ems.validation.ValidationException;
import model.Department;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Manages departments and saves changes to the department file. */
public class DepartmentManager {
    private final ArrayList<Department> departments = new ArrayList<>();
    private final FileHandler fileHandler;
    private EmployeeManager employeeManager;

    public DepartmentManager() {
        this(new FileHandler());
    }

    public DepartmentManager(FileHandler fileHandler) {
        if (fileHandler == null) {
            throw new IllegalArgumentException("File handler cannot be null.");
        }
        this.fileHandler = fileHandler;
        try {
            departments.addAll(fileHandler.loadDepartments());
        } catch (IOException e) {
            throw new IllegalStateException("Could not load departments: " + e.getMessage(), e);
        }
    }

    public void addDepartment(String idText, String name) throws ValidationException {
        int id = parsePositiveInt(idText, "Department ID");
        String validName = requireText(name, "Department name");

        // Department IDs and names must both be unique.
        if (departmentExists(id)) {
            throw new ValidationException("A department with ID " + id + " already exists.");
        }
        for (Department department : departments) {
            if (department.getName().equalsIgnoreCase(validName)) {
                throw new ValidationException("A department named '" + validName + "' already exists.");
            }
        }

        Department department = new Department(id, validName);
        ArrayList<Department> updated = new ArrayList<>(departments);
        updated.add(department);
        save(updated);
        departments.add(department);
    }

    public void removeDepartment(String idText) throws ValidationException {
        int id = parsePositiveInt(idText, "Department ID");
        Department department = findDepartmentById(id);
        if (department == null) {
            throw new ValidationException("No department exists with ID " + id + ".");
        }
        // Keep departments that are still used by employees.
        if (employeeManager != null && employeeManager.hasEmployeesInDepartment(id)) {
            throw new ValidationException("Cannot remove department '" + department.getName()
                    + "' because employees are assigned to it.");
        }

        ArrayList<Department> updated = new ArrayList<>(departments);
        updated.remove(department);
        save(updated);
        departments.remove(department);
    }

    public boolean departmentExists(int departmentId) {
        return findDepartmentById(departmentId) != null;
    }

    public Department findDepartmentById(int departmentId) {
        for (Department department : departments) {
            if (department.getId() == departmentId) {
                return department;
            }
        }
        return null;
    }

    public ArrayList<Department> getDepartments() {
        return new ArrayList<>(departments);
    }

    void registerEmployeeManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }

    private void save(List<Department> state) throws ValidationException {
        try {
            fileHandler.saveDepartments(state);
        } catch (IOException e) {
            throw new ValidationException("Could not save departments: " + e.getMessage(), e);
        }
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
}
