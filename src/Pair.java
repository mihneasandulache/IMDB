public class Pair<S, T>{
    String name;
    ProductionType productionType;

    public Pair(String name, ProductionType productionType) {
        this.name = name;
        this.productionType = productionType;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "name='" + name + '\'' +
                ", productionType=" + productionType +
                '}';
    }
}
