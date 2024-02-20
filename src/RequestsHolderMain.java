import java.util.*;
public class RequestsHolderMain {
    public static class RequestsHolder {
        private static List<Request> requests = new ArrayList<Request>();

        public static void addRequest(Request request) {
            requests.add(request);
        }

        public static void removeRequest(Request request) {
            requests.remove(request);
        }

        public static List<Request> getRequests() {
            return requests;
        }

        public void setRequests(List<Request> newRequests) {
            requests = newRequests;
        }
    }
}
