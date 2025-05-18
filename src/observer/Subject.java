package observer;
//Giao diện Subject cho Observer pattern
public interface Subject {
	void registerObserver(Observer observer);
	void removeObserver(Observer observer);
	void notifyObservers(String event, Object data);

}
