![GitHub language count](https://img.shields.io/github/languages/count/souzafcharles/Fake-Store-API-SpringBoot)
![GitHub top language](https://img.shields.io/github/languages/top/souzafcharles/Fake-Store-API-SpringBoot)
![GitHub](https://img.shields.io/github/license/souzafcharles/Fake-Store-API-SpringBoot)
![GitHub last commit](https://img.shields.io/github/last-commit/souzafcharles/Fake-Store-API-SpringBoot)

# Java Backend Challenge - FakeStore API Integration

---

## 1. Business Problem Solved

This backend system integrates with the **FakeStore API** to manage **Products, Users, Carts, and CartProducts**.  
The focus is on **Products**, which are the core of the e-commerce domain. The application does more than CRUD: it provides **aggregations, analytics, and search features** that make the system useful for real business cases.

Examples of value-added functionalities:
- Search products by **keyword** across title and description.
- Get the **Top N most expensive** or **Top N cheapest** products.
- Calculate **average product price per category**.
- Filter products by **price range**.
- Track **most sold products** and calculate **revenue per product** based on carts.
- Count **total products per user cart** and filter carts by **total value**.
- Guarantee **data consistency** when linking products, users, and carts.

This addresses the challenge of turning a **simple external API** into a **functional e-commerce intelligence backend** that could be directly consumed by a frontend application or analytics dashboard.

### Fake Store API Model Entity:

![Fake Store API Entity](https://github.com/souzafcharles/Fake-Store-API-SpringBoot/blob/main/src/main/resources/static/img/entities.png)

#### Relationships:

1. **User → Cart**: One user can have multiple carts (`1:N`).
2. **Cart → CartProduct**: Each cart can contain multiple products (`1:N`), represented by `CartProduct`.
3. **Product → CartProduct**: Each product can appear in multiple carts (`1:N`), represented by `CartProduct`.
4. **CartProduct** is a **join entity** to handle the many-to-many relationship between `Cart` and `Product`, with additional attribute `quantity`.

---

## 2. Project Stack

| Technology           | Version   | Description                                                                 |
|----------------------|-----------|-----------------------------------------------------------------------------|
| ☕ Java               | `21`      | Backend programming language                                                |
| 🌱 Spring Boot       | `3.5.5`   | Core framework for building backend services                                |
| 💾 Spring Data JPA   | `3.5.5`   | ORM for relational database persistence                                     |
| 🌐 WebClient         | `3.5.5`   | Reactive HTTP client for external API integration                           |
| 🐘 H2 Database       | `2.2.224` | In-memory relational database (local development and testing)               |
| 📄 SpringDoc OpenAPI | `2.8.13`  | Automatic REST API documentation with Swagger UI                            |
| 🧪 JUnit / Mockito   | -         | Unit testing framework                                                      |
| 🧾 JaCoCo            | `0.8.10`  | Code coverage reporting for tests                                           |
| 🐦 Maven             | `3.9.9`   | Build automation and dependency management tool                             |
| 🚀 Postman           | `11.x`    | API testing and exploration tool                                            |

---

## Dependencies

| Dependency                    | Category          | Description                                                                                                     |
|-------------------------------|-------------------|-----------------------------------------------------------------------------------------------------------------|
| 🌐 Spring Web                 | Web              | Builds web applications, including RESTful APIs, using Spring MVC. Uses Apache Tomcat as the default container. |
| 💾 Spring Data JPA            | SQL              | Facilitates database access using JPA with Spring Data and Hibernate.                                           |
| 🐘 H2 Database Driver         | SQL              | JDBC driver enabling Java applications to interact with H2 in-memory database.                                  |
| ✔️ Validation                 | Validation (I/O) | Enables Java Bean Validation using Hibernate Validator.                                                         |
| 🌱 Spring WebFlux / WebClient | Web / Reactive   | Provides reactive programming support and non-blocking HTTP client for external APIs.                           |
| 📄 SpringDoc OpenAPI          | Documentation    | Generates Swagger UI automatically for REST API endpoints.                                                       |
| 🧪 JUnit / Mockito            | Testing          | Unit testing frameworks for behavior-driven and isolated tests.                                                 |
| 🧾 JaCoCo                     | Testing          | Generates code coverage reports for unit and integration tests.                                                  |

---

## 3. Architecture & Design Patterns

The application follows a **Layered Architecture**:  
`Controller → Service → Repository → Database/External API`.

- **Controller Layer**: Exposes REST endpoints, handles request validation, and orchestrates responses.
- **Service Layer**: Implements all business logic, enforces domain rules, and coordinates interactions between repositories and external APIs.
- **Repository Layer**: Abstracts data persistence using **Spring Data JPA** for local H2 database storage.
- **Database (H2)**: Stores local entities for testing, offline usage, and as a cache layer to reduce dependency on external APIs.
- **External API Client**: Consumes **FakeStore API** via **WebClient**, encapsulating all external HTTP calls in a dedicated client class (`FakeStoreClient`).

**Design Patterns applied:**
- **Repository Pattern**: Centralizes all persistence logic and abstracts database interactions.
- **DTO Pattern**: Separates internal domain entities from external API contracts, enforcing clear boundaries between request/response payloads and internal models.
- **Exception Handling**: Centralized custom exceptions (`ResourceNotFoundException`, `DuplicateEmailException`, `DatabaseException`) with a unified error response (`StandardError`) to ensure consistent API error reporting.
- **Factory / Data Initializer**: Seeds local database at startup using data from FakeStore API, ensuring baseline data availability for development and testing.
- **Centralized Messages Utility**: Manages all system messages (logs, validation messages, Swagger descriptions, exception messages) in a single utility class (`Messages`), reducing duplication and preparing for future **i18n** support.

### Project Logic Layered Architecture:

![Layered Architecture](https://github.com/souzafcharles/Fake-Store-API-SpringBoot/blob/main/src/main/resources/static/img/logical-layers.png)


**Justification:**  
A **Layered Architecture** was chosen for its clarity and suitability, ensuring maintainability and readability while fulfilling the challenge requirements.

---

## 4. Algorithms & Business Logic (Method Explanations)

Below is a detailed explanation of the most important methods across services.

### 🔹 ProductService
Handles **all product-related logic**.
- `getAllProducts(Pageable pageable)`: Returns a paginated list of all products.
- `getProductById(String id)`: Finds a product by its ID or throws `ResourceNotFoundException`.
- `createProduct(ProductRequestDTO dto)`: Creates a new product from request data.
- `updateProduct(String id, ProductRequestDTO dto)`: Updates fields of an existing product.
- `deleteProduct(String id)`: Deletes a product safely.
- `searchProducts(String keyword)`: Searches products by title or description (case-insensitive).
- `getTopExpensiveProducts(int topN)`: Returns the top N most expensive products.
- `getTopCheapestProducts(int topN)`: Returns the top N cheapest products.
- `getAveragePricePerCategory()`: Calculates the average price per category.
- `getProductsByPriceRange(Double min, Double max)`: Finds products within a given price range.

---

### 🔹 CartService
Handles **shopping cart management**.
- `getAllCarts(Pageable pageable)`: Lists carts with pagination.
- `getCartById(String id)`: Retrieves a cart by its ID.
- `createCart(CartRequestDTO dto)`: Creates a cart linked to a valid user and products.
- `updateCart(String id, CartRequestDTO dto)`: Updates user and products inside an existing cart.
- `deleteCart(String id)`: Removes a cart, handling integrity issues.
- `getCartsByUserId(String userId)`: Gets all carts belonging to a specific user.
- `getTotalProductsForUser(String userId)`: Counts all products (sum of quantities) in a user’s carts.
- `getCartsWithTotalValueGreaterThan(Double minTotal)`: Returns carts whose total value exceeds a threshold.

---

### 🔹 UserService
Manages **user accounts and validations**.
- `getAllUsers(Pageable pageable)`: Returns a paginated list of users.
- `getUserById(String id)`: Retrieves a user by ID.
- `createUser(UserRequestDTO dto)`: Creates a new user with **email duplication check**.
- `updateUser(String id, UserRequestDTO dto)`: Updates user data, validating unique email.
- `deleteUser(String id)`: Deletes a user with exception handling.
- `getUserByUsername(String username)`: Finds a user by username.
- `getUserByEmail(String email)`: Finds a user by email.
- `searchUsersByUsername(String keyword)`: Searches users by partial username match.
- `countUsers()`: Counts all registered users.

---

### 🔹 CartProductService
Handles **products inside carts**.
- `addProductToCart(String cartId, CartProductRequestDTO dto)`: Adds a product to a cart; if already present, increases its quantity.
- `deleteProductFromCart(String cartId, String productId)`: Removes a product from a cart.
- `getProductsInCart(String cartId)`: Lists all products inside a given cart.
- `getMostSoldProducts(int topN)`: Returns the top N most sold products by total quantity.
- `getRevenuePerProduct()`: Calculates total revenue generated per product.
- `getTotalItemsInCarts()`: Returns the global count of all items in all carts.
- `getCartsContainingProduct(String productId)`: Finds all carts that contain a specific product.
- Queries like `findCartsByProductId`, `findMostSoldProductsByCategory`, and `findRevenuePerProductRaw` now execute efficiently at the database level.
- Avoids unnecessary in-memory processing.
- Supports dynamic filtering using query parameters (e.g., by category).

---

## 5. Technical Decisions & Trade-offs

- **Spring Boot with WebClient**: Chosen for modern, reactive, non-blocking API calls.
- **WebClientConfig + FakeStoreClient**: The `WebClient` bean configuration was separated into a dedicated class (`WebClientConfig`), while the external API integration logic was encapsulated in `FakeStoreClient`. This separation improves testability, promotes single responsibility, and makes it easier to swap or extend external API providers.
- **H2 Database**: Enables local development with zero setup; downside is data resets on restart.
- **DTO separation**: Prevents exposing internal entity models directly to API consumers. Both **request and response DTOs** are defined for **Product, User, and Cart** to enforce clear contracts between API and domain layers.
- **Data Initializers**: Each domain (**Product, User, Cart**) includes a `DataInitializer` that loads data from the external **FakeStore API** at startup when the local repository is empty. This ensures baseline data availability for development and testing, while accepting the trade-off of external dependency during initialization.
- **Centralized Exception Handling**: A dedicated package consolidates custom exceptions (`ResourceNotFoundException`, `DuplicateEmailException`, `DatabaseException`) and a unified error response model (`StandardError`) through `ResourceExceptionHandler`. This approach ensures consistent, descriptive, and user-friendly error messages across the entire API.
- **Centralized Messages Utility**: All system messages (logs, validation, Swagger descriptions, and exception messages) are managed in a single utility class (`Messages`). This reduces code duplication, keeps controllers/services cleaner, and prepares the project for future **internationalization (i18n)**.
- **Layered Package Structure by Responsibility**:
    - **client** → integration with external APIs (`FakeStoreClient`);
    - **config** → bean and application configuration (`WebClientConfig`);
    - **endpoint** → domain logic and REST controllers, organized by module (`cart`, `cartproduct`, `product`, `user`);
    - **exceptions** → centralized exception classes, handlers, and error models;
    - **utils** → shared utilities, such as centralized messages. This organization enforces **single responsibility per package**, improves **readability**, and facilitates **maintenance and testing**.
- **HATEOAS + Pagination**: The API uses **HATEOAS-compliant responses** with `_links` and `_embedded` elements, enabling discoverability of related resources. Pagination is implemented in all list endpoints, providing `page`, `size`, `totalElements`, `totalPages`, and navigation links (`first`, `self`, `next`, `last`). This ensures scalability and consistency when dealing with large datasets.
- **Layered Architecture**: Better for project readability.
- **AAA (Arrange, Act, Assert) Testing Pattern**: Adopted for all unit tests to enforce readability, maintain consistency, and clearly separate test setup, execution, and validation.
---

## 6. Testing Strategy

The project adopts a structured **unit testing strategy** using **JUnit 5** and **Mockito** to ensure reliable, maintainable, and predictable behavior across all modules.  
Tests are consistently written following the **AAA (Arrange, Act, Assert) pattern**, which improves readability and enforces clarity of intent.

### Key Testing Areas

#### Product Domain
- Validation of core business logic (**CRUD operations**, search, filtering by price range).
- Calculation of top **expensive** and **cheapest** products.
- Aggregation metrics such as **average price per category**.

#### Cart Domain
- Validation of cart aggregation logic, including **total price calculation** and **threshold validations**.
- Ensures correct handling of **empty carts** and **invalid product references**.

#### User Domain
- Validation of user lifecycle operations (**create, update, delete, retrieve**).
- **Duplicate email prevention** and proper exception handling.
- Handling of **not found** scenarios with appropriate error responses.

#### Cross-Cutting Concerns
- Exception handling (`ResourceNotFoundException`, `DuplicateEmailException`, `DatabaseException`).
- Proper translation of exceptions into standardized API error responses.
- Data initialization logic ensuring external API sync runs only when the repository is empty.

### Goals
- **>90% line and branch coverage**, focusing on **business-critical paths** rather than trivial getters/setters.
- Validate both **happy paths** and **failure scenarios** (error propagation, invalid inputs, empty results).
- Ensure **stable and predictable** application behavior under normal and exceptional conditions.
- Provide confidence for future refactoring by covering all **critical use cases** with repeatable, isolated unit tests.


## 7. Running Locally

7.1. Clone the repository:
```bash
git clone https://github.com/souzafcharles/Fake-Store-API-SpringBoot.git
cd Fake-Store-API-SpringBoot
```

7.2. Build the project using Maven:
```bash
mvn clean install
```

7.3. Run the Spring Boot application:
```bash
mvn spring-boot:run
```

7.4. The H2 console is available at:

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

7.5. Running JaCoCo for Test Coverage

7.5.1. Execute the Maven test goal with JaCoCo enabled:

```bash
mvn clean test jacoco:report
```

7.5.2. After the tests run, generate the coverage report. By default, it will be located at:

```bash
target/site/jacoco/index.html
```

7.5.3. Open the report in your browser to inspect coverage metrics:

```bash
open target/site/jacoco/index.html
```

## 8. Examples of Usage (HTTP Requests & Responses)

Access the complete API documentation via Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

Additionally, use the provided collection containing all HTTP requests and example responses. This collection is compatible with tools such as Postman, Insomnia, or any other REST client. Requests can be executed directly against the API for testing, exploration, and integration purposes.

[JSON Collection](https://github.com/souzafcharles/Fake-Store-API-SpringBoot/blob/main/src/main/resources/static/local/FakeStoreAPI.collection.json)


### PRODUCTS

#### GET Top N Most Expensive Products
```
http://localhost:8080/products/top-expensive?topN=5
```

#### Response
```json
[
    {
        "id": "14",
        "title": "Samsung 49-Inch CHG90 144Hz Curved Gaming Monitor (LC49HG90DMNXZA) – Super Ultrawide Screen QLED ",
        "price": 999.99,
        "description": "49 INCH SUPER ULTRAWIDE 32:9 CURVED GAMING MONITOR with dual 27 inch screen side by side QUANTUM DOT (QLED) TECHNOLOGY, HDR support and factory calibration provides stunningly realistic and accurate color and contrast 144HZ HIGH REFRESH RATE and 1ms ultra fast response time work to eliminate motion blur, ghosting, and reduce input lag",
        "category": "electronics",
        "image": "https://fakestoreapi.com/img/81Zt42ioCgL._AC_SX679_t.png"
    },
    {
        "id": "5",
        "title": "John Hardy Women's Legends Naga Gold & Silver Dragon Station Chain Bracelet",
        "price": 695.0,
        "description": "From our Legends Collection, the Naga was inspired by the mythical water dragon that protects the ocean's pearl. Wear facing inward to be bestowed with love and abundance, or outward for protection.",
        "category": "jewelery",
        "image": "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_t.png"
    },
    {
        "id": "13",
        "title": "Acer SB220Q bi 21.5 inches Full HD (1920 x 1080) IPS Ultra-Thin",
        "price": 599.0,
        "description": "21. 5 inches Full HD (1920 x 1080) widescreen IPS display And Radeon free Sync technology. No compatibility for VESA Mount Refresh Rate: 75Hz - Using HDMI port Zero-frame design | ultra-thin | 4ms response time | IPS panel Aspect ratio - 16: 9. Color Supported - 16. 7 million colors. Brightness - 250 nit Tilt angle -5 degree to 15 degree. Horizontal viewing angle-178 degree. Vertical viewing angle-178 degree 75 hertz",
        "category": "electronics",
        "image": "https://fakestoreapi.com/img/81QpkIctqPL._AC_SX679_t.png"
    },
    {
        "id": "6bb54563-4601-4c7b-8b45-1aec9a274574",
        "title": "Sports Trainers",
        "price": 199.9,
        "description": "Comfortable trainers for running",
        "category": "Sports",
        "image": "http://example.com/trainers.jpg"
    },
    {
        "id": "6",
        "title": "Solid Gold Petite Micropave ",
        "price": 168.0,
        "description": "Satisfaction Guaranteed. Return or exchange any order within 30 days.Designed and sold by Hafeez Center in the United States. Satisfaction Guaranteed. Return or exchange any order within 30 days.",
        "category": "jewelery",
        "image": "https://fakestoreapi.com/img/61sbMiUnoGL._AC_UL640_QL65_ML3_t.png"
    }
]
```

#### GET Top N Cheapest Products

```
http://localhost:8080/products/top-cheapest?topN=5
```

#### Response
```json
[
    {
        "id": "19",
        "title": "Opna Women's Short Sleeve Moisture",
        "price": 7.95,
        "description": "100% Polyester, Machine wash, 100% cationic polyester interlock, Machine Wash & Pre Shrunk for a Great Fit, Lightweight, roomy and highly breathable with moisture wicking fabric which helps to keep moisture away, Soft Lightweight Fabric with comfortable V-neck collar and a slimmer fit, delivers a sleek, more feminine silhouette and Added Comfort",
        "category": "women's clothing",
        "image": "https://fakestoreapi.com/img/51eg55uWmdL._AC_UX679_t.png"
    },
    {
        "id": "18",
        "title": "MBJ Women's Solid Short Sleeve Boat Neck V ",
        "price": 9.85,
        "description": "95% RAYON 5% SPANDEX, Made in USA or Imported, Do Not Bleach, Lightweight fabric with great stretch for comfort, Ribbed on sleeves and neckline / Double stitching on bottom hem",
        "category": "women's clothing",
        "image": "https://fakestoreapi.com/img/71z3kpMAYsL._AC_UY879_t.png"
    },
    {
        "id": "7",
        "title": "White Gold Plated Princess",
        "price": 9.99,
        "description": "Classic Created Wedding Engagement Solitaire Diamond Promise Ring for Her. Gifts to spoil your love more for Engagement, Wedding, Anniversary, Valentine's Day...",
        "category": "jewelery",
        "image": "https://fakestoreapi.com/img/71YAIFU48IL._AC_UL640_QL65_ML3_t.png"
    },
    {
        "id": "8",
        "title": "Pierced Owl Rose Gold Plated Stainless Steel Double",
        "price": 10.99,
        "description": "Rose Gold Plated Double Flared Tunnel Plug Earrings. Made of 316L Stainless Steel",
        "category": "jewelery",
        "image": "https://fakestoreapi.com/img/51UDEzMJVpL._AC_UL640_QL65_ML3_t.png"
    },
    {
        "id": "20",
        "title": "DANVOUY Womens T Shirt Casual Cotton Short",
        "price": 12.99,
        "description": "95%Cotton,5%Spandex, Features: Casual, Short Sleeve, Letter Print,V-Neck,Fashion Tees, The fabric is soft and has some stretch., Occasion: Casual/Office/Beach/School/Home/Street. Season: Spring,Summer,Autumn,Winter.",
        "category": "women's clothing",
        "image": "https://fakestoreapi.com/img/61pHAEJ4NML._AC_UX679_t.png"
    }
]
```

#### GET Average Price per Category

```
http://localhost:8080/products/average-price-category
```

#### Response
```json
{
    "women's clothing": 26.286666666666665,
    "electronics": 332.49833333333333,
    "jewelery": 220.995,
    "men's clothing": 31.426666666666666,
    "Sports": 199.9
}
```

#### GET Products by Price Range

```
http://localhost:8080/products//price-range?min=50&max=200
```

#### Response
```json
[
    {
        "id": "3",
        "title": "Mens Cotton Jacket",
        "price": 55.99,
        "description": "great outerwear jackets for Spring/Autumn/Winter, suitable for many occasions, such as working, hiking, camping, mountain/rock climbing, cycling, traveling or other outdoors. Good gift choice for you or your family member. A warm hearted love to Father, husband or son in this thanksgiving or Christmas Day.",
        "category": "men's clothing",
        "image": "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_t.png"
    },
    {
        "id": "6",
        "title": "Solid Gold Petite Micropave ",
        "price": 168.0,
        "description": "Satisfaction Guaranteed. Return or exchange any order within 30 days.Designed and sold by Hafeez Center in the United States. Satisfaction Guaranteed. Return or exchange any order within 30 days.",
        "category": "jewelery",
        "image": "https://fakestoreapi.com/img/61sbMiUnoGL._AC_UL640_QL65_ML3_t.png"
    },
    {
        "id": "9",
        "title": "WD 2TB Elements Portable External Hard Drive - USB 3.0 ",
        "price": 64.0,
        "description": "USB 3.0 and USB 2.0 Compatibility Fast data transfers Improve PC Performance High Capacity; Compatibility Formatted NTFS for Windows 10, Windows 8.1, Windows 7; Reformatting may be required for other operating systems; Compatibility may vary depending on user’s hardware configuration and operating system",
        "category": "electronics",
        "image": "https://fakestoreapi.com/img/61IBBVJvSDL._AC_SY879_t.png"
    },
    {
        "id": "10",
        "title": "SanDisk SSD PLUS 1TB Internal SSD - SATA III 6 Gb/s",
        "price": 109.0,
        "description": "Easy upgrade for faster boot up, shutdown, application load and response (As compared to 5400 RPM SATA 2.5” hard drive; Based on published specifications and internal benchmarking tests using PCMark vantage scores) Boosts burst write performance, making it ideal for typical PC workloads The perfect balance of performance and reliability Read/write speeds of up to 535MB/s/450MB/s (Based on internal testing; Performance may vary depending upon drive capacity, host device, OS and application.)",
        "category": "electronics",
        "image": "https://fakestoreapi.com/img/61U7T1koQqL._AC_SX679_t.png"
    },
    {
        "id": "11",
        "title": "Silicon Power 256GB SSD 3D NAND A55 SLC Cache Performance Boost SATA III 2.5",
        "price": 109.0,
        "description": "3D NAND flash are applied to deliver high transfer speeds Remarkable transfer speeds that enable faster bootup and improved overall system performance. The advanced SLC Cache Technology allows performance boost and longer lifespan 7mm slim design suitable for Ultrabooks and Ultra-slim notebooks. Supports TRIM command, Garbage Collection technology, RAID, and ECC (Error Checking & Correction) to provide the optimized performance and enhanced reliability.",
        "category": "electronics",
        "image": "https://fakestoreapi.com/img/71kWymZ+c+L._AC_SX679_t.png"
    },
    {
        "id": "12",
        "title": "WD 4TB Gaming Drive Works with Playstation 4 Portable External Hard Drive",
        "price": 114.0,
        "description": "Expand your PS4 gaming experience, Play anywhere Fast and easy, setup Sleek design with high capacity, 3-year manufacturer's limited warranty",
        "category": "electronics",
        "image": "https://fakestoreapi.com/img/61mtL65D4cL._AC_SX679_t.png"
    },
    {
        "id": "15",
        "title": "BIYLACLESEN Women's 3-in-1 Snowboard Jacket Winter Coats",
        "price": 56.99,
        "description": "Note:The Jackets is US standard size, Please choose size as your usual wear Material: 100% Polyester; Detachable Liner Fabric: Warm Fleece. Detachable Functional Liner: Skin Friendly, Lightweigt and Warm.Stand Collar Liner jacket, keep you warm in cold weather. Zippered Pockets: 2 Zippered Hand Pockets, 2 Zippered Pockets on Chest (enough to keep cards or keys)and 1 Hidden Pocket Inside.Zippered Hand Pockets and Hidden Pocket keep your things secure. Humanized Design: Adjustable and Detachable Hood and Adjustable cuff to prevent the wind and water,for a comfortable fit. 3 in 1 Detachable Design provide more convenience, you can separate the coat and inner as needed, or wear it together. It is suitable for different season and help you adapt to different climates",
        "category": "women's clothing",
        "image": "https://fakestoreapi.com/img/51Y5NI-I5jL._AC_UX679_t.png"
    },
    {
        "id": "6bb54563-4601-4c7b-8b45-1aec9a274574",
        "title": "Sports Trainers",
        "price": 199.9,
        "description": "Comfortable trainers for running",
        "category": "Sports",
        "image": "http://example.com/trainers.jpg"
    }
]
```

#### GET Search Products by Keyword

```
http://localhost:8080/products/search?keyword=power 
```

#### Response
```json
[
    {
        "id": "11",
        "title": "Silicon Power 256GB SSD 3D NAND A55 SLC Cache Performance Boost SATA III 2.5",
        "price": 109.0,
        "description": "3D NAND flash are applied to deliver high transfer speeds Remarkable transfer speeds that enable faster bootup and improved overall system performance. The advanced SLC Cache Technology allows performance boost and longer lifespan 7mm slim design suitable for Ultrabooks and Ultra-slim notebooks. Supports TRIM command, Garbage Collection technology, RAID, and ECC (Error Checking & Correction) to provide the optimized performance and enhanced reliability.",
        "category": "electronics",
        "image": "https://fakestoreapi.com/img/71kWymZ+c+L._AC_SX679_t.png"
    }
]
```

#### GET - List Paginated Products
```
http://localhost:8080/products?page=0&size=10&sort=price,desc
```
#### Response
```json
{
    "_embedded": {
        "productResponseDTOList": [
            {
                "id": "1",
                "title": "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops",
                "price": 109.95,
                "description": "Your perfect pack for everyday use and walks in the forest. Stash your laptop (up to 15 inches) in the padded sleeve, your everyday",
                "category": "men's clothing",
                "image": "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_t.png",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/products/1"
                    }
                }
            },
            {
                "id": "2",
                "title": "Mens Casual Premium Slim Fit T-Shirts ",
                "price": 22.3,
                "description": "Slim-fitting style, contrast raglan long sleeve, three-button henley placket, light weight & soft fabric for breathable and comfortable wearing. And Solid stitched shirts with round neck made for durability and a great fit for casual fashion wear and diehard baseball fans. The Henley style round neckline includes a three-button placket.",
                "category": "men's clothing",
                "image": "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_t.png",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/products/2"
                    }
                }
            },
            {
                "id": "3",
                "title": "Mens Cotton Jacket",
                "price": 55.99,
                "description": "great outerwear jackets for Spring/Autumn/Winter, suitable for many occasions, such as working, hiking, camping, mountain/rock climbing, cycling, traveling or other outdoors. Good gift choice for you or your family member. A warm hearted love to Father, husband or son in this thanksgiving or Christmas Day.",
                "category": "men's clothing",
                "image": "https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_t.png",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/products/3"
                    }
                }
            },
            {
                "id": "4",
                "title": "Mens Casual Slim Fit",
                "price": 15.99,
                "description": "The color could be slightly different between on the screen and in practice. / Please note that body builds vary by person, therefore, detailed size information should be reviewed below on the product description.",
                "category": "men's clothing",
                "image": "https://fakestoreapi.com/img/71YXzeOuslL._AC_UY879_t.png",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/products/4"
                    }
                }
            },
            {
                "id": "5",
                "title": "John Hardy Women's Legends Naga Gold & Silver Dragon Station Chain Bracelet",
                "price": 695.0,
                "description": "From our Legends Collection, the Naga was inspired by the mythical water dragon that protects the ocean's pearl. Wear facing inward to be bestowed with love and abundance, or outward for protection.",
                "category": "jewelery",
                "image": "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_t.png",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/products/5"
                    }
                }
            },
            {
                "id": "6",
                "title": "Solid Gold Petite Micropave ",
                "price": 168.0,
                "description": "Satisfaction Guaranteed. Return or exchange any order within 30 days.Designed and sold by Hafeez Center in the United States. Satisfaction Guaranteed. Return or exchange any order within 30 days.",
                "category": "jewelery",
                "image": "https://fakestoreapi.com/img/61sbMiUnoGL._AC_UL640_QL65_ML3_t.png",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/products/6"
                    }
                }
            },
            {
                "id": "7",
                "title": "White Gold Plated Princess",
                "price": 9.99,
                "description": "Classic Created Wedding Engagement Solitaire Diamond Promise Ring for Her. Gifts to spoil your love more for Engagement, Wedding, Anniversary, Valentine's Day...",
                "category": "jewelery",
                "image": "https://fakestoreapi.com/img/71YAIFU48IL._AC_UL640_QL65_ML3_t.png",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/products/7"
                    }
                }
            },
            {
                "id": "8",
                "title": "Pierced Owl Rose Gold Plated Stainless Steel Double",
                "price": 10.99,
                "description": "Rose Gold Plated Double Flared Tunnel Plug Earrings. Made of 316L Stainless Steel",
                "category": "jewelery",
                "image": "https://fakestoreapi.com/img/51UDEzMJVpL._AC_UL640_QL65_ML3_t.png",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/products/8"
                    }
                }
            },
            {
                "id": "9",
                "title": "WD 2TB Elements Portable External Hard Drive - USB 3.0 ",
                "price": 64.0,
                "description": "USB 3.0 and USB 2.0 Compatibility Fast data transfers Improve PC Performance High Capacity; Compatibility Formatted NTFS for Windows 10, Windows 8.1, Windows 7; Reformatting may be required for other operating systems; Compatibility may vary depending on user’s hardware configuration and operating system",
                "category": "electronics",
                "image": "https://fakestoreapi.com/img/61IBBVJvSDL._AC_SY879_t.png",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/products/9"
                    }
                }
            },
            {
                "id": "10",
                "title": "SanDisk SSD PLUS 1TB Internal SSD - SATA III 6 Gb/s",
                "price": 109.0,
                "description": "Easy upgrade for faster boot up, shutdown, application load and response (As compared to 5400 RPM SATA 2.5” hard drive; Based on published specifications and internal benchmarking tests using PCMark vantage scores) Boosts burst write performance, making it ideal for typical PC workloads The perfect balance of performance and reliability Read/write speeds of up to 535MB/s/450MB/s (Based on internal testing; Performance may vary depending upon drive capacity, host device, OS and application.)",
                "category": "electronics",
                "image": "https://fakestoreapi.com/img/61U7T1koQqL._AC_SX679_t.png",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/products/10"
                    }
                }
            }
        ]
    },
    "_links": {
        "first": {
            "href": "http://localhost:8080/products?page=0&size=10&sort=price,desc"
        },
        "self": {
            "href": "http://localhost:8080/products?page=0&size=10&sort=price,desc"
        },
        "next": {
            "href": "http://localhost:8080/products?page=1&size=10&sort=price,desc"
        },
        "last": {
            "href": "http://localhost:8080/products?page=2&size=10&sort=price,desc"
        }
    },
    "page": {
        "size": 10,
        "totalElements": 21,
        "totalPages": 3,
        "number": 0
    }
}
```

#### GET - Find Product by ID
```
http://localhost:8080/products/1
```
#### Response
```json
{
    "id": "1",
    "title": "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops",
    "price": 109.95,
    "description": "Your perfect pack for everyday use and walks in the forest. Stash your laptop (up to 15 inches) in the padded sleeve, your everyday",
    "category": "men's clothing",
    "image": "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_t.png"
}
```

#### POST - Create Product
```
http://localhost:8080/products
```
#### Body
```json
{
    "id": "6bb54563-4601-4c7b-8b45-1aec9a274574",
    "title": "Sports Trainers",
    "price": 199.9,
    "description": "Comfortable trainers for running",
    "category": "Sports",
    "image": "http://example.com/trainers.jpg"
}
```

#### PUT - Update Product

```
http://localhost:8080/products/1
```

#### Body
```json
{
    "id": "1",
    "title": "Updated Trainers",
    "price": 210.0,
    "description": "Updated",
    "category": "Sports",
    "image": "http://example.com/trainers.jpg"
}

```

#### DELETE - Delete Product
```
http://localhost:8080/products/1
```

## 9. Conclusion

This project implements a backend system integrating with the **FakeStore API**, addressing all core requirements of the Java Backend Challenge. Beyond simple CRUD operations, it delivers **value-added features** such as product search, top N expensive/cheapest products, price range filtering, average price per category, and cart analytics.

Key achievements:
- **External API Integration** with `WebClient`, encapsulated in `FakeStoreClient` for modularity and testability.
- **Business Logic & Data Processing** to generate actionable insights, including revenue per product and top-selling items.
- **Layered Architecture & Design Patterns** ensuring maintainability, clarity, and scalability.
- **Clean Code & Best Practices** with RESTful APIs, SOLID principles, and full OpenAPI/Swagger documentation.
- **Unit Testing** with JUnit and Mockito, covering critical business paths for reliability.

This solution demonstrates practical problem-solving, technical implementation, and readiness for real-world backend integration and service consumption, fulfilling and exceeding the challenge requirements.


## References

Henrique S. (2023, October 29). *External API with Java, Spring Boot and Gradle*. Sorai Tech. Updated August 17, 2025. https://soraitech.com/external-api-with-java-spring-boot-and-gradle/

Build & Run. (n.d.). *Java Spring Boot Tutorials Playlist* [Video playlist]. YouTube. https://www.youtube.com/watch?v=Tnl4YnB6E54&list=PLxCh3SsamNs62j6T7bv6f1_1j9H9pEzkC&ab_channel=Build%26Run

Wellington de Oliveira. (n.d.). *Spring Boot and Angular | 07 - Code Coverage with Jacoco (Testing)* [Video]. YouTube. https://www.youtube.com/watch?v=eTQmjY4oO_s&list=PLtII2Mw41oA0LLJgLVeyRWO5IPUtdfRth&index=15&ab_channel=WellingtondeOliveira


