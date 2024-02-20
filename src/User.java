import java.time.LocalDate;
import java.util.*;
abstract public class User<T extends Comparable<T>> implements Observer {
    Information userInformation;
    AccountType userType;
    String username;
    int userExperience;
    ArrayList<String> userNotifications = new ArrayList<String>();
    ExperienceStrategy experienceStrategy;
    ArrayList<String> givenRating = new ArrayList<String>();
    ArrayList<Production> productionsContribution = new ArrayList<Production>();
    ArrayList<Actor> actorsContribution = new ArrayList<Actor>();


    public void setProductionsContribution(ArrayList<Production> productionsContribution) {
        for(Production production : IMDB.getInstance().productions) {
            if(production.inserterUsername != null && production.inserterUsername.equals(this.username)) {
                productionsContribution.add(production);
            }
        }
    }

    public void setActorsContribution(ArrayList<Actor> actorsContribution) {
        for(Actor actor : IMDB.getInstance().actors) {
            if(actor.inserterUsername != null && (actor.inserterUsername.equals(this.username) || (this.userType == AccountType.Admin && actor.inserterUsername.equals("Admin")))) {
                actorsContribution.add(actor);
            }
        }
    }
    Comparator<T> comparator = new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
            if(o1 instanceof Production && o2 instanceof Production) {
                return ((Production) o1).productionTitle.compareTo(((Production) o2).productionTitle);
            } else if (o1 instanceof Production && o2 instanceof Actor) {
                return ((Production) o1).productionTitle.compareTo(((Actor) o2).name);
            } else if (o1 instanceof Actor && o2 instanceof Production) {
                return ((Actor) o1).name.compareTo(((Production) o2).productionTitle);
            } else if (o1 instanceof Actor && o2 instanceof Actor) {
                return ((Actor) o1).name.compareTo(((Actor) o2).name);
            } else {
                return -999;
            }
        }
    };
    SortedSet<T> userPreferences = new TreeSet<>();

    public User(Information userInformation, AccountType userType, int userExperience, String username){
        this.userInformation = userInformation;
        this.userType = userType;
        this.userExperience = userExperience;
        this.username = username;
    }

    public void addPreference(T item) {
        userPreferences.add(item);
    }

    public void removePreference(T item) {
        userPreferences.remove(item);
    }

    public void updateExperience(int newExperience) {
        this.userExperience = newExperience;
    }

    public void displayPreferences() {
        for(T item : userPreferences) {
            if(item instanceof Production) {
                if(item instanceof Movie) {
                    System.out.println("Movie: " + ((Movie) item).movieName);
                } else if (item instanceof Series) {
                    System.out.println("Series: " + ((Series) item).productionTitle);
                }
            } else if (item instanceof Actor) {
                System.out.println("Actor: " + ((Actor) item).name);
            }
        }
    }

    public boolean isActorInPreferences(Actor actor) {
        for(T item : userPreferences) {
            if(item instanceof Actor) {
                if(((Actor) item).name.equals(actor.name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isProductionInPreferences(Production production) {
        for(T item : userPreferences) {
            if(item instanceof Production) {
                if(((Production) item).productionTitle.equals(production.productionTitle)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void sendNotification(ArrayList<String> notifications, String notification) {
        notifications.add(notification);
    }

    public void registerObserver(Request request) {
        request.registerObserver(this);
    }

    public void removeObserver(Request request) {
        request.removeObserver(this);
    }

    public void setExperienceStrategy(ExperienceStrategy experienceStrategy) {
        this.experienceStrategy = experienceStrategy;
    }

    public int computeExperience() {
        return experienceStrategy.calculateExperience(this);
    }

    class Information {
        private Credentials userCredentials;
        private String userName;
        private String userCountry;
        private int userAge;
        private String userGender;
        private LocalDate birthDate;

        public Information(Credentials userCredentials, String userName, String userCountry, int userAge, String userGender, LocalDate birthDate) {
            this.userCredentials = userCredentials;
            this.userName = userName;
            this.userCountry = userCountry;
            this.userAge = userAge;
            this.userGender = userGender;
            this.birthDate = birthDate;
        }
        public Credentials getUserCredentials() {
            return userCredentials;
        }

        public void setUserCredentials(Credentials userCredentials) {
            this.userCredentials = userCredentials;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserCountry() {
            return userCountry;
        }

        public void setUserCountry(String userCountry) {
            this.userCountry = userCountry;
        }

        public int getUserAge() {
            return userAge;
        }

        public void setUserAge(int userAge) {
            this.userAge = userAge;
        }

        public LocalDate getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
        }
        public void setUserGender(String userGender) {
            this.userGender = userGender;
        }

        String getUserGender() {
            return userGender;
        }

    }

    public static class InformationBuilder {
        private Credentials userCredentials;
        private String userName;
        private String userCountry;
        private int userAge;
        private String userGender;
        private LocalDate birthDate;

        public InformationBuilder credentials(Credentials userCredentials) {
            this.userCredentials = userCredentials;
            return this;
        }

        public InformationBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public InformationBuilder userCountry(String userCountry) {
            this.userCountry = userCountry;
            return this;
        }

        public InformationBuilder userAge(int userAge) {
            this.userAge = userAge;
            return this;
        }

        public InformationBuilder userGender(String userGender) {
            this.userGender = userGender;
            return this;
        }

        public InformationBuilder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public User.Information build(User user) {
            return user.new Information(userCredentials, userName, userCountry, userAge, userGender, birthDate);
        }
    }

}
