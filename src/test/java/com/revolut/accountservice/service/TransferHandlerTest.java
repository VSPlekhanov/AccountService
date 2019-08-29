package com.revolut.accountservice.service;

import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.service.payload.TransferPayload;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferHandlerTest
{
	@Test
	public void correctTransferWithFloatAmount() throws Exception
	{
		TransferPayload transfer = new TransferPayload("1", "2", "100.23");
		assertTrue(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(new Answer(204), handler.process(transfer, Collections.emptyMap()));
		verify(accountDAO, only()).transfer(1L, 2L, 10023L);
	}
	
	@Test
	public void correctTransferWithFloatAmountWithOneDigitAfterPoint() throws Exception
	{
		TransferPayload transfer = new TransferPayload("12", "21", "100.2");
		assertTrue(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(new Answer(204), handler.process(transfer, Collections.emptyMap()));
		verify(accountDAO, only()).transfer(12L, 21L, 10020L);
	}
	
	@Test
	public void correctTransferWithIntegerAmount() throws Exception
	{
		TransferPayload transfer = new TransferPayload("12", "21", "100");
		assertTrue(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(new Answer(204), handler.process(transfer, Collections.emptyMap()));
		verify(accountDAO, only()).transfer(12L, 21L, 10000L);
	}
	
	@Test
	public void correctMinimumAmount() throws Exception
	{
		TransferPayload transfer = new TransferPayload("12", "21", "0.01");
		assertTrue(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(new Answer(204), handler.process(transfer, Collections.emptyMap()));
		verify(accountDAO, only()).transfer(12L, 21L, 1L);
	}
	
	@Test
	public void correctHugeAmount() throws Exception
	{
		TransferPayload transfer = new TransferPayload("12", "21",
				"1000000000000"); //12 zeros
		assertTrue(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(new Answer(204), handler.process(transfer, Collections.emptyMap()));
		verify(accountDAO, only()).transfer(12L, 21L, 100_000_000_000_000L);
	}
	
	@Test
	public void incorrectId() throws Exception
	{
		TransferPayload transfer = new TransferPayload("incorrect", "21",
				"100.23");
		assertFalse(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(400, handler.process(transfer, Collections.emptyMap()).getCode());
	}
	
	@Test
	public void incorrectAmountTooMuchDigitsAfterPoint() throws Exception
	{
		TransferPayload transfer = new TransferPayload("12", "21",
				"100.233");
		assertFalse(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(400, handler.process(transfer, Collections.emptyMap()).getCode());
	}
	
	@Test
	public void incorrectAmountZero() throws Exception
	{
		TransferPayload transfer = new TransferPayload("12", "21",
				"0");
		assertFalse(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(400, handler.process(transfer, Collections.emptyMap()).getCode());
	}
	
	@Test
	public void incorrectAmountNegative() throws Exception
	{
		TransferPayload transfer = new TransferPayload("12", "21",
				"-1");
		assertFalse(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(400, handler.process(transfer, Collections.emptyMap()).getCode());
	}
	
	@Test
	public void incorrectAmount() throws Exception
	{
		TransferPayload transfer = new TransferPayload("12", "21",
				"incorrect");
		assertFalse(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(400, handler.process(transfer, Collections.emptyMap()).getCode());
	}
	
	@Test
	public void incorrectAmountNull() throws Exception
	{
		TransferPayload transfer = new TransferPayload("12", "21",
				null);
		assertFalse(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(400, handler.process(transfer, Collections.emptyMap()).getCode());
	}
	
	@Test
	public void incorrectSenderIdNull() throws Exception
	{
		TransferPayload transfer = new TransferPayload(null, "21",
				"100.23");
		assertFalse(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(400, handler.process(transfer, Collections.emptyMap()).getCode());
	}
	
	@Test
	public void incorrectReceiverIdNull() throws Exception
	{
		TransferPayload transfer = new TransferPayload("12", null,
				"100.23");
		assertFalse(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(400, handler.process(transfer, Collections.emptyMap()).getCode());
	}
	
	@Test
	public void incorrectSameAccountId() throws Exception
	{
		TransferPayload transfer = new TransferPayload("12", "12",
				"100.23");
		assertFalse(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(400, handler.process(transfer, Collections.emptyMap()).getCode());
	}
	
	@Test
	public void incorrectSameAccountIdButDifferentStrings() throws Exception
	{
		TransferPayload transfer = new TransferPayload("0012", "12",
				"100.23");
		assertFalse(transfer.isValid());
		
		AccountDAO accountDAO = mock(AccountDAO.class);
		TransferHandler handler = new TransferHandler(accountDAO);
		assertEquals(400, handler.process(transfer, Collections.emptyMap()).getCode());
	}
	
	
}
