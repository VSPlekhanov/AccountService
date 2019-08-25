package com.revolut.accountservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.model.Account;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService
{
	private final AccountDAO accountDAO;
	
	public AccountServiceImpl(AccountDAO accountDAO)
	{
		this.accountDAO = accountDAO;
	}
	
	@Override public String getAccount(String accountId)
	{
		long id = Long.parseLong(accountId);
		Account account = accountDAO.getAccount(id);
		ObjectMapper mapper = new ObjectMapper();
		try
		{
			return mapper.writeValueAsString(account);
		} catch(JsonProcessingException e)
		{
			// TODO: 8/24/2019 handle the errors
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void transfer(String accountSenderId, String accountReceiverId, String transferAmount)
	{
		long senderId = Long.parseLong(accountSenderId);
		long receiverId = Long.parseLong(accountReceiverId);
		BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(transferAmount));
		accountDAO.transfer(senderId, receiverId, amount);
	}
}
