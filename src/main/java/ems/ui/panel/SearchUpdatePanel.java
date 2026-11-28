package employeemanagementsystem;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import java.util.List;

public class SearchUpdatePanel extends BasePanel{

    private JComboBox<String> searchTypeCombo;
    private JTextField searchField;
    private JButton searchButton;

    private JTable resultsTable;
    private EmployeeTableModel tableModel;

    private EmployeeFormPanel form;
    private JButton updateButton;

    private Integer selectedEmployeeId;
    
    public SearchUpdatePanel(
            EmployeeManager employeeManager,
            DepartmentManager departmentManager,
            Runnable onDataChanged) {

        super(employeeManager, departmentManager, onDataChanged);

        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchTypeCombo = new JComboBox<>(
            new String[]{"ID", "Name", "Department"}
        );

        searchField = new JTextField(20);

        searchButton = new JButton("Search");

        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);
        
        tableModel = new EmployeeTableModel();

        resultsTable = new JTable(tableModel);

        resultsTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        
        form = new EmployeeFormPanel(departmentManager);

        updateButton = new JButton("Update Employee");

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        bottomPanel.add(form, BorderLayout.CENTER);
        bottomPanel.add(updateButton, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        form.setVisible(false);
        updateButton.setEnabled(false);

        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleRowSelected();
            }
        });

        updateButton.addActionListener(e -> handleUpdate());
        searchButton.addActionListener(e -> handleSearch());
    }
    private void handleSearch() {

        String searchType = (String) searchTypeCombo.getSelectedItem();
        String searchText = searchField.getText().trim();

        List<Employee> results;
        results = new ArrayList<>();

        try {

        switch (searchType) {

            case "ID":

                int id;
                id = Validator.parseId(searchText);

                Employee employee = employeeManager.findById(id);

                if (employee != null) {
                    results.add(employee);
                }
                break;

                case "Name":

                    results = employeeManager.searchByName(searchText);

                    break;

                case "Department":

                    results = employeeManager.filterByDepartment(searchText);

                    break;
            }

            tableModel.setEmployees(results);

            if (results.isEmpty()) {
                showInfo("No matching employee found.");
            }

        } catch (Exception e) {

            showError(e.getMessage());
        }
    }
    private void handleRowSelected() {

        int selectedRow = resultsTable.getSelectedRow();

        if (selectedRow == -1) {
            selectedEmployeeId = null;
            form.setVisible(false);
            updateButton.setEnabled(false);
            return;
        }

        int modelRow = resultsTable.convertRowIndexToModel(selectedRow);

        Employee employee = tableModel.getEmployeeAt(modelRow);

        selectedEmployeeId = employee.getId();

        form.loadEmployee(employee);

        form.setVisible(true);
        updateButton.setEnabled(true);

        revalidate();
        repaint();
    }
    private void handleUpdate() {

        if (selectedEmployeeId == null) {
            showError("Please select an employee first.");
            return;
        }

        try {

            Employee updatedEmployee = form.buildEmployee();

            employeeManager.updateEmployee(
                    selectedEmployeeId,
                    updatedEmployee
            );

            showInfo("Employee updated successfully.");

            if (onDataChanged != null) {
                onDataChanged.run();
            }

            tableModel.setEmployees(employeeManager.getAllEmployees());

            selectedEmployeeId = null;

            form.setVisible(false);
            updateButton.setEnabled(false);

            resultsTable.clearSelection();

        } catch (IllegalArgumentException | IllegalStateException e) {

            showError(e.getMessage());
        }
    }
    @Override
    public void refresh() {

        tableModel.setEmployees(employeeManager.getAllEmployees());

        selectedEmployeeId = null;

        form.setVisible(false);
        updateButton.setEnabled(false);

        resultsTable.clearSelection();
     }
}