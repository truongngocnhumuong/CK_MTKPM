package singleton;

import java.util.Date;
import strategy.LateFeeCalculator;

//Lớp Loan quản lý giao dịch mượn sách
public class Loan {
	private String id;
    private Book book;
    private Member member;
    private Date ngayMuon;
    private Date ngayDenHan;
    private Date ngayTra;
    private boolean feePaid;

	public Loan(String id, Book book, Member member, Date ngayMuon, Date ngayDenHan) {
		this.id = id;
		this.book = book;
		this.member = member;
		this.ngayMuon = ngayMuon;
		this.ngayDenHan = ngayDenHan;
		this.ngayTra = null;
		this.feePaid = false;
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
	public Date getNgayTra() {
		return ngayTra;
	}
	public boolean isFeePaid() {
		return feePaid;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public void setNgayMuon(Date ngayMuon) {
		this.ngayMuon = ngayMuon;
	}
	public void setNgayDenHan(Date ngayDenHan) {
		this.ngayDenHan = ngayDenHan;
	}
	public void setNgayTra(Date ngayTra) {
		this.ngayTra = ngayTra;
	}
	public void setFeePaid(boolean feePaid) {
		this.feePaid = feePaid;
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

    // Cập nhật thông tin mượn trả vào database (nếu LoanManager có hàm updateLoan)
    public void upload() {
        LoanManager.getInstance().updateLoan(this);
    }

    // Đánh dấu đã trả sách, cập nhật ngày trả và lưu vào database
    public void returnBook(Date ngayTra) {
        setNgayTra(ngayTra);
        upload();
    }

    // Đánh dấu đã thanh toán phí, cập nhật trạng thái và lưu vào database
    public void payFee() {
        setFeePaid(true);
        upload();
    }
}
