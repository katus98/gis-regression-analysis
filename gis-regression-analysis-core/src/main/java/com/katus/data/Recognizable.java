package com.katus.data;

import java.io.Serializable;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-07
 */
public interface Recognizable<ID extends Serializable> {

    ID id();

    void setId(ID id);

    default ID getId() {
        return id();
    }
}
