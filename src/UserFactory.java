public class UserFactory {
    public static User createUser(User.Information userInformation, AccountType userType, int userExperience, String username) {
        if (userType == AccountType.Regular) {
            return new Regular(userInformation, userType, userExperience, username);
        } else if (userType == AccountType.Contributor) {
            return new Contributor(userInformation, userType, userExperience, username);
        } else if (userType == AccountType.Admin) {
            return new Admin(userInformation, userType, userExperience, username);
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }
    }
}
