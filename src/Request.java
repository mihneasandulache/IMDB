import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Request implements Subject{
    private RequestType requestType;
    private LocalDateTime requestDate;
    String productionTitle;
    String actorName;
    String problemDescription;
    String requesterUsername;
    String solverUsername;
    ArrayList<Observer> observers = new ArrayList<>();
    public Request(RequestType requestType, LocalDateTime requestDate, String productionTitle, String actorName, String problemDescription, String requesterUsername, String solverUsername) {
        this.requestType = requestType;
        this.requestDate = requestDate;
        this.productionTitle = productionTitle;
        this.actorName = actorName;
        this.problemDescription = problemDescription;
        this.requesterUsername = requesterUsername;
        this.solverUsername = solverUsername;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
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
            //1->cel care a facut request-ul
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

    public void displayInfo(Request request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formatDateTime = requestDate.format(formatter);
        System.out.println("Request type: " + requestType);
        System.out.println("Request date: " + formatDateTime);
        System.out.println("Production title: " + productionTitle);
        System.out.println("Actor name: " + actorName);
        System.out.println("Problem description: " + problemDescription);
    }
}
