package abstractfactory;

import singleton.Book;

public interface AbstractBookFactory {
	Book createBook(String id, String title, String author);

}
