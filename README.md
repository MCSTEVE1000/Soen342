# Soen342
Soen 342 Project

## Team Members
Section H

- **Steven Gourgy, 40213440**  
  Email: stevengourgy@hotmail.com

- **Harun Slahaldin Omar, 40250981**  
  Email: harunager@outlook.com

## Development Environment Setup

### 1. Clone a Specific Branch of the Repository
To clone a specific branch, use:
```bash
git clone -b <branch_name> https://github.com/MCSTEVE1000/Soen342
```

### 2. Navigate to the Project Directory
Once the repository is cloned, navigate to the project directory:
```bash
cd <project_directory>
```

### 3. Workflow and Version Control

- **Create a New Branch**  
  Create a new branch for your work before pushing changes:
  ```bash
  git checkout -b <branch_name>
  ```

- **Write Acceptance Tests**  
  Ensure your code meets project requirements by writing acceptance tests.

- **Commit Changes**  
  Once work is completed and tested locally, commit changes:
  ```bash
  git add .
  git commit -m "Brief description of changes"
  ```

- **Push Changes to Remote**  
  Push your branch to the remote repository:
  ```bash
  git push
  ```

---

## Setting Up Maven in VSCode

### 1.1 Install Java and Maven
Ensure the Java Development Kit (JDK) and Maven are installed on your system.

- **Install Java JDK**  
  Download the latest JDK from Oracle or use OpenJDK from AdoptOpenJDK.

- **Install Maven**

  **Option 1: Install Manually**  
  1. Download Maven from the Apache Maven website.
  2. Extract to a chosen directory.
  3. Add Maven's `bin` directory to your system's PATH.

  **Option 2: Use a Package Manager**  
  - **Windows**: Run `choco install maven`
  - **macOS**: Run `brew install maven`
  - **Linux**: Run `sudo apt-get install maven`

- **Verify Installation**  
  Open a terminal and check Maven's version:
  ```bash
  mvn -version
  ```

### 1.2 Install VSCode Extensions
Install these VSCode extensions for Java and Maven support:

- **Extension Pack for Java**  
  Essential extensions for Java development. Find it in the VSCode Marketplace.

- **Maven for Java**  
  Adds Maven support to VSCode. Find it in the VSCode Marketplace.

### 1.3 Compile and Run Maven Project
To compile and run the project:
```bash
mvn compile
mvn exec:java
```

---

## SQLite Database Setup
To open the SQLite shell, run this in your terminal:
```bash
  sqlite3 app_database.db
  ```

To interact with the database (`app_database.db`), use these commands in the SQLite shell.

- **Check Database Tables**  
  ```bash
  .tables
  ```

- **View Table Schema**  
  ```bash
  .schema TableName
  .schema Users
  ```

- **Display Data from a Table**  
  ```bash
  SELECT * FROM TableName;
  SELECT * FROM Users;
  ```

  For readable output:
  ```bash
  .mode column
  .headers on
  ```

- **Count Records in a Table**  
  ```bash
  SELECT COUNT(*) FROM TableName;
  ```

- **Filter Data**  
  ```bash
  SELECT * FROM Users WHERE userType = 'Client';
  ```

- **Describe a Table (List Columns and Their Types)**  
  ```bash
  PRAGMA table_info('TableName');
  ```

- **Exit SQLite Shell**  
  ```bash
  .exit
  ```
