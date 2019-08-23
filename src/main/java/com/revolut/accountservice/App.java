package com.revolut.accountservice;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import static spark.Spark.get;

public class App
{
	public static void main(String[] args) {
//		get("/hello", (req, res) -> "Hello, World!");
		createNewDatabase("DB");
		
	}
	
	public static void createNewDatabase(String name){
		String url = "jdbc:sqlite:memory:" + name;
		try(Connection connection = DriverManager.getConnection(url))
		{
			DatabaseMetaData meta = connection.getMetaData();
			System.out.println("The driver name is " + meta.getDriverName());
			System.out.println("A new database has been created.");
			
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
