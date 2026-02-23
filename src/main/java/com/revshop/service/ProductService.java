package com.revshop.service;

import java.sql.ResultSet;
import java.util.List;
import java.util.Scanner;
import com.revshop.dao.ProductDAO;
import com.revshop.model.Product;

public class ProductService {
    private ProductDAO productDAO = new ProductDAO();
    
    // NEW: Method to show available categories
    public void showAvailableCategories() {
        List<String[]> categories = productDAO.getAllCategories();
        
        System.out.println("\n📋 AVAILABLE CATEGORIES");
        System.out.println("=".repeat(40));
        
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            return;
        }
        
        System.out.println("ID  | CATEGORY NAME");
        System.out.println("----|--------------");
        for (String[] category : categories) {
            System.out.printf("%-3s | %s%n", category[0], category[1]);
        }
        System.out.println("=".repeat(40));
    }
    
    // View all products with improved formatting
    public void viewAllProducts() {
        List<Product> products = productDAO.getAllProducts();
        
        System.out.println("\n📦 ALL PRODUCTS");
        System.out.println("=".repeat(120));
        
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        
        // Table header - FIXED FORMATTING
        System.out.printf("%-6s %-25s %-18s %-12s %-12s %-7s %-7s %-12s%n",
            "ID", "PRODUCT NAME", "CATEGORY", "PRICE", "MRP", "DISC%", "STOCK", "STATUS");
        System.out.println("-".repeat(120));
        
        double totalValue = 0;
        for (Product p : products) {
            String status;
            if (p.getStock() == 0) {
                status = "❌ OUT";
            } else if (p.getStock() <= p.getThreshold()) {
                status = "⚠️ LOW";
            } else {
                status = "✅ IN";
            }
            
            String name = truncate(p.getName(), 25);
            String category = p.getCategoryName() != null ? truncate(p.getCategoryName(), 18) : "Unknown";
            
            // FIXED: Removed extra % sign - it's included in the format string
            System.out.printf("%-6d %-25s %-18s ₹%-11.2f ₹%-11.2f %-7d %-7d %-12s%n",
                p.getProductId(),
                name,
                category,
                p.getPrice(),
                p.getMrp(),
                p.getDiscountPercent(),  // This already includes % in the header
                p.getStock(),
                status);
            
            totalValue += p.getPrice() * p.getStock();
        }
        System.out.println("=".repeat(120));
        System.out.printf("Total Products: %d | Total Inventory Value: ₹%.2f%n", 
            products.size(), totalValue);
    }
    
    // View seller's products - FIXED FORMATTING
    public void viewSellerProducts(int sellerId) {
        List<Product> products = productDAO.getProductsBySeller(sellerId);
        
        System.out.println("\n🏪 MY PRODUCTS");
        System.out.println("=".repeat(120));
        
        if (products.isEmpty()) {
            System.out.println("You haven't added any products yet.");
            return;
        }
        
        System.out.printf("%-6s %-25s %-18s %-12s %-12s %-7s %-7s %-12s%n",
            "ID", "PRODUCT NAME", "CATEGORY", "PRICE", "MRP", "DISC%", "STOCK", "STATUS");
        System.out.println("-".repeat(120));
        
        double totalValue = 0;
        for (Product p : products) {
            String status;
            if (p.getStock() == 0) {
                status = "❌ OUT";
            } else if (p.getStock() <= p.getThreshold()) {
                status = "⚠️ LOW";
            } else {
                status = "✅ IN";
            }
            
            String name = truncate(p.getName(), 25);
            String category = p.getCategoryName() != null ? truncate(p.getCategoryName(), 18) : "Unknown";
            
            // FIXED: Removed extra % sign
            System.out.printf("%-6d %-25s %-18s ₹%-11.2f ₹%-11.2f %-7d %-7d %-12s%n",
                p.getProductId(),
                name,
                category,
                p.getPrice(),
                p.getMrp(),
                p.getDiscountPercent(),
                p.getStock(),
                status);
            
            totalValue += p.getPrice() * p.getStock();
        }
        System.out.println("=".repeat(120));
        System.out.printf("Total: %d product(s) | Inventory Value: ₹%.2f%n", 
            products.size(), totalValue);
    }
    
    // Updated: Browse by category with better handling
    public void browseByCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            System.out.println("❌ Please enter a category name.");
            return;
        }
        
        // Trim and clean the category name
        categoryName = categoryName.trim();
        
        List<Product> products = productDAO.getProductsByCategory(categoryName);
        
        // If no products found with exact name, try case-insensitive
        if (products.isEmpty()) {
            // Get all products and filter by category name (case-insensitive)
            List<Product> allProducts = productDAO.getAllProducts();
            for (Product p : allProducts) {
                if (p.getCategoryName() != null && 
                    p.getCategoryName().trim().equalsIgnoreCase(categoryName)) {
                    products.add(p);
                }
            }
        }
        
        System.out.println("\n🏷️ PRODUCTS IN CATEGORY: " + categoryName.toUpperCase());
        System.out.println("=".repeat(100));
        
        if (products.isEmpty()) {
            System.out.println("No products found in this category.");
            
            // Show available categories
            showAvailableCategories();
            return;
        }
        
        System.out.printf("%-6s %-25s %-18s %-12s %-7s %-12s%n",
            "ID", "PRODUCT NAME", "BRAND", "PRICE", "STOCK", "STATUS");
        System.out.println("-".repeat(100));
        
        for (Product p : products) {
            String name = truncate(p.getName(), 25);
            String brand = p.getBrand() != null ? truncate(p.getBrand(), 18) : "";
            String status = p.getStock() == 0 ? "❌ OUT" : p.getStock() <= p.getThreshold() ? "⚠️ LOW" : "✅ IN";
            
            System.out.printf("%-6d %-25s %-18s ₹%-11.2f %-6d %-12s%n",
                p.getProductId(),
                name,
                brand,
                p.getPrice(),
                p.getStock(),
                status);
        }
        System.out.println("=".repeat(100));
        System.out.println("Found: " + products.size() + " product(s)");
    }
    
    // Add product with category selection
    public void addProduct(Scanner sc, int sellerId) {
        System.out.println("\n➕ ADD NEW PRODUCT");
        System.out.println("==================");
        
        try {
            // Show available categories
            showAvailableCategories();
            
            Product product = new Product();
            product.setSellerId(sellerId);
            
            // Get product details
            System.out.print("\nProduct Name: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("❌ Product name cannot be empty.");
                return;
            }
            product.setName(name);
            
            System.out.print("Description: ");
            product.setDescription(sc.nextLine().trim());
            
            // Get category ID
            System.out.print("Select Category ID from above list: ");
            try {
                int categoryId = Integer.parseInt(sc.nextLine().trim());
                product.setCategoryId(categoryId);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid category ID. Using default (1).");
                product.setCategoryId(1);
            }
            
            System.out.print("Brand: ");
            product.setBrand(sc.nextLine().trim());
            
            // Get price
            System.out.print("Price: ");
            try {
                double price = Double.parseDouble(sc.nextLine());
                if (price <= 0) {
                    System.out.println("❌ Price must be greater than 0.");
                    return;
                }
                product.setPrice(price);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid price. Must be a number.");
                return;
            }
            
            // Get MRP
            System.out.print("MRP: ");
            try {
                double mrp = Double.parseDouble(sc.nextLine());
                if (mrp < product.getPrice()) {
                    System.out.println("❌ MRP cannot be less than selling price.");
                    return;
                }
                product.setMrp(mrp);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid MRP. Must be a number.");
                return;
            }
            
            // Get stock
            System.out.print("Stock Quantity: ");
            try {
                int stock = Integer.parseInt(sc.nextLine());
                if (stock < 0) {
                    System.out.println("❌ Stock cannot be negative.");
                    return;
                }
                product.setStock(stock);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid stock quantity. Must be a number.");
                return;
            }
            
            // Get threshold
            System.out.print("Low Stock Threshold (default 5): ");
            String thresholdInput = sc.nextLine();
            int threshold = thresholdInput.isEmpty() ? 5 : Integer.parseInt(thresholdInput);
            if (threshold < 0) {
                System.out.println("❌ Threshold cannot be negative.");
                return;
            }
            product.setThreshold(threshold);
            
            // Calculate discount percent automatically
            if (product.getMrp() > product.getPrice()) {
                double discount = ((product.getMrp() - product.getPrice()) / product.getMrp()) * 100;
                product.setDiscountPercent((int) Math.round(discount));
            } else {
                product.setDiscountPercent(0);
            }
            
            // Add product
            boolean success = productDAO.addProduct(product);
            if (success) {
                System.out.println("\n✅ Product added successfully!");
                System.out.println("📝 Product Details:");
                System.out.println("   Name: " + product.getName());
                System.out.println("   Price: ₹" + product.getPrice());
                System.out.println("   MRP: ₹" + product.getMrp());
                System.out.println("   Discount: " + product.getDiscountPercent() + "%");
                System.out.println("   Stock: " + product.getStock());
            } else {
                System.out.println("❌ Failed to add product. Please try again.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error adding product: " + e.getMessage());
        }
    }
    
    // Delete product (soft delete)
    public void deleteProduct(Scanner sc, int sellerId) {
        System.out.println("\n🗑️ DELETE PRODUCT");
        System.out.println("=================");
        
        // Show seller's products first
        viewSellerProducts(sellerId);
        
        System.out.print("\nEnter Product ID to delete: ");
        String input = sc.nextLine().trim();
        
        if (input.isEmpty()) {
            System.out.println("❌ No product ID entered. Cancelling.");
            return;
        }
        
        try {
            int productId = Integer.parseInt(input);
            
            // Confirm deletion
            System.out.print("Are you sure you want to delete product ID " + productId + "? (yes/no): ");
            String confirm = sc.nextLine().trim().toLowerCase();
            
            if (!confirm.equals("yes")) {
                System.out.println("❌ Deletion cancelled.");
                return;
            }
            
            // Perform deletion
            boolean success = productDAO.deleteProduct(productId, sellerId);
            if (success) {
                System.out.println("✅ Product deleted successfully!");
                System.out.println("💡 Note: The product has been soft-deleted and will not appear in listings.");
            } else {
                System.out.println("❌ Failed to delete product.");
                System.out.println("   Possible reasons:");
                System.out.println("   1. Product ID doesn't exist");
                System.out.println("   2. You don't own this product");
                System.out.println("   3. Product is already deleted");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid product ID. Please enter a number.");
        } catch (Exception e) {
            System.out.println("❌ Error deleting product: " + e.getMessage());
        }
    }
    
    // Update product
    public void updateProduct(Scanner sc, int sellerId) {
        System.out.println("\n✏️ UPDATE PRODUCT");
        System.out.println("=================");
        
        // Show seller's products first
        viewSellerProducts(sellerId);
        
        System.out.print("\nEnter Product ID to update: ");
        String input = sc.nextLine().trim();
        
        if (input.isEmpty()) {
            System.out.println("❌ No product ID entered. Cancelling.");
            return;
        }
        
        try {
            int productId = Integer.parseInt(input);
            
            System.out.println("\nEnter new values:");
            
            // Get new price
            System.out.print("New Price: ");
            double price = Double.parseDouble(sc.nextLine());
            if (price <= 0) {
                System.out.println("❌ Price must be greater than 0.");
                return;
            }
            
            // Get new stock
            System.out.print("New Stock: ");
            int stock = Integer.parseInt(sc.nextLine());
            if (stock < 0) {
                System.out.println("❌ Stock cannot be negative.");
                return;
            }
            
            // Get new threshold
            System.out.print("New Threshold: ");
            int threshold = Integer.parseInt(sc.nextLine());
            if (threshold < 0) {
                System.out.println("❌ Threshold cannot be negative.");
                return;
            }
            
            // Update product
            boolean success = productDAO.updateProduct(productId, sellerId, price, stock, threshold);
            if (success) {
                System.out.println("\n✅ Product updated successfully!");
            } else {
                System.out.println("❌ Failed to update product.");
                System.out.println("   Possible reasons:");
                System.out.println("   1. Product doesn't exist");
                System.out.println("   2. You don't own this product");
                System.out.println("   3. Product is deleted");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter numbers only.");
        } catch (Exception e) {
            System.out.println("❌ Error updating product: " + e.getMessage());
        }
    }
    
    // Search products
    public void searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("❌ Please enter a search keyword.");
            return;
        }
        
        keyword = keyword.trim();
        List<Product> products = productDAO.searchProducts(keyword);
        
        System.out.println("\n🔍 SEARCH RESULTS for: '" + keyword + "'");
        System.out.println("=".repeat(100));
        
        if (products.isEmpty()) {
            System.out.println("No products found matching your search.");
            return;
        }
        
        System.out.printf("%-6s %-25s %-18s %-12s %-7s %-12s%n",
            "ID", "PRODUCT NAME", "CATEGORY", "PRICE", "STOCK", "STATUS");
        System.out.println("-".repeat(100));
        
        for (Product p : products) {
            String name = truncate(p.getName(), 25);
            String category = p.getCategoryName() != null ? truncate(p.getCategoryName(), 18) : "Unknown";
            String status = p.getStock() == 0 ? "❌ OUT" : p.getStock() <= p.getThreshold() ? "⚠️ LOW" : "✅ IN";
            
            System.out.printf("%-6d %-25s %-18s ₹%-11.2f %-6d %-12s%n",
                p.getProductId(),
                name,
                category,
                p.getPrice(),
                p.getStock(),
                status);
        }
        System.out.println("=".repeat(100));
        System.out.println("Found: " + products.size() + " product(s)");
    }
    
    // Check low stock
    public void checkLowStock(int sellerId) {
        List<Product> products = productDAO.getLowStockProducts(sellerId);
        
        System.out.println("\n⚠️ LOW STOCK ALERTS");
        System.out.println("=".repeat(100));
        
        if (products.isEmpty()) {
            System.out.println("✅ All products have sufficient stock.");
            return;
        }
        
        System.out.printf("%-6s %-25s %-18s %-9s %-9s %-15s%n",
            "ID", "PRODUCT NAME", "CATEGORY", "STOCK", "THRESHOLD", "NEED TO ORDER");
        System.out.println("-".repeat(100));
        
        int totalToOrder = 0;
        for (Product p : products) {
            String name = truncate(p.getName(), 25);
            String category = p.getCategoryName() != null ? truncate(p.getCategoryName(), 18) : "Unknown";
            int needToOrder = Math.max(0, p.getThreshold() * 2 - p.getStock());
            totalToOrder += needToOrder;
            
            System.out.printf("%-6d %-25s %-18s %-8d %-8d %-14d%n",
                p.getProductId(),
                name,
                category,
                p.getStock(),
                p.getThreshold(),
                needToOrder);
        }
        System.out.println("=".repeat(100));
        System.out.printf("⚠️ Total: %d product(s) need restocking | Suggested order quantity: %d units%n", 
            products.size(), totalToOrder);
    }
    
    // View sales report
    public void viewSalesReport(int sellerId) {
        try {
            ResultSet rs = productDAO.getSalesReport(sellerId);
            
            System.out.println("\n📊 SALES REPORT");
            System.out.println("=".repeat(80));
            System.out.printf("%-25s %-20s %-10s %-15s%n", 
                "PRODUCT", "CATEGORY", "SOLD", "REVENUE");
            System.out.println("-".repeat(80));
            
            double totalRevenue = 0;
            int totalSold = 0;
            boolean hasSales = false;
            
            while (rs != null && rs.next()) {
                hasSales = true;
                double revenue = rs.getDouble("revenue");
                int sold = rs.getInt("total_sold");
                totalRevenue += revenue;
                totalSold += sold;
                
                String productName = truncate(rs.getString("name"), 25);
                String category = truncate(rs.getString("category_name"), 20);
                
                System.out.printf("%-25s %-20s %-9d ₹%-13.2f%n",
                    productName != null ? productName : "Unknown",
                    category != null ? category : "N/A",
                    sold,
                    revenue);
            }
            
            if (!hasSales) {
                System.out.println("No sales yet.");
            } else {
                System.out.println("-".repeat(80));
                System.out.printf("%-25s %-20s %-9d ₹%-13.2f%n",
                    "TOTAL", "", totalSold, totalRevenue);
            }
            
            if (rs != null) {
                rs.close();
            }
            
        } catch (Exception e) {
            System.out.println("Error generating sales report: " + e.getMessage());
        }
    }
    
    // Helper method to truncate text
    private String truncate(String text, int length) {
        if (text == null) return "";
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }
}