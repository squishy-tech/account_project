package lebleu.france.comp30022.lebleu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {



    private FirebaseAuth firebaseAuth;

    private TextView textViewHome;
    private Button buttonLogout;
    private Button buttonEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewHome = (TextView) findViewById(R.id.textViewHome);

        textViewHome.setText("Welcome " + user.getEmail());
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonEditProfile = (Button) findViewById(R.id.buttonEditProfile);

        buttonLogout.setOnClickListener(this);
        buttonEditProfile.setOnClickListener(this);
        textViewHome.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogout){
            firebaseAuth.signOut();
            this.startActivity(new Intent(this, LoginActivity.class));
        }

        if(view == textViewHome){
            //will open login activity here
            this.startActivity(new Intent(this, LoginActivity.class));
        }

        if(view == buttonEditProfile){
            this.startActivity(new Intent(this, AccountCreationActivity.class));
        }
    }
}
