package lebleu.france.comp30022.lebleu;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class AccountCreationActivity extends AppCompatActivity implements View.OnClickListener  {

    //tag used in logs
    private static final String TAG = "AccountCreationActivity";

    //reference to root of firebase database
    private DatabaseReference root;

    //references to firebase storage
    //will be used later
    private FirebaseStorage storage;
    private StorageReference mStorageRef;

    //unique firebase ID for each user
    private String userId;

    //getting the user instance from firebase to alter its data
    private FirebaseUser user;

    UserProfileChangeRequest profileUpdates;

    //various views in layout which are binded to variables
    private Button buttonCreateAccount;
    private Button buttonChangeAvatar;

    private ImageView imageViewAvatar;

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;

    private CheckBox checkBoxIsElderly;
    private CheckBox checkBoxIsHelper;

    //uri of avatar image
    Uri imageUri;

    private static final int PICK_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        //gets a unique userId from firebase
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //gets the root of the firebase database used for the app
        root = FirebaseDatabase.getInstance().getReference().getRoot();
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();

        //binds the buttons, image view, edit texts and
        //checkboxes to appropriate views on UI
        buttonCreateAccount = (Button) findViewById(R.id.buttonSubmitInformation);
        buttonChangeAvatar = (Button) findViewById(R.id.buttonChangeAvatar);

        imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

        checkBoxIsElderly = (CheckBox) findViewById(R.id.checkboxIsElderly);
        checkBoxIsHelper = (CheckBox) findViewById(R.id.checkboxIsHelper);

        //bind the buttons to on click listeners
        //such that they can trigger events
        buttonChangeAvatar.setOnClickListener(this);
        buttonCreateAccount.setOnClickListener(this);
    }

    //function to error check before storing information to firebase
    public Boolean errorCheck(){
        return true;
    }

    //function to store information to firebase
    public void storeInformation(){

        //storing username, email, password, and confirm password
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        //if the 2 new passwords match, then store the info
        if(password.equals(confirmPassword)) {

            if(!TextUtils.isEmpty(password)) {
                updatePassword(password);
            }

            if(!TextUtils.isEmpty(username)) {
                updateProfile(username, imageUri, email);
            }
            if(!TextUtils.isEmpty(password)|| !TextUtils.isEmpty(email) || checkBoxIsHelper.isChecked() || checkBoxIsElderly.isChecked()) {

                User user = new User(username, email, checkBoxIsElderly.isChecked(), checkBoxIsHelper.isChecked());

                //add the username to the root of the database
                root.child("users").child(userId).setValue(user);

                Toast.makeText(this, "Account successfully updated", Toast.LENGTH_SHORT).show();
            }

        } else{
            Toast.makeText(this, "Passwords did not match" , Toast.LENGTH_SHORT).show();
        }
    }

    //updates a new password to the firebase auth
    private void updatePassword(String password){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });
    }

    //updates the username, email and profile pic of user in firebaseauth
    public void updateProfile(String username, Uri uri, String email) {
        // [START update_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates;

        //if an image was selected then update it, otherwise leave the original
        if(uri == null) {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();
        } else{
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(uri)
                    .build();
        }

        //if an email was inputed, update it in firebase auth
        if(email != null){
            user.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User email address updated.");
                            }
                        }
                    });
        }

        //update the profile
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
        // [END update_profile]
    }

    //opens view listing the photos on phone
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    //image that is clicked is saved and displayed on view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageViewAvatar.setImageURI(imageUri);
        }
    }



    public void onClick(View view) {
        if(view == buttonCreateAccount){
            if(errorCheck()){
                storeInformation();
                startActivity(new Intent(this, HomeActivity.class));
            }
        }

        if(view == buttonChangeAvatar){
            openGallery();
        }

    }
}
