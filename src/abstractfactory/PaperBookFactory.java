package abstractfactory;

import singleton.Book;

public class PaperBookFactory implements AbstractBookFactory {
    @Override
    public Book createBook(String id, String title, String author) {
        return new Book(id, title, author, "paper");
    }
} 