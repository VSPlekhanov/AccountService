package com.revolut.accountservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.service.payload.Validable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public abstract class AbstractRequestHandler<V extends Validable>
		implements RequestHandler<V>, Route
{
	private static final Logger log = LoggerFactory.getLogger(AbstractRequestHandler.class);
	
	private Class<V> valueClass;
	protected AccountDAO accountDAO;
	
	public static final int HTTP_BAD_REQUEST = 400;
	public static final int HTTP_OK = 200;
	public static final int HTTP_OK_WITH_NO_BODY = 204;
	public static final int HTTP_SERVER_ERROR = 500;
	public static final String CONTENT_TYPE = "application/json";
	
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
			String errorMessage = value.gerErrorMessage().orElse("");
			log.warn(errorMessage);
			return new Answer(HTTP_BAD_REQUEST, errorMessage);
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
		Map<String, String> queryParams = request.params();
		Answer answer = process(value, queryParams);
		response.status(answer.getCode());
		response.type(CONTENT_TYPE);
		response.body(answer.getBody());
		return answer.getBody();
	}
	
}
