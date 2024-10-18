# Soen342
Soen 342 project

## Team Members
Section H

Steven Gourgy, 40213440         Email:stevengourgy@hotmail.com

Harun Slahaldin Omar, 40250981  Email:harunager@outlook.com



# Development Environment Setup

1. **Clone a specific branch of the Repository**:
    ```bash
    git clone -b branch_name https://github.com/MCSTEVE1000/Soen342
    ```

2. **Navigate to the Project Directory**:
    Once the repository is cloned, both teams should navigate to the project directory using the terminal or command prompt:
    ```bash
    cd <project_directory>
    ```

3. **Workflow and Version Control**

    - **Create a New Branch**:
        Before pushing their changes to the main repository, team members should create a new branch for their work:
        ```bash
        git checkout -b <branch_name>
        ```

    - **Write Acceptance Tests**:
        Before finalizing their work, team members should write acceptance tests to ensure their code meets project requirements.

    - **Commit Changes**:
        Once work is completed and tested locally, team members should commit changes to the local repository:
        ```bash
        git add .
        git commit -m "Brief description of changes"
        ```

    - **Push Changes to Remote**:
        After committing changes locally, team members should push their branch to the remote repository:
        ```bash
        git push
        ```


# Setting Up the Django Project

1. **Install virtualenv**:
    ```bash
    pip install virtualenv
    ```

2. **Create a virtual environment**:
    ```bash
    python -m venv venv
    ```

3. **Set Execution Policy (Windows only)**:
    ```powershell
    Set-ExecutionPolicy Unrestricted -Scope Process
    ```

4. **Activate the virtual environment**:
    - On **Windows**:
      ```bash
      .\venv\Scripts\activate
      ```
    - On **macOS/Linux**:
      ```bash
      source venv/bin/activate
      ```

5. **Install Django**:
    ```bash
    pip install django
    ```

6. **Generate `requirements.txt`**:
    ```bash
    pip freeze > requirements.txt
    ```

7. **Install dependencies from `requirements.txt`**:
    ```bash
    pip install -r requirements.txt
    ```

8. **Make migrations**:
    ```bash
    python manage.py makemigrations
    ```

9. **Apply migrations**:
    ```bash
    python manage.py migrate
    ```

10. **Run the server**:
    ```bash
    python manage.py runserver
    ```

11. **Access the admin panel**:
    - http://127.0.0.1:8000/admin/login/?next=/admin/

12. **Register Home page**:
    - http://127.0.0.1:8000/home/

13. **Create a superuser**:
    ```bash
    python manage.py createsuperuser
    ```

    - **Email**: stevengourgy@hotmail.com 
    - **Username**: steve
    - **Password**: password


