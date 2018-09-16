package vukan.com.chatRooms;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String CATEGORY = "category";
    public static final String USER = "user";
    public static final String USERNAME = "username";
    private static final int RC_SIGN_IN = 1;
    private static final int REQUEST_INVITE = 2;
    private static final String TAG = "MainActivity";
    private FirebaseAuth mFirebaseAuth;
    @Nullable
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Nullable
    private FirebaseUser user;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, data -> {
                    if (data == null) {
                        Log.d(TAG, "getInvitation: no data");
                        return;
                    }
                    Uri deepLink = data.getLink();
                    FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(data);
                    if (invite != null) {
                        String invitationId = invite.getInvitationId();
                    }
                })
                .addOnFailureListener(this, e -> Log.w(TAG, "getDynamicLink:onFailure", e));

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user == null) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(true, true)
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                                        new AuthUI.IdpConfig.TwitterBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                                .setTheme(R.style.GreenTheme)
                                .setLogo(R.mipmap.ic_launcher)
                                .build(),
                        RC_SIGN_IN);
            }
        };
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        animation.setDuration(100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK)
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signed out!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                if (response == null) {
                    Toast.makeText(this, "Sign in cancelled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
            } else Toast.makeText(this, "Failed to sent invitation", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_menu:
                Intent intent1 = new Intent(this, ProfileActivity.class);
                intent1.putExtra(USERNAME, Objects.requireNonNull(user).getDisplayName());
                intent1.setData(user.getPhotoUrl());
                startActivity(intent1);
                break;
            case R.id.chat_rules_menu:
                startActivity(new Intent(this, ChatRulesActivity.class));
                break;
            case R.id.share:
                Intent shareIntent = ShareCompat.IntentBuilder
                        .from(this)
                        .setType("text/plain")
                        .setText("Hello from Chat Rooms!")
                        .getIntent();
                if (shareIntent.resolveActivity(getPackageManager()) != null)
                    startActivity(shareIntent);
                break;
            case R.id.invite_friends:
                Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                        .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                        .setCallToActionText(getString(R.string.invitation_cta))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);
                break;
            case R.id.share_2:
                Intent sendIntent = new Intent();
                String msg = "Hey, check this out: https://vukan97.page.link/chat_rooms";
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.sign_out_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> Toast.makeText(this, "Signed out!", Toast.LENGTH_SHORT).show());
                break;
            case R.id.delete_account_menu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Delete account")
                        .setMessage("Are you sure?")
                        .setCancelable(false);
                builder.setPositiveButton(android.R.string.yes, (dialog, which) -> AuthUI.getInstance()
                        .delete(this)
                        .addOnCompleteListener(task -> Toast.makeText(this, "Your account is deleted :(", Toast.LENGTH_SHORT).show()));
                builder.setNegativeButton(android.R.string.no, null);
                builder.create();
                builder.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(Objects.requireNonNull(mAuthStateListener));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    public void clickHandler1(View view) {
        view.startAnimation(animation);
        openChat("sport");
    }

    public void clickHandler2(View view) {
        view.startAnimation(animation);
        openChat("economy");
    }

    public void clickHandler3(View view) {
        view.startAnimation(animation);
        openChat("technology");
    }

    public void clickHandler4(View view) {
        view.startAnimation(animation);
        openChat("movies");
    }

    public void clickHandler5(View view) {
        view.startAnimation(animation);
        openChat("series");
    }

    public void clickHandler6(View view) {
        view.startAnimation(animation);
        openChat("art");
    }

    public void clickHandler7(View view) {
        view.startAnimation(animation);
        openChat("music");
    }

    public void clickHandler8(View view) {
        view.startAnimation(animation);
        openChat("games");
    }

    public void clickHandler9(View view) {
        view.startAnimation(animation);
        openChat("countries");
    }

    private void openChat(String category) {
        Intent intent = new Intent(this, SubCategoriesActivity.class);
        intent.putExtra(CATEGORY, category);
        intent.putExtra(USER, Objects.requireNonNull(user).getDisplayName());
        intent.setData(user.getPhotoUrl());
        startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle());
    }
}