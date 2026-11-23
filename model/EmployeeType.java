package model;

public enum EmployeeType {
    FULL_TIME("Full-Time"),
    PART_TIME("Part-Time");

    private final String displayName;

    EmployeeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}