package com.revolut.accountservice.dao;

import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.util.Constants;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAOImpl implements AccountDAO
{
	private final DataSource dataSource;
	
	public AccountDAOImpl(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}
	
	@Override public Account getAccount(int accountId)
	{
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(Constants.GET_ACCOUNT_BY_ID))
		{
			preparedStatement.setInt(1, accountId);
			try(ResultSet resultSet = preparedStatement.executeQuery()){
				resultSet.next();
				return new Account(resultSet);
			}
			
			
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
