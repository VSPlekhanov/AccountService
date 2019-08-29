package com.revolut.accountservice.dao;

import com.revolut.accountservice.exception.InsufficientFundsException;
import com.revolut.accountservice.exception.NoSuchAccountException;
import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.util.Constants;
import org.apache.logging.log4j.message.LocalizedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AccountDAOImpl implements AccountDAO
{
	private final DataSource dataSource;
	private static final Logger log = LoggerFactory.getLogger(AccountDAOImpl.class);
	private final Lock readLock;
	private final Lock writeLock;
	
	public AccountDAOImpl(DataSource dataSource)
	{
		this.dataSource = dataSource;
		ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
		readLock = reentrantReadWriteLock.readLock();
		writeLock = reentrantReadWriteLock.writeLock();
	}
	
	@Override public Account getAccount(long accountId) throws SQLException
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
	
	@Override public void transfer(long senderId, long receiverId, long amount) throws SQLException
	{
		try(Connection connection = dataSource.getConnection();
				PreparedStatement updateSenderAccount = connection.prepareStatement(Constants.UPDATE_ACCOUNT_BY_ID);
				PreparedStatement updateReceiverAccount = connection.prepareStatement(Constants.UPDATE_ACCOUNT_BY_ID))
		{
			connection.setAutoCommit(false);
			writeLock.lock();
			try
			{
				long senderNewBalance = getAccount(senderId).getLongBalance() - amount;
				if(senderNewBalance < 0) {
					throw new InsufficientFundsException("insufficient funds for the transaction");
				}
				
				long receiverNewBalance = getAccount(receiverId).getLongBalance() + amount;
				
				updateSenderAccount.setLong(1, senderNewBalance);
				updateSenderAccount.setLong(2, senderId);
				
				updateReceiverAccount.setLong(1, receiverNewBalance);
				updateReceiverAccount.setLong(2, receiverId);
				
				updateSenderAccount.execute();
				updateReceiverAccount.execute();
				connection.commit();
				
			} catch(SQLException e)
			{
				connection.rollback();
				log.error("Failed to commit a transfer, got an error : " + e);
				throw e;
			} finally
			{
				writeLock.unlock();
			}
		}
	}
	
	private Account getAccount(long accountId, Connection connection) throws SQLException
	{
		try(PreparedStatement preparedStatement = connection.prepareStatement(
				Constants.GET_ACCOUNT_BY_ID))
		{
			preparedStatement.setLong(1, accountId);
			try(ResultSet resultSet = preparedStatement.executeQuery())
			{
				if(!resultSet.next())
				{
					log.error("Failed to get an Account with the given id : " + accountId);
					throw new NoSuchAccountException("There is no account the with given id : " + accountId);
				}
				log.info("Account is read from the Account table : " + accountId);
				return new Account(resultSet);
			}
		}
	}
}
