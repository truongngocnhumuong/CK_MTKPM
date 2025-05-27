package decorator;

import singleton.Book;

public abstract class BookDecorator extends Book {
    protected Book book;

    public BookDecorator(Book book) {
        super(book.getId(), book.getTitle(), book.getAuthor(), book.getType());
        this.book = book;
    }

    @Override
    public String getTitle() {
        return book.getTitle();
    }

    @Override
    public String getAuthor() {
        return book.getAuthor();
    }

    @Override
    public String getType() {
        return book.getType();
    }

    public abstract String getDescription();
}