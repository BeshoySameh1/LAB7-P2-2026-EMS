package model;

public abstract class Employee {
    private int id;
    private String fullName;
    private int age;
    private Department department;
    private String position;

    public Employee(int id, String fullName, int age,
                    Department department, String position) {
        setId(id);
        setFullName(fullName);
        setAge(age);
        setDepartment(department);
        setPosition(position);
    }

    public abstract double calculateSalary();
    public abstract EmployeeType getEmployeeType();
    public abstract String getSalaryDetails();
    public abstract String toFileString();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Employee ID must be a positive integer.");
        }
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        this.fullName = fullName.trim();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be a positive integer.");
        }
        this.age = age;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department must be selected.");
        }
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Position cannot be empty.");
        }
        this.position = position.trim();
    }

    protected String baseFileString() {
        return id + "," + fullName + "," + age + "," + department.getId() + "," + position;
    }
}