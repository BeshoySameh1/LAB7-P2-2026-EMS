# Discussion Guide

Use this guide to explain the current Employee Management System implementation in a review, lab discussion, or presentation.

## Short overview

The project separates employee and department data from the operations that manage them. The model classes represent the records and calculate salaries. The manager classes validate requests and perform the operations. `FileHandler` saves and loads records using Java object serialization.

## How the pieces fit together

| Class or group | What it does |
| --- | --- |
| `Department` | Stores a department ID and name. Its ID is fixed after construction. |
| `Employee` | Stores fields shared by all employees and defines the salary operations that subclasses implement. |
| `FullTimeEmployee` | Adds basic salary and bonus; total salary is their sum. |
| `PartTimeEmployee` | Adds hourly rate and hours worked; total salary is rate multiplied by hours. |
| `EmployeeType` | Defines the two supported employee types and their display names. |
| `DepartmentManager` | Adds, finds, lists, and removes departments, while preventing duplicate departments and removal of a department that still has employees. |
| `EmployeeManager` | Adds, edits, deletes, searches, and filters employees. It also checks employee data and verifies that the selected department exists. |
| `FileHandler` | Creates the data directory when needed and saves or loads serialized department and employee objects. |
| `ValidationException` | Carries a clear, user-correctable error message from manager logic to a future user interface. |

## Example: adding an employee

1. A caller passes employee details to `EmployeeManager.addEmployee` as strings, as they commonly arrive from a form.
2. The manager parses and checks the ID, age, department ID, name, position, employee type, and the salary fields for that type.
3. It rejects an ID that is already in use and rejects a department ID that does not match a known department.
4. Once the details are valid, the manager creates the matching subclass: `FullTimeEmployee` or `PartTimeEmployee`.
5. It adds the new object to the in-memory employee list and asks `FileHandler` to save that list.
6. If saving fails, the manager reports the I/O problem as a `ValidationException`.

## Employee operations

- **Update:** the manager checks the new details first. If the employee type stays the same, it updates the existing object. If the type changes, it replaces that list entry with an instance of the other subclass, since each type has different salary fields.
- **Delete:** removes the selected employee, then saves the list.
- **Search:** parses a positive employee ID and returns the matching employee, or `null` if there is no match.
- **Filter:** compares department names without regard to letter case. An empty filter returns an empty list.
- **List access:** manager getters return new list copies, so callers cannot add or remove entries directly through the returned list.

## Validation rules

- Employee and department IDs must be positive integers.
- Age must be a positive integer.
- Names and positions must contain non-whitespace text; surrounding whitespace is trimmed.
- The department selected for an employee must already exist.
- Employee IDs must be unique. Department IDs and names must each be unique (names are compared without regard to case).
- Salary, bonus, hourly rate, and hours worked must be finite, non-negative numbers.
- A department cannot be removed while at least one managed employee belongs to it.

Managers use `ValidationException` for these request-level errors. The model constructors and setters also check core values and use `IllegalArgumentException` if called directly with invalid data.

## Saving records

`FileHandler` writes the complete in-memory lists as Java serialized objects to:

- `data/departments.dat`
- `data/employees.dat`

The manager constructors load these files automatically. Construct `DepartmentManager` before `EmployeeManager`; employee references are reconnected to the department instances managed by the department manager. Existing `.txt` files are not imported into the serialized format.

## Discussion talking points

- **Why separate managers and models?** Models describe the data and salary rules; managers handle multi-step operations, checks, and coordination.
- **Why use subclasses for employee types?** The employee types have different pay inputs and salary formulas, while sharing identity and department fields.
- **Why validate before creating or changing an employee?** It keeps invalid form input from becoming a managed record and gives the caller a specific error to show.
- **Why protect department removal?** It prevents managed employees from referencing a department that no longer exists.
- **Why inject `FileHandler`?** Constructors accept a handler, which makes the storage location configurable and makes it easier to substitute a handler in future tests.

## Current boundaries and next steps

- There is no user interface in the current repository; a UI or command-line layer can call the manager methods and display validation messages.
- Persistence uses Java built-in object serialization, so the files are binary and should be treated as application data rather than edited by hand.
- There is no build file or test suite in the repository yet.

These are useful next steps to mention if the discussion asks what remains before the system can be used end to end.
