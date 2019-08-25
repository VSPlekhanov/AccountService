package com.revolut.accountservice.dao;

import com.revolut.accountservice.model.Account;

import java.math.BigDecimal;

public interface AccountDAO
{
	Account getAccount(long accountId);
	
	void transfer(long senderId, long receiverId, BigDecimal amount);
}
