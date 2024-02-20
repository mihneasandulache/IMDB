import java.security.SecureRandom;

public class UsernamePasswordGenerator {

    private static final String[] SPECIAL_CHARS = {"!", "@", "#", "$", "%", "&"};

    public static String generateUsername(String fullName) throws InformationIncompleteException {
        SecureRandom random = new SecureRandom();
        String[] names = fullName.split(" ");
        if (names.length != 2) {
            throw new InformationIncompleteException("Full name must contain both first and last name.");
        }

        String firstName = names[0].toLowerCase();
        String lastName = names[1].toLowerCase();

        int randomNum = new SecureRandom().nextInt(90) + 10;

        return firstName + SPECIAL_CHARS[random.nextInt(SPECIAL_CHARS.length)] + lastName + randomNum;
    }

    public static String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int choice = random.nextInt(3);
            switch (choice) {
                case 0:
                    password.append((char) (random.nextInt(26) + 'a'));
                    break;
                case 1:
                    password.append(random.nextInt(10));
                    break;
                case 2:
                    password.append(SPECIAL_CHARS[random.nextInt(SPECIAL_CHARS.length)]);
                    break;
            }
        }

        return password.toString();
    }
}

