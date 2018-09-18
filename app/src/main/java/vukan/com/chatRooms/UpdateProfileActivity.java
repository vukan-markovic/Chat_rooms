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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UpdateProfileActivity extends AppCompatActivity {
    private static final int RC_PHOTO_PICKER = 1;
    @Nullable
    private Uri mSelectedImageUri;
    private Animation mAnimation;
    @Nullable
    private EditText mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mName = findViewById(R.id.update_username);
        ImageButton photo = findViewById(R.id.photo);
        if (getIntent().hasExtra(MainActivity.USERNAME) && mName != null)
            mName.setText(getIntent().getStringExtra(MainActivity.USERNAME));
        mAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        mAnimation.setDuration(100);

        photo.setOnClickListener(view -> {
            view.startAnimation(mAnimation);
            Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
            intent1.setType("image/jpeg");
            intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent1, getString(R.string.photo_picker_title)), RC_PHOTO_PICKER);
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
            if (data != null) mSelectedImageUri = data.getData();
    }
}