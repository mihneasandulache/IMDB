import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class IMDB {

    private static IMDB instance = null;
    ArrayList<User> users = new ArrayList<>();
    ArrayList<Production> productions = new ArrayList<>();
    ArrayList<Actor> actors = new ArrayList<>();
    ArrayList<Request> requests = new ArrayList<>();

    public static IMDB getInstance() {
        if (instance == null) {
            instance = new IMDB();
        }
        return instance;
    }

    public void parseActors() throws FileNotFoundException, IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/actors.json");
        Object obj = jsonParser.parse(reader);
        JSONArray actorsArray = (JSONArray) obj;

        for (Object actorObj : actorsArray) {
            JSONObject jsonObject = (JSONObject) actorObj;
            String name = (String) jsonObject.get("name");
            ArrayList<Pair<String, ProductionType>> titleTypePair = new ArrayList<>();
            JSONArray array = (JSONArray) jsonObject.get("performances");

            for (int i = 0; i < array.size(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                String title = (String) object.get("title");
                String type = (String) object.get("type");
                ProductionType productionType = ProductionType.valueOf(type);
                Pair<String, ProductionType> pair = new Pair<>(title, productionType);
                titleTypePair.add(pair);
            }

            String biography = (String) jsonObject.get("biography");
            String inserterUsername = " ";
            Actor actor = new Actor(name, titleTypePair, biography, inserterUsername);
            IMDB.getInstance().actors.add(actor);
        }
    }

    public void parseProductions() throws FileNotFoundException, IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/production.json");
        Object obj = jsonParser.parse(reader);
        JSONArray productionsArray = (JSONArray) obj;
        for (Object productionObj : productionsArray) {
            JSONObject jsonObject = (JSONObject) productionObj;
            String title = (String) jsonObject.get("title");
            String type = (String) jsonObject.get("type");
            JSONArray array = (JSONArray) jsonObject.get("directors");
            ArrayList<String> directors = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                String director = (String) array.get(i);
                directors.add(director);
            }
            array = (JSONArray) jsonObject.get("actors");
            ArrayList<String> actors = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                String actor = (String) array.get(i);
                int found = 0;
                for (Actor actor1 : IMDB.getInstance().actors) {
                    if (actor1.name.equals(actor)) {
                        actors.add(actor);
                        found = 1;
                        break;
                    }
                }
                if (found == 0) {
                    Actor actor1 = new Actor(actor, null, null, null);
                    actor1.pairList = new ArrayList<Pair<String, ProductionType>>();
                    actor1.inserterUsername = AccountType.Admin.toString();
                    actor1.addPair(title, ProductionType.valueOf(type));
                    IMDB.getInstance().actors.add(actor1);
                    actors.add(actor);
                }
            }
            array = (JSONArray) jsonObject.get("genres");
            ArrayList<Genre> genres = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                String genre = (String) array.get(i);
                genres.add(Genre.valueOf(genre));
            }
            array = (JSONArray) jsonObject.get("ratings");
            ArrayList<Rating> ratings = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                String username = (String) object.get("username");
                Long longGrade = (Long) object.get("rating");
                int grade = longGrade.intValue();
                String comment = (String) object.get("comment");
                Rating rating = new Rating(username, grade, comment);
                ratings.add(rating);
            }
            String plot = (String) jsonObject.get("plot");
            double averageRating;
            Object averageRatingObj = jsonObject.get("averageRating");
            if (averageRatingObj != null) {
                String averageRatingString = String.valueOf(averageRatingObj);
                try {
                    averageRating = Double.parseDouble(averageRatingString);
                } catch (NumberFormatException e) {
                    averageRating = 0;
                }
            } else {
                averageRating = 0;
            }
            if (type.equals("Movie")) {
                String duration = (String) jsonObject.get("duration");
                Long longReleaseYear = (Long) jsonObject.get("releaseYear");
                int releaseYear;
                if (longReleaseYear != null) {
                    releaseYear = longReleaseYear.intValue();
                } else {
                    releaseYear = 0;
                }
                Movie movie = new Movie(title, plot, averageRating, directors, actors, genres, ratings, title, duration, releaseYear);
                IMDB.getInstance().productions.add(movie);
            } else {
                Long longReleaseYear = (Long) jsonObject.get("releaseYear");
                int releaseYear = longReleaseYear.intValue();
                Long longNumberOfSeasons = (Long) jsonObject.get("numSeasons");
                int numberOfSeasons = longNumberOfSeasons.intValue();
                JSONObject seasonsObject = (JSONObject) jsonObject.get("seasons");
                Map<String, List<Episode>> episodesPerSeason = new LinkedHashMap<>();
                for (int i = 1; i <= numberOfSeasons; i++) {
                    JSONArray episodesArray = (JSONArray) seasonsObject.get("Season " + i);
                    List<Episode> episodes = new ArrayList<>();
                    for (int j = 0; j < episodesArray.size(); j++) {
                        JSONObject episodeObject = (JSONObject) episodesArray.get(j);
                        String episodeTitle = (String) episodeObject.get("episodeName");
                        String episodeDuration = (String) episodeObject.get("duration");
                        Episode episode = new Episode(episodeTitle, episodeDuration);
                        episodes.add(episode);
                    }
                    episodesPerSeason.put("Season " + i, episodes);
                }
                Series series = new Series(title, plot, averageRating, directors, actors, genres, ratings, releaseYear, numberOfSeasons, episodesPerSeason);
                IMDB.getInstance().productions.add(series);
            }
        }
    }

    public void parseAccounts() throws FileNotFoundException, IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/accounts.json");
        Object obj = jsonParser.parse(reader);
        JSONArray accountsArray = (JSONArray) obj;
        for (Object accountObject : accountsArray) {
            JSONObject jsonObject = (JSONObject) accountObject;
            String username = (String) jsonObject.get("username");
            int experience;
            long longExperience = 0L;
            Object experienceObj = jsonObject.get("experience");
            if (experienceObj != null) {
                String experienceString = String.valueOf(experienceObj);
                try {
                    longExperience = Long.parseLong(experienceString);
                } catch (NumberFormatException e) {
                    experience = 0;
                }
            }
            experience = Integer.parseInt(String.valueOf(longExperience));
            AccountType accountType = AccountType.valueOf((String) jsonObject.get("userType"));
            User user = UserFactory.createUser(null, accountType, experience, username);
            JSONObject informationObject = (JSONObject) jsonObject.get("information");
            JSONObject credentialsObject = (JSONObject) informationObject.get("credentials");
            String email = (String) credentialsObject.get("email");
            String password = (String) credentialsObject.get("password");
            String name = (String) informationObject.get("name");
            String country = (String) informationObject.get("country");
            Long longAge = (Long) informationObject.get("age");
            int age = longAge.intValue();
            String gender = (String) informationObject.get("gender");
            LocalDate birthDate = LocalDate.parse((String) informationObject.get("birthDate"));
            JSONArray productionsContribution = (JSONArray) jsonObject.get("productionsContribution");
            if (productionsContribution != null) {
                for (int i = 0; i < productionsContribution.size(); i++) {
                    String productionTitle = (String) productionsContribution.get(i);
                    for (Production production : IMDB.getInstance().productions) {
                        if (production.productionTitle.equals(productionTitle)) {
                            production.inserterUsername = username;
                            break;
                        }
                    }
                }
            }
            JSONArray actorsContribution = (JSONArray) jsonObject.get("actorsContribution");
            if (actorsContribution != null) {
                for (int i = 0; i < actorsContribution.size(); i++) {
                    String actorName = (String) actorsContribution.get(i);
                    for (Actor actor : IMDB.getInstance().actors) {
                        if (actor.name.equals(actorName)) {
                            actor.inserterUsername = username;
                            break;
                        }
                    }
                }
            }
            JSONArray favoriteProductions = (JSONArray) jsonObject.get("favoriteProductions");
            if (favoriteProductions != null) {
                for (int i = 0; i < favoriteProductions.size(); i++) {
                    String productionTitle = (String) favoriteProductions.get(i);
                    for (Production production : IMDB.getInstance().productions) {
                        if (production.productionTitle.equals(productionTitle) && !user.userPreferences.contains(production)) {
                            user.userPreferences.add(production);
                            break;
                        }
                    }
                }
            }
            JSONArray favoriteActors = (JSONArray) jsonObject.get("favoriteActors");
            if (favoriteActors != null) {
                for (int i = 0; i < favoriteActors.size(); i++) {
                    String actorName = (String) favoriteActors.get(i);
                    for (Actor actor : IMDB.getInstance().actors) {
                        if (actor.name.equals(actorName) && !user.userPreferences.contains(actor)) {
                            user.userPreferences.add(actor);
                            break;
                        }
                    }
                }
            }
            JSONArray notifications = (JSONArray) jsonObject.get("notifications");
            if (notifications != null) {
                for (int i = 0; i < notifications.size(); i++) {
                    String notification = (String) notifications.get(i);
                    user.userNotifications.add(notification);
                }
            }
            User.InformationBuilder builder = new User.InformationBuilder();
            user.userInformation = builder
                    .credentials(new Credentials(email, password))
                    .userName(name)
                    .userCountry(country)
                    .userAge(age)
                    .userGender(gender)
                    .birthDate(birthDate)
                    .build(user);
            IMDB.getInstance().users.add(user);
        }
    }

    public void parseRequests() throws FileNotFoundException, IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/requests.json");
        Object obj = jsonParser.parse(reader);
        JSONArray requestsArray = (JSONArray) obj;
        for (Object requestObject : requestsArray) {
            JSONObject jsonObject = (JSONObject) requestObject;
            RequestType requestType = RequestType.valueOf((String) jsonObject.get("type"));
            LocalDateTime requestDate = LocalDateTime.parse((String) jsonObject.get("createdDate"));
            String username = (String) jsonObject.get("username");
            String actorName = (String) jsonObject.get("actorName");
            String solver = (String) jsonObject.get("to");
            String description = (String) jsonObject.get("description");
            String movieTitle = (String) jsonObject.get("movieTitle");
            Request request = new Request(requestType, requestDate, movieTitle, actorName, description, username, solver);
            IMDB.getInstance().requests.add(request);
        }
    }

    public void assignateRequests() {
        for (Request request : IMDB.getInstance().requests) {
            if (request.solverUsername.equals("Admin")) {
                RequestsHolderMain.RequestsHolder.addRequest(request);
            }
        }
    }

    public void moveRequests() {
        for(Request request : RequestsHolderMain.RequestsHolder.getRequests()) {
            IMDB.getInstance().requests.add(request);
            request.solverUsername = "Admin";
        }
    }

    public void createDeleteRequests(User user) throws InvalidCommandException {
        System.out.println("Choose an option:");
        System.out.println("\t1. Create a request");
        System.out.println("\t2. Delete a request");
        System.out.println("\t3. View requests");
        System.out.print("Enter option:");
        Scanner scanner3 = new Scanner(System.in);
        int option2 = scanner3.nextInt();
        if (option2 == 1) {
            int found = 0;
            while (found == 0) {
                System.out.println("Choose an option:");
                System.out.println("\t1. Create a request for an actor");
                System.out.println("\t2. Create a request for a production");
                System.out.println("\t3. Delete my account");
                System.out.println("\t4. Other request");
                System.out.print("Enter option:");
                Scanner scanner4 = new Scanner(System.in);
                int option3 = scanner4.nextInt();
                if (option3 == 1) {
                    System.out.print("Enter actor name:");
                    Scanner scanner5 = new Scanner(System.in);
                    String actorName = scanner5.nextLine();
                    Actor actor = null;
                    for (Actor actor1 : IMDB.getInstance().actors) {
                        if (actor1.name.equals(actorName)) {
                            if((actor1.inserterUsername.equals(user.username)) || (user.userType.equals(AccountType.Admin) && actor1.inserterUsername.equals(AccountType.Admin.toString()))) {
                                System.out.println("You cannot create a request for this actor!");
                                break;
                            }
                            actor = actor1;
                            found = 1;
                            break;
                        }
                    }
                    if (actor == null) {
                        System.out.println("Actor not found!");
                    } else {
                        System.out.print("Enter description:");
                        Scanner scanner6 = new Scanner(System.in);
                        String description = scanner6.nextLine();
                        Request request = new Request(RequestType.ACTOR_ISSUE, LocalDateTime.now(), null, actorName, description, user.username, actor.inserterUsername);
                        IMDB.getInstance().requests.add(request);
                        request.registerObserver(user);
                        boolean found1 = false;
                        for(User user1 : users) {
                            if(user1.username.equals(actor.inserterUsername)) {
                                request.registerObserver(user1);
                                request.notifyObservers("You have a new request regarding " + actorName + "from " + request.requesterUsername + "!", 2);
                                found1 = true;
                                break;
                            }
                        }
                        if(!found1) {
                            for(User user1 : users) {
                                if(user1.userType.equals(AccountType.Admin)) {
                                    request.registerObserver(user1);
                                    request.notifyObservers("You have a new request regarding " + actorName + "from " + request.requesterUsername + "!", 2);
                                    break;
                                }
                            }
                        }
                    }
                } else if (option3 == 2) {
                    System.out.print("Enter production name:");
                    Scanner scanner5 = new Scanner(System.in);
                    String productionName = scanner5.nextLine();
                    Production production = null;
                    for (Production production1 : IMDB.getInstance().productions) {
                        if (production1.productionTitle.equals(productionName)) {
                            if((production1.inserterUsername.equals(user.username)) || (user.userType.equals(AccountType.Admin) && production1.inserterUsername.equals(AccountType.Admin.toString()))) {
                                System.out.println("You cannot create a request for this production!");
                                break;
                            }
                            production = production1;
                            found = 1;
                            break;
                        }
                    }
                    if (production == null) {
                        System.out.println("Production not found!");
                    } else {
                        System.out.print("Enter description:");
                        Scanner scanner6 = new Scanner(System.in);
                        String description = scanner6.nextLine();
                        Request request = new Request(RequestType.MOVIE_ISSUE, LocalDateTime.now(), productionName, null, description, user.username, production.inserterUsername);
                        IMDB.getInstance().requests.add(request);
                        request.registerObserver(user);
                        for(User user1 : IMDB.getInstance().users) {
                            if(user1.username.equals(production.inserterUsername)) {
                                request.registerObserver(user1);
                                request.notifyObservers("You have a new request regarding " + productionName + "from " + request.requesterUsername + "!", 2);
                                break;
                            }
                        }
                    }
                } else if (option3 == 3) {
                    System.out.print("Enter description:");
                    Scanner scanner6 = new Scanner(System.in);
                    String description = scanner6.nextLine();
                    Request request = new Request(RequestType.DELETE_ACCOUNT, LocalDateTime.now(), null, null, description, user.username, "Admin");
                    RequestsHolderMain.RequestsHolder.addRequest(request);
                    moveRequests();
                    request.registerObserver(user);
                    found = 1;
                    for(User user1 : IMDB.getInstance().users) {
                        if(user1.userType.equals(AccountType.Admin)) {
                            request.registerObserver(user1);
                            request.notifyObservers("You have a new delete request from " + user.username + "!", 2);
                        }
                    }
                } else if (option3 == 4) {
                    System.out.print("Enter description:");
                    Scanner scanner6 = new Scanner(System.in);
                    String description = scanner6.nextLine();
                    Request request = new Request(RequestType.OTHERS, LocalDateTime.now(), null, null, description, user.username, "Admin");
                    RequestsHolderMain.RequestsHolder.addRequest(request);
                    moveRequests();
                    found = 1;
                    request.registerObserver(user);
                    for(User user1 : IMDB.getInstance().users) {
                        if(user1.userType.equals(AccountType.Admin)) {
                            request.registerObserver(user1);
                            request.notifyObservers("You have a new request from " + user.username + "!", 2);
                        }
                    }
                } else {
                    throw new InvalidCommandException("Invalid option!");
                }
                if (found == 1) {
                    break;
                }
            }
        } else if (option2 == 2) {
            int i = 0;
            for (Request request : IMDB.getInstance().requests) {
                if (request.requesterUsername.equals(user.username) || ((request.requesterUsername.equals(AccountType.Admin.toString()) && user.userType.equals(AccountType.Admin)))) {
                    System.out.println("Request ID: " + i);
                    System.out.println("Request type: " + request.getRequestType());
                    System.out.println("Request date: " + request.getRequestDate());
                    System.out.println("Request description: " + request.problemDescription);
                    System.out.println();
                    i++;
                }
            }
            System.out.print("Enter request ID:");
            Scanner scanner4 = new Scanner(System.in);
            int requestID = scanner4.nextInt();
            int found = 0;
            i = 0;
            for (Request request : IMDB.getInstance().requests) {
                if (request.requesterUsername.equals(user.username) || ((request.requesterUsername.equals(AccountType.Admin.toString()) && user.userType.equals(AccountType.Admin)))) {
                    if (i == requestID) {
                        requests.remove(request);
                        found = 1;
                        break;
                    }
                    i++;
                }
            }
            if (found == 0) {
                System.out.println("Request not found!");
            }
        } else {
            int i = 0;
            for (Request request : IMDB.getInstance().requests) {
                if (request.requesterUsername.equals(user.username) || ((request.requesterUsername.equals(AccountType.Admin.toString()) && user.userType.equals(AccountType.Admin)))) {
                    System.out.println("Request ID: " + i);
                    System.out.println("Request type: " + request.getRequestType());
                    System.out.println("Request date: " + request.getRequestDate());
                    System.out.println("Request description: " + request.problemDescription);
                    System.out.println();
                    i++;
                }
            }
        }
    }


    public void recalculateAverageProductionRating(Production production) {
        double sum = 0;
        for (Rating rating : production.ratings) {
            sum += rating.rating;
        }
        production.finalRating = sum / production.ratings.size();
    }

    public void addProduction(User user, String productionTitle) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\t\tEnter production type: ");
        ProductionType productionType = ProductionType.valueOf(scanner.nextLine());
        System.out.print("\t\tEnter description: ");
        String plot = scanner.nextLine();
        if(productionType.equals(ProductionType.Movie)) {
            System.out.print("\t\tEnter duration: ");
            String duration = scanner.nextLine();
            System.out.print("\t\tEnter release year: ");
            int releaseYear = scanner.nextInt();
            System.out.print("\t\tEnter actors(type done when finishing the list): ");
            ArrayList<String> actors = new ArrayList<>();
            String actorName = scanner.nextLine();
            while(!actorName.equals("done")) {
                actors.add(actorName);
                actorName = scanner.nextLine();
            }
            System.out.print("\t\tEnter directors(type done when finishing the list): ");
            ArrayList<String> directors = new ArrayList<>();
            String directorName = scanner.nextLine();
            while(!directorName.equals("done")) {
                directors.add(directorName);
                directorName = scanner.nextLine();
            }
            System.out.print("\t\tEnter genres(type done when finishing the list): ");
            ArrayList<Genre> genres = new ArrayList<>();
            String genreName = scanner.nextLine();
            while(!genreName.equals("done")) {
                genres.add(Genre.valueOf(genreName));
                genreName = scanner.nextLine();
            }
            Movie movie = new Movie(productionTitle, plot, 0, directors, actors, genres, null, productionTitle, duration, releaseYear);
            IMDB.getInstance().productions.add(movie);
            ((Staff)user).addedActorsAndProductions.add(movie);
            movie.inserterUsername = user.username;
        } else {
            System.out.print("\t\tEnter number of seasons: ");
            int numberOfSeasons = scanner.nextInt();
            Map<String, List<Episode>> episodesPerSeason = new LinkedHashMap<>();
            for (int i = 1; i <= numberOfSeasons; i++) {
                System.out.print("\t\tEnter number of episodes for season " + i + ": ");
                int numberOfEpisodes = scanner.nextInt();
                List<Episode> episodes = new ArrayList<>();
                for (int j = 1; j <= numberOfEpisodes; j++) {
                    System.out.print("\t\tEnter episode title: ");
                    String episodeTitle = scanner.nextLine();
                    System.out.print("\t\tEnter episode duration: ");
                    String episodeDuration = scanner.nextLine();
                    Episode episode = new Episode(episodeTitle, episodeDuration);
                    episodes.add(episode);
                }
                episodesPerSeason.put("Season " + i, episodes);
            }
            System.out.print("\t\tEnter actors(type done when finishing the list): ");
            ArrayList<String> actors = new ArrayList<>();
            String actorName = scanner.nextLine();
            while (!actorName.equals("done")) {
                actors.add(actorName);
                actorName = scanner.nextLine();
            }
            System.out.print("\t\tEnter directors(type done when finishing the list): ");
            ArrayList<String> directors = new ArrayList<>();
            String directorName = scanner.nextLine();
            while (!directorName.equals("done")) {
                directors.add(directorName);
                directorName = scanner.nextLine();
            }
            System.out.print("\t\tEnter genres(type done when finishing the list): ");
            ArrayList<Genre> genres = new ArrayList<>();
            String genreName = scanner.nextLine();
            while (!genreName.equals("done")) {
                genres.add(Genre.valueOf(genreName));
                genreName = scanner.nextLine();
            }
            Series series = new Series(productionTitle, plot, 0, directors, actors, genres, null, 0, numberOfSeasons, episodesPerSeason);
            IMDB.getInstance().productions.add(series);
            ((Staff)user).addedActorsAndProductions.add(series);
            series.inserterUsername = user.username;
        }
        ProductionExperience productionExperience = new ProductionExperience();
        user.userExperience = productionExperience.calculateExperience(user);
    }

    public void addActor(User user) {
        System.out.print("\t\tEnter name: ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        System.out.print("\t\tEnter biography:");
        String biography = scanner.nextLine();
        System.out.print("\t\tEnter pair list of title and production type(type 'done' when finishing the list): ");
        ArrayList<Pair<String, ProductionType>> pairList = new ArrayList<>();
        String pair = scanner.nextLine();
        while(!pair.equals("done")) {
            String[] pairArray = pair.split(" ");
            String title = pairArray[0];
            ProductionType productionType = ProductionType.valueOf(pairArray[1]);
            Pair<String, ProductionType> pair1 = new Pair<>(title, productionType);
            pairList.add(pair1);
            pair = scanner.nextLine();
        }
        Actor actor = new Actor(name, pairList, biography, user.username);
        IMDB.getInstance().actors.add(actor);
        ((Staff)user).addedActorsAndProductions.add(actor);
        ActorExperience actorExperience = new ActorExperience();
        user.userExperience = actorExperience.calculateExperience(user);
    }

    public void deleteActor(User user) {
        int actorFound = 0;
        boolean error = false;
        while(actorFound == 0) {
            System.out.print("\t\tEnter name: ");
            Scanner scanner = new Scanner(System.in);
            String name = scanner.nextLine();
            for(Actor actor : IMDB.getInstance().actors) {
                if(actor.name.equals(name)) {
                    if(actor.inserterUsername.equals(user.username) || (actor.inserterUsername.equals(AccountType.Admin.toString()) && user.userType.equals(AccountType.Admin))) {
                        if(actor.name.equals(name)) {
                            actors.remove(actor);
                            ((Staff)user).addedActorsAndProductions.remove(actor);
                            actorFound = 1;
                            for(User user1 : users) {
                                if(user1.userPreferences.contains(actor)) {
                                    user1.userPreferences.remove(actor);
                                }
                            }
                            break;
                        }
                    } else {
                        System.out.println("You cannot delete this actor!");
                        error = true;
                        break;
                    }
                }
            }
            if(actorFound == 0 && !error) {
                System.out.println("Actor not found!\n\n");
            }
        }
    }

    public void deleteProduction(User user) {
        int productionFound = 0;
        while(productionFound == 0) {
            System.out.print("\t\tEnter title: ");
            Scanner scanner = new Scanner(System.in);
            String title = scanner.nextLine();
            for(Production production : IMDB.getInstance().productions) {
                System.out.println(production.productionTitle.equals(title) + "\n");
                System.out.println(production.productionTitle + " " + title);
                if(production.productionTitle.equals(title)) {
                    System.out.println(production.inserterUsername + " " + user.username);
                    if(production.inserterUsername.equals(user.username) || (production.inserterUsername.equals(AccountType.Admin.toString()) && user.userType.equals(AccountType.Admin))) {
                        productions.remove(production);
                        ((Staff)user).addedActorsAndProductions.remove(production);
                        productionFound = 1;
                        for(User user1 : users) {
                            if(user1.userPreferences.contains(production)) {
                                user1.userPreferences.remove(production);
                            }
                        }
                        break;
                    } else {
                        System.out.println("You cannot delete this production!");
                        break;
                    }
                }
            }
            if(productionFound == 0) {
                System.out.println("Production not found!\n\n");
            }
        }
    }

    public void addDeleteProductionActors(User user) throws InvalidCommandException {
        int found = 0;
        while(found == 0) {
            System.out.println("Choose option:");
            System.out.println("\t1. Add actor");
            System.out.println("\t2. Add production");
            System.out.println("\t3. Delete actor");
            System.out.println("\t4. Delete production");
            System.out.print("Enter option:");
            Scanner scanner = new Scanner(System.in);
            int option = scanner.nextInt();
            if(option == 1) {
                addActor(user);
                found = 1;
            } else if(option == 2) {
                Scanner scanner1 = new Scanner(System.in);
                System.out.print("\t\tEnter title: ");
                String title = scanner1.nextLine();
                Production aux = null;
                for(Production production : IMDB.getInstance().productions) {
                    if(production.productionTitle.equals(title)) {
                        aux = production;
                        break;
                    }
                }
                if(aux == null) {
                    addProduction(user, title);
                    found = 1;
                } else {
                    System.out.println("Production already exists!");
                }
            } else if(option == 3) {
                deleteActor(user);
                found = 1;
            } else if (option == 4) {
                deleteProduction(user);
                found = 1;
            } else {
                throw new InvalidCommandException("Invalid option!");
            }
        }
    }

    public void viewAndSolveRequests(User user) throws InvalidCommandException {
        int found = 0;
        while (found == 0) {
            System.out.println("Choose an option: ");
            System.out.println("\t1. View requests");
            System.out.println("\t2. Solve requests");
            System.out.print("Enter option: ");
            Scanner scanner3 = new Scanner(System.in);
            int option2 = scanner3.nextInt();
            if (option2 == 1) {
                int i = 0;
                for (Request request : requests) {
                    if (request.solverUsername.equals(user.username) || (request.solverUsername.equals(AccountType.Admin.toString()) && user.userType.equals(AccountType.Admin))) {
                        System.out.println("Request ID: " + i);
                        request.displayInfo(request);
                        System.out.println("----------------------------------------");
                        i++;
                    }
                }
                found = 1;
            } else if (option2 == 2) {
                System.out.print("Enter request ID: ");
                Scanner scanner4 = new Scanner(System.in);
                int requestID = scanner4.nextInt();
                int i = 0;
                for (Request request : requests) {
                    if (request.solverUsername.equals(user.username) || (request.solverUsername.equals(AccountType.Admin.toString()) && user.userType.equals(AccountType.Admin))) {
                        if(i == requestID) {
                            System.out.println("1. Solve request");
                            System.out.println("2. Decline request");
                            System.out.print("Enter option: ");
                            Scanner scanner5 = new Scanner(System.in);
                            int option3 = scanner5.nextInt();
                            if(option3 == 1) {
                                requests.remove(request);
                                if(request.getRequestType().equals(RequestType.ACTOR_ISSUE)) {
                                    User user1 = null;
                                    for(User user2 : IMDB.getInstance().users) {
                                        if(user2.username.equals(request.requesterUsername)) {
                                            user1 = user2;
                                            break;
                                        }
                                    }
                                    RequestExperience requestExperience = new RequestExperience();
                                    user1.userExperience = requestExperience.calculateExperience(user1);
                                    request.notifyObservers("Your request regarding " + request.actorName + " has been solved!", 1);
                                } else {
                                    User user1 = null;
                                    for(User user2 : IMDB.getInstance().users) {
                                        if(user2.username.equals(request.requesterUsername)) {
                                            user1 = user2;
                                            break;
                                        }
                                    }
                                    RequestExperience requestExperience = new RequestExperience();
                                    user1.userExperience = requestExperience.calculateExperience(user1);
                                    request.notifyObservers("Your request regarding " + request.productionTitle + " has been solved!", 1);
                                }
                            } else if(option3 == 2) {
                                requests.remove(request);
                                request.notifyObservers("Your request has been declined!", 1);
                            } else {
                                throw new InvalidCommandException("Invalid option!");
                            }
                            found = 1;
                            break;
                        }
                        i++;
                    }
                }
                if (found == 0) {
                    System.out.println("Request not found!");
                }
            } else {
                throw new InvalidCommandException("Invalid option!");
            }
            if (found == 1) {
                break;
            }
        }
    }

    public void updateActorProduction(User user, Scanner scanner) throws InvalidCommandException {
        int found = 0;
        while (found == 0) {
            System.out.println("Choose an option: ");
            System.out.println("\t1. Update actor details");
            System.out.println("\t2. Update production details");
            System.out.print("Enter option: ");
            Scanner scanner3 = new Scanner(System.in);
            int option2 = scanner3.nextInt();
            if(option2 == 1) {
                System.out.print("Enter actor name: ");
                Scanner scanner4 = new Scanner(System.in);
                String actorName = scanner4.nextLine();
                Actor actor = null;
                for(Actor actor1 : IMDB.getInstance().actors) {
                    if(actor1.name.equals(actorName)) {
                        actor = actor1;
                        found = 1;
                        break;
                    }
                }
                if(actor == null) {
                    System.out.println("Actor not found!");
                } else if(actor.inserterUsername.equals(user.username) || (actor.inserterUsername.equals(AccountType.Admin.toString()) && user.userType.equals(AccountType.Admin))) {
                    System.out.println("Choose an option: ");
                    System.out.println("\t1. Update name");
                    System.out.println("\t2. Update biography");
                    System.out.println("\t3. Update pair list");
                    System.out.print("Enter option: ");
                    Scanner scanner5 = new Scanner(System.in);
                    int option3 = scanner5.nextInt();
                    if(option3 == 1) {
                        System.out.print("Enter new name: ");
                        Scanner scanner6 = new Scanner(System.in);
                        String newName = scanner6.nextLine();
                        actor.name = newName;
                    } else if(option3 == 2) {
                        System.out.print("Enter new biography: ");
                        Scanner scanner6 = new Scanner(System.in);
                        String newBiography = scanner6.nextLine();
                        actor.biography = newBiography;
                    } else if(option3 == 3) {
                        System.out.print("Enter new pair list(type done when finishing the list): ");
                        ArrayList<Pair<String, ProductionType>> pairList = new ArrayList<>();
                        String pair = scanner.nextLine();
                        while(!pair.equals("done")) {
                            String[] pairArray = pair.split(" ");
                            String title = pairArray[0];
                            ProductionType productionType = ProductionType.valueOf(pairArray[1]);
                            Pair<String, ProductionType> pair1 = new Pair<>(title, productionType);
                            pairList.add(pair1);
                            pair = scanner.nextLine();
                        }
                        actor.pairList = pairList;
                    } else {
                        throw new InvalidCommandException("Invalid option!");
                    }
                } else {
                    System.out.println("You cannot update this actor!");
                }
            } else if(option2 == 2) {
                System.out.println("Choose an option: ");
                System.out.println("\t1. Update movie details");
                System.out.println("\t2. Update series details");
                System.out.print("Enter option: ");
                Scanner scanner4 = new Scanner(System.in);
                int option3 = scanner4.nextInt();
                if(option3 == 1) {
                    System.out.print("Enter movie name: ");
                    Scanner scanner5 = new Scanner(System.in);
                    String movieName = scanner5.nextLine();
                    Movie movie = null;
                    for(Production production : IMDB.getInstance().productions) {
                        if(production.productionTitle.equals(movieName) && production instanceof Movie) {
                            movie = (Movie) production;
                            found = 1;
                            break;
                        }
                    }
                    if(movie == null) {
                        System.out.println("Movie not found!");
                    } else if(movie.inserterUsername.equals(user.username)) {
                        System.out.println("Choose an option: ");
                        System.out.println("\t1. Update title");
                        System.out.println("\t2. Update plot");
                        System.out.println("\t3. Update duration");
                        System.out.println("\t4. Update release year");
                        System.out.println("\t5. Update actors");
                        System.out.println("\t6. Update directors");
                        System.out.println("\t7. Update genres");
                        System.out.print("Enter option: ");
                        Scanner scanner6 = new Scanner(System.in);
                        int option4 = scanner6.nextInt();
                        if (option4 == 1) {
                            System.out.print("Enter new title: ");
                            Scanner scanner7 = new Scanner(System.in);
                            String newTitle = scanner7.nextLine();
                            movie.productionTitle = newTitle;
                        } else if (option4 == 2) {
                            System.out.print("Enter new plot: ");
                            Scanner scanner7 = new Scanner(System.in);
                            String newPlot = scanner7.nextLine();
                            movie.movieDescription = newPlot;
                        } else if (option4 == 3) {
                            System.out.print("Enter new duration: ");
                            Scanner scanner7 = new Scanner(System.in);
                            String newDuration = scanner7.nextLine();
                            movie.movieDuration = newDuration;
                        } else if (option4 == 4) {
                            System.out.print("Enter new release year: ");
                            Scanner scanner7 = new Scanner(System.in);
                            int newReleaseYear = scanner7.nextInt();
                            movie.releaseYear = newReleaseYear;
                        } else if (option4 == 5) {
                            System.out.print("Enter new actors(type done when finishing the list): ");
                            ArrayList<String> actors = new ArrayList<>();
                            String actorName = scanner.nextLine();
                            while (!actorName.equals("done")) {
                                actors.add(actorName);
                                actorName = scanner.nextLine();
                            }
                            movie.actors = actors;
                        } else if (option4 == 6) {
                            System.out.print("Enter new directors(type done when finishing the list): ");
                            ArrayList<String> directors = new ArrayList<>();
                            String directorName = scanner.nextLine();
                            while (!directorName.equals("done")) {
                                directors.add(directorName);
                                directorName = scanner.nextLine();
                            }
                            movie.directors = directors;
                        } else if (option4 == 7) {
                            System.out.print("Enter new genres(type done when finishing the list): ");
                            ArrayList<Genre> genres = new ArrayList<>();
                            String genreName = scanner.nextLine();
                            while (!genreName.equals("done")) {
                                genres.add(Genre.valueOf(genreName));
                                genreName = scanner.nextLine();
                            }
                            movie.genres = genres;
                        } else {
                            throw new InvalidCommandException("Invalid option!");
                        }
                    } else {
                        System.out.println("You cannot update this movie!");
                    }
                } else if(option3 == 2) {
                    System.out.print("Enter series name: ");
                    Scanner scanner5 = new Scanner(System.in);
                    String seriesName = scanner5.nextLine();
                    Series series = null;
                    for (Production production : productions) {
                        if (production.productionTitle.equals(seriesName) && production instanceof Series) {
                            series = (Series) production;
                            found = 1;
                            break;
                        }
                    }
                    if (series == null) {
                        System.out.println("Series not found!");
                    } else if (series.inserterUsername.equals(user.username)) {
                        System.out.println("Choose an option: ");
                        System.out.println("\t1. Update title");
                        System.out.println("\t2. Update plot");
                        System.out.println("\t3. Update release year");
                        System.out.println("\t4. Update number of seasons");
                        System.out.println("\t5. Update episodes per season");
                        System.out.println("\t6. Update actors");
                        System.out.println("\t7. Update directors");
                        System.out.println("\t8. Update genres");
                        System.out.print("Enter option: ");
                        Scanner scanner6 = new Scanner(System.in);
                        int option4 = scanner6.nextInt();
                        if (option4 == 1) {
                            System.out.print("Enter new title: ");
                            Scanner scanner7 = new Scanner(System.in);
                            String newTitle = scanner7.nextLine();
                            series.productionTitle = newTitle;
                        } else if (option4 == 2) {
                            System.out.print("Enter new plot: ");
                            Scanner scanner7 = new Scanner(System.in);
                            String newPlot = scanner7.nextLine();
                            series.movieDescription = newPlot;
                        } else if (option4 == 3) {
                            System.out.print("Enter new release year: ");
                            Scanner scanner7 = new Scanner(System.in);
                            int newReleaseYear = scanner7.nextInt();
                            series.releaseYear = newReleaseYear;
                        } else if (option4 == 4) {
                            System.out.print("Enter new number of seasons: ");
                            Scanner scanner7 = new Scanner(System.in);
                            int newNumberOfSeasons = scanner7.nextInt();
                            series.numberOfSeasons = newNumberOfSeasons;
                        } else if (option4 == 5) {
                            System.out.print("Enter new episodes per season(type done when finishing the list): ");
                            Map<String, List<Episode>> episodesPerSeason = new LinkedHashMap<>();
                            for (int i = 1; i <= series.numberOfSeasons; i++) {
                                System.out.print("Enter season " + i + " episodes(type done when finishing the list): ");
                                ArrayList<Episode> episodes = new ArrayList<>();
                                String episodeName = scanner.nextLine();
                                while (!episodeName.equals("done")) {
                                    System.out.print("Enter episode duration: ");
                                    Scanner scanner8 = new Scanner(System.in);
                                    String episodeDuration = scanner8.nextLine();
                                    Episode episode = new Episode(episodeName, episodeDuration);
                                    episodes.add(episode);
                                    episodeName = scanner.nextLine();
                                }
                                episodesPerSeason.put("Season " + i, episodes);
                            }
                        } else if (option4 == 6) {
                            System.out.print("Enter new actors(type done when finishing the list): ");
                            ArrayList<String> actors = new ArrayList<>();
                            String actorName = scanner.nextLine();
                            while (!actorName.equals("done")) {
                                actors.add(actorName);
                                actorName = scanner.nextLine();
                            }
                            series.actors = actors;
                        } else if (option4 == 7) {
                            System.out.print("Enter new directors(type done when finishing the list): ");
                            ArrayList<String> directors = new ArrayList<>();
                            String directorName = scanner.nextLine();
                            while (!directorName.equals("done")) {
                                directors.add(directorName);
                                directorName = scanner.nextLine();
                            }
                            series.directors = directors;
                        } else if (option4 == 8) {
                            System.out.print("Enter new genres(type done when finishing the list): ");
                            ArrayList<Genre> genres = new ArrayList<>();
                            String genreName = scanner.nextLine();
                            while (!genreName.equals("done")) {
                                genres.add(Genre.valueOf(genreName));
                                genreName = scanner.nextLine();
                            }
                            series.genres = genres;
                        } else {
                            throw new InvalidCommandException("Invalid option!");
                        }
                    } else {
                        System.out.println("You cannot update this series!");
                    }
                } else {
                    throw new InvalidCommandException("Invalid option!");
                }
            }
        }
    }

    public void run() throws IOException, ParseException, InvalidCommandException, InformationIncompleteException {
        IMDB imdb = IMDB.getInstance();
        imdb.parseRequests();
        imdb.parseActors();
        imdb.parseProductions();
        imdb.parseAccounts();
        imdb.assignateRequests();
        imdb.moveRequests();
        System.out.println("Choose an option: \n\t1.CLI\n\t2.GUI");
        Scanner scanner1 = new Scanner(System.in);
        System.out.print("\tEnter option: ");
        int format_option = scanner1.nextInt();
        if (format_option == 1) {
            int found_user = 0;
            while (found_user == 0) {
                System.out.println("Welcome to IMDB!Please enter your credentials!");
                Scanner scanner = new Scanner(System.in);
                System.out.print("email: ");
                String email = scanner.nextLine();
                if (email.isEmpty()) {
                    throw new InformationIncompleteException("Email field is empty!");
                }
                System.out.print("password: ");
                String password = scanner.nextLine();
                if (password.isEmpty()) {
                    throw new InformationIncompleteException("Password field is empty!");
                }
                User user = null;
                for (User user1 : imdb.users) {
                    if (user1.userInformation.getUserCredentials().getEmail().equals(email) && user1.userInformation.getUserCredentials().getPassword().equals(password)) {
                        found_user = 1;
                        user = user1;
                        break;
                    }
                }
                if (found_user == 0) {
                    System.out.println("Wrong credentials!Please try again!");
                } else {
                    boolean stop = true;
                    while (stop) {
                        if (user.userType.equals(AccountType.Admin)) {
                            System.out.println("Account type: Admin");
                            System.out.println("Experience: -");
                            System.out.println();

                            System.out.println("Choose an option: ");
                            System.out.println("\t1. View productions details");
                            System.out.println("\t2. View actors details");
                            System.out.println("\t3. View notifications");
                            System.out.println("\t4. Search actor/movie/series");
                            System.out.println("\t5. Add/Delete actor/movie/series to/from favorites");
                            System.out.println("\t6. Add/Delete actor/movie/series to/from system");
                            System.out.println("\t7. View/Solve requests");
                            System.out.println("\t8. Update Production/Actor details");
                            System.out.println("\t9. Add/Delete user from system");
                            System.out.println("\t10. Logout");
                        } else if (user.userType.equals(AccountType.Contributor)) {
                            System.out.println("Account type: Contributor");
                            System.out.println("Experience: " + user.userExperience);
                            System.out.println();
                            System.out.println("Choose an option: ");
                            System.out.println("\t1. View productions details");
                            System.out.println("\t2. View actors details");
                            System.out.println("\t3. View notifications");
                            System.out.println("\t4. Search actor/movie/series");
                            System.out.println("\t5. Add/Delete actor/production to/from favorites");
                            System.out.println("\t6. Create/Delete a request");
                            System.out.println("\t7. Add/Delete actor/production to/from system");
                            System.out.println("\t8. View/Solve requests");
                            System.out.println("\t9. Update Production/Actor details");
                            System.out.println("\t10. Logout");
                        } else {
                            System.out.println("Account type: Regular");
                            System.out.println("Experience: " + user.userExperience);
                            System.out.println();

                            System.out.println("Choose an option: ");
                            System.out.println("\t1. View productions details");
                            System.out.println("\t2. View actors details");
                            System.out.println("\t3. View notifications");
                            System.out.println("\t4. Search actor/movie/series");
                            System.out.println("\t5. Add/Delete actor/production to/from favorites");
                            System.out.println("\t6. Create/Delete a request");
                            System.out.println("\t7. Add/Delete rating to/from production");
                            System.out.println("\t8. Logout");
                        }
                        Scanner scanner2 = new Scanner(System.in);
                        System.out.print("Enter option: ");
                        int option = scanner2.nextInt();
                        if (option == 1) {
                            System.out.println("Choose an option: ");
                            System.out.println("\t1. Search by genre");
                            System.out.println("\t2. Search by number of reviews");
                            System.out.println("\t3. Display all productions");
                            System.out.print("Enter option: ");
                            Scanner scanner3 = new Scanner(System.in);
                            int option2 = scanner3.nextInt();
                            if (option2 == 1) {
                                Scanner scanner4 = new Scanner(System.in);
                                System.out.print("Enter genre: ");
                                String genre = scanner4.nextLine();
                                for (Production production : imdb.productions) {
                                    if (production.genres.contains(Genre.valueOf(genre))) {
                                        production.displayInfo();
                                    }
                                }
                            } else if (option2 == 2) {
                                Scanner scanner4 = new Scanner(System.in);
                                System.out.print("Enter number of reviews: ");
                                int numberOfReviews = scanner4.nextInt();
                                for (Production production : imdb.productions) {
                                    if (production.ratings.size() == numberOfReviews) {
                                        production.displayInfo();
                                    }
                                }
                            } else {
                                for (Production production : imdb.productions) {
                                    production.displayInfo();
                                }
                            }
                        } else if (option == 2) {
                            System.out.println("Choose an option:");
                            System.out.println("\t1. Sort by name");
                            System.out.println("\t2. Display all actors");
                            System.out.print("Enter option: ");
                            Scanner scanner3 = new Scanner(System.in);
                            int option2 = scanner3.nextInt();
                            if (option2 == 1) {
                                ArrayList<Actor> newActors = (ArrayList<Actor>) imdb.actors.clone();
                                Collections.sort(newActors);
                                for (Actor actor : newActors) {
                                    actor.displayInfo();
                                }
                            } else if (option2 == 2) {
                                for (Actor actor : imdb.actors) {
                                    actor.displayInfo();
                                }
                            } else {
                                throw new InvalidCommandException("Invalid option!");
                            }
                        } else if (option == 3) {
                            for (Object notification : user.userNotifications) {
                                System.out.println((String) notification);
                            }
                        } else if (option == 4) {
                            int found = 0;
                            while (found == 0) {
                                System.out.println("Choose an option: ");
                                System.out.println("\t1. Search actor");
                                System.out.println("\t2. Search movie");
                                System.out.println("\t3. Search series");
                                System.out.print("Enter option: ");
                                Scanner scanner3 = new Scanner(System.in);
                                int option2 = scanner3.nextInt();
                                if (option2 == 1) {
                                    System.out.print("Enter actor name: ");
                                    Scanner scanner4 = new Scanner(System.in);
                                    String actorName = scanner4.nextLine();
                                    for (Actor actor : imdb.actors) {
                                        if (actor.name.equals(actorName)) {
                                            actor.displayInfo();
                                            found = 1;
                                            break;
                                        }
                                    }
                                    if (found == 0) {
                                        System.out.println("Actor not found!");
                                    }
                                } else if (option2 == 2) {
                                    System.out.print("Enter movie name: ");
                                    Scanner scanner4 = new Scanner(System.in);
                                    String movieName = scanner4.nextLine();
                                    for (Production production : imdb.productions) {
                                        if (production.productionTitle.equals(movieName) && production instanceof Movie) {
                                            production.displayInfo();
                                            found = 1;
                                            break;
                                        }
                                    }
                                    if (found == 0) {
                                        System.out.println("Movie not found!");
                                    }
                                } else if (option2 == 3) {
                                    System.out.print("Enter series name: ");
                                    Scanner scanner4 = new Scanner(System.in);
                                    String seriesName = scanner4.nextLine();
                                    for (Production production : imdb.productions) {
                                        if (production.productionTitle.equals(seriesName) && production instanceof Series) {
                                            production.displayInfo();
                                            found = 1;
                                            break;
                                        }
                                    }
                                } else {
                                    throw new InvalidCommandException("Invalid option!");
                                }
                                if (found == 1) {
                                    break;
                                }
                            }
                        } else if (option == 5) {
                            int found = 0;
                            while (found == 0) {
                                System.out.println("Choose an option: ");
                                System.out.println("\t1. Add actor to favorites");
                                System.out.println("\t2. Add production to favorites");
                                System.out.println("\t3. Delete actor from favorites");
                                System.out.println("\t4. Delete production from favorites");
                                System.out.println("\t5. Show favorites");
                                System.out.print("Enter option: ");
                                Scanner scanner3 = new Scanner(System.in);
                                int option2 = scanner3.nextInt();
                                if (option2 == 1) {
                                    System.out.print("Enter actor name:");
                                    Scanner scanner4 = new Scanner(System.in);
                                    String actorName = scanner4.nextLine();
                                    Actor actorToAdd = null;
                                    for (Actor actor : imdb.actors) {
                                        if (actor.name.equals(actorName)) {
                                            actorToAdd = actor;
                                            found = 1;
                                            break;
                                        }
                                    }
                                    if (found == 1) {
                                        if (user.isActorInPreferences(new Actor(actorName, null, null, null))) {
                                            System.out.println("\t\tActor already in favorites!");
                                            found = 0;
                                        } else {
                                            user.addPreference(actorToAdd);
                                        }
                                    } else if (found == 0) {
                                        System.out.println("Actor not found!");
                                    }
                                } else if (option2 == 2) {
                                    System.out.print("Enter production name: ");
                                    Scanner scanner4 = new Scanner(System.in);
                                    String productionName = scanner4.nextLine();
                                    Production productionToAdd = null;
                                    for (Production production : imdb.productions) {
                                        if (production.productionTitle.equals(productionName)) {
                                            productionToAdd = production;
                                            found = 1;
                                            break;
                                        }
                                    }
                                    if (found == 1) {
                                        if (user.isProductionInPreferences(productionToAdd)) {
                                            System.out.println("\t\tProduction already in favorites!");
                                            found = 0;
                                        } else {
                                            user.addPreference(productionToAdd);
                                        }
                                    } else if (found == 0) {
                                        System.out.println("Production not found!");
                                    }
                                } else if (option2 == 3) {
                                    System.out.print("Enter actor name: ");
                                    Scanner scanner4 = new Scanner(System.in);
                                    String actorName = scanner4.nextLine();
                                    for (Actor actor : imdb.actors) {
                                        if (actor.name.equals(actorName)) {
                                            user.userPreferences.remove(actor);
                                            found = 1;
                                            break;
                                        }
                                    }
                                    if (found == 0) {
                                        System.out.println("Actor not found!");
                                    }
                                } else if (option2 == 4) {
                                    System.out.print("Enter production name: ");
                                    Scanner scanner4 = new Scanner(System.in);
                                    String productionName = scanner4.nextLine();
                                    for (Production production : imdb.productions) {
                                        if (production.productionTitle.equals(productionName)) {
                                            user.userPreferences.remove(production);
                                            found = 1;
                                            break;
                                        }
                                    }
                                    if (found == 0) {
                                        System.out.println("Production not found!");
                                    }
                                } else if (option2 == 5) {
                                    user.displayPreferences();
                                    found = 1;
                                } else {
                                    throw new InvalidCommandException("Invalid option!");
                                }
                                if (found == 1) {
                                    break;
                                }
                            }
                        } else if (user.userType.equals(AccountType.Regular)) {
                            if (option == 6) {
                                imdb.createDeleteRequests(user);
                            } else if (option == 7) {
                                int found = 0;
                                while (found == 0) {
                                    System.out.println("Choose an option: ");
                                    System.out.println("\t1. Add rating to production");
                                    System.out.println("\t2. Delete rating from production");
                                    System.out.print("Enter option: ");
                                    Scanner scanner3 = new Scanner(System.in);
                                    int option2 = scanner3.nextInt();
                                    if (option2 == 1) {
                                        System.out.print("Enter production name: ");
                                        Scanner scanner4 = new Scanner(System.in);
                                        String productionName = scanner4.nextLine();
                                        Production production = null;
                                        for (Production production1 : imdb.productions) {
                                            if (production1.productionTitle.equals(productionName)) {
                                                production = production1;
                                                found = 1;
                                                break;
                                            }
                                        }
                                        if (production == null) {
                                            System.out.println("Production not found!");
                                        } else {
                                            System.out.print("Enter grade: ");
                                            Scanner scanner5 = new Scanner(System.in);
                                            imdb.recalculateAverageProductionRating(production);
                                            int grade = scanner5.nextInt();
                                            System.out.print("Enter comment: ");
                                            Scanner scanner6 = new Scanner(System.in);
                                            String comment = scanner6.nextLine();
                                            Rating rating = new Rating(user.username, grade, comment);
                                            rating.registerObserver(user);
                                            for(Rating rating1 : production.ratings) {
                                                for(User user1 : users) {
                                                    if(user1.username.equals(rating1.user)) {
                                                        rating1.registerObserver(user1);
                                                        rating1.notifyObservers("A new rating has been added to " + production.productionTitle + "!", 1);
                                                        break;
                                                    }
                                                }
                                            }
                                            production.ratings.add(rating);
                                            imdb.recalculateAverageProductionRating(production);
                                            boolean givenRating = false;
                                            for(Object s1 : user.givenRating) {
                                                if(((String)s1).equals(production.productionTitle)) {
                                                    givenRating = true;
                                                    break;
                                                }
                                            }
                                            if(!givenRating) {
                                                RatingExperience ratingExperience = new RatingExperience();
                                                user.userExperience = ratingExperience.calculateExperience(user);
                                                user.givenRating.add(production.productionTitle);
                                            }
                                        }
                                    } else if (option2 == 2) {
                                        System.out.print("Enter production name: ");
                                        Scanner scanner4 = new Scanner(System.in);
                                        String productionName = scanner4.nextLine();
                                        Production production = null;
                                        for (Production production1 : productions) {
                                            if (production1.productionTitle.equals(productionName)) {
                                                production = production1;
                                                found = 1;
                                                break;
                                            }
                                        }
                                        if (production == null) {
                                            System.out.println("Production not found!");
                                        } else {
                                            int i = 0;
                                            for (Rating rating : production.ratings) {
                                                if (rating.user.equals(user.username)) {
                                                    production.ratings.remove(rating);
                                                    for(Observer observer : rating.observers) {
                                                        rating.removeObserver(observer);
                                                    }
                                                    imdb.recalculateAverageProductionRating(production);
                                                    found = 1;
                                                    break;
                                                }
                                                i++;
                                            }
                                            if (found == 0) {
                                                System.out.println("Rating not found!");
                                            }
                                        }
                                    } else {
                                        throw new InvalidCommandException("Invalid option!");
                                    }
                                    if (found == 1) {
                                        break;
                                    }
                                }
                            } else if (option == 8) {
                                System.out.println("\t1. Exit");
                                System.out.println("\t2. Sign in with a different account");
                                System.out.print("Choose option: ");
                                Scanner scanner3 = new Scanner(System.in);
                                int option2 = scanner3.nextInt();
                                if (option2 == 1) {
                                    System.out.println("Have a nice day, " + user.username + "!");
                                    return;
                                } else {
                                    stop = false;
                                    found_user = 0;
                                    break;
                                }
                            } else {
                                throw new InvalidCommandException("Invalid option!");
                            }
                        } else if (user.userType.equals(AccountType.Contributor)) {
                            if(option == 6) {
                                imdb.createDeleteRequests(user);
                            } else if (option == 7) {
                                imdb.addDeleteProductionActors(user);
                            } else if(option == 8) {
                                imdb.viewAndSolveRequests(user);
                            } else if (option == 9) {
                                imdb.updateActorProduction(user, scanner);
                            } else if(option == 10){
                                System.out.println("\t1. Exit");
                                System.out.println("\t2. Sign in with a different account");
                                System.out.print("Choose option: ");
                                Scanner scanner3 = new Scanner(System.in);
                                int option2 = scanner3.nextInt();
                                if (option2 == 1) {
                                    System.out.println("Have a nice day, " + user.username + "!");
                                    return;
                                } else {
                                    stop = false;
                                    found_user = 0;
                                    break;
                                }
                            } else {
                                throw new InvalidCommandException("Invalid option!");
                            }
                        } else {
                            if(option == 6) {
                                imdb.addDeleteProductionActors(user);
                            } else if(option == 7){
                                imdb.viewAndSolveRequests(user);
                            } else if (option == 8) {
                                imdb.updateActorProduction(user, scanner);
                            } else if (option == 9) {
                                int found = 0;
                                while(found == 0) {
                                    System.out.println("Choose an option: ");
                                    System.out.println("\t1. Add user to system");
                                    System.out.println("\t2. Delete user from system");
                                    System.out.print("Enter option: ");
                                    Scanner scanner3 = new Scanner(System.in);
                                    int option2 = scanner3.nextInt();
                                    if(option2 == 1) {
                                        System.out.println("Choose an option: ");
                                        System.out.println("\t1. Add regular");
                                        System.out.println("\t2. Add contributor");
                                        System.out.println("\t3. Add admin");
                                        System.out.print("Enter option: ");
                                        Scanner scanner4 = new Scanner(System.in);
                                        int option3 = scanner4.nextInt();
                                        AccountType accountType;
                                        if(option3 == 1) {
                                            accountType = AccountType.Regular;
                                        } else if (option3 == 2) {
                                            accountType = AccountType.Contributor;
                                        } else if (option3 == 3) {
                                            accountType = AccountType.Admin;
                                        } else {
                                            throw new InvalidCommandException("Invalid option!");
                                        }
                                        System.out.print("Enter user's full name: ");
                                        Scanner scanner5 = new Scanner(System.in);
                                        String name = scanner5.nextLine();
                                        System.out.print("Enter user's country: ");
                                        Scanner scanner6 = new Scanner(System.in);
                                        String country = scanner6.nextLine();
                                        System.out.print("Enter user's age: ");
                                        Scanner scanner7 = new Scanner(System.in);
                                        int age = scanner7.nextInt();
                                        System.out.print("Enter user's birth date(use this format: yyyy-mm-dd): ");
                                        Scanner scanner8 = new Scanner(System.in);
                                        LocalDate birthDate = LocalDate.parse(scanner8.nextLine());
                                        System.out.print("Enter user's email: ");
                                        Scanner scanner9 = new Scanner(System.in);
                                        String userEmail = scanner9.nextLine();
                                        String userPassword = UsernamePasswordGenerator.generatePassword();
                                        boolean isUnique;
                                        String username;
                                        while(true) {
                                            username = UsernamePasswordGenerator.generateUsername(name);
                                            isUnique = true;
                                            for(User user1 : users) {
                                                if(user1.username.equals(username)) {
                                                    isUnique = false;
                                                    break;
                                                }
                                            }
                                            if(isUnique) {
                                                break;
                                            }
                                        }
                                        User user1 = UserFactory.createUser(null, accountType, 0, username);
                                        User.InformationBuilder builder = new User.InformationBuilder();
                                        user1.userInformation = builder
                                                .credentials(new Credentials(userEmail, userPassword))
                                                .userName(name)
                                                .userCountry(country)
                                                .userAge(age)
                                                .userGender(null)
                                                .birthDate(birthDate)
                                                .build(user1);
                                        imdb.users.add(user1);
                                        System.out.println("The newly created user has the following credentials: ");
                                        System.out.println("\tUsername: " + username);
                                        System.out.println("\tPassword: " + userPassword);
                                        found = 1;
                                    } else if (option2 ==2) {
                                        System.out.print("Enter username: ");
                                        Scanner scanner4 = new Scanner(System.in);
                                        String name = scanner4.nextLine();
                                        User user1 = null;
                                        for(User user2 : users) {
                                            if(user2.username.equals(name)) {
                                                user1 = user2;
                                                break;
                                            }
                                        }
                                        if(user1 == null) {
                                            System.out.println("User not found!");
                                        } else {
                                            if(user1.userType.equals(AccountType.Contributor)) {
                                                for(Request request : requests) {
                                                    if(request.solverUsername.equals(user1.username)) {
                                                        request.solverUsername = "Admin";
                                                    }
                                                }
                                                for(Production production : productions) {
                                                    if(production.inserterUsername.equals(user1.username)) {
                                                        production.inserterUsername = "Admin";
                                                    }
                                                }
                                                for(Actor actor : actors) {
                                                    if(actor.inserterUsername.equals(user1.username)) {
                                                        actor.inserterUsername = "Admin";
                                                    }
                                                }
                                            }
                                            users.remove(user1);
                                            found = 1;
                                        }
                                    } else {
                                        throw new InvalidCommandException("Invalid option!");
                                    }
                                }
                            } else if (option == 10) {
                                System.out.println("\t1. Exit");
                                System.out.println("\t2. Sign in with a different account");
                                System.out.print("Choose option: ");
                                Scanner scanner3 = new Scanner(System.in);
                                int option2 = scanner3.nextInt();
                                if (option2 == 1) {
                                    System.out.println("Have a nice day, " + user.username + "!");
                                    return;
                                } else {
                                    stop = false;
                                    found_user = 0;
                                    break;
                                }
                            } else {
                                throw new InvalidCommandException("Invalid option!");
                            }
                        }
                    }
                }
            }
        } else if(format_option == 2) {
            GUI gui = new GUI();
            gui.run();
        } else {
            throw new InvalidCommandException("Invalid option!");
        }
    }

public static void main(String[] args) throws IOException, ParseException, InvalidCommandException, InformationIncompleteException {
    IMDB imdb = IMDB.getInstance();
    imdb.run();
    }
}
