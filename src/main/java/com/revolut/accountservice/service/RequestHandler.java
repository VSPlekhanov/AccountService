package com.revolut.accountservice.service;

import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.model.Validable;

import java.util.Map;

public interface RequestHandler<V extends Validable>
{
	Answer process(V value, Map<String, String> urlParams);
}
