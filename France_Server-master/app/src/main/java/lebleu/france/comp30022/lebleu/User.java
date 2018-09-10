package lebleu.france.comp30022.lebleu;

public class User {

    public String username;
    public String email;
    Boolean isElderly;
    Boolean isHelper;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, Boolean isElderly, Boolean isHelper) {
        this.username = username;
        this.email = email;
        this.isElderly = isElderly;
        this.isHelper = isHelper;
    }
}
