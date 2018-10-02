package vukan.com.chatRooms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class UpdateProfileActivity extends AppCompatActivity {
    private static final int RC_PHOTO_PICKER = 1;
    private static final String PHOTO = "PHOTO";
    @Nullable
    private Uri mSelectedImageUri;
    private Animation mAnimation;
    @Nullable
    private EditText mName;
    private CircleImageView photo;

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mSelectedImageUri != null) outState.putString(PHOTO, mSelectedImageUri.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSelectedImageUri = Uri.parse(savedInstanceState.getString(PHOTO));
        Glide.with(photo.getContext())
                .load(mSelectedImageUri)
                .into(photo);
    }
}