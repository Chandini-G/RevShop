package com.revshop.model;

public class Review {
    private int productId;
    private int buyerId;
    private int rating;
    private String comment;

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
