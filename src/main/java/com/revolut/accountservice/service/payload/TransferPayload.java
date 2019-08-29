package com.revolut.accountservice.service.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class TransferPayload implements Validable
{
	private long senderAccountId;
	private long receiverAccountId;
	private long amount;
	private String errorMessage;
	
	@Override public boolean isValid()
	{
		return errorMessage == null;
	}
	
	@Override public Optional<String> gerErrorMessage()
	{
		return Optional.ofNullable(errorMessage);
	}
	
	
	public TransferPayload(@JsonProperty("senderAccountId") String senderAccountId,
			@JsonProperty("receiverAccountId") String receiverAccountId,
			@JsonProperty("amount") String amount)
	{
		if(senderAccountId == null)
		{
			errorMessage = "senderAccountId is null!";
		}
		else if(receiverAccountId == null)
		{
			errorMessage = "receiverAccountId is null!";
		}
		else if(amount == null)
		{
			errorMessage = "amount is null!";
		}
		else
		{
			try
			{
				this.senderAccountId = Long.parseLong(senderAccountId);
				this.receiverAccountId = Long.parseLong(receiverAccountId);
				double doubleAmount = Double.parseDouble(amount) * 100;
				this.amount = (long) doubleAmount;
				
				if(this.amount <= 0)
				{
					errorMessage = "Amount should be more than zero!";
				}
				else if(doubleAmount - this.amount != 0)
				{
					errorMessage = "Wrong amount format, too much digits after point!";
				}
				else if(this.receiverAccountId == this.senderAccountId)
				{
					errorMessage = "Transfer to the same account is not allowed!";
				}
			} catch(NumberFormatException e)
			{
				errorMessage = e.toString();
			}
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
