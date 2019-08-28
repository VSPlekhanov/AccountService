package com.revolut.accountservice.service;

import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.model.EmptyPayload;

import java.util.Map;

public class GetAccountHandler extends AbstractRequestHandler<EmptyPayload>
{
	public GetAccountHandler(AccountDAO accountDAO)
	{
		super(EmptyPayload.class, accountDAO);
	}
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> queryParams)
	{
		String accountId = null;
		try
		{
			accountId = queryParams.get("accountId");
			if(accountId == null)
			{
				return new Answer(400, "accountId is null!");
			}
			Account account = accountDAO.getAccount(Long.parseLong(accountId));
			return new Answer(200, accountToJson(account));
		} catch(NumberFormatException e)
		{
			return new Answer(400, String.format("Cannot parse int value of accountId : %s", accountId));
		} catch(Exception e)
		{
			return new Answer(400, String.format("%s : %s", e.getClass().getName(), e.getMessage()));
		}
	}
}
