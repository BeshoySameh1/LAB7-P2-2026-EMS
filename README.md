# Employee Management System

A Java project for managing departments and two kinds of employees: full-time and part-time. The code provides the data model, manager operations, input validation, and persistence with Java object serialization.

## What is included

- **Departments:** create, look up, and remove departments. A department cannot be removed while employees are assigned to it.
- **Employees:** add, update, delete, search by ID, and filter by department.
- **Employee types:** full-time salary is basic salary plus bonus; part-time salary is hourly rate multiplied by hours worked.
- **Validation:** managers check user-provided values and report problems with `ValidationException`.
- **Persistence:** `FileHandler` saves serialized department and employee objects to `data/departments.dat` and `data/employees.dat`. Managers load those objects when they are constructed.

## Project layout

| Location | Responsibility |
| --- | --- |
| `model/` | Department and employee data, employee types, salary calculations, and file-record formatting |
| `src/main/java/ems/manager/` | Department and employee operations, validation, and coordination |
| `src/main/java/ems/persistence/` | Writing records to the `data/` directory |
| `src/main/java/ems/validation/` | User-correctable operation and input errors |
| `docs/DISCUSSION_GUIDE.md` | A walkthrough and presentation notes for discussing the implementation |

## Current scope

This repository contains the management and model logic. It does not currently include a graphical or command-line interface or a build configuration. The managers load existing serialized data during construction; create `DepartmentManager` before `EmployeeManager` so employee records can reconnect to the loaded departments. Existing `departments.txt` and `employees.txt` files are not imported; the app starts with empty data until records are saved in the new `.dat` format.
