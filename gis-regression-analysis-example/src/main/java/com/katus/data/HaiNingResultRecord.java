package com.katus.data;

import com.katus.test.r.TrainedRecordJudgement;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-04
 */
public class HaiNingResultRecord extends AbstractResultRecordWithInfo<HaiNingRecord> implements TrainedRecordJudgement<HaiNingResultRecord> {
    @Override
    public String put() {
        StringBuilder builder = new StringBuilder();
        builder.append(record.id()).append(",").append(y());
        for (int i = 0; i < xSize(); i++) {
            builder.append(",").append(x(i));
        }
        builder.append(",").append(prediction());
        for (int i = 0; i < betaSize(); i++) {
            builder.append(",").append(beta(i));
        }
        builder.append(",").append(rSquare()).append(",").append(record.getLonX()).append(",").append(record.getLatY());
        return builder.toString();
    }

    @Override
    public HaiNingResultRecord clone() throws CloneNotSupportedException {
        return (HaiNingResultRecord) super.clone();
    }

    @Override
    public boolean isTrained(HaiNingResultRecord record) {
        return record.getBaseRecord().getId() % 3 == 1;
    }
}
