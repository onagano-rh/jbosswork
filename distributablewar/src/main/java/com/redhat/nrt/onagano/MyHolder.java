package com.redhat.nrt.onagano;

import java.io.Serializable;

public class MyHolder implements Serializable {
    private static final long serialVersionUID = 1L;

    private String value;
    public String getValue() {
        return value;
    }
    public void setValue(String v) {
        value = v;
    }

    private MyHolder2 myHolder2;
    public void setMyHolder2(MyHolder2 mh) {
        myHolder2 = mh;
    }
}
