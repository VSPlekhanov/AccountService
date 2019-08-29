package com.revolut.accountservice.util;

public class Constants {
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
}
