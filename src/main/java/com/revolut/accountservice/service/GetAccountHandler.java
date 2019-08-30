package com.revolut.accountservice.service;

import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.exception.NoSuchAccountException;
import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.service.payload.EmptyPayload;
import com.revolut.accountservice.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class GetAccountHandler extends AbstractRequestHandler<EmptyPayload> {
    private static final Logger log = LoggerFactory.getLogger(GetAccountHandler.class);
    public static final String ACCOUNT_ID = ":accountid";

    public GetAccountHandler(AccountDAO accountDAO) {
        super(EmptyPayload.class, accountDAO);
    }

    @Override
    protected Answer processImpl(EmptyPayload value, Map<String, String> queryParams) {
        String accountId;
        try {
            accountId = queryParams.get(ACCOUNT_ID);
            Account account = accountDAO.getAccount(Account.parseAccountId(accountId));
            return Answer.ok(account.toJson());
        } catch (IllegalArgumentException | NoSuchAccountException e) {
            log.warn(e.toString());
            return Answer.badRequest(e.toString());
        } catch (Throwable e) {
            log.error(e.toString());
            return Answer.serverError();
        }
    }
}
