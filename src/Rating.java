import java.util.ArrayList;

public class Rating implements Subject{
    String user;
    int rating;
    String comments;
    ArrayList<Observer> observers = new ArrayList<>();
    public Rating(String user, int rating, String comments) {
        this.user = user;
        this.rating = rating;
        this.comments = comments;
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add((User) observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove((User) observer);
    }

    @Override
    public void notifyObservers(String message, int notificationType) {
        if(notificationType == 1) {
            for(Observer observer : observers) {
                observer.sendNotification(((User) observer).userNotifications, message);
                break;
            }
        } else if (notificationType == 2) {
            int i = 0;
            for(Observer observer : observers) {
                if(i == 0) {
                    i++;
                    continue;
                }
                observer.sendNotification(((User) observer).userNotifications, message);
            }
        }
    }
}
