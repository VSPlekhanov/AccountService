package com.revolut.accountservice.dao;

import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.util.Constants;
import com.revolut.accountservice.util.Util;

import javax.sql.DataSource;
import java.math.BigDecimal;
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
	
	@Override public Account getAccount(long accountId)
	{
		try(Connection connection = dataSource.getConnection())
		{
			return getAccount(accountId, connection);
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	@Override public void transfer(long senderId, long receiverId, BigDecimal amount)
	{
		try(Connection connection = dataSource.getConnection();
				PreparedStatement updateSenderAccount = connection.prepareStatement(Constants.UPDATE_ACCOUNT_BY_ID);
				PreparedStatement updateReceiverAccount = connection.prepareStatement(Constants.UPDATE_ACCOUNT_BY_ID))
		{
			// TODO: 8/25/2019 haandle the errors
//			connection.setAutoCommit(false);
			Account sender = getAccount(senderId);
			long senderNewBalance = Util.parseBigDecimalValueToDatabaseFormat(sender.getBalance().subtract(amount));
			
			if(senderNewBalance < 0){
				throw new IllegalStateException("insufficient funds for the transaction");
			}
			Account receiver = getAccount(receiverId);
			long receiverNewBalance = Util.parseBigDecimalValueToDatabaseFormat(receiver.getBalance().add(amount));
			
			updateSenderAccount.setLong(1, senderNewBalance);
			updateSenderAccount.setLong(2, senderId);
			
			updateReceiverAccount.setLong(1, receiverNewBalance);
			updateReceiverAccount.setLong(2, receiverId);
			
			updateSenderAccount.execute();
			updateReceiverAccount.execute();
			
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private Account getAccount(long accountId, Connection connection){
		try(PreparedStatement preparedStatement = connection.prepareStatement(Constants.GET_ACCOUNT_BY_ID))
		{
			preparedStatement.setLong(1, accountId);
			try(ResultSet resultSet = preparedStatement.executeQuery())
			{
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
