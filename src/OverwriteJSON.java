import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OverwriteJSON {
    public static void updateUsers() {
        JSONArray updatedUsersArray = new JSONArray();
        for (User user : IMDB.getInstance().users) {
            JSONObject userObject = new JSONObject();

            userObject.put("username", user.username);
            userObject.put("experience", user.userExperience);
            userObject.put("userType", user.userType.toString());

            JSONObject informationObject = new JSONObject();
            informationObject.put("name", user.userInformation.getUserName());
            informationObject.put("country", user.userInformation.getUserCountry());
            informationObject.put("age", user.userInformation.getUserAge());
            informationObject.put("gender", user.userInformation.getUserGender());
            informationObject.put("birthDate", user.userInformation.getBirthDate().toString());

            JSONObject credentialsObject = new JSONObject();
            credentialsObject.put("email", user.userInformation.getUserCredentials().getEmail());
            credentialsObject.put("password", user.userInformation.getUserCredentials().getPassword());

            informationObject.put("credentials", credentialsObject);
            JSONArray productionsContributionArray = new JSONArray();
            ArrayList<Production> productionsContribution = new ArrayList<>();
            ArrayList<Actor> actorsContribution = new ArrayList<>();
            user.setActorsContribution(actorsContribution);
            user.setProductionsContribution(productionsContribution);
            for (Object production : user.productionsContribution) {
                if(production instanceof Movie || production instanceof Series) {
                    productionsContributionArray.add(((Production) production).productionTitle);
                }
            }
            userObject.put("productionsContribution", productionsContributionArray);

            // Add actorsContribution
            JSONArray actorsContributionArray = new JSONArray();
            for (Object actor : user.actorsContribution) {
                if(actor instanceof Actor) {
                    actorsContributionArray.add(((Actor) actor).name);
                }
            }
            userObject.put("actorsContribution", actorsContributionArray);

            // Add favoriteProductions
            JSONArray favoriteProductionsArray = new JSONArray();
            for (Object production : user.userPreferences) {
                if(production instanceof Movie || production instanceof Series) {
                    favoriteProductionsArray.add(((Production) production).productionTitle);
                }
            }
            userObject.put("favoriteProductions", favoriteProductionsArray);

            // Add favoriteActors
            JSONArray favoriteActorsArray = new JSONArray();
            for (Object actor : user.userPreferences) {
                if(actor instanceof Actor) {
                    favoriteActorsArray.add(((Actor) actor).name);
                }
            }
            userObject.put("favoriteActors", favoriteActorsArray);

            // Add notifications
            JSONArray notificationsArray = new JSONArray();
            for (Object notification : user.userNotifications) {
                if(notification instanceof String) {
                    notificationsArray.add(notification);
                }
            }
            userObject.put("notifications", notificationsArray);
            userObject.put("information", informationObject);
            updatedUsersArray.add(userObject);
        }

        try (FileWriter file = new FileWriter("src/accounts.json")) {
            file.write(updatedUsersArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateActors() {
        JSONArray actorsArray = new JSONArray();
        for (Actor actor : IMDB.getInstance().actors) {
            JSONObject actorObject = new JSONObject();

            actorObject.put("name", actor.name);
            actorObject.put("biography", actor.biography);

            JSONArray performancesArray = new JSONArray();
            if(actor.pairList != null) {
                for (Pair<String, ProductionType> pair : actor.pairList) {
                    JSONObject performanceObject = new JSONObject();
                    performanceObject.put("title", pair.name);
                    performanceObject.put("type", pair.productionType.toString());
                    performancesArray.add(performanceObject);
                }
            }
            actorObject.put("performances", performancesArray);

            actorsArray.add(actorObject);
        }

        try (FileWriter file = new FileWriter("src/actors.json")) {
            file.write(actorsArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateProductions() {
        JSONArray productionsArray = new JSONArray();
        for (Production production : IMDB.getInstance().productions) {
            JSONObject productionObject = new JSONObject();

            productionObject.put("title", production.productionTitle);
            if(production instanceof Movie) {
                productionObject.put("type", ProductionType.Movie.toString());
            } else if (production instanceof Series) {
                productionObject.put("type", ProductionType.Series.toString());
            }
            JSONArray directorsArray = new JSONArray();
            if(production.directors != null) {
                for (String director : production.directors) {
                    directorsArray.add(director);
                }
                productionObject.put("directors", directorsArray);
            }

            JSONArray actorsArray = new JSONArray();
            if(production.actors != null) {
                for (String actor : production.actors) {
                    actorsArray.add(actor);
                }
                productionObject.put("actors", actorsArray);
            }

            JSONArray genresArray = new JSONArray();
            if(production.genres != null) {
                for (Genre genre : production.genres) {
                    genresArray.add(genre.toString());
                }
                productionObject.put("genres", genresArray);
            }

            JSONArray ratingsArray = new JSONArray();
            if(production.ratings != null) {
                for (Rating rating : production.ratings) {
                    JSONObject ratingObject = new JSONObject();
                    ratingObject.put("username", rating.user);
                    ratingObject.put("rating", rating.rating);
                    ratingObject.put("comment", rating.comments);
                    ratingsArray.add(ratingObject);
                }
                productionObject.put("ratings", ratingsArray);
            }

            productionObject.put("plot", production.movieDescription);
            if(production.finalRating == 0) {
                productionObject.put("averageRating", "N/A");
            } else {
                productionObject.put("averageRating", production.finalRating);
            }

            if (production instanceof Movie) {
                Movie movie = (Movie) production;
                productionObject.put("duration", movie.movieDuration);
                productionObject.put("releaseYear", movie.releaseYear);
            } else if (production instanceof Series) {
                Series series = (Series) production;
                productionObject.put("releaseYear", series.releaseYear);
                productionObject.put("numSeasons", series.numberOfSeasons);
                if(series.numberOfSeasons != 0) {
                    JSONObject seasonsObject = new JSONObject();
                    Map<String, List<Episode>> episodesPerSeason = series.getEpisodesPerSeason();
                    for (Map.Entry<String, List<Episode>> entry : episodesPerSeason.entrySet()) {
                        JSONArray episodesArray = new JSONArray();
                        for (Episode episode : entry.getValue()) {
                            JSONObject episodeObject = new JSONObject();
                            episodeObject.put("episodeName", episode.episodeName);
                            episodeObject.put("duration", episode.episodeDuration);
                            episodesArray.add(episodeObject);
                        }
                        seasonsObject.put(entry.getKey(), episodesArray);
                    }
                    productionObject.put("seasons", seasonsObject);
                }
            }
            productionsArray.add(productionObject);
        }

        try (FileWriter file = new FileWriter("src/production.json")) {
            file.write(productionsArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateRequests() {
        JSONArray requestsArray = new JSONArray();
        for (Request request : IMDB.getInstance().requests) {
            JSONObject requestObject = new JSONObject();

            requestObject.put("type", request.getRequestType().toString());
            requestObject.put("createdDate", request.getRequestDate().toString());
            requestObject.put("username", request.requesterUsername);
            if(request.actorName != null) {
                requestObject.put("actorName", request.actorName);
            }
            requestObject.put("to", request.solverUsername);
            requestObject.put("description", request.problemDescription);
            if(request.productionTitle != null) {
                requestObject.put("movieTitle", request.productionTitle);
            }

            requestsArray.add(requestObject);
        }

        try (FileWriter file = new FileWriter("src/requests.json")) {
            file.write(requestsArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
