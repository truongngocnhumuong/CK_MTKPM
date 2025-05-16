package singleton;
//Lớp BookManager quản lý danh sách sách

import java.util.ArrayList;
import java.util.List;

public class BookManager {
	private static BookManager instanse;
	private List<Book> books = new ArrayList<>();
	private BookManager() {} // Constructor private để đảm bảo Singleton
	
	public static synchronized BookManager getInstanse() {
		if(instanse == null) {
			instanse = new BookManager();
		}
		return instanse;
	}
	//Thêm, xóa, cập nhật sách
	public void addBook(Book book ) {
		books.add(book);
		
	}
	public void removeBook(String bookId) {
		books.removeIf(b -> b.getId().equals(bookId));
		
	}
	public void updateBook(Book book) {
		removeBook(book.getId());
		addBook(book);
		
	}
	public Book getBook(String bookId) { 
		return books.stream().filter(b -> b.getId().equals(bookId)).findFirst().orElse(null); 
	}
	

}
