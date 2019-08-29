package com.revolut.accountservice.service.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revolut.accountservice.model.Account;

import java.util.Optional;

public class TransferPayload implements Validable {
    private long senderAccountId;
    private long receiverAccountId;
    private long amount;
    private String errorMessage;

    @Override
    public boolean isValid() {
        return errorMessage == null;
    }

    @Override
    public Optional<String> gerErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }


    public TransferPayload(@JsonProperty("senderAccountId") String senderAccountId,
                           @JsonProperty("receiverAccountId") String receiverAccountId,
                           @JsonProperty("amount") String amount) {
        if (amount == null) {
            errorMessage = "amount is null!";
        } else {
            try {
                this.senderAccountId = Account.parseAccountId(senderAccountId);
                this.receiverAccountId = Account.parseAccountId(receiverAccountId);
                double doubleAmount = Double.parseDouble(amount) * 100;
                this.amount = (long) doubleAmount;

                if (this.amount <= 0) {
                    errorMessage = "Amount should be more than zero!";
                } else if (doubleAmount - this.amount != 0) {
                    errorMessage = "Wrong amount format, too much digits after point!";
                } else if (this.receiverAccountId == this.senderAccountId) {
                    errorMessage = "Transfer to the same account is not allowed!";
                }
            } catch (IllegalArgumentException e) {
                errorMessage = e.toString();
            }
        }
    }

    public long getSenderAccountId() {
        return senderAccountId;
    }

    public long getReceiverAccountId() {
        return receiverAccountId;
    }

    public long getAmount() {
        return amount;
    }
}
