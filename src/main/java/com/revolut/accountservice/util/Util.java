package com.revolut.accountservice.util;

import java.math.BigDecimal;

public class Util
{
	public static BigDecimal parseLongValueFromDatabaseFormat(long value)
	{
		return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100));
	}
	
	public static long parseBigDecimalValueToDatabaseFormat(BigDecimal value)
	{
		return value.movePointRight(2).longValueExact();
	}
}
