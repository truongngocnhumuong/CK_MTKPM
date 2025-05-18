package observer;

public class UIObserver implements Observer{

	@Override
	public void update(String event, Object data) {
		System.out.println("UI updated: " + event + " - Data: " + data);
		
	}

}
