package com.revolut.accountservice.service;

import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.exception.InsufficientFundsException;
import com.revolut.accountservice.exception.NoSuchAccountException;
import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.service.payload.TransferPayload;
import com.revolut.accountservice.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TransferHandler extends AbstractRequestHandler<TransferPayload> {
    private static final Logger log = LoggerFactory.getLogger(TransferHandler.class);

    public TransferHandler(AccountDAO accountDAO) {
        super(TransferPayload.class, accountDAO);
    }

    @Override
    protected Answer processImpl(TransferPayload value, Map<String, String> queryParams) {
        try {
            accountDAO.transfer(value.getSenderAccountId(), value.getReceiverAccountId(),
                    value.getAmount());
        } catch (NoSuchAccountException | InsufficientFundsException e) {
            return new Answer(Constants.HTTP_BAD_REQUEST, e.toString());
        } catch (Throwable e) {
            log.error(e.toString());
            return new Answer(Constants.HTTP_SERVER_ERROR);
        }
        return new Answer(Constants.HTTP_OK_WITH_NO_BODY);
    }
}
