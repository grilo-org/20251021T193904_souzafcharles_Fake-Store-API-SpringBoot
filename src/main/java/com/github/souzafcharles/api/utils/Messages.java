package com.github.souzafcharles.api.utils;

public class Messages {

    private Messages() {
        throw new IllegalStateException("Utility class");
    }

    // ===== General Exceptions =====
    public static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found with the specified identifier or criteria.";
    public static final String ERROR_DUPLICATE_EMAIL = "Email address already in use.";
    public static final String ERROR_DATABASE = "Database integrity violation.";
    public static final String ERROR_GENERIC = "Unexpected internal server error.";

    // ===== Custom Exception Messages =====
    public static final String EXCEPTION_DATABASE = "Database error detected: %s. Please verify database constraints and configurations";
    public static final String EXCEPTION_DUPLICATE_EMAIL = "The email address '%s' is already associated with an existing account.";

    // ===== Product (Validation & Exceptions) =====
    public static final String PRODUCT_TITLE_REQUIRED = "The title is required";
    public static final String PRODUCT_PRICE_REQUIRED = "The price is required";
    public static final String PRODUCT_PRICE_POSITIVE = "The price must be a positive value";
    public static final String PRODUCT_DESCRIPTION_MAX_LENGTH = "The description must be no longer than 5000 characters";
    public static final String PRODUCT_CATEGORY_REQUIRED = "The category is required";

    // For exceptions
    public static final String PRODUCT_NOT_FOUND = "Product not found with ID: %s";

    // For logging
    public static final String PRODUCT_ALREADY_INITIALIZED = "Products already initialized. Skipping load.";
    public static final String PRODUCT_NO_RETURNED = "No product returned from the Fake Store API.";
    public static final String PRODUCT_SAVED_SUCCESS = "{} products were saved to the H2 database.";


    // ===== Product Swagger =====
    public static final String PRODUCT_TAG_DESCRIPTION = "Endpoints to manage products and extract useful insights from the catalogue.";
    public static final String PRODUCT_GET_ALL_SUMMARY = "Retrieve all products";
    public static final String PRODUCT_GET_ALL_DESCRIPTION = "Fetches all products with pagination, enabling analysis of catalogue size and pricing distribution.";
    public static final String PRODUCT_GET_BY_ID_SUMMARY = "Retrieve a specific product";
    public static final String PRODUCT_GET_BY_ID_DESCRIPTION = "Fetches a product by its ID for detailed inspection and analytics.";
    public static final String PRODUCT_CREATE_SUMMARY = "Create a new product";
    public static final String PRODUCT_CREATE_DESCRIPTION = "Adds a new product to the catalogue, providing data for sales and inventory analysis.";
    public static final String PRODUCT_UPDATE_SUMMARY = "Update an existing product";
    public static final String PRODUCT_UPDATE_DESCRIPTION = "Updates product details such as price, title, and description, enabling recalculation of metrics and insights.";
    public static final String PRODUCT_DELETE_SUMMARY = "Delete a product";
    public static final String PRODUCT_DELETE_DESCRIPTION = "Removes a product from the catalogue, ensuring obsolete data does not affect analysis.";
    public static final String PRODUCT_SEARCH_SUMMARY = "Search products by keyword";
    public static final String PRODUCT_SEARCH_DESCRIPTION = "Searches products by title or description, supporting market and trend analysis.";
    public static final String PRODUCT_TOP_EXPENSIVE_SUMMARY = "Retrieve top expensive products";
    public static final String PRODUCT_TOP_EXPENSIVE_DESCRIPTION = "Fetches the most expensive products, allowing identification of high-value inventory.";
    public static final String PRODUCT_TOP_CHEAPEST_SUMMARY = "Retrieve top cheapest products";
    public static final String PRODUCT_TOP_CHEAPEST_DESCRIPTION = "Fetches the least expensive products, supporting budget-conscious analytics.";
    public static final String PRODUCT_AVG_PRICE_CATEGORY_SUMMARY = "Calculate average price per category";
    public static final String PRODUCT_AVG_PRICE_CATEGORY_DESCRIPTION = "Computes the average product price per category to derive pricing insights and strategy.";
    public static final String PRODUCT_PRICE_RANGE_SUMMARY = "Retrieve products within a price range";
    public static final String PRODUCT_PRICE_RANGE_DESCRIPTION = "Fetches products filtered by minimum and maximum price, enabling targeted analysis.";

    // ===== User (Validation & Exceptions) =====
    public static final String USERNAME_REQUIRED = "The username is required";
    public static final String EMAIL_REQUIRED = "The email is required";
    public static final String EMAIL_INVALID = "The email is invalid";
    public static final String PASSWORD_REQUIRED = "The password is required";
    public static final String PASSWORD_MIN_LENGTH = "The password must be at least 6 characters long";

    // For exceptions
    public static final String USER_NOT_FOUND = "User not found with ID: %s";

    // For logging
    public static final String USER_ALREADY_INITIALIZED = "Users already initialized. Skipping load.";
    public static final String USER_NO_RETURNED = "No users returned from the Fake Store API.";
    public static final String USER_SAVED_SUCCESS = "{} users were saved to the H2 database.";

    // ===== User Swagger =====
    public static final String USER_TAG_DESCRIPTION = "Endpoints to manage users and extract insights from user behaviour.";
    public static final String USER_GET_ALL_SUMMARY = "Retrieve all users";
    public static final String USER_GET_ALL_DESCRIPTION = "Fetches all users with pagination, enabling analysis of user base and activity.";
    public static final String USER_GET_BY_ID_SUMMARY = "Retrieve a specific user";
    public static final String USER_GET_BY_ID_DESCRIPTION = "Fetches a user by ID to inspect details and behaviour.";
    public static final String USER_CREATE_SUMMARY = "Create a new user";
    public static final String USER_CREATE_DESCRIPTION = "Registers a new user, providing data for engagement and analysis.";
    public static final String USER_UPDATE_SUMMARY = "Update an existing user";
    public static final String USER_UPDATE_DESCRIPTION = "Updates user details, allowing recalculation of analytics and insights.";
    public static final String USER_DELETE_SUMMARY = "Delete a user";
    public static final String USER_DELETE_DESCRIPTION = "Removes a user from the system, ensuring obsolete or irrelevant data is not processed.";
    public static final String USER_SEARCH_SUMMARY = "Search users by username";
    public static final String USER_SEARCH_DESCRIPTION = "Searches users by username, supporting engagement and trend analysis.";
    public static final String USER_GET_BY_USERNAME_SUMMARY = "Retrieve user by username";
    public static final String USER_GET_BY_USERNAME_DESCRIPTION = "Fetches a user based on username, for precise lookup and analysis.";
    public static final String USER_GET_BY_EMAIL_SUMMARY = "Retrieve user by email";
    public static final String USER_GET_BY_EMAIL_DESCRIPTION = "Fetches a user by email, supporting contact and behavioural insights.";
    public static final String USER_COUNT_SUMMARY = "Count total users";
    public static final String USER_COUNT_DESCRIPTION = "Counts all registered users, providing a key metric for engagement and growth analysis.";

    // ===== Cart (Validation & Exceptions) =====
    public static final String CART_USER_REQUIRED = "The user is required for the cart";
    public static final String CART_PRODUCTS_REQUIRED = "The products list is required for the cart";
    public static final String CART_PRODUCT_QUANTITY_POSITIVE = "The quantity must be greater than zero";

    // For exceptions
    public static final String CART_NOT_FOUND = "Cart not found with ID: %s";

    // For logging
    public static final String CART_ALREADY_INITIALIZED = "Carts already initialized. Skipping load.";
    public static final String CART_NO_RETURNED = "No cart returned from the Fake Store API.";
    public static final String CART_PRODUCT_IGNORED = "Ignoring CartProduct in Cart ID {}: Product ID {} not found.";
    public static final String CART_IGNORED_USER_NOT_FOUND = "Ignoring Cart ID {}: User ID {} not found.";
    public static final String CART_SAVED_SUCCESS = "{} carts were saved to the H2 database.";
    public static final String CART_NO_VALID = "No valid cart to save.";

    // ===== Cart Swagger =====
    public static final String CART_TAG_DESCRIPTION = "Endpoints to manage user shopping carts and generate insights.";
    public static final String CART_GET_ALL_SUMMARY = "Retrieve all carts";
    public static final String CART_GET_ALL_DESCRIPTION = "Fetches all carts with pagination, allowing analysis of total items and cart usage patterns.";
    public static final String CART_GET_BY_ID_SUMMARY = "Retrieve a specific cart item";
    public static final String CART_GET_BY_ID_DESCRIPTION = "Fetches a cart by its ID, enabling detailed inspection of items and quantities for insight generation.";
    public static final String CART_CREATE_SUMMARY = "Create a new cart item";
    public static final String CART_CREATE_DESCRIPTION = "Creates a cart for a specified user and their selected products, providing an initial dataset for further analysis.";
    public static final String CART_UPDATE_SUMMARY = "Update an existing cart";
    public static final String CART_UPDATE_DESCRIPTION = "Updates cart details including products and quantities, allowing recalculation of totals and insights.";
    public static final String CART_DELETE_SUMMARY = "Delete a cart item";
    public static final String CART_DELETE_DESCRIPTION = "Removes a cart by ID, maintaining data integrity and ensuring obsolete datasets are not processed.";
    public static final String CART_BY_USER_SUMMARY = "Retrieve carts by user";
    public static final String CART_BY_USER_DESCRIPTION = "Fetches all carts for a specific user, enabling calculation of total products and user behaviour insights.";
    public static final String CART_BY_PRODUCT_SUMMARY = "Retrieve carts containing a product";
    public static final String CART_BY_PRODUCT_DESCRIPTION = "Fetches all carts containing a specific product, useful for sales analysis and product popularity metrics.";
    public static final String CART_TOTAL_PRODUCTS_SUMMARY = "Calculate total products for a user";
    public static final String CART_TOTAL_PRODUCTS_DESCRIPTION = "Sums all product quantities across a user's carts to provide a key metric for engagement and consumption.";
    public static final String CART_TOTAL_VALUE_SUMMARY = "Retrieve carts above a total value";
    public static final String CART_TOTAL_VALUE_DESCRIPTION = "Filters carts whose total value exceeds a specified minimum, providing insight into high-value purchasing behaviour.";

    // ===== CartProduct Swagger =====
    public static final String CART_PRODUCT_TAG_DESCRIPTION = "Endpoints to manage products within carts and analyze sales/analytics.";
    public static final String CART_PRODUCT_ADD_SUMMARY = "Add a product to a cart";
    public static final String CART_PRODUCT_ADD_DESCRIPTION = "Adds a product to a specified cart, updating quantities and totals.";
    public static final String CART_PRODUCT_REMOVE_SUMMARY = "Remove a product from a cart";
    public static final String CART_PRODUCT_REMOVE_DESCRIPTION = "Removes a product from a cart, maintaining integrity of cart data.";
    public static final String CART_PRODUCT_LIST_SUMMARY = "List products in a cart";
    public static final String CART_PRODUCT_LIST_DESCRIPTION = "Fetches all products currently in a specific cart for detailed inspection.";
    public static final String CART_PRODUCT_MOST_SOLD_SUMMARY = "Retrieve most sold products";
    public static final String CART_PRODUCT_MOST_SOLD_DESCRIPTION = "Fetches the top-selling products across all carts, useful for sales analysis.";
    public static final String CART_PRODUCT_REVENUE_SUMMARY = "Calculate revenue per product";
    public static final String CART_PRODUCT_REVENUE_DESCRIPTION = "Computes total revenue generated per product across all carts.";
    public static final String CART_PRODUCT_TOTAL_ITEMS_SUMMARY = "Count total items in all carts";
    public static final String CART_PRODUCT_TOTAL_ITEMS_DESCRIPTION = "Sums all quantities of products across every cart.";
    public static final String CART_PRODUCT_CARTS_BY_PRODUCT_SUMMARY = "List carts containing a specific product";
    public static final String CART_PRODUCT_CARTS_BY_PRODUCT_DESCRIPTION = "Returns the IDs of all carts that contain a given product, useful for inventory and sales analysis.";

    // ===== CartProduct Logging =====
    public static final String CART_PRODUCTS_FOUND = "Products found for cart {}: {} items.";
    public static final String MOST_SOLD_PRODUCTS_RETURNED = "Top {} most sold products returned.";
    public static final String REVENUE_PER_PRODUCT_RETURNED = "Revenue calculated for {} products.";
    public static final String TOTAL_ITEMS_IN_CARTS_RETURNED = "Total number of items across all carts: {}.";
    public static final String CARTS_CONTAINING_PRODUCT_FOUND = "Product {} found in {} carts.";
    public static final String CARTS_CONTAINING_PRODUCT_NOT_FOUND = "Product {} not found in any cart.";
    public static final String MOST_SOLD_PRODUCTS_BY_CATEGORY_RETURNED = "Top sold products for category '{}' returned: {}.";

}
