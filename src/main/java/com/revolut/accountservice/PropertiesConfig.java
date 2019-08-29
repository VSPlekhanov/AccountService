package com.revolut.accountservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfig {
    private static final Logger log = LoggerFactory.getLogger(PropertiesConfig.class);
    private static final String PROPERTIES_PATH = "application.properties";

    private long accountsBalance;
    private int accountsCount;
    private boolean fairLocks;
    private String databaseUrl;

    public static PropertiesConfig getPropertiesConfig() {
        Properties properties = new Properties();
        try (InputStream inputStream = PropertiesConfig.class.getResourceAsStream(PROPERTIES_PATH)) {
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            log.error("Properties file not found!");
            System.exit(1);
        } catch (IOException e) {
            log.error("Failed to load properties : " + e.toString());
            System.exit(1);
        }
        String databaseUrl = null;
        databaseUrl = properties.getProperty("app.database_url");
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

        return new PropertiesConfig(accountsBalance, accountsCount, fairLocks, databaseUrl);
    }

    private PropertiesConfig(long accountsBalance, int accountsCount, boolean fairLocks, String databaseUrl) {
        this.accountsBalance = accountsBalance;
        this.accountsCount = accountsCount;
        this.fairLocks = fairLocks;
        this.databaseUrl = databaseUrl;
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
}
