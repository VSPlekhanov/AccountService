package com.revolut.accountservice;
import com.revolut.accountservice.util.Constants;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static spark.Spark.get;

public class App
{
	public static void main(String[] args) {
		get("/hello", (req, res) -> "Hello, World!");
		createNewDatabase();
		
	}
	
	public static void createNewDatabase(){
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(Constants.DATABASE_URL);
		try
		{
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			System.out.println(statement);
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
