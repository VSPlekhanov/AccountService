package com.revolut.accountservice;
import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.dao.AccountDAOImpl;
import com.revolut.accountservice.service.GetAccountHandler;
import com.revolut.accountservice.service.TransferHandler;
import com.revolut.accountservice.util.Constants;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static spark.Spark.get;
import static spark.Spark.post;


public class App
{
	private static final Logger log = LoggerFactory.getLogger(App.class);
	
	public static void main(String[] args) {
		// TODO: 8/26/2019 configure app starting
		// TODO: 8/27/2019 javadoc, rest api docs
		// TODO: 8/29/2019 add the params such as database url, contentType, fair thread hanling
		// TODO: 8/29/2019 functional testing
		log.info("Application is started with DataBase url: " + Constants.DATABASE_URL);
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(Constants.DATABASE_URL);
		fillTheDataBase(dataSource);
		startApp(dataSource);
	}
	
	public static void startApp(DataSource dataSource){
		AccountDAO accountDAO = new AccountDAOImpl(dataSource);
		
		get("/:accountid", new GetAccountHandler(accountDAO));
		
		post("/transfer", new TransferHandler(accountDAO));
	}
	
	
	public static void fillTheDataBase(DataSource dataSource){
		try(Connection connection = dataSource.getConnection())
		{
			connection.prepareStatement(Constants.DROP_TABLE_ACCOUNTS).execute();
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
			log.error("Error while filling the database : " + e.toString());
		}
	}
}
