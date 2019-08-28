package com.revolut.accountservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.model.Validable;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRequestHandler<V extends Validable>
		implements RequestHandler<V>, Route
{
	private Class<V> valueClass;
	protected AccountDAO accountDAO;
	
	private static final int HTTP_BAD_REQUEST = 400;
	
	public AbstractRequestHandler(Class<V> valueClass, AccountDAO accountDAO)
	{
		this.valueClass = valueClass;
		this.accountDAO = accountDAO;
	}
	
	public static String accountToJson(Account account) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(account);
	}
	
	public final Answer process(V value, Map<String, String> queryParams)
	{
		if(!value.isValid())
		{
			return new Answer(HTTP_BAD_REQUEST, value.gerErrorMessage().orElse(""));
		}
		else
		{
			return processImpl(value, queryParams);
		}
	}
	
	protected abstract Answer processImpl(V value, Map<String, String> queryParams);
	
	
	@Override
	public Object handle(Request request, Response response) throws Exception
	{
		ObjectMapper objectMapper = new ObjectMapper();
		V value = objectMapper.readValue(request.body(), valueClass);
		Map<String, String> queryParams = new HashMap<>();
		Answer answer = process(value, queryParams);
		response.status(answer.getCode());
		response.type("application/json");
		response.body(answer.getBody());
		return answer.getBody();
	}
	
}
