package com.katus.regression.weight;

import com.katus.data.AbstractDataSet;
import com.katus.data.Record;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-09
 */
public abstract class WeightCalculator<R extends Record> implements Cloneable {
    protected final BandwidthType bandwidthType;
    protected final WeightType weightType;
    protected final double bandwidth;
    protected final AbstractDataSet<R> trainingDataSet;
    protected final WeightFunction weightFunction;
    protected Map<R, Double> distances = null;

    protected WeightCalculator(BandwidthType bandwidthType, WeightType weightType, double bandwidth, AbstractDataSet<R> trainingDataSet, WeightFunction weightFunction) {
        this.bandwidthType = bandwidthType;
        this.weightType = weightType;
        this.bandwidth = bandwidth;
        this.trainingDataSet = trainingDataSet;
        this.weightFunction = weightFunction;
    }

    public INDArray calWeightMatrix(R r1) {
        initDistances(r1);
        INDArray matrix = Nd4j.eye(trainingDataSet.size());
        for (int i = 0; i < trainingDataSet.size(); i++) {
            R r2 = trainingDataSet.getRecord(i);
            matrix.putScalar(new int[]{i, i}, calWeight(r2));
        }
        return matrix;
    }

    public boolean[] calBoolArray(R r1) {
        initDistances(r1);
        boolean[] array = new boolean[trainingDataSet.size()];
        for (int i = 0; i < trainingDataSet.size(); i++) {
            R r2 = trainingDataSet.getRecord(i);
            array[i] = withInBandwidth(r2);
        }
        return array;
    }

    public abstract double calDistance(R r1, R r2);

    private double calWeight(R r2) {
        double weight;
        if (withInBandwidth(r2)) {
            switch (weightType) {
                case BI_SQUARE:
                    weight = Math.pow(1 - Math.pow(getDistance(r2) / getBandwidth(), 2), 2);
                    break;
                case GAUSSIAN:
                    weight = Math.pow(Math.E, -Math.pow(getDistance(r2) / getBandwidth(), 2));
                    break;
                case CUSTOMIZED:
                    weight = weightFunction.weight(getDistance(r2), getBandwidth());
                    break;
                default:
                    weight = 0.0;
            }
        } else {
            weight = 0.0;
        }
        return weight;
    }

    private boolean withInBandwidth(R r2) {
        return getDistance(r2) <= getBandwidth();
    }

    private double getDistance(R r2) {
        return distances.get(r2);
    }

    private double getBandwidth() {
        double band = 0.0;
        switch (bandwidthType) {
            case FIXED:
                band = bandwidth;
                break;
            case ADAPTIVE:
                if (distances.size() <= bandwidth) {
                    band = Double.MAX_VALUE;
                } else {
                    int i = 0;
                    for (Map.Entry<R, Double> entry : distances.entrySet()) {
                        if (++i >= Math.round(bandwidth)) {
                            band = entry.getValue();
                            break;
                        }
                    }
                }
                break;
            default:
                band = Double.MAX_VALUE;
        }
        return band;
    }

    private void initDistances(R r1) {
        this.distances = new HashMap<>();
        for (int i = 0; i < trainingDataSet.size(); i++) {
            R record = trainingDataSet.getRecord(i);
            distances.put(record, calDistance(r1, record));
        }
        this.distances = distances.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    @Override
    @SuppressWarnings("unchecked")
    public WeightCalculator<R> clone() throws CloneNotSupportedException {
        return (WeightCalculator<R>) super.clone();
    }

    public abstract static class WeightCalculatorBuilder<R extends Record> {
        protected BandwidthType bandwidthType = BandwidthType.FIXED;
        protected WeightType weightType = WeightType.BI_SQUARE;
        protected double bandwidth = 0.0;
        protected AbstractDataSet<R> trainingDataSet = null;
        protected WeightFunction weightFunction = null;

        public abstract WeightCalculator<R> build();

        public boolean check() {
            return bandwidth > 0.0 && trainingDataSet != null && (!WeightType.CUSTOMIZED.equals(weightType) || weightFunction != null);
        }

        public WeightCalculatorBuilder<R> bandwidthType(BandwidthType bandwidthType) {
            this.bandwidthType = bandwidthType;
            return this;
        }

        public WeightCalculatorBuilder<R> weightType(WeightType weightType) {
            this.weightType = weightType;
            return this;
        }

        public WeightCalculatorBuilder<R> bandwidth(double bandwidth) {
            this.bandwidth = bandwidth;
            return this;
        }

        public WeightCalculatorBuilder<R> trainingDataSet(AbstractDataSet<R> trainingDataSet) {
            this.trainingDataSet = trainingDataSet;
            return this;
        }

        public WeightCalculatorBuilder<R> weightFunction(WeightFunction weightFunction) {
            this.weightFunction = weightFunction;
            return this;
        }
    }
}
