package com.revolut.accountservice.model;

import java.util.Optional;

public class EmptyPayload implements Validable
{
	@Override public boolean isValid()
	{
		return true;
	}
	
	@Override public Optional<String> gerErrorMessage()
	{
		return Optional.empty();
	}
}
