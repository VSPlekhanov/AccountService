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

import static spark.Spark.*;


public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static final int DEFAULT_BALANCE = 1000;
    public static final int DEFAULT_COUNT = 10;
    public static final boolean FAIR_LOCKS = false;

    public static void main(String[] args) {
        BasicDataSource dataSource = new BasicDataSource();
        PropertiesConfig propertiesConfig = PropertiesConfig.getPropertiesConfig();
        dataSource.setUrl(propertiesConfig.getDatabaseUrl());
        log.info("Application is started with DataBase url: " + Constants.DATABASE_URL);
        fillTheDataBase(dataSource, propertiesConfig.getAccountsCount(), propertiesConfig.getAccountsBalance());
        startApp(dataSource, propertiesConfig.isFairLocks());
    }

    public static void startApp(DataSource dataSource, boolean fairLocks) {
        AccountDAO accountDAO = new AccountDAOImpl(dataSource, fairLocks);

        get(Constants.GET_ACCOUNT_REQUEST_URL + Constants.ACCOUNT_ID_QUERY_PARAMETER, new GetAccountHandler(accountDAO));

        post(Constants.TRANSFER_REQUEST_URL, new TransferHandler(accountDAO));
    }

    public static void fillTheDataBase(DataSource dataSource, int accountsCount, long accountsBalance) {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement(Constants.DROP_TABLE_ACCOUNTS).execute();
            connection.prepareStatement(Constants.CREATE_ACCOUNT_TABLE).execute();
            PreparedStatement preparedStatement = connection
                    .prepareStatement(Constants.INSERT_INTO_ACCOUNT);

            preparedStatement.setLong(1, Constants.toDataBaseFormat(accountsBalance));
            for (int i = 0; i < accountsCount; i++) {
                preparedStatement.execute();
            }

        } catch (SQLException e) {
            log.error("Error while filling the database : " + e.toString());
        }
    }
}
