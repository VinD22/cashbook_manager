package model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Model class for managing expense!
 */

public class Transaction extends RealmObject {

    private String transactionDetails;
    private double amount;
    private boolean isCredited;
    private int id;
    private String addtionalData;
    private Date date;

    public String getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(String transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isCredited() {
        return isCredited;
    }

    public void setCredited(boolean credited) {
        isCredited = credited;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddtionalData() {
        return addtionalData;
    }

    public void setAddtionalData(String addtionalData) {
        this.addtionalData = addtionalData;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
