package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2023-02-12
 */
public class JinHuaResultDataSet extends AbstractResultDataSet<JinHuaRecord, JinHuaResultRecord> {
    public JinHuaResultDataSet(DataSetInput<JinHuaResultRecord> loader) {
        super(loader);
    }

    @Override
    public JinHuaResultDataSet clone() throws CloneNotSupportedException {
        return (JinHuaResultDataSet) super.clone();
    }
}
