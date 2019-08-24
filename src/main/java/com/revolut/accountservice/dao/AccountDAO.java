package com.revolut.accountservice.dao;

import com.revolut.accountservice.model.Account;

public interface AccountDAO
{
	Account getAccount(int accountId);
}
