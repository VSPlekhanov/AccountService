package com.revolut.accountservice.service.payload;

import java.util.Optional;

public interface Validable {
    /**
     * Checks if the value is valid.
     * If not, you can get the error message via methon <code>getErrorMessage()</code>
     * @return true if valid, false otherwise
     */
    boolean isValid();

    /**
     * @return the error message if the value is not valid.
     * Otherwise - <code>Optional.empty()</code>
     */
    Optional<String> gerErrorMessage();

}
