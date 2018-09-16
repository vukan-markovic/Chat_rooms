package vukan.com.chatRooms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    public static final String USERNAME = "username";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView username = findViewById(R.id.username);
        ImageView profilePictureImageView = findViewById(R.id.profile_picture);
        intent = getIntent();
        if (intent.hasExtra(MainActivity.USERNAME) && intent.getStringExtra(MainActivity.USERNAME) != null)
            username.setText(intent.getStringExtra(MainActivity.USERNAME));
        if (intent.getData() != null) {
            Glide.with(profilePictureImageView.getContext())
                    .load(Objects.requireNonNull(intent.getData()))
                    .into(profilePictureImageView);
        }
    }

    public void updateProfile(View view) {
        Intent intent1 = new Intent(this, UpdateProfileActivity.class);
        intent1.putExtra(USERNAME, intent.getStringExtra(MainActivity.USERNAME));
        startActivity(intent1);
    }
}