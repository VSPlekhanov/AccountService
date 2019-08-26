package com.revolut.accountservice.service;


public interface AccountService
{
	String getAccount(String accountId) throws Exception;
	
	void transfer(String accountSenderId, String accountReceiverId, String amount) throws Exception;
}
