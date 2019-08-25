package com.revolut.accountservice.util;

import java.math.BigDecimal;

public class Util
{
	public static BigDecimal parseLongValueFromDatabase(long value){
		return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100));
	}
	
	public static long parseBigDecimalAmountValue(BigDecimal value){
		try{
			long amount = value.movePointRight(2).longValueExact();
			if (amount <= 0){
				throw new IllegalStateException("The value of the amount should be more than zero");
			}
			return amount;
		} catch(ArithmeticException e){
			throw new IllegalStateException("Wrong format of the given amount : " + value);
		}
	}
}
