package factory;

import abstractfactory.AbstractBookFactory;
import singleton.Book;
//Triển khai BookFactory cho sách điện tử
public class EBookFactory implements AbstractBookFactory{

	@Override
	public Book createBook(String id, String title, String author) {
		return new Book(id, title, author, "ebook");
	}

}
