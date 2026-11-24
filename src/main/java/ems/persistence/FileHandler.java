package ems.persistence;

import model.Department;
import model.Employee;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/** Writes departments and employees to their comma-separated files in data/. */
public class FileHandler {
    private final Path dataDirectory;

    public FileHandler() {
        this(Paths.get("data"));
    }

    public FileHandler(Path dataDirectory) {
        if (dataDirectory == null) {
            throw new IllegalArgumentException("Data directory cannot be null.");
        }
        this.dataDirectory = dataDirectory;
    }

    public void saveDepartments(List<Department> departments) throws IOException {
        writeLines("departments.txt", departments.stream()
                .map(Department::toFileString).collect(Collectors.toList()));
    }

    public void saveEmployees(List<Employee> employees) throws IOException {
        writeLines("employees.txt", employees.stream()
                .map(Employee::toFileString).collect(Collectors.toList()));
    }

    private void writeLines(String fileName, List<String> lines) throws IOException {
        // Create data/ on the first save, then replace the file with the current records.
        Files.createDirectories(dataDirectory);
        Files.write(dataDirectory.resolve(fileName), lines, StandardCharsets.UTF_8);
    }
}
