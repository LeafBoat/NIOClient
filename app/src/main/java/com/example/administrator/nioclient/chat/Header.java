package com.example.administrator.nioclient.chat;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/6.
 */

public class Header {

    private final List<Pair<String, String>> headers;

    public Header(Builder builder) {
        headers = builder.headers;
    }

    public static class Builder {

        List<Pair<String, String>> headers = new ArrayList<>();

        public Builder addHeader(String name, String value) {
            Pair<String, String> pair = new Pair<>(name, value);
            headers.add(pair);
            return this;
        }

        public Header build() {
            return new Header(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0, size = headers.size(); i < size; i++) {
            result.append(headers.get(i).first).append(":").append(headers.get(i).second).append("\n");
        }
        return result.toString();
    }
}
