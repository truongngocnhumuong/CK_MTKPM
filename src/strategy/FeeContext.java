package strategy;

public class FeeContext {
	private FeeCalculator calculator;

	public void setCalculator(FeeCalculator calculator) {
		this.calculator = calculator;
	}

	public double calculateFee(long overdueDays) {
		return calculator != null ? calculator.calculateFee(overdueDays) : 0;
	}
}
