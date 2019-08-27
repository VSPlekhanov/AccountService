package com.revolut.accountservice.dao;

import com.revolut.accountservice.model.Account;

import java.math.BigDecimal;

public interface AccountDAO
{
	Account getAccount(long accountId) throws Exception;
	
	void transfer(long senderId, long receiverId, long amount) throws Exception;
}
