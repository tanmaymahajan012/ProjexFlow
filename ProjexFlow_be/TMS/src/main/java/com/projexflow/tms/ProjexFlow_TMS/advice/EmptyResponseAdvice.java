package com.projexflow.tms.ProjexFlow_TMS.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collection;
import java.util.Map;

/**
 * Adds a UI-friendly hint header when a successful response is empty.
 * Frontend can read: X-Ui-Message
 */
@ControllerAdvice
public class EmptyResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                 MethodParameter returnType,
                                 MediaType selectedContentType,
                                 Class selectedConverterType,
                                 ServerHttpRequest request,
                                 ServerHttpResponse response) {

        // Only tag successful responses
        if (response instanceof ServletServerHttpResponse servletResp) {
            int status = servletResp.getServletResponse().getStatus();
            if (status >= 200 && status < 300) {
                boolean empty = false;

                if (body == null) {
                    empty = true;
                } else if (body instanceof Collection<?> c) {
                    empty = c.isEmpty();
                } else if (body instanceof Map<?, ?> m) {
                    empty = m.isEmpty();
                } else if (body instanceof String s) {
                    empty = s.isBlank();
                }

                if (empty) {
                    servletResp.getServletResponse().setHeader("X-Ui-Message", "No records found.");
                }
            }
        }

        return body;
    }
}
