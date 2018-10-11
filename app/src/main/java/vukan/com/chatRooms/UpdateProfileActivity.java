package vukan.com.chatRooms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * <h1>UpdateProfileActivity</h1>
 *
 * <p><b>UpdateProfileActivity</b> is responsible for allowing user to change it's profile picture and username.</p>
 */
public class UpdateProfileActivity extends AppCompatActivity {
    private static final int RC_PHOTO_PICKER = 1;
    private static final String PHOTO = "PHOTO";
    @Nullable
    private Uri mSelectedImageUri;
    private Animation mAnimation;
    @Nullable
    private EditText mName;
    private CircleImageView photo;

    /**
     * This method is responsible for constructing screen for UpdateProfileActivity.
     *
     * @param savedInstanceState potentially contain saved state due to configurations changes.
     * @see AppCompatActivity#onCreate(Bundle, PersistableBundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mName = findViewById(R.id.update_username);
        photo = findViewById(R.id.photo);
        mAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        mAnimation.setDuration(100);
        Intent intent = getIntent();

        if (intent.hasExtra(MainActivity.USERNAME) && mName != null)
            mName.setText(intent.getStringExtra(MainActivity.USERNAME));
        if (intent.getData() != null) mSelectedImageUri = intent.getData();

        Glide.with(photo.getContext())
                .load(mSelectedImageUri)
                .into(photo);

        photo.setOnClickListener(view -> {
            view.startAnimation(mAnimation);
            Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
            intent1.setType("image/jpeg");
            intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            Intent chooser = Intent.createChooser(intent1, getString(R.string.photo_picker_title));
            if (intent1.resolveActivity(getPackageManager()) != null)
                startActivityForResult(chooser, RC_PHOTO_PICKER);
        });
    }

    /**
     * This method change user profile picture and/or username.
     *
     * @param view represent button which user pressed to update profile.
     * @see FirebaseUser#updateProfile(UserProfileChangeRequest)
     */
    public void changeProfile(@NonNull View view) {
        view.startAnimation(mAnimation);
        if (mName != null && mName.length() != 0 && !mName.getText().toString().equals("") && !mName.getText().toString().isEmpty()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
            builder.setDisplayName(mName.getText().toString());
            if (mSelectedImageUri != null) builder.setPhotoUri(mSelectedImageUri);

            if (user != null) {
                user.updateProfile(builder.build())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
            }
        }
    }

    /**
     * This method is called after user choose profile picture from phone.
     *
     * @param requestCode with request code we can check from which activity is user back
     * @param resultCode  with result code we can check if result of called activity was successful
     * @param data        represent picture which user was choose, or null if user does not choose any picture
     * @see AppCompatActivity#onActivityResult(int, int, Intent)
     * @see Glide
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK)
            if (data != null) {
                mSelectedImageUri = data.getData();
                Glide.with(photo.getContext())
                        .load(data.getData())
                        .into(photo);
            }
    }

    /**
     * In this method we save profile picture which user choose, if user maybe change screen orientation, picture will not be lost.
     *
     * @param outState represent Bundle were we save profile picture uri like string
     * @see AppCompatActivity#onSaveInstanceState(Bundle, PersistableBundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mSelectedImageUri != null) outState.putString(PHOTO, mSelectedImageUri.toString());
        super.onSaveInstanceState(outState);
    }

    /**
     * This method is called if we previously saved some data in onSaveInstanceState() method, and here we can get that data.
     *
     * @param savedInstanceState previously saved data in onSaveInstanceState() method, in this case contain profile picture uri.
     * @see AppCompatActivity#onRestoreInstanceState(Bundle)
     * @see Glide
     * @see UpdateProfileActivity#onSaveInstanceState(Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSelectedImageUri = Uri.parse(savedInstanceState.getString(PHOTO));
        Glide.with(photo.getContext())
                .load(mSelectedImageUri)
                .into(photo);
    }
}