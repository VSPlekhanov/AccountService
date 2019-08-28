package com.revolut.accountservice.service.payload;

/*
 * Copyright 2001-2019 by HireRight, Inc. All rights reserved.
 * This software is the confidential and proprietary information
 * of HireRight, Inc. Use is subject to license terms.
 */

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
