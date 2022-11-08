package com.techelevator;

public class Vendable {

    //product properties
    private String slotLocation; //product's slot location in the vending machine
    private String productName; //name of the product
    private double purchasePrice; //how much the product costs per unit
    private String productType; //type of product
    private int numberInStock; //quantity stocked in the vending machine

    //constructor initializes each property of the product
    public Vendable(String slotLocation, String productName, double purchasePrice, String productType, int numberInStock) {
        this.slotLocation = slotLocation;
        this.productName = productName;
        this.purchasePrice = purchasePrice;
        this.productType = productType;
        this.numberInStock = numberInStock;
    }

    //getters and setters for each property

    public String getSlotLocation() {
        return slotLocation;
    }

    public void setSlotLocation(String slotLocation) {
        this.slotLocation = slotLocation;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public int getNumberInStock() {
        return numberInStock;
    }

    public void setNumberInStock(int numberInStock) {
        this.numberInStock = numberInStock;
    }
}
