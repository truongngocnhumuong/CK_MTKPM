package strategy;
//Giao diện LateFeeCalculator cho Strategy pattern
public interface LateFeeCalculator {
	double calculateFee(int daysLate);

}
