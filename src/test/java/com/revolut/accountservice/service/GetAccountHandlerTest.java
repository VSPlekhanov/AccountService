package com.revolut.accountservice.service;

import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.model.Account;
import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.service.payload.EmptyPayload;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetAccountHandlerTest {
    @Test
    public void correctAccountId() throws Exception {
        Account account = new Account(123L, 1000L);
        AccountDAO accountDAO = mock(AccountDAO.class);
        GetAccountHandler handler = new GetAccountHandler(accountDAO);
        when(accountDAO.getAccount(account.getId())).thenReturn(account);
        assertEquals(new Answer(200, "{\"id\":123,\"balance\":10}"),
                handler.process(new EmptyPayload(), Map.of("accountId", "123")));
    }

    @Test
    public void correctAccountIdWithNoMoney() throws Exception {
        Account account = new Account(123L, 0L);
        AccountDAO accountDAO = mock(AccountDAO.class);
        GetAccountHandler handler = new GetAccountHandler(accountDAO);
        when(accountDAO.getAccount(account.getId())).thenReturn(account);
        assertEquals(new Answer(200, "{\"id\":123,\"balance\":0}"),
                handler.process(new EmptyPayload(), Map.of("accountId", "123")));
    }

    @Test
    public void incorrectEmptyQueryParamsMap() throws Exception {
        Account account = new Account(123L, 0L);
        AccountDAO accountDAO = mock(AccountDAO.class);
        GetAccountHandler handler = new GetAccountHandler(accountDAO);
        when(accountDAO.getAccount(account.getId())).thenReturn(account);
        assertEquals(400,
                handler.process(new EmptyPayload(), Collections.emptyMap()).getCode());
    }

    @Test
    public void incorrectFloatAccountId() throws Exception {
        Account account = new Account(123L, 0L);
        AccountDAO accountDAO = mock(AccountDAO.class);
        GetAccountHandler handler = new GetAccountHandler(accountDAO);
        when(accountDAO.getAccount(account.getId())).thenReturn(account);
        assertEquals(400,
                handler.process(new EmptyPayload(), Map.of("accountId", "123.12")).getCode());
    }

    @Test
    public void incorrectAccountId() throws Exception {
        Account account = new Account(123L, 0L);
        AccountDAO accountDAO = mock(AccountDAO.class);
        GetAccountHandler handler = new GetAccountHandler(accountDAO);
        when(accountDAO.getAccount(account.getId())).thenReturn(account);
        assertEquals(400,
                handler.process(new EmptyPayload(), Map.of("accountId", "incorrect")).getCode());
    }
}
