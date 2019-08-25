package com.revolut.accountservice.controller;

import com.revolut.accountservice.service.AccountService;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public class AccountController
{
	private final AccountService accountService;
	
	public AccountController(AccountService accountService)
	{
		this.accountService = accountService;
		setupRoutes();
	}
	
	private void setupRoutes()
	{
		get("/:accountId", (req, res) -> {
			String accountId = req.params(":accountId");
			return accountService.getAccount(accountId);
		});
		
		
		put("/:accountSenderId/:accountReceiverId", (req, res) -> {
			accountService.transfer(
					req.params(":accountSenderId"),
					req.params(":accountReceiverId"),
					req.queryParams("amount"));
			return null;
		});
	}
}
