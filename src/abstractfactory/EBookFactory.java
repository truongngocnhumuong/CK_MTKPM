package abstractfactory;

import singleton.Book;

public class EBookFactory implements AbstractBookFactory {
    @Override
    public Book createBook(String id, String title, String author) {
        return new Book(id, title, author, "ebook");
    }
} 