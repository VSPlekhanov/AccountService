package com.revolut.accountservice.controller;

import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.service.GetAccountHandler;
import com.revolut.accountservice.service.TransferHandler;

import static spark.Spark.get;
import static spark.Spark.post;

public class AccountController
{
	private final AccountDAO accountDAO;
	
	public AccountController(AccountDAO accountDAO)
	{
		this.accountDAO = accountDAO;
		setupRoutes();
	}
	
	private void setupRoutes()
	{
		get("/:accountId", new GetAccountHandler(accountDAO));
		
		post("/transfer", new TransferHandler(accountDAO));
	}
}
