package com.revolut.accountservice.service;

import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.exception.NoSuchAccountException;
import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.service.payload.EmptyPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class GetAccountHandler extends AbstractRequestHandler<EmptyPayload>
{
	private static final Logger log = LoggerFactory.getLogger(GetAccountHandler.class);
	
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
				log.warn("Failed to get accountId from given queryParams");
				return new Answer(HTTP_BAD_REQUEST, "accountId is null!");
			}
			Account account = accountDAO.getAccount(Long.parseLong(accountId));
			return new Answer(HTTP_OK, accountToJson(account));
		} catch(NumberFormatException e)
		{
			log.warn("Failed to parse accountId from given queryParams");
			return new Answer(HTTP_BAD_REQUEST, "Cannot parse int value of accountId : " + accountId);
		} catch(NoSuchAccountException e)
		{
			return new Answer(HTTP_BAD_REQUEST, e.toString());
		} catch(Throwable e)
		{
			log.error(e.toString());
			return new Answer(HTTP_SERVER_ERROR);
		}
	}
}
