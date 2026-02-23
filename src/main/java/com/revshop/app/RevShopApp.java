package com.revshop.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Scanner;
import com.revshop.model.User;
import com.revshop.service.*;

public class RevShopApp {
    private static final Logger logger = LogManager.getLogger(RevShopApp.class);
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("🚀 REVSHOP APPLICATION STARTING");
        logger.info("========================================");
        
        printSectionHeader("🛍️ WELCOME TO REVSHOP");
        System.out.println("    Secure E-Commerce Platform for Buyers & Sellers");

        AuthService authService = new AuthService();

        while (true) {
            displayMainMenu();
            int choice = getIntInput();
            logger.debug("User selected main menu option: {}", choice);

            switch (choice) {
                case 1 -> registerUser(authService);
                case 2 -> loginUser(authService);
                case 3 -> changePassword(authService);
                case 4 -> forgotPasswordWithSecurityQuestion(authService);
                case 5 -> browseProducts();
                case 6 -> showPasswordHint(authService);
                case 7 -> {
                    printSuccess("Thank you for using RevShop! Goodbye! 👋");
                    logger.info("Application shutting down normally.");
                    System.exit(0);
                }
                default -> {
                    printError("Invalid choice. Please try again.");
                    logger.warn("Invalid menu choice entered: {}", choice);
                }
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n📋 MAIN MENU");
        System.out.println("1. 👤 Register");
        System.out.println("2. 🔐 Login");
        System.out.println("3. 🔑 Change Password");
        System.out.println("4. ❓ Forgot Password");
        System.out.println("5. 🔍 Browse Products (Public)");
        System.out.println("6. 💡 View Password Hint");
        System.out.println("7. 🚪 Exit");
        System.out.print("\nEnter your choice: ");
    }

    private static int getIntInput() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void registerUser(AuthService authService) {
        logger.info("Starting user registration flow");
        printSectionHeader("📝 REGISTRATION");

        User user = new User();

        System.out.print("Full Name: ");
        user.setName(sc.nextLine());
        System.out.print("Email: ");
        user.setEmail(sc.nextLine());
        System.out.print("Password: ");
        user.setPassword(sc.nextLine());
        System.out.print("Role (BUYER/SELLER): ");
        user.setRole(sc.nextLine().toUpperCase());
        System.out.print("Security Question: ");
        user.setSecurityQuestion(sc.nextLine());
        System.out.print("Security Answer: ");
        user.setSecurityAnswer(sc.nextLine());
        System.out.print("Password Hint (optional): ");
        user.setPasswordHint(sc.nextLine());

        if (authService.register(user)) {
            printSuccess("Registration successful! You can now login.");
            logger.info("New user registered: {} (Role: {})", user.getEmail(), user.getRole());
        } else {
            printError("Registration failed. Email may already exist.");
            logger.warn("Registration failed – email already exists: {}", user.getEmail());
        }
    }

    private static void loginUser(AuthService authService) {
        logger.info("User attempting login");
        printSectionHeader("🔐 LOGIN");

        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        User user = authService.login(email, password);

        if (user != null) {
            printSuccess("Login successful! Welcome, " + user.getName() + "!");
            logger.info("User logged in: {} (ID: {}, Role: {})", 
                        user.getEmail(), user.getUserId(), user.getRole());

            NotificationService notificationService = new NotificationService();
            notificationService.sendWelcomeNotification(user.getUserId(), user.getName());

            if ("BUYER".equalsIgnoreCase(user.getRole())) {
                showBuyerMenu(user);
            } else {
                showSellerMenu(user);
            }
        } else {
            printError("Invalid email or password.");
            logger.warn("Failed login attempt for email: {}", email);
        }
    }

    private static void changePassword(AuthService authService) {
        logger.info("User attempting to change password");
        printSectionHeader("🔑 CHANGE PASSWORD");

        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Current Password: ");
        String oldPassword = sc.nextLine();
        System.out.print("New Password: ");
        String newPassword = sc.nextLine();

        if (authService.changePassword(email, oldPassword, newPassword)) {
            printSuccess("Password changed successfully!");
            logger.info("Password changed for user: {}", email);
        } else {
            printError("Failed to change password. Check your current password.");
            logger.warn("Password change failed for user: {}", email);
        }
    }

    private static void forgotPasswordWithSecurityQuestion(AuthService authService) {
        logger.info("User initiated forgot password flow");
        printSectionHeader("❓ FORGOT PASSWORD");

        System.out.print("Enter your email: ");
        String email = sc.nextLine();

        String securityQuestion = authService.getSecurityQuestion(email);
        if (securityQuestion == null) {
            printError("Email not found.");
            logger.warn("Forgot password – email not found: {}", email);
            return;
        }

        System.out.println("Security Question: " + securityQuestion);
        System.out.print("Your Answer: ");
        String answer = sc.nextLine();
        System.out.print("New Password: ");
        String newPassword = sc.nextLine();

        if (authService.forgotPassword(email, answer, newPassword)) {
            printSuccess("Password reset successful! You can now login.");
            logger.info("Password reset successful for: {}", email);
        } else {
            printError("Password reset failed. Check your answer.");
            logger.warn("Password reset failed for: {}", email);
        }
    }

    private static void showPasswordHint(AuthService authService) {
        logger.info("User requested password hint");
        printSectionHeader("💡 PASSWORD HINT");

        System.out.print("Enter your email: ");
        String email = sc.nextLine();

        String hint = authService.getPasswordHint(email);
        if (hint != null && !hint.isEmpty()) {
            System.out.println("\nYour Password Hint: " + hint);
            logger.info("Password hint shown for: {}", email);
        } else {
            printError("No password hint found for this email.");
            logger.warn("No password hint found for: {}", email);
        }
    }

    private static void browseProducts() {
        logger.info("User browsing public product catalog");
        printSectionHeader("🔍 PUBLIC PRODUCT CATALOG");

        ProductService productService = new ProductService();
        productService.showAvailableCategories();

        System.out.println("\nOptions:");
        System.out.println("1. View All Products");
        System.out.println("2. Browse by Category");
        System.out.println("3. Search Products");
        System.out.println("4. Back to Main Menu");
        System.out.print("\nEnter your choice: ");

        int choice = getIntInput();
        logger.debug("Browse products choice: {}", choice);

        switch (choice) {
            case 1 -> productService.viewAllProducts();
            case 2 -> {
                System.out.print("Enter category name: ");
                String category = sc.nextLine();
                productService.browseByCategory(category);
            }
            case 3 -> {
                System.out.print("Enter search keyword: ");
                String keyword = sc.nextLine();
                productService.searchProducts(keyword);
            }
            case 4 -> { return; }
            default -> printError("Invalid choice.");
        }
    }

    private static void showBuyerMenu(User user) {
        logger.info("Buyer menu opened for user: {}", user.getEmail());
        ProductService productService = new ProductService();
        CartService cartService = new CartService();
        OrderService orderService = new OrderService();
        FavouriteService favouriteService = new FavouriteService();
        ReviewService reviewService = new ReviewService();
        NotificationService notificationService = new NotificationService();

        while (true) {
            System.out.println("\n🛍️ BUYER MENU");
            System.out.println("1. 🔍 View All Products");
            System.out.println("2. 🔎 Search Products");
            System.out.println("3. 🏷️ Browse by Category");
            System.out.println("4. 🛒 View Cart");
            System.out.println("5. 🛒 Add to Cart");
            System.out.println("6. 🛒 Remove from Cart");
            System.out.println("7. 💳 Place Order");
            System.out.println("8. 📦 Order History");
            System.out.println("9. 📦 Track Order");
            System.out.println("10. ⭐ Favourites");
            System.out.println("11. 📝 Reviews");
            System.out.println("12. ❌ Cancel Order");
            System.out.println("13. 🔔 Notifications");
            System.out.println("14. 🚪 Logout");
            System.out.print("\nEnter your choice: ");

            int choice = getIntInput();
            logger.debug("Buyer menu choice: {} (User ID: {})", choice, user.getUserId());

            switch (choice) {
                case 1 -> productService.viewAllProducts();
                case 2 -> {
                    System.out.print("Enter search keyword: ");
                    String keyword = sc.nextLine();
                    productService.searchProducts(keyword);
                }
                case 3 -> {
                    productService.showAvailableCategories();
                    System.out.print("\nEnter category name: ");
                    String category = sc.nextLine();
                    productService.browseByCategory(category);
                }
                case 4 -> cartService.viewCart(user.getUserId());
                case 5 -> {
                    productService.viewAllProducts();
                    System.out.print("\nEnter Product ID: ");
                    int productId = getIntInput();
                    System.out.print("Enter Quantity: ");
                    int quantity = getIntInput();
                    cartService.addToCart(user.getUserId(), productId, quantity);
                }
                case 6 -> {
                    cartService.viewCart(user.getUserId());
                    System.out.print("\nEnter Product ID to remove: ");
                    int productId = getIntInput();
                    System.out.print("Enter Quantity to remove (0 to remove all): ");
                    int quantity = getIntInput();
                    cartService.removeFromCart(user.getUserId(), productId, quantity);
                }
                case 7 -> {
                    cartService.viewCart(user.getUserId());
                    System.out.print("\nProceed to checkout? (yes/no): ");
                    String confirm = sc.nextLine();
                    if (confirm.equalsIgnoreCase("yes")) {
                        System.out.print("Delivery Address: ");
                        String address = sc.nextLine();
                        System.out.print("Payment Method (COD/UPI/Credit Card/Debit Card/Net Banking/Wallet): ");
                        String method = sc.nextLine();
                        orderService.placeOrder(user.getUserId(), address, method);
                    }
                }
                case 8 -> orderService.viewOrderHistory(user.getUserId());
                case 9 -> {
                    System.out.print("Enter Order ID to track: ");
                    int orderId = getIntInput();
                    if (orderId > 0) {
                        orderService.trackOrder(orderId, user.getUserId(), user.getRole());
                    }
                }
                case 10 -> showFavouritesMenu(user, favouriteService);
                case 11 -> showReviewsMenu(user, reviewService);
                case 12 -> {
                    System.out.print("Enter Order ID to cancel: ");
                    int orderId = getIntInput();
                    orderService.cancelOrder(orderId, user.getUserId(), user.getRole());
                }
                case 13 -> notificationService.viewNotifications(user.getUserId());
                case 14 -> {
                    printSuccess("Logging out... Goodbye, " + user.getName() + "!");
                    logger.info("Buyer logged out: {}", user.getEmail());
                    return;
                }
                default -> printError("Invalid choice.");
            }
        }
    }

    private static void showSellerMenu(User user) {
        logger.info("Seller menu opened for user: {}", user.getEmail());
        ProductService productService = new ProductService();
        SellerOrderService sellerOrderService = new SellerOrderService();
        ReviewService reviewService = new ReviewService();
        NotificationService notificationService = new NotificationService();
        OrderService orderService = new OrderService();

        while (true) {
            System.out.println("\n🏪 SELLER MENU");
            System.out.println("1. ➕ Add Product");
            System.out.println("2. 📋 View My Products");
            System.out.println("3. ✏️ Update Product");
            System.out.println("4. 🗑️ Delete Product");
            System.out.println("5. 📦 View Orders");
            System.out.println("6. ⭐ View Reviews");
            System.out.println("7. ⚠️ Low Stock Alerts");
            System.out.println("8. 📊 Sales Report");
            System.out.println("9. 🔄 Update Order Status");
            System.out.println("10. ❌ Cancel Order");
            System.out.println("11. 🔔 Notifications");
            System.out.println("12. 🚪 Logout");
            System.out.print("\nEnter your choice: ");

            int choice = getIntInput();
            logger.debug("Seller menu choice: {} (User ID: {})", choice, user.getUserId());

            switch (choice) {
                case 1 -> productService.addProduct(sc, user.getUserId());
                case 2 -> productService.viewSellerProducts(user.getUserId());
                case 3 -> productService.updateProduct(sc, user.getUserId());
                case 4 -> productService.deleteProduct(sc, user.getUserId());
                case 5 -> sellerOrderService.viewSellerOrders(user.getUserId());
                case 6 -> reviewService.viewSellerReviews(user.getUserId());
                case 7 -> productService.checkLowStock(user.getUserId());
                case 8 -> productService.viewSalesReport(user.getUserId());
                case 9 -> {
                    System.out.print("Enter Order ID: ");
                    int orderId = getIntInput();
                    System.out.print("New Status (PROCESSING/SHIPPED/DELIVERED/CANCELLED): ");
                    String status = sc.nextLine().toUpperCase();
                    orderService.updateOrderStatus(orderId, user.getUserId(), status);
                }
                case 10 -> {
                    System.out.print("Enter Order ID to cancel: ");
                    int orderId = getIntInput();
                    orderService.cancelOrder(orderId, user.getUserId(), user.getRole());
                }
                case 11 -> notificationService.viewNotifications(user.getUserId());
                case 12 -> {
                    printSuccess("Logging out... Goodbye, " + user.getName() + "!");
                    logger.info("Seller logged out: {}", user.getEmail());
                    return;
                }
                default -> printError("Invalid choice.");
            }
        }
    }

    private static void showFavouritesMenu(User user, FavouriteService favouriteService) {
        logger.debug("Favourites menu opened for user ID: {}", user.getUserId());
        while (true) {
            System.out.println("\n⭐ FAVOURITES MENU");
            System.out.println("1. View Favourites");
            System.out.println("2. Add to Favourites");
            System.out.println("3. Remove from Favourites");
            System.out.println("4. Move to Cart");
            System.out.println("5. Back to Main Menu");
            System.out.print("\nEnter your choice: ");

            int choice = getIntInput();
            switch (choice) {
                case 1 -> favouriteService.viewFavourites(user.getUserId());
                case 2 -> {
                    System.out.print("Enter Product ID: ");
                    int productId = getIntInput();
                    favouriteService.addToFavourites(user.getUserId(), productId);
                }
                case 3 -> {
                    System.out.print("Enter Product ID: ");
                    int productId = getIntInput();
                    favouriteService.removeFromFavourites(user.getUserId(), productId);
                }
                case 4 -> {
                    System.out.print("Enter Product ID from favourites: ");
                    int productId = getIntInput();
                    System.out.print("Enter Quantity: ");
                    int quantity = getIntInput();
                    favouriteService.moveToCart(user.getUserId(), productId, quantity);
                }
                case 5 -> { return; }
                default -> printError("Invalid choice.");
            }
        }
    }

    private static void showReviewsMenu(User user, ReviewService reviewService) {
        logger.debug("Reviews menu opened for user ID: {}", user.getUserId());
        while (true) {
            System.out.println("\n📝 REVIEWS MENU");
            System.out.println("1. View Product Reviews");
            System.out.println("2. Add Review");
            System.out.println("3. Delete Review (Your reviews only)");
            System.out.println("4. Back to Main Menu");
            System.out.print("\nEnter your choice: ");

            int choice = getIntInput();
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Product ID: ");
                    int productId = getIntInput();
                    if (productId > 0) {
                        reviewService.viewProductReviews(productId);
                    } else {
                        printError("Invalid Product ID.");
                    }
                }
                case 2 -> {
                    System.out.print("Enter Product ID: ");
                    int productId = getIntInput();
                    if (productId <= 0) {
                        printError("Invalid Product ID.");
                        break;
                    }
                    System.out.print("Rating (1-5): ");
                    int rating = getIntInput();
                    System.out.print("Comment: ");
                    String comment = sc.nextLine();
                    reviewService.addReview(productId, user.getUserId(), rating, comment);
                }
                case 3 -> {
                    if ("BUYER".equalsIgnoreCase(user.getRole())) {
                        System.out.print("Enter Review ID to delete: ");
                        int reviewId = getIntInput();
                        if (reviewId > 0) {
                            reviewService.deleteReview(reviewId, user.getUserId(), user.getRole());
                        }
                    } else {
                        printError("Only buyers can delete their own reviews.");
                    }
                }
                case 4 -> { return; }
                default -> printError("Invalid choice.");
            }
        }
    }

    private static void printSectionHeader(String title) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    " + title);
        System.out.println("=".repeat(50));
    }

    private static void printSuccess(String message) {
        System.out.println("✅ " + message);
    }

    private static void printError(String message) {
        System.out.println("❌ " + message);
    }
}