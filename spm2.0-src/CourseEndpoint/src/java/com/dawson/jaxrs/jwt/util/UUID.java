/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dawson.jaxrs.jwt.util;

import javax.enterprise.inject.Default;

/**
 *
 * @author Dawson
 */
@Default
public interface UUID {
    String build();
}
