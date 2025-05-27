package strategy;

public class FlexibleFeeCalculator implements FeeCalculator {
    private static final double BASE_RATE = 20000;
    private static final double DAILY_INCREASE = 5000;

    @Override
    public double calculateFee(long overdueDays) {
        return overdueDays * (BASE_RATE + (overdueDays * DAILY_INCREASE));
    }
} 