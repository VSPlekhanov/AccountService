package com.revolut.accountservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revolut.accountservice.util.Util;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account
{
	@JsonProperty("id")
	private final long id;
	private final long balance;
	
	public Account(long id, long balance)
	{
		this.id = id;
		this.balance = balance;
	}
	
	public Account(ResultSet resultSet) throws SQLException
	{
		id = resultSet.getInt(1);
		balance = resultSet.getLong(2);
	}
	
	public long getId()
	{
		return id;
	}
	
	public long getLongBalance()
	{
		return balance;
	}
	
	@JsonProperty("balance")
	public BigDecimal getBalance(){
		return BigDecimal.valueOf(balance).divide(BigDecimal.valueOf(100));
	}
}
