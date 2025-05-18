package singleton;

import java.util.Date;
import strategy.*;

import strategy.LateFeeCalculator;
//Lớp Loan quản lý giao dịch mượn sách
public class Loan {
	private String id;
    private Book book;
    private Member member;
    private Date ngayMuon;
    private Date ngayDenHan;
    private Date ngayTra;
	public Loan(String id, Book book, Member member, Date ngayMuon, Date ngayDenHan) {
		this.id = id;
		this.book = book;
		this.member = member;
		this.ngayMuon = ngayMuon;
		this.ngayDenHan = ngayDenHan;
	}
	public Date getNgayTra() {
		return ngayTra;
	}
	public void setNgayTra(Date ngayTra) {
		this.ngayTra = ngayTra;
	}
	public String getId() {
		return id;
	}
	public Book getBook() {
		return book;
	}
	public Member getMember() {
		return member;
	}
	public Date getNgayMuon() {
		return ngayMuon;
	}
	public Date getNgayDenHan() {
		return ngayDenHan;
	}
	// Tính phí trễ hạn với Strategy pattern (giai thich ki)
	public double calculateLateFee(LateFeeCalculator calculator) {
        if (ngayTra == null || ngayTra.after(ngayDenHan)) {
            long diffInMillies = ngayTra != null ? ngayTra.getTime() - ngayDenHan.getTime() : 0;
            int daysLate = (int) (diffInMillies / (1000 * 60 * 60 * 24));
            return calculator.calculateFee(daysLate);
        }
        return 0.0;
    }

}
