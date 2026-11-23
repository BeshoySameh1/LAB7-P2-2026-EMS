package model;

public class Department {
    private final int id;
    private String name;

    public Department(int id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException("Department ID must be a positive integer.");
        }
        this.id = id;
        setName(name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String toFileString() {
        return id + "," + name;
    }

    @Override
    public String toString() {
        return name;
    }
}