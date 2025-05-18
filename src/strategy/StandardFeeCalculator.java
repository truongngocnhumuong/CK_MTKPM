package strategy;
//Triển khai tính phí tiêu chuẩn
public class StandardFeeCalculator implements LateFeeCalculator{

	@Override
	public double calculateFee(int daysLate) {
		return daysLate > 0 ? daysLate * 1.0 : 0.0; // 1 USD/ngày trễ
	}

}
