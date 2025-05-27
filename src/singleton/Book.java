package singleton;

import observer.Subject;
import observer.Observer;
import java.util.ArrayList;
import java.util.List;

public class Book implements Subject {
	private String id;
	private String title;
	private String author;
	private String type;
	private boolean favorite;
	private List<Observer> observers;

	public Book(String id, String title, String author, String type, boolean favorite) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.type = type;
		this.favorite = favorite;
		this.observers = new ArrayList<>();
	}

	public Book(String id, String title, String author, String type) {
		this(id, title, author, type, false);
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		notifyObservers("title_changed", this);
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
		notifyObservers("author_changed", this);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		notifyObservers("type_changed", this);
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	@Override
	public void registerObserver(Observer observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	@Override
	public void notifyObservers(String event, Object data) {
		for (Observer observer : observers) {
			observer.update(event, data);
		}
	}
}
