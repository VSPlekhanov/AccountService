package com.revolut.accountservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfig {
    private static final Logger log = LoggerFactory.getLogger(PropertiesConfig.class);
    private static final String APPLICATION_PROPERTIES_PATH = "application.properties";
    private static final String ACCOUNT_DAO_PROPERTIES_PATH = "account_dao.properties";

    private final long accountsBalance;
    private final int accountsCount;
    private final boolean fairLocks;
    private final String databaseUrl;
    private final Properties accountDaoProperties;

    public static PropertiesConfig getPropertiesConfig() {
        Properties properties = new Properties();
        Properties accountDaoProperties = new Properties();
        try (InputStream appProps = PropertiesConfig.class.getResourceAsStream(APPLICATION_PROPERTIES_PATH);
             InputStream daoProps = PropertiesConfig.class.getResourceAsStream(ACCOUNT_DAO_PROPERTIES_PATH)) {
            properties.load(appProps);
            accountDaoProperties.load(daoProps);
        } catch (FileNotFoundException e) {
            log.error("Properties file not found!");
            System.exit(1);
        } catch (IOException e) {
            log.error("Failed to load properties : " + e.toString());
            System.exit(1);
        }
        String databaseUrl = properties.getProperty("app.database_url");
        if (databaseUrl == null) {
            log.error("Database url not found!");
            System.exit(1);
        }

        long accountsBalance = App.DEFAULT_BALANCE;
        int accountsCount = App.DEFAULT_COUNT;
        boolean fairLocks = App.FAIR_LOCKS;
        try {
            accountsCount = Integer.parseInt(properties.getProperty("app.default_accounts_count"));
            accountsBalance = Integer.parseInt(properties.getProperty("app.default_account_balance"));
            fairLocks = Boolean.parseBoolean(properties.getProperty("app.fair_locks"));
        } catch (Exception e) {
            log.error("Fai led to load accounts properties, using defalt values : " + e.toString());
        }

        return new PropertiesConfig(accountsBalance, accountsCount, fairLocks, databaseUrl, accountDaoProperties);
    }

    private PropertiesConfig(long accountsBalance, int accountsCount, boolean fairLocks, String databaseUrl, Properties accountDaoProperties) {
        this.accountsBalance = accountsBalance;
        this.accountsCount = accountsCount;
        this.fairLocks = fairLocks;
        this.databaseUrl = databaseUrl;
        this.accountDaoProperties = accountDaoProperties;
    }

    public long getAccountsBalance() {
        return accountsBalance;
    }

    public int getAccountsCount() {
        return accountsCount;
    }

    public boolean isFairLocks() {
        return fairLocks;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public Properties getAccountDaoProperties() {
        return accountDaoProperties;
    }
}
