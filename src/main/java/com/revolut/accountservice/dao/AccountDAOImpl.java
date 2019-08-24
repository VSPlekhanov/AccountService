package com.revolut.accountservice.dao;

import com.revolut.accountservice.model.Account;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAOImpl implements AccountDAO
{
	private static final String GET_ACCOUNT_BY_ID = "SELECT * FROM account WHERE id = ?";
	
	private DataSource dataSource;
	
	@Override public Account getAccount(int accountId)
	{
		try(Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_ACCOUNT_BY_ID))
		{
			preparedStatement.setInt(1, accountId);
			try(ResultSet resultSet = preparedStatement.executeQuery()){
				resultSet.next();
				
			}
			
			
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
