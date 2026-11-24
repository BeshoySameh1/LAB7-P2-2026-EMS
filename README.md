# Employee Management System

A Java project for managing departments and two kinds of employees: full-time and part-time. The current code provides the data model, manager operations, input validation, and saving records to text files.

## What is included

- **Departments:** create, look up, and remove departments. A department cannot be removed while employees are assigned to it.
- **Employees:** add, update, delete, search by ID, and filter by department.
- **Employee types:** full-time salary is basic salary plus bonus; part-time salary is hourly rate multiplied by hours worked.
- **Validation:** managers check user-provided values and report problems with `ValidationException`.
- **Saving:** `FileHandler` writes department and employee records to `data/departments.txt` and `data/employees.txt`.

## Project layout

| Location | Responsibility |
| --- | --- |
| `model/` | Department and employee data, employee types, salary calculations, and file-record formatting |
| `src/main/java/ems/manager/` | Department and employee operations, validation, and coordination |
| `src/main/java/ems/persistence/` | Writing records to the `data/` directory |
| `src/main/java/ems/validation/` | User-correctable operation and input errors |
| `docs/DISCUSSION_GUIDE.md` | A walkthrough and presentation notes for discussing the implementation |

## Current scope

This repository contains the management and model logic. It does not currently include a graphical or command-line interface, a build configuration, or code to load saved records when the application starts. Managers begin with empty in-memory lists; saving writes those lists to disk. See the discussion guide for the operation flow and these boundaries.

## Example record format

Department records contain an ID and name, separated by commas:

```text
12,Finance
```

Employee records begin with the employee type, followed by shared employee fields and type-specific salary fields. For example:

```text
FULL_TIME,101,Amina Hassan,30,12,Accountant,12000.0,1000.0
PART_TIME,102,Omar Ali,22,12,Assistant,80.0,25.0
```

The current format is simple comma-separated text. Values containing commas are not escaped, so names and positions should not contain commas if the saved files are to remain unambiguous.
