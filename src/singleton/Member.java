package singleton;

import java.util.ArrayList;
import java.util.List;

import observer.Observer;
import observer.Subject;

//Lớp Member đại diện cho thành viên trong thư viện
public class Member implements Subject{ 
	private String id;
    private String name;
    private String email;
    private List<Observer> observers = new ArrayList<>();
	public Member(String id, String name, String email) {
		this.id = id;
		this.name = name;
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		notifyObservers("name_changed", this);
	}
	public String getId() {
		return id;
	}
	public String getEmail() {
		return email;
	}
	/*
	 *  Triển khai Observer pattern
	 */
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
