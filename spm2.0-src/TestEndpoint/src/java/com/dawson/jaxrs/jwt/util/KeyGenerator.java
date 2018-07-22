package com.dawson.jaxrs.jwt.util;

import java.security.Key;

/**
 * @author Dawson
 */
public interface KeyGenerator {

    Key generateKey();
}
