package employeemanagementsystem;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public final class ViewEmployeesPanel extends BasePanel{
    
    private EmployeeTableModel tableModel;
    private JTable employeeTable;
    private JComboBox<String> departmentFilterCombo;
    private JButton deleteButton;
    private JButton refreshButton;
    public ViewEmployeesPanel(EmployeeManager employeeManager,
        DepartmentManager departmentManager, Runnable onDataChanged) {

        super(employeeManager, departmentManager, onDataChanged);

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Employees");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        departmentFilterCombo = new JComboBox<>();
        departmentFilterCombo.addItem("All Departments");

        for (Department department : departmentManager.getAllDepartments()) {
            departmentFilterCombo.addItem(department.getName());
        }

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(departmentFilterCombo, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new EmployeeTableModel();

        employeeTable = new JTable(tableModel);
        employeeTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();

        deleteButton = new JButton("Delete Selected Employee");
        refreshButton = new JButton("Refresh");

        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        deleteButton.addActionListener(e -> handleDelete());
        refreshButton.addActionListener(e -> refresh());
        departmentFilterCombo.addActionListener(e -> applyDepartmentFilter());

        refresh();
    }
    private void handleDelete() {

        int selectedRow = employeeTable.getSelectedRow();

        if (selectedRow == -1) {
            showError("Please select an employee to delete.");
            return;
        }

        int modelRow = employeeTable.convertRowIndexToModel(selectedRow);

        Employee employee = tableModel.getEmployeeAt(modelRow);

        int choice = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete " + employee.getFullName() + "?",
                "Confirm Deletion",
                javax.swing.JOptionPane.YES_NO_OPTION
        );

       if (choice == javax.swing.JOptionPane.YES_OPTION) {

    try {
        boolean deleted = employeeManager.deleteEmployee(employee.getId());

        if (deleted) {
            applyDepartmentFilter();

            if (onDataChanged != null) {
                onDataChanged.run();
            }

            showInfo("Employee deleted successfully.");
        } else {
            showError("Could not delete employee.");
        }

    } catch (IllegalStateException e) {
        showError(e.getMessage());
    }
}
    }
    @Override
    public void refresh() {

        String selectedDepartment =
                (String) departmentFilterCombo.getSelectedItem();

        departmentFilterCombo.removeAllItems();
        departmentFilterCombo.addItem("All Departments");

        for (Department department : departmentManager.getAllDepartments()) {
            departmentFilterCombo.addItem(department.getName());
        }

        if (selectedDepartment != null) {
            departmentFilterCombo.setSelectedItem(selectedDepartment);
        }

        applyDepartmentFilter();
    }

    private void applyDepartmentFilter() {

        String selectedDepartment =
                (String) departmentFilterCombo.getSelectedItem();

        if (selectedDepartment == null ||
                selectedDepartment.equals("All Departments")) {

            tableModel.setEmployees(employeeManager.getAllEmployees());

        } else {

            tableModel.setEmployees(
                    employeeManager.filterByDepartment(selectedDepartment)
            );
        }
    }    
}