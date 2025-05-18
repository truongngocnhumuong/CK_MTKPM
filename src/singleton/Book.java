package singleton;

import java.util.ArrayList;
import java.util.List;

import observer.Observer;
import observer.Subject;

//Lớp Book đại diện cho một cuốn sách trong thư viện
public class Book implements Subject{ 
	private String id;
    private String title;
    private String author;
    private String type;
    private List<Observer> observers = new ArrayList<>();
	public Book(String id, String title, String author, String type) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.type = type;
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
	public String getType() {
		return type;
	}
	 // Triển khai Observer pattern
   
	@Override
	public void registerObserver(Observer observer) {
		observers.add(observer);
		
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
