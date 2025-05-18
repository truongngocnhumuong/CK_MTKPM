package abstractfactory;

import singleton.Book;
//Triển khai AbstractBookFactory cho sách học thuật
public class AcademicBookFactory implements AbstractBookFactory{

	@Override
	public Book createBook(String id, String title, String author) {
		return new Book(id, title, author, "academic");
	}

}
