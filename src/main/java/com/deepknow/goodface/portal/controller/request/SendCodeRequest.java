package com.deepknow.goodface.portal.controller.request;

import lombok.Data;

import java.io.Serializable;


@Data
public class SendCodeRequest implements Serializable {
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}