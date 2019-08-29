package com.revolut.accountservice.functional;

import com.revolut.accountservice.App;
import com.revolut.accountservice.PropertiesConfig;
import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.dao.AccountDAOImpl;
import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.util.Constants;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class TransferSteps {
    private HttpURLConnection httpURLTransferConnection;
    private HttpURLConnection httpURLGetAccountConnection;

    @Given("the first account with id $senderId and balance $senderBalance " +
            "and the second account with id $receiverId and balance $receiverBalance")
    public void prepareDataBaseAndStartTheApp(long senderId, long senderBalance, long receiverId, long receiverBalance)
            throws SQLException, IOException {
        PropertiesConfig propertiesConfig = PropertiesConfig.getPropertiesConfig();
        Properties accountDaoProperties = propertiesConfig.getAccountDaoProperties();
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(propertiesConfig.getDatabaseUrl());

        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement(accountDaoProperties.getProperty("drop_table_account")).execute();
            connection.prepareStatement(accountDaoProperties.getProperty("create_account_table")).execute();
            PreparedStatement preparedStatement = connection
                    .prepareStatement(accountDaoProperties.getProperty("insert_into_account_with_id"));

            preparedStatement.setLong(1, senderId);
            preparedStatement.setLong(2, Account.toDataBaseFormat(senderBalance));
            preparedStatement.execute();

            preparedStatement.setLong(1, receiverId);
            preparedStatement.setLong(2, Account.toDataBaseFormat(receiverBalance));
            preparedStatement.execute();
        }
        AccountDAO accountDAO = new AccountDAOImpl(dataSource, accountDaoProperties, propertiesConfig.isFairLocks());
        App.startApp(accountDAO);
    }

    @When("the user sends a transfer request with senderAccountId $senderAccountId" +
            " and receiverAccountId $receiverAccountId and amount $amount")
    public void sendTransferRequest(String senderAccountId, String receiverAccountId, String amount)
            throws IOException {
        URL transferUrl = new URL(Constants.TEST_HOST + Constants.TRANSFER_REQUEST_URL);
        httpURLTransferConnection = (HttpURLConnection) transferUrl.openConnection();

        String body = String.format(Constants.EXAMPLE_TRANSFER_BODY, senderAccountId, receiverAccountId, amount);
        httpURLTransferConnection.setRequestMethod("POST");
        httpURLTransferConnection.setConnectTimeout(10_000);
        httpURLTransferConnection.setRequestProperty("Content-Type", Constants.CONTENT_TYPE_JSON);
        httpURLTransferConnection.setDoOutput(true);
        httpURLTransferConnection.setRequestProperty("Content-Length", Integer.toString(body.length()));
        httpURLTransferConnection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }

    @Then("the transfer is committed")
    public void verifyThatTransferIsCommitted() throws IOException {
        if (httpURLTransferConnection.getResponseCode() != Constants.HTTP_OK_WITH_NO_BODY) {
            throw new IllegalStateException("Transfer wasn't committed!");
        }
        httpURLTransferConnection.disconnect();
    }

    @When("user sends a getAccount request with accountId $accountId")
    public void sendGetAccountRequest(String accountId)
            throws IOException {
        URL getAccountUrl = new URL(Constants.TEST_HOST + Constants.GET_ACCOUNT_REQUEST_URL + accountId);
        httpURLGetAccountConnection = (HttpURLConnection) getAccountUrl.openConnection();
        httpURLGetAccountConnection.setRequestMethod("GET");
        httpURLGetAccountConnection.setConnectTimeout(10_000);
    }

    @Then("account has id $accountId and balance $balance returned")
    public void verifyGivenAccount(long accountId, long balance) throws IOException {
        if (httpURLGetAccountConnection.getResponseCode() != 200) {
            throw new IllegalStateException("Failed to get account");
        }
        String result = IOUtils.toString((InputStream) httpURLGetAccountConnection.getContent(),
                StandardCharsets.UTF_8);
        if (!result.equals(String.format(Constants.DEFAULT_RESPONSE_BODY_GET_ACCOUNT, accountId, balance))) {
            throw new IllegalStateException("Account parameters mismatch!");
        }
    }
}
