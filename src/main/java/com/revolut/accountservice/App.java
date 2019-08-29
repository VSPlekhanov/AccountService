package com.revolut.accountservice;
import com.revolut.accountservice.controller.AccountController;
import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.dao.AccountDAOImpl;
import com.revolut.accountservice.util.Constants;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.print.attribute.IntegerSyntax;
import javax.sql.DataSource;
import javax.xml.crypto.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.IntStream;

import static spark.Spark.get;

public class App
{
	public static void main(String[] args) {
		// TODO: 8/26/2019 configure app starting
		// TODO: 8/26/2019 add logging
		// TODO: 8/26/2019 handle all errors
		// TODO: 8/26/2019 write all tests
		// TODO: 8/27/2019 javadoc, rest api docs
		// TODO: 8/28/2019 same error handling occurs
		startApp();
		
	}
	
	public static void startApp(){
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(Constants.DATABASE_URL);
		fillTheDataBase(dataSource);
		AccountDAO accountDAO = new AccountDAOImpl(dataSource);
		new AccountController(accountDAO);
	}
	
	public static void fillTheDataBase(DataSource dataSource){
		try(Connection connection = dataSource.getConnection())
		{
			connection.prepareStatement(Constants.CREATE_ACCOUNT_TABLE).execute();
			PreparedStatement preparedStatement = connection
					.prepareStatement(Constants.INSERT_INTO_ACCOUNT);
			
			preparedStatement.setLong(1, 100_000);
			for(int i = 0; i < 10; i++)
			{
				preparedStatement.execute();
			}
			
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
