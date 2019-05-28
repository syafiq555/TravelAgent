/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author User
 */
public class Customer {
    private String customerName, destination, date;
    private float budgetPrice, depositPrice;
    
    public Customer(){}
    
    /**
     * @return the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName the customerName to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the budgetPrice
     */
    public float getBudgetPrice() {
        return budgetPrice;
    }

    /**
     * @param budgetPrice the budgetPrice to set
     */
    public void setBudgetPrice(float budgetPrice) {
        this.budgetPrice = budgetPrice;
    }

    /**
     * @return the depositPrice
     */
    public float getDepositPrice() {
        return depositPrice;
    }

    /**
     * @param depositPrice the depositPrice to set
     */
    public void setDepositPrice(float depositPrice) {
        this.depositPrice = depositPrice;
    }
}
