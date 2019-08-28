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
		try
		{
			Account account = accountDAO.getAccount(Long.parseLong(queryParams.get("accountId")));
			return new Answer(200, accountToJson(account));
		}catch(Exception e){
			return new Answer(400, e.getMessage());
		}
	}
}
