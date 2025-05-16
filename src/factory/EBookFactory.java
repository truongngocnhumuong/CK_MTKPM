package factory;

import singleton.Book;
//Triển khai BookFactory cho sách điện tử
public class EBookFactory implements BookFactory{

	@Override
	public Book createBook(String id, String title, String author) {
		return new Book(id, title, author, "ebook");
	}

}
