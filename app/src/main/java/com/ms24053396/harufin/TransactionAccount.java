package com.ms24053396.harufin;

import java.util.List;

class Transaction {
    String TransactionId;
    String sourceUserName;
    String destUserName;
    Float amount;

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getEpisodeNumber() {
        return destUserName;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.destUserName = episodeNumber;
    }

    public String getSourceUserName() {
        return sourceUserName;
    }

    public void setSourceUserName(String sourceUserName) {
        this.sourceUserName = sourceUserName;
    }
}

public class TransactionAccount {

    String accountID;
    String name;
    //Integer episodeCount = 0;
    List<Transaction> transactions;
    Double balance;
    String description;

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Integer getEpisodeCount() {
//        return episodeCount;
//    }
//
//    public void setEpisodeCount(Integer episodeCount) {
//        this.episodeCount = episodeCount;
//    }

    public List<Transaction> getEpisodes() {
        return transactions;
    }

    public void setEpisodes(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
