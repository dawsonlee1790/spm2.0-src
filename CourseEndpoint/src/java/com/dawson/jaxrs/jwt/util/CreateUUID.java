/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dawson.jaxrs.jwt.util;

/**
 *
 * @author Dawson
 */
public class CreateUUID implements UUID {

    // ======================================
    // =          Business methods          =
    // ======================================
    @Override
    public String build() {
        String uuid = java.util.UUID.randomUUID().toString();	//获取UUID并转化为String对象
        uuid = uuid.replace("-", "");				//因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可
        System.out.println(uuid);
        return uuid;
    }
}
