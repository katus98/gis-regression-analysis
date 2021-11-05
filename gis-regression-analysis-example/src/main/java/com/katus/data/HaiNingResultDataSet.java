package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-04
 */
public class HaiNingResultDataSet extends AbstractResultDataSet<HaiNingRecord, HaiNingResultRecord> {

    public HaiNingResultDataSet(DataSetInput<HaiNingResultRecord> loader) {
        super(loader);
    }

    @Override
    public HaiNingResultDataSet clone() throws CloneNotSupportedException {
        return (HaiNingResultDataSet) super.clone();
    }
}
