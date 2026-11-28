package employeemanagementsystem;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public abstract class BasePanel extends JPanel{
    protected EmployeeManager employeeManager;
    protected DepartmentManager departmentManager;
    protected Runnable onDataChanged;
    
    public BasePanel(
            EmployeeManager employeeManager,
            DepartmentManager departmentManager,
            Runnable onDataChanged){
        
        this.employeeManager = employeeManager;
        this.departmentManager = departmentManager;
        this.onDataChanged = onDataChanged;
    }
    
    public abstract void refresh();
    
    protected void showError(String message)
    {
        JOptionPane.showMessageDialog(
                this, 
                message, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
    }
    
    protected void showInfo(String message)
    {
        JOptionPane.showMessageDialog(
                this, 
                message, 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
    }
}