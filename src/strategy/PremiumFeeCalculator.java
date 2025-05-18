package strategy;
//Triển khai tính phí cao cấp
public class PremiumFeeCalculator implements LateFeeCalculator{

	@Override
	public double calculateFee(int daysLate) {
		return daysLate > 0 ? daysLate * 2.0 : 0.0; // 2 USD/ngày trễ
	}

}
