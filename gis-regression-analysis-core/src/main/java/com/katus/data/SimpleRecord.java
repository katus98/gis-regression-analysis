package com.katus.data;

import com.katus.exception.InvalidParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-07
 */
public class SimpleRecord extends AbstractRecord<String> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleRecord.class);

    protected final String SEPARATOR;

    public SimpleRecord(String str, String sep) {
        super();
        this.SEPARATOR = sep;
        init(str);
    }

    @Override
    public double[] load(String s) {
        String[] items = s.split(SEPARATOR);
        if (items.length < 1) {
            logger.error("item length is too short");
            throw new InvalidParamException();
        }
        double[] data = new double[items.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = Double.parseDouble(items[i]);
        }
        return data;
    }

    @Override
    public String put() {
        StringBuilder sb = new StringBuilder();
        sb.append(y);
        for (int i = 0; i < xSize(); i++) {
            sb.append(SEPARATOR).append(x(i));
        }
        return sb.toString();
    }
}
