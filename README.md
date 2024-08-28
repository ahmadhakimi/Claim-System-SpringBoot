# **Claim Request System** 💻
This project's purpose to help staff to make claim requests via an online application.

## **Features** 🌟
- CRUD staff and claim
- Upload claims details with an attachment

## Technologies Used : 🛠️
- Java 21
- Spring Boot 3.2.5
- Hibernate
- MySQL
- Maven
- Spring Security
- JWT
- Java Mail Service
- Lombok
- PDFbox
- itextpdf

## **Requirements** ✔️
for building and running the application you will need the:

- [JDK 21](https://www.oracle.com/my/java/technologies/downloads/#jdk21-windows)
- [Maven 3](https://maven.apache.org/download.cgi)

### Installation 🔽

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/claim-request-system.git

## **Running application locally** ⏯️
- ``` bash
  cd claim-request-system
- run the application by running the ClaimSystemApplication.java
- or using maven command mvn spring-boot:run


## **Setting up the database ⚙️
- Make sure the MySQL is running
- update the `application.properties` file with your database details such as database uri, username, password, etc.

### Usage 📖

1. **Create new staffs:**
   - Navigate to `http://localhost:8080/api/staffs/create` and fill the request body in JSON such as:
     - fullName, email, password, role, createdBy, and updatedBy.
   ![POSTMAN create new staff](https://github.com/user-attachments/assets/d6cf2ca6-f6e8-4414-87d4-60c4f95d5416)


2. **Submit a Claim:**
   - Go to `http://localhost:8080/api/claims` and go to form data 

3. **Manage Claims:**
   - Admins can access `http://localhost:8080/admin` to view and manage claims.
