# MallConnect 🛒

A full-featured B2C multi-vendor e-commerce platform built with Vue 3 and Spring Boot.

---

## Overview

MallConnect is a multi-vendor marketplace where **customers** browse and purchase products, **sellers** manage their stores and inventory, and **administrators** oversee the entire platform. The project follows a structured PRD with a planned 6-week MVP timeline.

### Key Features (MVP)

| Feature | Description |
|---------|-------------|
| 🔐 **Authentication** | JWT-based login/register with role-based access (Customer, Seller, Admin) |
| 🛍️ **Product Browsing** | Category navigation, keyword search, price filters, sorting, pagination |
| 🛒 **Shopping Cart** | Add/update/remove items, persistent storage, order summary |
| 💳 **Checkout** | Multi-step checkout with shipping address & simulated payment |
| 📦 **Order Management** | Order history, status tracking, cancel orders |
| 🏪 **Seller Dashboard** | Product CRUD, inventory management, order fulfillment |
| ⚙️ **Admin Panel** | User management, seller approval, platform oversight |
| 🌐 **Multi-language** | English & Chinese (简体中文) with Vue I18n |

---

## Tech Stack

### Frontend

| Technology | Version | Purpose |
|------------|---------|---------|
| [Vue 3](https://vuejs.org/) | 3.4 | UI framework (Composition API) |
| [Vite](https://vitejs.dev/) | 5.0 | Build tool & dev server |
| [Element Plus](https://element-plus.org/) | 2.5 | UI component library |
| [Pinia](https://pinia.vuejs.org/) | 2.1 | State management |
| [Vue Router](https://router.vuejs.org/) | 4.3 | Client-side routing |
| [Axios](https://axios-http.com/) | 1.6 | HTTP client |
| [Vue I18n](https://vue-i18n.intlify.dev/) | 9.9 | Internationalization |
| Sass | 1.70 | CSS preprocessing |

### Backend

| Technology | Version | Purpose |
|------------|---------|---------|
| [Spring Boot](https://spring.io/projects/spring-boot) | 4.0.0 | Application framework |
| Java | 25 | Language runtime |
| [Spring Security](https://spring.io/projects/spring-security) | — | Authentication & authorization |
| [JWT (jjwt)](https://github.com/jwtk/jjwt) | 0.12.7 | JSON Web Token |
| [Spring Data JPA](https://spring.io/projects/spring-data-jpa) | — | ORM & data access |
| [MySQL](https://www.mysql.com/) | 8.0 | Relational database |
| [Lombok](https://projectlombok.org/) | 1.18.40 | Boilerplate reduction |
| Maven | — | Build & dependency management |

---

## Project Structure

```
cooperation with Nana/
├── Backend/                     # Spring Boot backend
│   ├── src/main/java/com/mallconnect/
│   │   ├── config/              # CORS, seed data initializer
│   │   ├── controller/          # REST API controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── exception/           # Global exception handler
│   │   ├── model/               # JPA entity models
│   │   ├── repository/          # Spring Data repositories
│   │   ├── security/            # JWT filter, security config
│   │   └── service/             # Business logic services
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── Frontend/                    # Vue 3 frontend
│   ├── src/
│   │   ├── locales/             # i18n (en.json, zh.json)
│   │   ├── router/              # Vue Router config
│   │   ├── services/            # Axios API modules
│   │   ├── stores/              # Pinia stores (auth, cart)
│   │   └── views/               # Page components
│   ├── vite.config.js
│   └── package.json
├── prd.md                       # Product Requirements Document
├── mvp.md                       # MVP scope & timeline
└── README.md
```

---

## Database Schema

| Table | Description |
|-------|-------------|
| `users` | User accounts (email, password, role) |
| `sellers` | Seller profiles (store name, approval status) |
| `categories` | Product categories (hierarchical) |
| `products` | Product listings (name, price, stock, images) |
| `orders` | Customer orders (status, total, shipping) |
| `order_items` | Line items within orders |
| `cart_items` | Shopping cart entries |
| `addresses` | User shipping addresses |

---

## Getting Started

### Prerequisites

- **Node.js** >= 18
- **Java** >= 25
- **MySQL** >= 8.0
- **Maven** (or use the included `mvnw` wrapper)

### 1. Clone & Setup Database

```bash
# Create the MySQL database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS mallconnect;"
```

### 2. Start the Backend

```bash
cd Backend

# Edit application.properties with your MySQL credentials
# src/main/resources/application.properties:
#   spring.datasource.username=root
#   spring.datasource.password=YOUR_PASSWORD

./mvnw spring-boot:run
```

The API will be available at **`http://localhost:8080`**.

Seed data is automatically loaded on first run (`DataInitializer.java`), creating demo users:
- **Admin:** admin@mallconnect.com
- **Seller:** seller@mallconnect.com
- **Customer:** customer@mallconnect.com

*(Default password for all seed accounts is set in the initializer)*

### 3. Start the Frontend

```bash
cd Frontend
npm install
npm run dev
```

The frontend will be available at **`http://localhost:3000`** and proxies `/api` requests to the backend.

---

## API Overview

All endpoints are prefixed with `/api/v1`.

| Module | Key Endpoints |
|--------|---------------|
| **Auth** | `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh` |
| **Products** | `GET /products`, `GET /products/{id}`, `POST /products` |
| **Categories** | `GET /categories`, `POST /categories` |
| **Cart** | `GET /cart`, `POST /cart/items`, `PUT/DELETE /cart/items/{id}` |
| **Orders** | `POST /orders`, `GET /orders`, `GET /orders/{id}` |
| **Profile** | `GET /users/profile`, `PUT /users/profile`, `POST /users/addresses` |
| **Seller** | `GET /seller/dashboard`, `GET /seller/orders`, `POST /seller/products` |
| **Admin** | `GET /admin/dashboard`, `GET /admin/users`, `GET /admin/sellers` |

---

## User Roles

| Role | Permissions |
|------|-------------|
| **Customer** | Browse products, manage cart, place orders, manage profile & addresses |
| **Seller** | Manage products & inventory, view/fulfill orders for their store |
| **Admin** | Full platform access: user management, seller approval, category management |

---

## Roadmap

- [x] Project scaffold (frontend + backend)
- [x] Authentication (JWT, role-based access)
- [x] Product browsing, search, filters
- [x] Shopping cart & checkout
- [x] Order management (customer, seller, admin)
- [x] Multi-language support (EN + ZH)
- [ ] Product reviews & ratings *(Phase 2)*
- [ ] Advanced analytics dashboard *(Phase 2)*
- [ ] Real payment gateway integration *(Post-MVP)*
- [ ] Cloud image storage *(Post-MVP)*

---

## Documentation

- **[prd.md](./prd.md)** — Full Product Requirements Document
- **[mvp.md](./mvp.md)** — MVP Scope, timeline, and feature checklist

---

## License

This project is currently for internal development use.
