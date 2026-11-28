
package employeemanagementsystem;

import java.awt.GridLayout;
import javax.swing.*;

public class EmployeeFormPanel extends JPanel {

    private JTextField idField;
    private JTextField nameField;
    private JTextField ageField;
    private JComboBox<Department> departmentCombo;
    private JTextField positionField;

    private JComboBox<EmployeeType> typeCombo;
    private JLabel firstSalaryLabel;
    private JLabel secondSalaryLabel;
    private JTextField firstSalaryField;
    private JTextField secondSalaryField;

    public EmployeeFormPanel(DepartmentManager departmentManager) {

        setLayout(new GridLayout(0, 2, 8, 8));

        idField = new JTextField();
        idField.setEditable(false);

        nameField = new JTextField();
        ageField = new JTextField();

        departmentCombo = new JComboBox<>();
        for (Department department : departmentManager.getAllDepartments()) {
            departmentCombo.addItem(department);
        }

        departmentCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof Department) {
                    setText(((Department) value).getName());
                }

                return this;
            }
        });

        positionField = new JTextField();

        typeCombo = new JComboBox<>(EmployeeType.values());

        firstSalaryLabel = new JLabel();
        secondSalaryLabel = new JLabel();

        firstSalaryField = new JTextField();
        secondSalaryField = new JTextField();

        add(new JLabel("Employee ID:"));
        add(idField);

        add(new JLabel("Full Name:"));
        add(nameField);

        add(new JLabel("Age:"));
        add(ageField);

        add(new JLabel("Department:"));
        add(departmentCombo);

        add(new JLabel("Position:"));
        add(positionField);

        add(new JLabel("Employee Type:"));
        add(typeCombo);

        add(firstSalaryLabel);
        add(firstSalaryField);

        add(secondSalaryLabel);
        add(secondSalaryField);

        typeCombo.addActionListener(e -> updateSalaryLabels());

        updateSalaryLabels();
    }

    private void updateSalaryLabels() {

        EmployeeType type = (EmployeeType) typeCombo.getSelectedItem();

        if (type == EmployeeType.FULL_TIME) {
            firstSalaryLabel.setText("Basic Salary:");
            secondSalaryLabel.setText("Bonus:");
        } else {
            firstSalaryLabel.setText("Hourly Rate:");
            secondSalaryLabel.setText("Hours Worked:");
        }
    }

    public void setEmployeeId(int id) {
        idField.setText(String.valueOf(id));
    }

    public void loadEmployee(Employee employee) {

        idField.setText(String.valueOf(employee.getId()));
        nameField.setText(employee.getFullName());
        ageField.setText(String.valueOf(employee.getAge()));
        departmentCombo.setSelectedItem(employee.getDepartment());
        positionField.setText(employee.getPosition());
        typeCombo.setSelectedItem(employee.getEmployeeType());

        if (employee instanceof FullTimeEmployee) {

            FullTimeEmployee fullTime = (FullTimeEmployee) employee;

            firstSalaryField.setText(
                    String.valueOf(fullTime.getBasicSalary()));

            secondSalaryField.setText(
                    String.valueOf(fullTime.getBonus()));

        } else if (employee instanceof PartTimeEmployee) {

            PartTimeEmployee partTime = (PartTimeEmployee) employee;

            firstSalaryField.setText(
                    String.valueOf(partTime.getHourlyRate()));

            secondSalaryField.setText(
                    String.valueOf(partTime.getHoursWorked()));
        }
    }

    public Employee buildEmployee() {

        try {
            int id = Integer.parseInt(idField.getText().trim());
            int age = Integer.parseInt(ageField.getText().trim());

            String name = nameField.getText().trim();
            String position = positionField.getText().trim();

            Department department =
                    (Department) departmentCombo.getSelectedItem();

            EmployeeType type =
                    (EmployeeType) typeCombo.getSelectedItem();

            double firstValue =
                    Double.parseDouble(firstSalaryField.getText().trim());

            double secondValue =
                    Double.parseDouble(secondSalaryField.getText().trim());

            if (type == EmployeeType.FULL_TIME) {
                return new FullTimeEmployee(
                        id, name, age, department, position,
                        firstValue, secondValue);
            }

            return new PartTimeEmployee(
                    id, name, age, department, position,
                    firstValue, secondValue);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Please enter valid numbers in the ID, age, and salary fields.");
        }
    }
}