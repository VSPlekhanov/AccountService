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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AccountDAOImpl implements AccountDAO
{
	private final DataSource dataSource;
	private final Lock readLock;
	private final Lock writeLock;
	
	public AccountDAOImpl(DataSource dataSource)
	{
		this.dataSource = dataSource;
		ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
		readLock = reentrantReadWriteLock.readLock();
		writeLock = reentrantReadWriteLock.writeLock();
	}
	
	@Override public Account getAccount(long accountId) throws Exception
	{
		readLock.lock();
		try(Connection connection = dataSource.getConnection())
		{
			return getAccount(accountId, connection);
		} finally
		{
			readLock.unlock();
		}
	}
	
	@Override public void transfer(long senderId, long receiverId, BigDecimal amount) throws Exception
	{
		try(Connection connection = dataSource.getConnection();
				PreparedStatement updateSenderAccount = connection.prepareStatement(Constants.UPDATE_ACCOUNT_BY_ID);
				PreparedStatement updateReceiverAccount = connection.prepareStatement(Constants.UPDATE_ACCOUNT_BY_ID))
		{
			// TODO: 8/25/2019 haandle the errors
			connection.setAutoCommit(false);
			writeLock.lock();
			try
			{
				Account sender = getAccount(senderId, connection);
				long senderNewBalance = Util.parseBigDecimalValueToDatabaseFormat(
						sender.getBalance().subtract(amount));
				
				if(senderNewBalance < 0)
				{
					throw new IllegalStateException("insufficient funds for the transaction");
				}
				Account receiver = getAccount(receiverId, connection);
				long receiverNewBalance = Util.parseBigDecimalValueToDatabaseFormat(
						receiver.getBalance().add(amount));
				
				updateSenderAccount.setLong(1, senderNewBalance);
				updateSenderAccount.setLong(2, senderId);
				
				updateReceiverAccount.setLong(1, receiverNewBalance);
				updateReceiverAccount.setLong(2, receiverId);
				
				updateSenderAccount.execute();
				updateReceiverAccount.execute();
				connection.commit();
			} catch(Exception e)
			{
				connection.rollback();
				throw e;
			} finally
			{
				writeLock.unlock();
			}
		}
	}
	
	private Account getAccount(long accountId, Connection connection) throws Exception
	{
		try(PreparedStatement preparedStatement = connection.prepareStatement(
				Constants.GET_ACCOUNT_BY_ID))
		{
			preparedStatement.setLong(1, accountId);
			try(ResultSet resultSet = preparedStatement.executeQuery())
			{
				resultSet.next();
				return new Account(resultSet);
			}
		}
	}
}
