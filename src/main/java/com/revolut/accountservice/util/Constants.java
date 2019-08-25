package com.revolut.accountservice.util;

public class Constants
{
	public static final String DATABASE_URL = "jdbc:sqlite:memory:DB";
	
	public static final String GET_ACCOUNT_BY_ID = "SELECT * FROM account WHERE id = ?";
	
	public static final String CREATE_ACCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS account (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"balance INTEGER NOT NULL DEFAULT 0)";
	
	public static final String DROP_TABLE_ACCOUNTS = "DROP TABLE account";
	
	public static final String INSERT_INTO_ACCOUNT = "INSERT INTO account(balance) VALUES(?)";
	
	public static final String UPDATE_ACCOUNT_BY_ID = "UPDATE account SET balance = ? WHERE id = ?";
}
