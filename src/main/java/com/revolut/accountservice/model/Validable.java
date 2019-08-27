package com.revolut.accountservice.model;

import java.util.Optional;

public interface Validable
{
	boolean isValid();
	
	Optional<String> gerErrorMessage();
	
}
