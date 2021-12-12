package com.github.sujankumarmitra.ebookprocessor.v1.config;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public abstract class RedisProperties {
    public abstract String getUrl();
    public abstract String getPassword();
    public abstract Long getDefaultKeyExpirationInMilliseconds();
}
