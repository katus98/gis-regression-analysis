package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2023-02-12
 */
public class JinHuaResultRecord extends AbstractResultRecordWithInfo<JinHuaRecord> {
    @Override
    public String put() {
        StringBuilder builder = new StringBuilder();
        builder.append(record.put()).append(",").append(prediction());
        for (int i = 0; i < betaSize(); i++) {
            builder.append(",").append(beta(i));
        }
        builder.append(",").append(rSquare()).append(",").append(record.getLonX()).append(",").append(record.getLatY());
        return builder.toString();
    }

    @Override
    public JinHuaResultRecord clone() throws CloneNotSupportedException {
        return (JinHuaResultRecord) super.clone();
    }
}
