package com.weather.data.exception;

import lombok.Getter;

/**
 * Business exception to throw custom messages.
 */
@Getter
public class BusinessException extends RuntimeException {
    /**
     * Field name.
     */
    private final String field;

    /**
     * Field value.
     */
    private final String value;

    /**
     * Constructor.
     *
     * @param message   error message
     * @param field     field name
     * @param value     field value
     */
    public BusinessException(final String message, final String field, final String value) {
        super(message);
        super.setStackTrace(new StackTraceElement[0]);
        this.field = field;
        this.value = value;
    }
}
