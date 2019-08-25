package com.revolut.accountservice.dao;

import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.util.Constants;
import com.revolut.accountservice.util.Util;
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
	
	private static final BigDecimal FIRST_ACCOUNT_DEFAULT_BALANCE = BigDecimal.valueOf(1000);
	private static final BigDecimal SECOND_ACCOUNT_DEFAULT_BALANCE = BigDecimal.valueOf(2000);
	private static final BigDecimal THIRD_ACCOUNT_DEFAULT_BALANCE = BigDecimal.valueOf(3000);
	
	private static final long FIRST_ACCOUNT_ID = 1;
	private static final long SECOND_ACCOUNT_ID = 2;
	private static final long THIRD_ACCOUNT_ID = 3;
	
	
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
				
				insertFirstAccount.setLong(1, Util.parseBigDecimalValueToDatabaseFormat(FIRST_ACCOUNT_DEFAULT_BALANCE));
				insertFirstAccount.execute();
				
				insertSecondAccount.setLong(1, Util.parseBigDecimalValueToDatabaseFormat(SECOND_ACCOUNT_DEFAULT_BALANCE));
				insertSecondAccount.execute();
				
				insertThirdAccount.setLong(1, Util.parseBigDecimalValueToDatabaseFormat(THIRD_ACCOUNT_DEFAULT_BALANCE));
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
		Account firstAccount = accountDAO.getAccount(FIRST_ACCOUNT_ID);
		assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, firstAccount.getBalance());
		assertEquals(FIRST_ACCOUNT_ID, firstAccount.getId());
		
		Account secondAccount = accountDAO.getAccount(SECOND_ACCOUNT_ID);
		assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, secondAccount.getBalance());
		assertEquals(SECOND_ACCOUNT_ID, secondAccount.getId());
		
		Account thirdAccount = accountDAO.getAccount(THIRD_ACCOUNT_ID);
		assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, thirdAccount.getBalance());
		assertEquals(THIRD_ACCOUNT_ID, thirdAccount.getId());
	}
	
	@Test void transferAllMoneyFromFirst2Second()
	{
		accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, BigDecimal.valueOf(1000));
		
		assertEquals(BigDecimal.ZERO, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalance());
		assertEquals(BigDecimal.valueOf(3000), accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalance());
		assertEquals(BigDecimal.valueOf(3000), accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalance());
	}
	
	@Test void transferTooMuchMoney(){
		assertThrows(IllegalStateException.class,
				() -> accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, FIRST_ACCOUNT_DEFAULT_BALANCE.add(BigDecimal.ONE)));
		
		assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalance());
		assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalance());
		assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalance());
	}
	
	@Test void transferFromFirst2SecondThenFromSecond2Third(){
		accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, BigDecimal.valueOf(500));
		accountDAO.transfer(SECOND_ACCOUNT_ID, THIRD_ACCOUNT_ID, BigDecimal.valueOf(500));
		
		assertEquals(BigDecimal.valueOf(500), accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalance());
		assertEquals(BigDecimal.valueOf(2000), accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalance());
		assertEquals(BigDecimal.valueOf(3500), accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalance());
	}
	
	@Test void transferThroughAllByCircle(){
		accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, BigDecimal.valueOf(500));
		accountDAO.transfer(SECOND_ACCOUNT_ID, THIRD_ACCOUNT_ID, BigDecimal.valueOf(500));
		accountDAO.transfer(THIRD_ACCOUNT_ID, FIRST_ACCOUNT_ID, BigDecimal.valueOf(500));
		
		assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalance());
		assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalance());
		assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalance());
	}
	
}
