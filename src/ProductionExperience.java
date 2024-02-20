public class ProductionExperience implements ExperienceStrategy{
    @Override
    public int calculateExperience(User user) {
        for(User user1 : IMDB.getInstance().users) {
            if(user1.username.equals(user.username)) {
                user1.userExperience = user.userExperience + 5;
                return user1.userExperience;
            }
        }
        return -1;
    }
}
