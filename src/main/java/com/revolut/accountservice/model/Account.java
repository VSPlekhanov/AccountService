package com.revolut.accountservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    @JsonProperty("id")
    private final long id;
    @JsonIgnore
    private final long balance;

    public Account(long id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    public Account(ResultSet resultSet) throws SQLException {
        id = resultSet.getLong("id");
        balance = resultSet.getLong("balance");
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public static long parseAccountId(String accountId){
        if (accountId == null){
            throw new IllegalArgumentException("accountId is null!");
        }
        return Long.parseLong(accountId);
    }

    public long getId() {
        return id;
    }

    @JsonIgnore
    public long getBalanceInDatabaseFormat() {
        return balance;
    }

    @JsonProperty("balance")
    public BigDecimal getBalance() {
        return BigDecimal.valueOf(balance).divide(BigDecimal.valueOf(100));
    }
}
