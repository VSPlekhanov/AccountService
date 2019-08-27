package com.revolut.accountservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.ConstructorProperties;
import java.util.Optional;

public class TransferPayload implements Validable
{
	private long senderAccountId;
	private long receiverAccountId;
	private long amount;
	private String errorMessage;
	
	@Override public boolean isValid()
	{
		if (errorMessage != null){
			return false;
		}
		if(amount <= 0)
		{
			errorMessage = "Amount should be more than zero!";
			return false;
		}
		if(receiverAccountId == senderAccountId)
		{
			errorMessage = "Transfer to the same account is not allowed!";
			return false;
		}
		return true;
	}
	
	@Override public Optional<String> gerErrorMessage()
	{
		return Optional.ofNullable(errorMessage);
	}
	
	
	@ConstructorProperties({"senderAccountId", "receiverAccountId", "amount"})
	public TransferPayload(String senderAccountId, String receiverAccountId, String amount)
	{
		try
		{
			this.senderAccountId = Long.parseLong(senderAccountId);
			this.receiverAccountId = Long.parseLong(receiverAccountId);
			double doubleAmount = Double.parseDouble(amount) * 100;
			this.amount = (long) doubleAmount;
			
			if(doubleAmount - this.amount != 0)
			{
				errorMessage = "Wrong amount format, too much digits after point!";
			}
		} catch(NumberFormatException e)
		{
			errorMessage = e.getMessage();
		}
	}
	
	public long getSenderAccountId()
	{
		return senderAccountId;
	}
	
	public long getReceiverAccountId()
	{
		return receiverAccountId;
	}
	
	public long getAmount()
	{
		return amount;
	}
}