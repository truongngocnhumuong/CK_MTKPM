package strategy;
//Triển khai tính phí cao cấp
public class PremiumFeeCalculator implements FeeCalculator {
	private static final double DAILY_RATE = 40000;

	@Override
	public double calculateFee(long overdueDays) {
		return overdueDays * DAILY_RATE;
	}

}
