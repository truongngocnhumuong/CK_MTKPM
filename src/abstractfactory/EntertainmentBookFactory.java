package abstractfactory;

import singleton.Book;
//Triển khai AbstractBookFactory cho sách giải trí
public class EntertainmentBookFactory implements AbstractBookFactory{

	@Override
	public Book createBook(String id, String title, String author) {
		return new Book(id, title, author, "entertainment");
	}

}
