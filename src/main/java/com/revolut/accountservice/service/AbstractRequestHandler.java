package com.revolut.accountservice.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.accountservice.dao.AccountDAO;
import com.revolut.accountservice.model.Answer;
import com.revolut.accountservice.service.payload.Validable;
import com.revolut.accountservice.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public abstract class AbstractRequestHandler<V extends Validable>
        implements RequestHandler<V>, Route {
    private static final Logger log = LoggerFactory.getLogger(AbstractRequestHandler.class);
    public static final String EMPTY_BODY = "{}";

    private Class<V> valueClass;
    protected AccountDAO accountDAO;

    public AbstractRequestHandler(Class<V> valueClass, AccountDAO accountDAO) {
        this.valueClass = valueClass;
        this.accountDAO = accountDAO;
    }

    public final Answer process(V value, Map<String, String> queryParams) {
        if (!value.isValid()) {
            String errorMessage = value.gerErrorMessage().orElse("");
            log.warn(errorMessage);
            return new Answer(Constants.HTTP_BAD_REQUEST, errorMessage);
        } else {
            return processImpl(value, queryParams);
        }
    }

    protected abstract Answer processImpl(V value, Map<String, String> queryParams);


    @Override
    public Object handle(Request request, Response response) throws Exception {
        Answer answer;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String body = request.body().isEmpty()
                    ? EMPTY_BODY
                    : request.body();
            V value = objectMapper.readValue(body, valueClass);
            Map<String, String> queryParams = request.params();
            answer = process(value, queryParams);
        } catch (JsonParseException e) {
            answer = new Answer(Constants.HTTP_BAD_REQUEST, e.getMessage());
        }
        response.status(answer.getCode());
        response.type(Constants.CONTENT_TYPE_JSON);
        response.body(answer.getBody());
        return answer.getBody();
    }

}
