package com.revolut.accountservice.dao;

import com.revolut.accountservice.model.Account;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

public interface AccountDAO
{
	Account getAccount(long accountId) throws SQLException;
	
	void transfer(long senderId, long receiverId, long amount) throws SQLException;
}
