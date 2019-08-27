package com.revolut.accountservice.service;

/*
 * Copyright 2001-2019 by HireRight, Inc. All rights reserved.
 * This software is the confidential and proprietary information
 * of HireRight, Inc. Use is subject to license terms.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.model.EmptyPayload;
import com.revolut.accountservice.service.AbstractRequestHandler;

import java.util.Map;

public class GetAccountHandler extends AbstractRequestHandler<EmptyPayload>
{
	public GetAccountHandler(AccountDAO accountDAO)
	{
		super(EmptyPayload.class, accountDAO);
	}
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> queryParams, boolean shouldReturnHtml)
	{
		try
		{
			Account account = accountDAO.getAccount(Long.parseLong(queryParams.get("accountId")));
			ObjectMapper mapper = new ObjectMapper();
			return new Answer(200, mapper.writeValueAsString(account));
		}catch(Exception e){
			return new Answer(400, e.getMessage());
		}
	}
}
