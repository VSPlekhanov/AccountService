package com.revolut.accountservice.service.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.util.Constants;

import java.util.Optional;

public class TransferPayload implements Validable {
    public static final String REGEXP = "\\d+(.\\d{1,2})?";
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
        } else if (!amount.matches(REGEXP)){
            errorMessage = "wrong format of the amount value (no more than two digits after point is allowed)";
        } else {
            try {
                this.senderAccountId = Account.parseAccountId(senderAccountId);
                this.receiverAccountId = Account.parseAccountId(receiverAccountId);
                this.amount = Account.toDataBaseFormat((Double.parseDouble(amount)));

                if (this.amount <= 0) {
                    errorMessage = "Amount should be more than zero!";
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
