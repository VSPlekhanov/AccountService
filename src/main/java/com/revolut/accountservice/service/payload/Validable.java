package com.revolut.accountservice.service.payload;

import java.util.Optional;

public interface Validable {
    boolean isValid();

    Optional<String> gerErrorMessage();

}
