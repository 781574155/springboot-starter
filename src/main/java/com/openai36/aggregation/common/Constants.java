package com.openai36.aggregation.common;

import java.util.regex.Pattern;

public class Constants {
    public static final String DEFAULT = "default";
    public static final String ADMIN = "admin";

    public static final Pattern WECHAT_PATTERN = Pattern.compile("MicroMessenger", Pattern.CASE_INSENSITIVE);

    public static final String Bearer = "Bearer ";
}
