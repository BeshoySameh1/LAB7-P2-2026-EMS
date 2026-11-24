package ems.persistence;

import model.Department;
import model.Employee;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/** Saves and loads departments and employees using Java object serialization. */
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
        writeObjects("departments.dat", departments);
    }

    public void saveEmployees(List<Employee> employees) throws IOException {
        writeObjects("employees.dat", employees);
    }

    public List<Department> loadDepartments() throws IOException {
        return readObjects("departments.dat", Department.class);
    }

    public List<Employee> loadEmployees() throws IOException {
        return readObjects("employees.dat", Employee.class);
    }

    private void writeObjects(String fileName, List<?> objects) throws IOException {
        Files.createDirectories(dataDirectory);
        try (ObjectOutputStream output = new ObjectOutputStream(
                Files.newOutputStream(dataDirectory.resolve(fileName)))) {
            output.writeObject(new ArrayList<>(objects));
        }
    }

    private <T> List<T> readObjects(String fileName, Class<T> itemType) throws IOException {
        Path file = dataDirectory.resolve(fileName);
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(file))) {
            Object stored = input.readObject();
            if (!(stored instanceof List<?>)) {
                throw new IOException("Stored data in " + fileName + " is not a list.");
            }

            List<T> result = new ArrayList<>();
            for (Object item : (List<?>) stored) {
                if (!itemType.isInstance(item)) {
                    throw new IOException("Stored data in " + fileName
                            + " contains an unexpected record type.");
                }
                result.add(itemType.cast(item));
            }
            return result;
        } catch (ClassNotFoundException e) {
            throw new IOException("Could not read stored " + itemType.getSimpleName()
                    + " records.", e);
        }
    }
}
