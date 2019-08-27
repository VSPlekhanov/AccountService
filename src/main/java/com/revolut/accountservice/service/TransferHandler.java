package com.revolut.accountservice.service;

/*
 * Copyright 2001-2019 by HireRight, Inc. All rights reserved.
 * This software is the confidential and proprietary information
 * of HireRight, Inc. Use is subject to license terms.
 */

import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.model.TransferPayload;
import com.revolut.accountservice.service.AbstractRequestHandler;

import java.util.Map;

public class TransferHandler extends AbstractRequestHandler<TransferPayload>
{
	public TransferHandler(AccountDAO accountDAO)
	{
		super(TransferPayload.class, accountDAO);
	}
	
	@Override
	protected Answer processImpl(TransferPayload value, Map<String, String> queryParams, boolean shouldReturnHtml)
	{
		try
		{
			accountDAO.transfer(value.getSenderAccountId(), value.getReceiverAccountId(), value.getAmount());
		}catch(Exception e){
			return new Answer(400, e.getMessage());
		}
		return new Answer(204);
	}
}
