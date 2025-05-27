package strategy;
//Triển khai tính phí tiêu chuẩn
public class StandardFeeCalculator implements FeeCalculator {
	private static final double DAILY_RATE = 20000;

	@Override
	public double calculateFee(long overdueDays) {
		return overdueDays * DAILY_RATE;
	}

}
