# RevShop - Console-Based E-Commerce Application

## Project Overview
RevShop is a secure console-based e-commerce application developed for both buyers and sellers.  
The application allows buyers to browse products, manage carts, place orders, review purchases, and save favorites.  
It also allows sellers to manage inventory, add or update products, view orders, monitor stock levels, and receive notifications.

This project was designed using a modular layered architecture so that it can be extended into a web or microservices-based application in the future.

---

## Features

### Buyer Features
- Register on the platform
- Login using email and password
- Browse products by category
- Search products by keyword
- View product details, price, description, and reviews
- Add products to cart with quantity
- Remove products from cart
- Checkout with shipping and billing information
- Place orders using simulated payment methods
- View order history
- Review and rate purchased products
- Save products as favorites
- Receive in-app notifications

### Seller Features
- Register as a seller
- Login using email and password
- Add new products
- Update existing products
- Delete products
- Manage inventory and stock
- View placed orders for their products
- View product reviews and ratings
- Set discounted price along with MRP
- Set inventory threshold values
- Receive console alerts when stock is low
- Receive notifications when an order is placed

### Common Features
- User login and logout
- Change password
- Forgot password recovery using security questions or password hint
- Modular code structure for easy maintenance

---

## Technologies Used
- Java
- Maven
- Oracle Database
- JDBC
- Log4j2
- JUnit 4

---

## Project Architecture
This project follows a modular layered architecture.

### Layers Used
- **App Layer** - application entry point and console flow
- **Service Layer** - business logic
- **DAO Layer** - database operations
- **Model Layer** - entity/data classes
- **Utility Layer** - database connection utilities and helpers

### Flow
Console UI → Service Layer → DAO Layer → Oracle Database

---

## Project Structure
```text
src
 └── main
     └── java
         └── com.revshop
             ├── app
             ├── dao
             ├── model
             ├── service
             └── util
