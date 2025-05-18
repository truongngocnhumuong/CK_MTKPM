package strategy;
//Lớp FeeContext quản lý chiến lược tính phí
public class FeeContext {
	private LateFeeCalculator calculator;

	public void setCalculator(LateFeeCalculator calculator) {
		this.calculator = calculator;
	}
	public LateFeeCalculator getCalculator() { return calculator; }
	
	public double calculateFee(int daysLate) {
		return calculator.calculateFee(daysLate);
		
	}

}
