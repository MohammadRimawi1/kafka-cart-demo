package com.exalt.cart_service.model;

/**
 *
 * @author Mohammad Rimawi
 */
public class CartItem {
    private int productId;
    private int quantity;

    /**
     * Default constructor
     */
    public CartItem() { }

//    ==== GETTERS ====
    /**
     * a method for getting the product Id
     * @return
     */
    public int getProductId() {
        return productId;
    }

    /**
     * a method for getting the quantity
     * @return
     */
    public int getQuantity() {
        return quantity;
    }
//    ==== GETTERS ====

//    ==== SETTERS ====
    /**
     * a method for setting the product Id
     * @param productId
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * a method for setting the quantity
     * @param quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
//    ==== SETTERS ====

    @Override
    public String toString() {
        return "CartItem{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
