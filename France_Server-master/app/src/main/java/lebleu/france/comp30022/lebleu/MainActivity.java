package lebleu.france.comp30022.lebleu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextUtils;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //root of firebase database and unique userID
    private DatabaseReference root;

    private FirebaseAuth firebaseAuth;

    private Button buttonRegister;
    private Button buttonLogin;

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    private CheckBox checkBoxIsElderly;
    private CheckBox checkBoxIsHelper;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //gets the root of the firebase database used for the app
        root = FirebaseDatabase.getInstance().getReference().getRoot();

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);


        //get the username, email and password
        //submitted by the user
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        checkBoxIsElderly = (CheckBox) findViewById(R.id.checkboxIsElderly);
        checkBoxIsHelper = (CheckBox) findViewById(R.id.checkboxIsHelper);

        textViewSignup = (TextView) findViewById(R.id.textViewSignup);

        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);

    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        //checking if email and password is empty
        if (TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }



        //if validations are ok we will first show a progressbar

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        if(task.isSuccessful()){
                            //user is successfully registered and logged in
                            Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();

                            storeInformation();
                            finish();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }else{
                            Toast.makeText(MainActivity.this, "Could not register.. please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        progressDialog.dismiss();
    }

    //stores user information in database
    public void storeInformation(){
        String userId;
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();

        //gets a unique userId from firebase
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //add the username to the root of the database
        User user = new User(username, email, checkBoxIsElderly.isChecked(), checkBoxIsHelper.isChecked());
        root.child("users").child(userId).setValue(user);

    }

    @Override
    public void onClick(View view) {
        if(view == buttonRegister) {
            registerUser();
        }

        if(view == buttonLogin){
            finish();
            this.startActivity(new Intent(this, LoginActivity.class));
        }

        if(view == textViewSignup ){
            //will open login activity here
            this.startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
