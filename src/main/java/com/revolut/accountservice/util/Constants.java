package com.revolut.accountservice.util;

public class Constants {
    public static final String TEST_DATABASE_URL = "jdbc:sqlite:memory:DB";

    public static final String GET_ACCOUNT_BY_ID = "SELECT * FROM account WHERE id = ?";

    public static final String CREATE_ACCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS account (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "balance INTEGER NOT NULL DEFAULT 0)";

    public static final String DROP_TABLE_ACCOUNTS = "DROP TABLE IF EXISTS account;";

    public static final String INSERT_INTO_ACCOUNT = "INSERT INTO account(balance) VALUES(?)";

    public static final String INSERT_INTO_ACCOUNT_WITH_ID = "INSERT INTO account(id, balance) VALUES(?, ?)";

    public static final String UPDATE_ACCOUNT_BY_ID = "UPDATE account SET balance = ? WHERE id = ?";

    public static final String TEST_HOST = "http://localhost:4567";

    public static final String TRANSFER_REQUEST_URL = "/transfer";

    public static final String GET_ACCOUNT_REQUEST_URL = "/";

    public static final String ACCOUNT_ID_QUERY_PARAMETER = ":accountid";

    public static final int HTTP_BAD_REQUEST = 400;

    public static final int HTTP_OK = 200;

    public static final int HTTP_OK_WITH_NO_BODY = 204;

    public static final int HTTP_SERVER_ERROR = 500;

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final String EXAMPLE_TRANSFER_BODY = "{\"senderAccountId\": %s, \"receiverAccountId\": %s, \"amount\": %s}";

    public static final String DEFAULT_RESPONSE_BODY_GET_ACCOUNT = "{\"id\":%s,\"balance\":%s}";

    public static long toDataBaseFormat(long balance){
        return balance * 100;
    }
}
