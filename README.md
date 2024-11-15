# Soen342
Soen 342 Project

## Video
https://drive.google.com/file/d/1uezFR-EAgNVbyCw2DyoK68datjpBiOH2/view?usp=sharing

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
mvn clean compile
mvn exec:java
```

---
ADMIN LOGIN:
Enter phone number: 123
Enter password: 123



## MySQL Database Setup

### Install MySQL Server
If you don't have MySQL installed, you need to install it to run the project.

#### Download and Install MySQL
  Download the MySQL Installer from [MySQL Downloads](https://dev.mysql.com/downloads/installer/).
  Run the installer and follow the installation steps.
    -Choose "Custom" setup.
    -Install MySQL Server (and MySQL Workbench if desired).
    -Use Legacy Authentication Method when prompted.
    -Set a root password when configuring the server. (I used 1234)


#### Create the Database and User
  Open Command Prompt and connect to MySQL:
  ```bash
mysql -u root -p
```
Create Database:
```bash
CREATE DATABASE project_db;
```
Create User and Grant Privileges:
```bash
CREATE USER 'project_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON project_db.* TO 'project_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;

```




#### Configure the Project to Use Your Database

Ensure that the database connection parameters in DBConnection.java match your MySQL setup.

Open src/main/java/com/project/DBConnection.java

Set Connection Parameters:
```bash
// Database credentials
String url = "jdbc:mysql://localhost:3306/project_db?useSSL=false&serverTimezone=UTC";
String user = "project_user";
String password = "secure_password";
```
Replace secure_password with the password you set up (I used 1234)
 

    
#### Build the Project
 In the project directory, run:
```bash
mvn clean compile
mvn exec:java
```

## Log in as Admin
  When prompted in the application, log in using:
Phone Number:123
Password: 123

## Check Database on MySql Workbench

#### Step 1: Launch MySQL Workbench

#### Step 2: Create a New Connection
Create a new Connection with

Connection Name: Enter a name for your connection, e.g., "Soen 342 Project Database".

Connection Method: Keep it as "Standard (TCP/IP)".

Hostname: localhost (since the MySQL server is running on your local machine).

Port: 3306 (default MySQL port).

Username: Enter the username you created earlier, e.g., project_user.

Password: 1234

Click "OK" to save the password.

Click on "Test Connection".

If the connection is successful, you'll see a message:
```bash
Successfully made the MySQL connection.
```
Click "OK" to save the connection configuration.

#### Step 3: Explore the project_db Database

```bash
SELECT * FROM Offerings;
SELECT * FROM Users;
ETC
```
