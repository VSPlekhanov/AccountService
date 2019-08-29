package com.revolut.accountservice;

import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.dao.AccountDAOImpl;
import com.revolut.accountservice.model.Account;
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
import java.util.Properties;

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
        log.info("Application is started with DataBase url: " + propertiesConfig.getDatabaseUrl());
        fillTheDataBase(dataSource, propertiesConfig.getAccountsCount(), propertiesConfig.getAccountsBalance(), propertiesConfig.getAccountDaoProperties());
        AccountDAO accountDAO = new AccountDAOImpl(dataSource, propertiesConfig.getAccountDaoProperties(), propertiesConfig.isFairLocks());
        startApp(accountDAO);
    }

    public static void startApp(AccountDAO accountDAO) {
        get(Constants.GET_ACCOUNT_REQUEST_URL + Constants.ACCOUNT_ID_QUERY_PARAMETER, new GetAccountHandler(accountDAO));

        post(Constants.TRANSFER_REQUEST_URL, new TransferHandler(accountDAO));
    }

    public static void fillTheDataBase(DataSource dataSource, int accountsCount, long accountsBalance, Properties daoProps) {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement(daoProps.getProperty("drop_table_account")).execute();
            connection.prepareStatement(daoProps.getProperty("create_account_table")).execute();
            PreparedStatement preparedStatement = connection
                    .prepareStatement(daoProps.getProperty("insert_into_account"));

            preparedStatement.setLong(1, Account.toDataBaseFormat(accountsBalance));
            for (int i = 0; i < accountsCount; i++) {
                preparedStatement.execute();
            }

        } catch (SQLException e) {
            log.error("Error while filling the database : " + e.toString());
        }
    }
}
