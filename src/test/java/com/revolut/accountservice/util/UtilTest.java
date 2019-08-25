package com.revolut.accountservice.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class UtilTest
{
	
	@Test
	void parseLongValueFromDatabase()
	{
		Assertions.assertEquals(BigDecimal.valueOf(10.58), Util.parseLongValueFromDatabaseFormat(1058L));
		Assertions.assertEquals(BigDecimal.valueOf(0.01), Util.parseLongValueFromDatabaseFormat(1L));
		Assertions.assertEquals(BigDecimal.ZERO, Util.parseLongValueFromDatabaseFormat(0L));
		Assertions.assertEquals(BigDecimal.ONE, Util.parseLongValueFromDatabaseFormat(100L));
		Assertions.assertEquals(BigDecimal.valueOf(100.30), Util.parseLongValueFromDatabaseFormat(10030L));
		Assertions.assertEquals(BigDecimal.valueOf(1_000_000_000_000.05), Util.parseLongValueFromDatabaseFormat(100_000_000_000_005L));
	}
	
	@Test
	void parseBigDecimalAmountValue()
	{
		Assertions.assertEquals(1058L, Util.parseBigDecimalValueToDatabaseFormat(BigDecimal.valueOf(10.58)));
		Assertions.assertEquals(1L, Util.parseBigDecimalValueToDatabaseFormat(BigDecimal.valueOf(0.01)));
		Assertions.assertEquals(0L, Util.parseBigDecimalValueToDatabaseFormat(BigDecimal.ZERO));
		Assertions.assertEquals(100L, Util.parseBigDecimalValueToDatabaseFormat(BigDecimal.ONE));
		Assertions.assertEquals(10030L, Util.parseBigDecimalValueToDatabaseFormat(BigDecimal.valueOf(100.30)));
		Assertions.assertEquals(100_000_000_000_005L, Util.parseBigDecimalValueToDatabaseFormat(BigDecimal.valueOf(1_000_000_000_000.05)));
	}
}
