package wts.com.accountpe.models;

public class AddMoneyReportModel {

    String name, openingBal, amount, commission, surcharge, payAbleAmount, closingBal, uniqueTxnId, txnID, status, createdOn, orderId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpeningBal() {
        return openingBal;
    }

    public void setOpeningBal(String openingBal) {
        this.openingBal = openingBal;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(String surcharge) {
        this.surcharge = surcharge;
    }

    public String getClosingBal() {
        return closingBal;
    }

    public void setClosingBal(String closingBal) {
        this.closingBal = closingBal;
    }

    public String getUniqueTxnId() {
        return uniqueTxnId;
    }

    public void setUniqueTxnId(String uniqueTxnId) {
        this.uniqueTxnId = uniqueTxnId;
    }

    public String getTxnID() {
        return txnID;
    }

    public void setTxnID(String txnID) {
        this.txnID = txnID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getPayAbleAmount() {
        return payAbleAmount;
    }

    public void setPayAbleAmount(String payAbleAmount) {
        this.payAbleAmount = payAbleAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
