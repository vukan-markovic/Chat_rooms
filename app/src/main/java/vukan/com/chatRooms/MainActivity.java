package vukan.com.chatRooms;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String CATEGORY = "category";
    public static final String USER = "mFirebaseUser";
    public static final String USERNAME = "username";
    private static final int RC_SIGN_IN = 1;
    private static final int REQUEST_INVITE = 2;
    private static final String TAG = "MainActivity";
    private FirebaseAuth mFirebaseAuth;
    Animation animation;
    @Nullable
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Nullable
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        animation.setDuration(100);
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnFailureListener(this, e -> Toast.makeText(this, R.string.dynamic_link_fail, Toast.LENGTH_SHORT).show());

        if (!isConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.internet_connection)
                    .setMessage(R.string.wi_fi)
                    .setCancelable(false)
                    .setIcon(R.drawable.signal_wifi_off);
            builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) wifiManager.setWifiEnabled(true);
            });
            builder.setNegativeButton(R.string.exit, ((dialog, which) -> finish()));
            builder.create();
            builder.show();
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            mFirebaseUser = firebaseAuth.getCurrentUser();
            if (mFirebaseUser == null) {
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(true, true)
                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build(), new AuthUI.IdpConfig.TwitterBuilder().build(), new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setTheme(R.style.GreenTheme)
                        .setLogo(R.mipmap.ic_launcher)
                        .build(), RC_SIGN_IN);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK)
                Toast.makeText(this, R.string.signed_in, Toast.LENGTH_SHORT).show();
            else if (IdpResponse.fromResultIntent(data) == null) finish();
        } else if (requestCode == REQUEST_INVITE)
            if (resultCode == RESULT_CANCELED)
                Toast.makeText(this, R.string.invitation, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_profile_menu:
                Intent intent1 = new Intent(this, UpdateProfileActivity.class);
                intent1.putExtra(USERNAME, Objects.requireNonNull(mFirebaseUser).getDisplayName());
                startActivity(intent1);
                break;
            case R.id.chat_rules_menu:
                startActivity(new Intent(this, ChatRulesActivity.class));
                break;
            case R.id.invite_friends_menu:
                Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                        .setCustomImage(Uri.parse(getString(R.string.invite_friends_email_photo)))
                        .setCallToActionText(getString(R.string.invitation_cta))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);
                break;
            case R.id.share_menu:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, R.string.share_app);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.sign_out_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> Toast.makeText(this, R.string.signed_out, Toast.LENGTH_SHORT).show());
                break;
            case R.id.delete_account_menu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_account)
                        .setMessage(R.string.confirm)
                        .setCancelable(false)
                        .setIcon(R.drawable.delete);
                builder.setPositiveButton(android.R.string.yes, (dialog, which) -> AuthUI.getInstance()
                        .delete(this)
                        .addOnCompleteListener(task -> Toast.makeText(this, R.string.account_deleted, Toast.LENGTH_SHORT).show()));
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

    public void sportCategoryClickHandler(@NonNull View view) {
        view.startAnimation(animation);
        openChat("sport");
    }

    public void economyCategoryClickHandler(@NonNull View view) {
        view.startAnimation(animation);
        openChat("economy");
    }

    public void technologyCategoryClickHandler(@NonNull View view) {
        view.startAnimation(animation);
        openChat("technology");
    }

    public void moviesCategoryClickHandler(@NonNull View view) {
        view.startAnimation(animation);
        openChat("movies");
    }

    public void seriesCategoryClickHandler(@NonNull View view) {
        view.startAnimation(animation);
        openChat("series");
    }

    public void artCategoryClickHandler(@NonNull View view) {
        view.startAnimation(animation);
        openChat("art");
    }

    public void musicCategoryClickHandler(@NonNull View view) {
        view.startAnimation(animation);
        openChat("music");
    }

    public void gamesCategoryClickHandler(@NonNull View view) {
        view.startAnimation(animation);
        openChat("games");
    }

    public void countriesCategoryClickHandler(@NonNull View view) {
        view.startAnimation(animation);
        openChat("countries");
    }

    private void openChat(String category) {
        Intent intent = new Intent(this, SubCategoriesActivity.class);
        intent.putExtra(CATEGORY, category);
        intent.putExtra(USER, Objects.requireNonNull(mFirebaseUser).getDisplayName());
        intent.setData(mFirebaseUser.getPhotoUrl());
        startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle());
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}