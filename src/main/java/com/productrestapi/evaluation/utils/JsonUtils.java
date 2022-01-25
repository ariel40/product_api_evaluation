package com.productrestapi.evaluation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper o = new ObjectMapper();
            o.registerModule(new JavaTimeModule());
            return o.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
