package com.revshop.model;

public class Product {
    private int productId;
    private int sellerId;
    private String name;
    private String description;
    private int categoryId;
    private String categoryName;  // NEW FIELD
    private String brand;
    private double price;
    private double mrp;
    private int stock;
    private int threshold;
    private int discountPercent;
    private String isActive;      // NEW FIELD
    private String imageUrl;
    private double ratingAvg;
    private int totalRatings;

    // Constructors
    public Product() {
        this.discountPercent = 0;
        this.isActive = "Y";
    }
    
    public Product(int productId, int sellerId, String name, double price, int stock) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.discountPercent = 0;
        this.isActive = "Y";
    }

    // Getters
    public int getProductId() { return productId; }
    public int getSellerId() { return sellerId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }  // NEW GETTER
    public String getBrand() { return brand; }
    public double getPrice() { return price; }
    public double getMrp() { return mrp; }
    public int getStock() { return stock; }
    public int getThreshold() { return threshold; }
    public int getDiscountPercent() { return discountPercent; }
    public String getIsActive() { return isActive; }          // NEW GETTER
    public String getImageUrl() { return imageUrl; }
    public double getRatingAvg() { return ratingAvg; }
    public int getTotalRatings() { return totalRatings; }

    // Setters
    public void setProductId(int productId) { this.productId = productId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }  // NEW SETTER
    public void setBrand(String brand) { this.brand = brand; }
    public void setPrice(double price) { this.price = price; }
    public void setMrp(double mrp) { this.mrp = mrp; }
    public void setStock(int stock) { this.stock = stock; }
    public void setThreshold(int threshold) { this.threshold = threshold; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }
    public void setIsActive(String isActive) { this.isActive = isActive; }  // NEW SETTER
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setRatingAvg(double ratingAvg) { this.ratingAvg = ratingAvg; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
    
    // Helper methods
    public boolean isActive() {
        return "Y".equalsIgnoreCase(isActive);
    }
    
    public boolean isLowStock() {
        return stock <= threshold;
    }
    
    public boolean isOutOfStock() {
        return stock == 0;
    }
    
    public double getDiscountedPrice() {
        if (discountPercent > 0) {
            return price * (100 - discountPercent) / 100;
        }
        return price;
    }
    
    public double getTotalValue() {
        return price * stock;
    }
    
    @Override
    public String toString() {
        return String.format("Product[ID: %d, Name: %s, Price: ₹%.2f, Stock: %d, Active: %s]", 
            productId, name, price, stock, isActive);
    }
}