package factory;

import singleton.Book;

//Triển khai BookFactory cho sách giấy
public class PaperBookFactory implements BookFactory{

	@Override
	public Book createBook(String id, String title, String author) {
		return new Book(id, title, author, "paper");
	}

}
