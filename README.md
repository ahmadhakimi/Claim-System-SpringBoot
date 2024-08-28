# **Claim Request System** 💻

![High-level architecture design](https://github.com/user-attachments/assets/328927e2-141c-467f-a080-a6ab2cef24e6)


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

## Functionality
1. create staff, view staff by ID, staffs list, update staff by ID, and delete staff by ID
2. CRUD claims, list of claims, download claim's attachment
3. Register new staff with auth
4. login staff using auth
5. update password and forget password
6. admin creates a new user
7. generate claims record as PDF
8. logout 
