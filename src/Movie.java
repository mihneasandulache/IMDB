import java.util.*;
public class Movie extends Production{
    String movieName;
    String movieDuration;
    int releaseYear;

    public Movie(String productionTitle, String movieDescription, double finalRating,
                 List<String> directors, List<String> actors, List<Genre> genres,
                 List<Rating> ratings, String movieName, String movieDuration, int releaseYear) {
        this.productionTitle = productionTitle;
        this.movieDescription = movieDescription;
        this.finalRating = finalRating;
        if(directors != null) {
            this.directors.addAll(directors);
        }
        if(actors != null) {
            this.actors.addAll(actors);
        }
        if(genres != null) {
            this.genres.addAll(genres);
        }
        if(ratings != null) {
            this.ratings.addAll(ratings);
        }
        this.movieName = movieName;
        this.movieDuration = movieDuration;
        this.releaseYear = releaseYear;
    }

    @Override
    public void displayInfo() {
        System.out.println("Movie name: " + movieName);
        System.out.println("Movie description: " + movieDescription);
        System.out.println("Movie duration: " + movieDuration);
        System.out.println("Movie release year: " + releaseYear);
        System.out.println("Movie directors: " + directors);
        System.out.println("Movie actors: " + actors);
        System.out.println("Movie genres: " + genres);
        for(Rating rating : this.ratings) {
            System.out.println("Rating: " + rating.rating);
            System.out.println("Comments: " + rating.comments);
            System.out.println("User: " + rating.user);
        }
        System.out.println("Movie comments: " + comments);
        System.out.println("Movie final rating: " + finalRating);
        System.out.print("----------------------------------------------------------\n");
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Movie) {
            return this.movieName.compareTo(((Movie) o).movieName);
        }
        return -999;
    }
}
