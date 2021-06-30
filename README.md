# Simple E-Commerce project

## Features:
- Categories and Products all CRUD operations with pagination/sorting/search
- Cart and Order checkout
- User registration (with email-confirmation), authorization via JWT

## Realization and technologies used
- Spring as backend (Spring Boot, Spring-Data-JPA, Spring-Security)
- Angular as frontend
- JWT stored in localstorage, lifetime can be set in application.properties
- Restricted access on user roles
- Rest API. For categories and products RESTFull realized (Spring- HATEOAS) 
- PostgreSQL as DB

to run:
1) install Postgres DB, create DB e-commerce with 
username: commerce
password: password

2) run application: 
mvn spring-boot:run

3) visit http://localhost:8080

By default, "stub" products and categories will be created as well as
'root' user with 
username: admin@admin.com
password: password

