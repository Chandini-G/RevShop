package com.revshop.app;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revshop.model.User;
import com.revshop.service.*;

public class RevShopApp {

    private static final Logger logger =
            LogManager.getLogger(RevShopApp.class);

    private static final Scanner sc = new Scanner(System.in);

    private static final AuthService authService = new AuthService();
    private static final UserService userService = new UserService();
    private static final ProductService productService = new ProductService();
    private static final CartService cartService = new CartService();
    private static final OrderService orderService = new OrderService();
    private static final ReviewService reviewService = new ReviewService();
    private static final FavouriteService favouriteService = new FavouriteService();

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n========== REVSHOP ==========");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Forgot Password");
            System.out.println("4. Exit");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> login();
                case 2 -> register();
                case 3 -> forgotPassword();
                case 4 -> {
                    System.out.println("Thank you for using RevShop");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    // ================= LOGIN =================
    private static void login() {

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        User user = userService.login(email, password);

        if (user == null) {
            logger.warn("Login failed for email: {}", email);
            System.out.println("Invalid credentials");
            return;
        }

        logger.info("Login success: {}", email);
        System.out.println("Welcome " + user.getName());

        if ("BUYER".equalsIgnoreCase(user.getRole())) {
            buyerMenu(user);
        } else if ("SELLER".equalsIgnoreCase(user.getRole())) {
            sellerMenu(user);
        }
    }

    // ================= REGISTER =================
    private static void register() {

        User user = new User();

        System.out.print("Name: ");
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

        boolean success = authService.register(user);

        if (success) {
            logger.info("Registration success: {}", user.getEmail());
            System.out.println("Registration successful");
        } else {
            logger.warn("Registration failed: {}", user.getEmail());
            System.out.println("Registration failed");
        }
    }

    // ================= FORGOT PASSWORD =================
    private static void forgotPassword() {

        System.out.print("Enter registered email: ");
        String email = sc.nextLine();

        System.out.print("Enter security answer: ");
        String answer = sc.nextLine();

        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine();

        boolean success =
                authService.forgotPassword(email, answer, newPassword);

        if (success) {
            System.out.println("Password reset successful");
        } else {
            System.out.println("Password reset failed. Details incorrect.");
        }
    }

    // ================= BUYER MENU (FULL) =================
    private static void buyerMenu(User user) {

        while (true) {
            System.out.println("\n--- BUYER MENU ---");
            System.out.println("1. View Products");
            System.out.println("2. View Products by Category");
            System.out.println("3. Add to Cart");
            System.out.println("4. View Cart");
            System.out.println("5. Remove from Cart");
            System.out.println("6. Place Order");
            System.out.println("7. View Order History");
            System.out.println("8. Cancel Order");
            System.out.println("9. Add Review");
            System.out.println("10. View Reviews");
            System.out.println("11. Add to Favourites");
            System.out.println("12. View Favourites");
            System.out.println("13. Remove from Favourites");
            System.out.println("14. Logout");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1 -> productService.viewProducts();

                case 2 -> productService.viewProductsByCategory(sc);

                case 3 -> {
                    System.out.print("Product ID: ");
                    int pid = sc.nextInt();
                    System.out.print("Quantity: ");
                    int qty = sc.nextInt();
                    sc.nextLine();
                    cartService.addToCart(user.getUserId(), pid, qty);
                }

                case 4 -> cartService.viewCart(user.getUserId());

                case 5 -> {
                    System.out.print("Product ID: ");
                    int pid = sc.nextInt();
                    System.out.print("Quantity: ");
                    int qty = sc.nextInt();
                    sc.nextLine();
                    cartService.removeFromCart(user.getUserId(), pid, qty);
                }

                case 6 -> {
                    System.out.print("Shipping Address: ");
                    String address = sc.nextLine();
                    System.out.print("Payment Method (CARD/UPI/COD): ");
                    String payment = sc.nextLine();
                    orderService.placeOrder(user.getUserId(), address, payment);
                }

                case 7 -> orderService.viewOrderHistory(user.getUserId());

                case 8 -> {
                    System.out.print("Order ID: ");
                    int oid = sc.nextInt();
                    sc.nextLine();
                    orderService.cancelOrder(oid);
                }

                case 9 -> {
                    System.out.print("Product ID: ");
                    int pid = sc.nextInt();
                    System.out.print("Rating (1-5): ");
                    int rating = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Comment: ");
                    String comment = sc.nextLine();
                    reviewService.addReview(pid, user.getUserId(), rating, comment);
                }

                case 10 -> {
                    System.out.print("Product ID: ");
                    int pid = sc.nextInt();
                    sc.nextLine();
                    reviewService.viewReviews(pid);
                }

                case 11 -> {
                    System.out.print("Product ID: ");
                    int pid = sc.nextInt();
                    sc.nextLine();
                    favouriteService.addToFavourites(user.getUserId(), pid);
                }

                case 12 -> favouriteService.viewFavourites(user.getUserId());

                case 13 -> {
                    System.out.print("Product ID: ");
                    int pid = sc.nextInt();
                    sc.nextLine();
                    favouriteService.removeFromFavourites(user.getUserId(), pid);
                }

                case 14 -> {
                    System.out.println("Logged out");
                    return;
                }

                default -> System.out.println("Invalid choice");
            }
        }
    }

    // ================= SELLER MENU =================
    private static void sellerMenu(User user) {

        while (true) {
            System.out.println("\n--- SELLER MENU ---");
            System.out.println("1. Add Product");
            System.out.println("2. View Inventory");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. View Orders");
            System.out.println("6. View Reviews");
            System.out.println("7. Logout");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> productService.addProduct(sc, user.getUserId());
                case 2 -> productService.viewSellerProducts(user.getUserId());
                case 3 -> productService.updateProduct(sc, user.getUserId());
                case 4 -> productService.deleteProduct(sc, user.getUserId());
                case 5 -> orderService.viewSellerOrders(user.getUserId());
                case 6 -> reviewService.viewSellerReviews(user.getUserId());
                case 7 -> {
                    System.out.println("Logged out");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }
}