package com.revolut.accountservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.model.Account;

public class AccountServiceImpl implements AccountService
{
	private final AccountDAO accountDAO;
	
	public AccountServiceImpl(AccountDAO accountDAO)
	{
		this.accountDAO = accountDAO;
	}
	
	@Override public String getAccount(String accountId)
	{
		int id = Integer.parseInt(accountId);
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
}
