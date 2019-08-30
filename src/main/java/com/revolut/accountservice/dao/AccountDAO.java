package com.revolut.accountservice.dao;

import com.revolut.accountservice.exception.InsufficientFundsException;
import com.revolut.accountservice.exception.NoSuchAccountException;
import com.revolut.accountservice.model.Account;

import java.sql.SQLException;

public interface AccountDAO {

    /**
     * Returns the account wiht the given id
     * @param accountId - id of the account
     * @return an Account with the given id
     * @throws SQLException - if some errors with database occurs
     * @throws NoSuchAccountException - if there is no account with this id in the database
     */
    Account getAccount(long accountId) throws SQLException;

    /**
     * Commit a transfer between two given accounts with given amount
     * @param senderId - sender account id
     * @param receiverId - receiver account id
     * @param amount - amount of money to transfer
     * @throws SQLException - if some errors with database occurs
     * @throws InsufficientFundsException - if there is not enough funds on the sender account
     * @throws NoSuchAccountException - if one of the accounts not found in the database
     */
    void transfer(long senderId, long receiverId, long amount) throws SQLException;
}
