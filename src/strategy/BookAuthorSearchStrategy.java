package strategy;

import singleton.Book;
import singleton.BookManager;
import java.util.List;
import java.util.stream.Collectors;

public class BookAuthorSearchStrategy implements SearchStrategy<Book> {
    private BookManager bookManager;

    public BookAuthorSearchStrategy(BookManager bookManager) {
        this.bookManager = bookManager;
    }

    @Override
    public List<Book> search(String query) {
        return bookManager.getAllBooks().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}
