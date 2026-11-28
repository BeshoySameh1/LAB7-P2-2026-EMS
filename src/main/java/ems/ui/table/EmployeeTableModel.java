package employeemanagementsystem;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class EmployeeTableModel extends AbstractTableModel{
    
    private List<Employee> employees;
    private final String[] COLUMNS = {"ID", "Name", "Age", 
        "Department", "Position", "Type", "Salary"};
    
    public EmployeeTableModel()
    {
        employees = new ArrayList<>();
    }
    @Override
    public int getRowCount() {
        return employees.size(); 
    }
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Integer.class;       
            case 1: return String.class;        
            case 2: return Integer.class;       
            case 3: return String.class;        
            case 4: return String.class;       
            case 5: return EmployeeType.class;  
            case 6: return Double.class;        
            default: return Object.class;
        }
    }
    public void setEmployees(List<Employee> employees) 
    {
        this.employees = new ArrayList<>(employees);
        fireTableDataChanged();
    }
    
    @Override
    public int getColumnCount() {
        return COLUMNS.length; 
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Employee employee = employees.get(rowIndex);

        switch (columnIndex) {

            case 0:
                return employee.getId();

            case 1:
                return employee.getFullName();

            case 2:
                return employee.getAge();

            case 3:
                return employee.getDepartment().getName();

            case 4:
                return employee.getPosition();

            case 5:
                return employee.getEmployeeType();

            case 6:
                return employee.calculateSalary();

            default:
                return null;
        }
    }
    
    @Override
    public String getColumnName(int column){
        return COLUMNS[column];
    }
    public Employee getEmployeeAt(int row) {
        return employees.get(row);
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}