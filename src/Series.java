import java.util.*;
public class Series extends Production{
    int releaseYear;
    int numberOfSeasons;
    private Map<String, List<Episode>> episodesPerSeason;

    public Series(String productionTitle, String movieDescription, double finalRating,
                  List<String> directors, List<String> actors, List<Genre> genres,
                  List<Rating> ratings, int releaseYear, int numberOfSeasons,
                  Map<String, List<Episode>> episodesPerSeason) {
        this.productionTitle = productionTitle;
        this.movieDescription = movieDescription;
        this.finalRating = finalRating;
        this.directors = directors;
        this.actors = actors;
        this.genres = genres;
        this.ratings = ratings;
        this.releaseYear = releaseYear;
        this.numberOfSeasons = numberOfSeasons;
        this.episodesPerSeason = episodesPerSeason;
    }
    public Map<String, List<Episode>> getEpisodesPerSeason() {
        return episodesPerSeason;
    }

    public void setEpisodesPerSeason(Map<String, List<Episode>> episodesPerSeason) {
        this.episodesPerSeason = episodesPerSeason;
    }

    @Override
    public void displayInfo() {
        System.out.println("Series name: " + productionTitle);
        System.out.println("Series description: " + movieDescription);
        System.out.println("Series release year: " + releaseYear);
        System.out.println("Series number of seasons: " + numberOfSeasons);
        System.out.println("Series directors: " + directors);
        System.out.println("Series actors: " + actors);
        System.out.println("Series genres: " + genres);
        for(Rating rating : this.ratings) {
            System.out.println("Rating: " + rating.rating);
            System.out.println("Comments: " + rating.comments);
            System.out.println("User: " + rating.user);
        }
        System.out.println("Series comments: " + comments);
        System.out.println("Series final rating: " + finalRating);
        for(Map.Entry<String, List<Episode>> entry : episodesPerSeason.entrySet()) {
            System.out.println(entry.getKey());
            for(Episode episode : entry.getValue()) {
                System.out.println("Episode Name: " + episode.episodeName);
                System.out.println("Episode Duration: " + episode.episodeDuration);
            }
        }
        System.out.print("----------------------------------------------------------\n");
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Series) {
            Series s = (Series) o;
            return this.productionTitle.compareTo(s.productionTitle);
        }
        return -999;
    }
}
