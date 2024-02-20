import java.util.Observable;

public class Regular<T extends Comparable<T>, arrayList> extends User<T> implements RequestsManager{

    public Regular(Information userInformation, AccountType userType, int userExperience, String username) {
        super(userInformation, userType, userExperience, username);
    }

    public void createRequest(Request request) {
        IMDB.getInstance().requests.add(request);
    }

    public void removeRequest(Request request) {
        IMDB.getInstance().requests.remove(request);
    }

    public void addReview(Rating rating, Production production, String comment) {
        production.addRating(rating);
        production.addComment(comment);
    }

}
