package com.revolut.accountservice.service;


public interface AccountService
{
	String getAccount(String accountId);
	
	void transfer(String accountSenderId, String accountReceiverId, String amount);
}
