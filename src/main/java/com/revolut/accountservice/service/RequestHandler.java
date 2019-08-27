package com.revolut.accountservice.service;

/*
 * Copyright 2001-2019 by HireRight, Inc. All rights reserved.
 * This software is the confidential and proprietary information
 * of HireRight, Inc. Use is subject to license terms.
 */

import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.model.Validable;

import java.util.Map;

public interface RequestHandler<V extends Validable>
{
	Answer process(V value, Map<String, String> urlParams, boolean shouldReturnHtml);
}
