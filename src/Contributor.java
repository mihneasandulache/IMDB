public class Contributor <T extends Comparable<T>> extends Staff<T> implements RequestsManager{
    public Contributor(Information userInformation, AccountType userType, int userExperience, String username) {
        super(userInformation, userType, userExperience, username);
    }
    public void createRequest(Request request) {
        RequestsHolderMain.RequestsHolder.addRequest(request);
    }

    @Override
    public void removeRequest(Request r) {
        RequestsHolderMain.RequestsHolder.removeRequest(r);
    }
}
