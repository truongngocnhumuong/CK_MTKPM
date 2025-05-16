package factory;
//Giao diện BookFactory cho Factory Method pattern

import singleton.Book;

public interface BookFactory {
	Book createBook(String id, String title, String author);

}
