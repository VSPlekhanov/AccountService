package com.revolut.accountservice.dao;

import com.revolut.accountservice.PropertiesConfig;
import com.revolut.accountservice.exception.InsufficientFundsException;
import com.revolut.accountservice.exception.NoSuchAccountException;
import com.revolut.accountservice.model.Account;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class AccountDAOImplTest {
    private static DataSource dataSource;
    private static AccountDAO accountDAO;
    private static Properties daoProperties;

    private static final long FIRST_ACCOUNT_DEFAULT_BALANCE = 1_000_000;
    private static final long SECOND_ACCOUNT_DEFAULT_BALANCE = 2_000_000;
    private static final long THIRD_ACCOUNT_DEFAULT_BALANCE = 3_000_000;

    private static final long FIRST_ACCOUNT_ID = 1;
    private static final long SECOND_ACCOUNT_ID = 2;
    private static final long THIRD_ACCOUNT_ID = 3;


    @BeforeAll
    static void beforeClass() {
        PropertiesConfig propertiesConfig = PropertiesConfig.getPropertiesConfig();
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(propertiesConfig.getDatabaseUrl());
        dataSource = basicDataSource;
        accountDAO = new AccountDAOImpl(dataSource, propertiesConfig.getAccountDaoProperties(), propertiesConfig.isFairLocks());
        daoProperties = propertiesConfig.getAccountDaoProperties();
    }

    @BeforeEach
    void setUp() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement dropTable = connection.prepareStatement(daoProperties.getProperty("drop_table_account"));
             PreparedStatement createTable = connection.prepareStatement(daoProperties.getProperty("create_account_table"))) {
            dropTable.execute();
            createTable.execute();

            try (PreparedStatement insertFirstAccount = connection.prepareStatement(
                    daoProperties.getProperty("insert_into_account"));
                 PreparedStatement insertSecondAccount = connection.prepareStatement(
                         daoProperties.getProperty("insert_into_account"));
                 PreparedStatement insertThirdAccount = connection.prepareStatement(
                         daoProperties.getProperty("insert_into_account"))) {

                insertFirstAccount.setLong(1, (FIRST_ACCOUNT_DEFAULT_BALANCE));
                insertFirstAccount.execute();

                insertSecondAccount.setLong(1, (SECOND_ACCOUNT_DEFAULT_BALANCE));
                insertSecondAccount.execute();

                insertThirdAccount.setLong(1, (THIRD_ACCOUNT_DEFAULT_BALANCE));
                insertThirdAccount.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getAccount() throws Exception {
        Account firstAccount = accountDAO.getAccount(FIRST_ACCOUNT_ID);
        assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, firstAccount.getBalanceInDatabaseFormat());
        assertEquals(FIRST_ACCOUNT_ID, firstAccount.getId());

        Account secondAccount = accountDAO.getAccount(SECOND_ACCOUNT_ID);
        assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, secondAccount.getBalanceInDatabaseFormat());
        assertEquals(SECOND_ACCOUNT_ID, secondAccount.getId());

        Account thirdAccount = accountDAO.getAccount(THIRD_ACCOUNT_ID);
        assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, thirdAccount.getBalanceInDatabaseFormat());
        assertEquals(THIRD_ACCOUNT_ID, thirdAccount.getId());
    }

    @Test
    void transferAllMoneyFromFirst2Second() throws Exception {
        long amount = FIRST_ACCOUNT_DEFAULT_BALANCE;
        accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, amount);

        assertEquals(0, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE + (amount), accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalanceInDatabaseFormat());
    }

    @Test
    void transferTooMuchMoney() throws Exception {
        assertThrows(InsufficientFundsException.class,
                () -> accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, FIRST_ACCOUNT_DEFAULT_BALANCE + 1));

        assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalanceInDatabaseFormat());
    }

    @Test
    void transferFromFirst2SecondThenFromSecond2Third() throws Exception {
        long amount = (500);
        accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, amount);
        accountDAO.transfer(SECOND_ACCOUNT_ID, THIRD_ACCOUNT_ID, amount);

        assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE - (amount), accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE + (amount), accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalanceInDatabaseFormat());
    }

    @Test
    void transferThroughAllByCircle() throws Exception {
        long amount = (500);
        accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, amount);
        accountDAO.transfer(SECOND_ACCOUNT_ID, THIRD_ACCOUNT_ID, amount);
        accountDAO.transfer(THIRD_ACCOUNT_ID, FIRST_ACCOUNT_ID, amount);

        assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalanceInDatabaseFormat());
    }


    @Test
    void incorrectTransferSenderDoesNotExists() throws Exception {
        long amount = 500;
        assertThrows(NoSuchAccountException.class,
                () -> accountDAO.transfer(FIRST_ACCOUNT_ID + SECOND_ACCOUNT_ID + THIRD_ACCOUNT_ID
                        , SECOND_ACCOUNT_ID, amount));

        assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalanceInDatabaseFormat());
    }

    @Test
    void incorrectTransferReceiverDoesNotExists() throws Exception {
        long amount = 500;
        assertThrows(NoSuchAccountException.class,
                () -> accountDAO.transfer(FIRST_ACCOUNT_ID
                        , FIRST_ACCOUNT_ID + SECOND_ACCOUNT_ID + THIRD_ACCOUNT_ID, amount));

        assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalanceInDatabaseFormat());
    }

    @Test
    void smallMultithreadTest() throws Exception {
        int count = Math.min(Math.min((int) FIRST_ACCOUNT_DEFAULT_BALANCE, (int) SECOND_ACCOUNT_DEFAULT_BALANCE), 20);

        Runnable fromFirstAccount2Second = () -> {
            for (int i = 0; i < count; i++) {
                try {
                    accountDAO.transfer(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable fromSecondAccount2First = () -> {
            for (int i = 0; i < count; i++) {
                try {
                    accountDAO.transfer(SECOND_ACCOUNT_ID, FIRST_ACCOUNT_ID, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread first = new Thread(fromFirstAccount2Second);
        Thread second = new Thread(fromSecondAccount2First);

        first.start();
        second.start();

        try {
            first.join();
            second.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(FIRST_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(FIRST_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(SECOND_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(SECOND_ACCOUNT_ID).getBalanceInDatabaseFormat());
        assertEquals(THIRD_ACCOUNT_DEFAULT_BALANCE, accountDAO.getAccount(THIRD_ACCOUNT_ID).getBalanceInDatabaseFormat());
    }

}
