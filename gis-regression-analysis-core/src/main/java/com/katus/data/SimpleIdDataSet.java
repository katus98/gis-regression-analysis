package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-08
 */
public class SimpleIdDataSet extends AbstractDataSet<SimpleIdRecord> {

    public SimpleIdDataSet(DataSetInput<SimpleIdRecord> loader) {
        super(loader);
    }

    public int idToIndex(Long id) {
        return id.intValue() - 1;
    }

    public SimpleIdRecord getRecord(Long id) {
        return getRecord(idToIndex(id));
    }
}
