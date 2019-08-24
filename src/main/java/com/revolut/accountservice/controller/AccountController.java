package com.revolut.accountservice.controller;

import com.revolut.accountservice.service.AccountService;

import static spark.Spark.get;

public class AccountController
{
	private AccountService accountService;
	
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
	}
}
