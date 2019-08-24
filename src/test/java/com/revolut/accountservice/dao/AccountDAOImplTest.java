package com.revolut.accountservice.dao;

import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.util.Constants;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AccountDAOImplTest
{
	private static DataSource dataSource;
	private static AccountDAO accountDAO;
	
	private static final long FIRST_ACCOUNT_DEFAULT_BALANCE = 1000;
	private static final long SECOND_ACCOUNT_DEFAULT_BALANCE = 2000;
	private static final long THIRD_ACCOUNT_DEFAULT_BALANCE = 3000;
	
	
	@BeforeAll
	static void beforeClass()
	{
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setUrl(Constants.DATABASE_URL);
		dataSource = basicDataSource;
		accountDAO = new AccountDAOImpl(dataSource);
	}
	
	@BeforeEach
	void setUp()
	{
		try(Connection connection = dataSource.getConnection();
				PreparedStatement createTable = connection.prepareStatement(Constants.CREATE_ACCOUNT_TABLE))
		{
			createTable.execute();
		
			try(PreparedStatement insertFirstAccount = connection.prepareStatement(
					Constants.INSERT_INTO_ACCOUNT);
					PreparedStatement insertSecondAccount = connection.prepareStatement(
							Constants.INSERT_INTO_ACCOUNT);
					PreparedStatement insertThirdAccount = connection.prepareStatement(
							Constants.INSERT_INTO_ACCOUNT))
			{
				
				insertFirstAccount.setLong(1, FIRST_ACCOUNT_DEFAULT_BALANCE);
				insertFirstAccount.execute();
				
				insertSecondAccount.setLong(1, SECOND_ACCOUNT_DEFAULT_BALANCE);
				insertSecondAccount.execute();
				
				insertThirdAccount.setLong(1, THIRD_ACCOUNT_DEFAULT_BALANCE);
				insertThirdAccount.execute();
			}
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	@AfterEach
	void tearDown()
	{
		
		try(Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(Constants.DROP_TABLE_ACCOUNTS)) {
			statement.execute();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	void getAccount()
	{
		Account firstAccount = accountDAO.getAccount(1);
		assertEquals(BigDecimal.valueOf(FIRST_ACCOUNT_DEFAULT_BALANCE), firstAccount.getBalance());
		assertEquals(1, firstAccount.getId());
		
		Account secondAccount = accountDAO.getAccount(1);
		assertEquals(BigDecimal.valueOf(FIRST_ACCOUNT_DEFAULT_BALANCE), secondAccount.getBalance());
		assertEquals(1, secondAccount.getId());
		
		Account thirdAccount = accountDAO.getAccount(1);
		assertEquals(BigDecimal.valueOf(FIRST_ACCOUNT_DEFAULT_BALANCE), thirdAccount.getBalance());
		assertEquals(1, thirdAccount.getId());
	}
}
