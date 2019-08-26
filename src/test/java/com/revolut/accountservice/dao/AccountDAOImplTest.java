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
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class AccountDAOImplTest
{
	private static DataSource dataSource;
	private static AccountDAO accountDAO;
	
	private static final BigDecimal FIRST_ACCOUNT_DEFAULT_BALANCE = BigDecimal.valueOf(1_000_000);
	private static final BigDecimal SECOND_ACCOUNT_DEFAULT_BALANCE = BigDecimal.valueOf(2_000_000);
	private static final BigDecimal THIRD_ACCOUNT_DEFAULT_BALANCE = BigDecimal.valueOf(3_000_000);
	
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
		BigDecimal amount = FIRST_ACCOUNT_DEFAULT_BALANCE;
		accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, amount);
		
		assertEquals(BigDecimal.ZERO, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalance());
		assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE.add(amount), accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalance());
		assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalance());
	}
	
	@Test void transferTooMuchMoney(){
		assertThrows(IllegalStateException.class,
				() -> accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, FIRST_ACCOUNT_DEFAULT_BALANCE.add(BigDecimal.ONE)));
		
		assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalance());
		assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalance());
		assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalance());
	}
	
	@Test void transferFromFirst2SecondThenFromSecond2Third(){
		BigDecimal amount = BigDecimal.valueOf(500);
		accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, amount);
		accountDAO.transfer(SECOND_ACCOUNT_ID, THIRD_ACCOUNT_ID, amount);
		
		assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE.subtract(amount), accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalance());
		assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalance());
		assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE.add(amount), accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalance());
	}
	
	@Test void transferThroughAllByCircle(){
		BigDecimal amount = BigDecimal.valueOf(500);
		accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, amount);
		accountDAO.transfer(SECOND_ACCOUNT_ID, THIRD_ACCOUNT_ID, amount);
		accountDAO.transfer(THIRD_ACCOUNT_ID, FIRST_ACCOUNT_ID, amount);
		
		assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalance());
		assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalance());
		assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalance());
	}
	
	@Test void smallMultithreadTest(){
		int count = Math.min(Math.min(FIRST_ACCOUNT_DEFAULT_BALANCE.intValue(), SECOND_ACCOUNT_DEFAULT_BALANCE.intValue()), 1_00);
		
		Runnable fromFirstAccount2Second = () ->
			IntStream.range(0, count)
					.forEach((i) -> accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, BigDecimal.ONE));
		
		Runnable fromSecondAccount2First = () ->
				IntStream.range(0, count)
						.forEach((i) -> accountDAO.transfer(SECOND_ACCOUNT_ID, FIRST_ACCOUNT_ID, BigDecimal.ONE));
		
		Thread first = new Thread(fromFirstAccount2Second);
		Thread second = new Thread(fromSecondAccount2First);
		
		first.start();
		second.start();
		
		try
		{
			first.join();
			second.join();
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalance());
		assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalance());
		assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalance());
	}
	
	
	@Test void smallMultithreadTest2(){
		int count = Math.min(Math.min(FIRST_ACCOUNT_DEFAULT_BALANCE.intValue(), SECOND_ACCOUNT_DEFAULT_BALANCE.intValue()), 50);
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		Runnable fromFirstAccount2Second = () ->
				IntStream.range(0, count)
						.forEach((i) -> accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, BigDecimal.ONE));
		
		Runnable fromSecondAccount2First = () ->
				IntStream.range(0, count)
						.forEach((i) -> accountDAO.transfer(SECOND_ACCOUNT_ID, FIRST_ACCOUNT_ID, BigDecimal.ONE));
		
		executorService.submit(fromFirstAccount2Second);
		executorService.submit(fromSecondAccount2First);
		
		executorService.shutdown();
		
		assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalance());
		assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalance());
		assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalance());
	}
	
}
