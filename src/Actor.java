import java.util.ArrayList;

public class Actor implements Comparable {
    String name;
    ArrayList<Pair<String, ProductionType>> pairList = new ArrayList<Pair<String, ProductionType>>();
    String biography;
    String inserterUsername;

    public Actor(String name, ArrayList<Pair<String, ProductionType>> pairList, String biography, String inserterUsername) {
        this.name = name;
        this.pairList = pairList;
        this.biography = biography;
        this.inserterUsername = inserterUsername;
    }

    public void addPair(String name, ProductionType productionType) {
        pairList.add(new Pair<String, ProductionType>(name, productionType));
    }
    public void displayInfo() {
        System.out.println("Actor name: " + name);
        System.out.println("Actor biography: " + biography);
        System.out.println("Actor pair list: ");
        for (Pair<String, ProductionType> pair : pairList) {
            System.out.println("\t Production Type: " + pair.productionType);
            System.out.println("\t Production Name: " + pair.name);
        }
    }

    public int compareTo(Object o) {
        if (!(o instanceof Actor))
            return -1;
        Actor o1 = (Actor) o;
        return name.compareTo(o1.name);
    }

}
