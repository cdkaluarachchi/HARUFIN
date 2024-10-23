package com.ms24053396.harufin;

class Transaction {
    String dte;
    Long accountId;
    String TransactionId;
    String sourceUserName;
    String destUserName;
    Long amount;
    public Transaction(){
    }
    public Transaction(String dte, Long accountId, String transactionId, String sourceUserName, String destUserName, Long amount) {
        this.dte = dte;
        this.accountId = accountId;
        this.TransactionId = transactionId;
        this.sourceUserName = sourceUserName;
        this.destUserName = destUserName;
        this.amount = amount;
    }

    public String getDte() {
        return dte;
    }

    public void setDte(String dte) {
        this.dte = dte;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getDestUserName() {
        return destUserName;
    }

    public void setDestUserName(String episodeNumber) {
        this.destUserName = episodeNumber;
    }

    public String getSourceUserName() {
        return sourceUserName;
    }

    public void setSourceUserName(String sourceUserName) {
        this.sourceUserName = sourceUserName;
    }
}

