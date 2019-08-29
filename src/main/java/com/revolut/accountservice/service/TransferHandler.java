package com.revolut.accountservice.service;

import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.service.payload.TransferPayload;

import java.sql.SQLException;
import java.util.Map;

public class TransferHandler extends AbstractRequestHandler<TransferPayload>
{
	public TransferHandler(AccountDAO accountDAO)
	{
		super(TransferPayload.class, accountDAO);
	}
	
	@Override
	protected Answer processImpl(TransferPayload value, Map<String, String> queryParams)
	{
		try
		{
			accountDAO.transfer(value.getSenderAccountId(), value.getReceiverAccountId(),
					value.getAmount());
		} catch(SQLException e)
		{
			return new Answer(500);
		} catch(Exception e)
		{
			return new Answer(400, String.format("%s : %s", e.getClass().getName(), e.getMessage()));
		}
		return new Answer(204);
	}
}
