import java.util.ArrayList;

public interface Observer {
    void sendNotification(ArrayList<String> notifications, String notification);
}
