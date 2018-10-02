package vukan.com.chatRooms;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
    public static final String CATEGORY = "category", USER = "user", USERNAME = "username";
    private static final int RC_SIGN_IN = 1, REQUEST_INVITE = 2;
    private boolean flag;
    private FirebaseAuth mFirebaseAuth;
    private Animation mAnimation;
    @Nullable
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Nullable
    private FirebaseUser mFirebaseUser;
    private DialogFragment mDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        mAnimation.setDuration(100);
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnFailureListener(this, e -> Toast.makeText(this, R.string.dynamic_link_fail, Toast.LENGTH_SHORT).show());

        if (!isConnected()) {
            mDialogFragment = DialogWindow.newInstance(getString(R.string.internet_connection), getString(R.string.wi_fi), flag);
            mDialogFragment.show(getSupportFragmentManager(), "tag");
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            mFirebaseUser = firebaseAuth.getCurrentUser();
            if (mFirebaseUser == null) {
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(true, true)
                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build(), new AuthUI.IdpConfig.TwitterBuilder().build(), new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setTheme(R.style.AuthTheme)
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
                Intent intent = new Intent(this, UpdateProfileActivity.class);
                intent.putExtra(USERNAME, Objects.requireNonNull(mFirebaseUser).getDisplayName());
                intent.setData(mFirebaseUser.getPhotoUrl());
                startActivity(intent);
                break;
            case R.id.chat_rules_menu:
                startActivity(new Intent(this, ChatRulesActivity.class));
                break;
            case R.id.invite_friends_menu:
                Intent intent1 = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                        .setCustomImage(Uri.parse(getString(R.string.invite_friends_email_photo)))
                        .setCallToActionText(getString(R.string.invitation_cta))
                        .build();
                startActivityForResult(intent1, REQUEST_INVITE);
                break;
            case R.id.share_menu:
                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_SEND);
                intent2.putExtra(Intent.EXTRA_TEXT, "Hey, check this out: https://vukan97.page.link/chat_rooms!");
                intent2.setType("text/plain");
                startActivity(intent2);
                break;
            case R.id.sign_out_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> Toast.makeText(this, R.string.signed_out, Toast.LENGTH_SHORT).show());
                break;
            case R.id.delete_account_menu:
                flag = true;
                mDialogFragment = DialogWindow.newInstance(getString(R.string.delete_account), getString(R.string.confirm), flag);
                mDialogFragment.show(getSupportFragmentManager(), "tag");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void doPositiveClickDelete() {
        AuthUI.getInstance().delete(this).addOnCompleteListener(task -> Toast.makeText(this, R.string.account_deleted, Toast.LENGTH_SHORT).show());
    }

    private void doPositiveClickConnection() {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) wifiManager.setWifiEnabled(true);
    }

    private void doNegativeClickConnection() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(Objects.requireNonNull(mAuthStateListener));
    }

    public void sportCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("sport");
    }

    public void economyCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("economy");
    }

    public void technologyCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("technology");
    }

    public void moviesCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("movies");
    }

    public void seriesCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("series");
    }

    public void artCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("art");
    }

    public void musicCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("music");
    }

    public void gamesCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("games");
    }

    public void countriesCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("countries");
    }

    public static class DialogWindow extends DialogFragment {

        static DialogWindow newInstance(String title, String message, Boolean flag) {
            DialogWindow dialog = new DialogWindow();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("message", message);
            args.putBoolean("flag", flag);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (getArguments() != null) {
                builder.setTitle(getArguments().getString("title"));
                builder.setMessage(getArguments().getString("message"));
                builder.setCancelable(false);
                if (getArguments().getBoolean("flag")) {
                    builder.setIcon(R.drawable.delete);
                    builder.setPositiveButton(android.R.string.yes, (dialog, which) -> ((MainActivity) Objects.requireNonNull(getActivity())).doPositiveClickDelete());
                    builder.setNegativeButton(android.R.string.no, null);
                } else {
                    builder.setIcon(R.drawable.signal_wifi_off);
                    builder.setPositiveButton(android.R.string.yes, (dialog, which) -> ((MainActivity) Objects.requireNonNull(getActivity())).doPositiveClickConnection());
                    builder.setNegativeButton(R.string.exit, (dialog, which) -> ((MainActivity) Objects.requireNonNull(getActivity())).doNegativeClickConnection());
                }
            }

            return builder.create();
        }
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
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null)
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}