import java.util.*;

public class Staff<T extends Comparable<T>> extends User<T> implements StaffInterface {
    List<Request> userRequests = new ArrayList<Request>();

    SortedSet<T> addedActorsAndProductions = new TreeSet<T>();

    public Staff(Information userInformation, AccountType userType, int userExperience, String username) {
        super(userInformation, userType, userExperience, username);
    }

    public void solveRequest(Request request) {
        //TO DO
    }

    @Override
    public void addProductionSystem(Production p) {
        addedActorsAndProductions.add((T) p);
    }

    @Override
    public void removeProductionSystem(Production p) {
        addedActorsAndProductions.remove((T) p);
    }

    @Override
    public void removeActorSystem(Actor a) {
        addedActorsAndProductions.remove((T) a);
    }

    @Override
    public void addActorSystem(Actor a) {
        addedActorsAndProductions.add((T) a);
    }

    @Override
    public void updateProduction(Production p) {
        //TO DO cu for prin lista de productii si daca gasesti p, faci update
    }

    @Override
    public void updateActor(Actor a) {

    }
}