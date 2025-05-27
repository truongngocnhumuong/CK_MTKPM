package decorator;

import singleton.Book;

public class FavoriteBookDecorator extends BookDecorator {
    private boolean isFavorite;

    public FavoriteBookDecorator(Book book) {
        super(book);
        this.isFavorite = false;
    }

    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
        book.setFavorite(favorite);
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    @Override
    public void setTitle(String title) {
        book.setTitle(title);
    }

    @Override
    public void setAuthor(String author) {
        book.setAuthor(author);
    }

    @Override
    public void setType(String type) {
        book.setType(type);
    }

    @Override
    public String getDescription() {
        return book.getTitle() + (isFavorite ? " ⭐" : "");
    }

    @Override
    public String toString() {
        return getDescription();
    }
}