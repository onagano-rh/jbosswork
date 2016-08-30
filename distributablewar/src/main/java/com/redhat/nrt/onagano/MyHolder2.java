package com.redhat.nrt.onagano;

import java.io.Serializable;

public class MyHolder2 implements Serializable {
    private static final long serialVersionUID = 1L;

    private String value;
    public String getValue() {
        return value;
    }
    public void setValue(String v) {
        value = v;
    }

    private NonSerializable myHolder;
    public void setMyHolder(NonSerializable mh) {
        myHolder = mh;
    }
}
