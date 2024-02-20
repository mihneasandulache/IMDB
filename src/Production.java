import java.util.*;

abstract class Production implements Comparable {
    String productionTitle;
    String movieDescription;
    double finalRating;
    String inserterUsername;

    List<String> directors = new ArrayList<String>();
    List<String> actors = new ArrayList<String>();
    List<Genre> genres = new ArrayList<Genre>();
    List<Rating> ratings = new ArrayList<Rating>();
    List<String> comments = new ArrayList<String>();

    public abstract void displayInfo();
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Production))
            return -1;
        Production o1 = (Production) o;
        return productionTitle.compareTo(o1.productionTitle);
    }

    public void addRating(Rating rating) {
        ratings.add(rating);
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

}
