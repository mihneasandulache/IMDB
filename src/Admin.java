import java.util.List;
public class Admin <T extends Comparable<T>> extends Staff<T>{
    public Admin(Information userInformation, AccountType userType, int userExperience, String username) {
        super(userInformation, userType, userExperience, username);
    }

    public void updateProductionInformation(T production, Information newInfo) {
    }
    public void updateActorInformation(T actor, Information newInfo) {
    }
    public void solveRequest(Request request) {
    }

    public void removeUser(User<T> u, List<User<T>> users) {
        users.remove(u);
    }
    public void addUser(User<T> u, List<User<T>> users) {
        users.add(u);
    }
}
