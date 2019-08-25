package com.revolut.accountservice.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest
{
	
	@Test
	void parseLongValueFromDatabase()
	{
		Assertions.assertEquals(BigDecimal.valueOf(10.58), Util.parseLongValueFromDatabase(1058L));
		Assertions.assertEquals(BigDecimal.valueOf(0.01), Util.parseLongValueFromDatabase(1L));
		Assertions.assertEquals(BigDecimal.ZERO, Util.parseLongValueFromDatabase(0L));
		Assertions.assertEquals(BigDecimal.ONE, Util.parseLongValueFromDatabase(100L));
		Assertions.assertEquals(BigDecimal.valueOf(100.30), Util.parseLongValueFromDatabase(10030L));
		Assertions.assertEquals(BigDecimal.valueOf(1_000_000_000_000.05), Util.parseLongValueFromDatabase(100_000_000_000_005L));
	}
	
	@Test
	void parseBigDecimalAmountValue()
	{
		Assertions.assertEquals(1058L, Util.parseBigDecimalAmountValue(BigDecimal.valueOf(10.58)));
		Assertions.assertEquals(1L, Util.parseBigDecimalAmountValue(BigDecimal.valueOf(0.01)));
		Assertions.assertEquals(100L, Util.parseBigDecimalAmountValue(BigDecimal.ONE));
		Assertions.assertEquals(10030L, Util.parseBigDecimalAmountValue(BigDecimal.valueOf(100.30)));
		Assertions.assertEquals(100_000_000_000_005L, Util.parseBigDecimalAmountValue(BigDecimal.valueOf(1_000_000_000_000.05)));
		
		Assertions.assertThrows(IllegalStateException.class, () -> Util.parseBigDecimalAmountValue(BigDecimal.ZERO));
		Assertions.assertThrows(IllegalStateException.class, () -> Util.parseBigDecimalAmountValue(BigDecimal.valueOf(10.589)));
		Assertions.assertThrows(IllegalStateException.class, () -> Util.parseBigDecimalAmountValue(BigDecimal.valueOf(-10.58)));
	}
}
