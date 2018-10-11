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
import android.os.PersistableBundle;
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

/**
 * <h1>MainActivity</h1>
 *
 * <p><b>MainActivity</b> present user main screen of the application, which display different topics for chat.</p>
 *
 * @author Vukan Marković
 * @version 1.4
 * @since 11.10.2018
 */
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

    /**
     * This method is responsible for constructing screen for MainActivity.
     *
     * @param savedInstanceState potentially contain saved state due to configurations changes.
     * @see AppCompatActivity#onCreate(Bundle, PersistableBundle)
     */
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

    /**
     * This method is called after user is back to this activity.
     *
     * @param requestCode with request code we can check from which activity is user back
     * @param resultCode  with result code we can check if result of called activity was successful
     * @param data        represent potentially data from called activity
     * @see AppCompatActivity#onActivityResult(int, int, Intent)
     */
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

    /**
     * This method create options menu.
     *
     * @param menu represent menu to create.
     * @return boolean indicate if creation was successful.
     * @see AppCompatActivity#onCreateOptionsMenu(Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * This method is called when user chose one of the options in menu, and it's take appropriate action depends on option which user choose.
     *
     * @param item represent menu option which user choose.
     * @return boolean which indicate if user action is handled properly.
     * @see AppCompatActivity#onOptionsItemSelected(MenuItem)
     */
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

    /**
     * This method is called when user choose to delete his account.
     *
     * @see AuthUI#getInstance()
     */
    private void doPositiveClickDelete() {
        AuthUI.getInstance().delete(this).addOnCompleteListener(task -> Toast.makeText(this, R.string.account_deleted, Toast.LENGTH_SHORT).show());
    }

    /**
     * This method is called when user choose to turn on wi-fi connection to continue to use application.
     *
     * @see WifiManager
     */
    private void doPositiveClickConnection() {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) wifiManager.setWifiEnabled(true);
    }

    /**
     * This method is called when user denied to turn on wi-fi connection, and in that case application is closed.
     *
     * @see AppCompatActivity#finish()
     */
    private void doNegativeClickConnection() {
        finish();
    }

    /**
     * This method remove listener from Firebase Authentication instance when this activity goes to paused state.
     *
     * @see AppCompatActivity#onPause()
     * @see FirebaseAuth#removeAuthStateListener(FirebaseAuth.AuthStateListener)
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    /**
     * This method return back previously deleted listener to the Firebase Authentication instance when user is back to this activity.
     *
     * @see MainActivity#onPause()
     * @see AppCompatActivity#onResume()
     * @see FirebaseAuth#addAuthStateListener(FirebaseAuth.AuthStateListener)
     */
    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(Objects.requireNonNull(mAuthStateListener));
    }

    /**
     * This method is called when user choose sport category.
     *
     * @param view represent picture that represent this category.
     */
    public void sportCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("sport");
    }

    /**
     * This method is called when user choose economy category.
     *
     * @param view represent picture that represent this category.
     */
    public void economyCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("economy");
    }

    /**
     * This method is called when user choose technology category.
     *
     * @param view represent picture that represent this category.
     */
    public void technologyCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("technology");
    }

    /**
     * This method is called when user choose movies category.
     *
     * @param view represent picture that represent this category.
     */
    public void moviesCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("movies");
    }

    /**
     * This method is called when user choose series category.
     *
     * @param view represent picture that represent this category.
     */
    public void seriesCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("series");
    }

    /**
     * This method is called when user choose art category.
     *
     * @param view represent picture that represent this category.
     */
    public void artCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("art");
    }

    /**
     * This method is called when user choose music category.
     *
     * @param view represent picture that represent this category.
     */
    public void musicCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("music");
    }

    /**
     * This method is called when user choose games category.
     *
     * @param view represent picture that represent this category.
     */
    public void gamesCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("games");
    }

    /**
     * This method is called when user choose countries category.
     *
     * @param view represent picture that represent this category.
     */
    public void countriesCategoryClickHandler(@NonNull View view) {
        view.startAnimation(mAnimation);
        openChat("countries");
    }

    /**
     * This class create dialog which survives in spite of configuration changes
     *
     * @see DialogFragment
     */
    public static class DialogWindow extends DialogFragment {

        /**
         * This method create new instance of dialog and set it's parameters depending on is this dialog is one that is shown when user choose to delete his account or
         * dialog which remind user that internet connection is required to use this application.
         *
         * @param title   represent title of the dialog
         * @param message represent message of the dialog
         * @param flag    indicate which dialog should be presented, delete account or turn on internet connection dialog.
         * @return DialogWindow which represent dialog instance which is created.
         */
        static DialogWindow newInstance(String title, String message, Boolean flag) {
            DialogWindow dialog = new DialogWindow();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("message", message);
            args.putBoolean("flag", flag);
            dialog.setArguments(args);
            return dialog;
        }

        /**
         * This method build dialog and set it's positive and negative buttons.
         *
         * @return Dialog which represent created dialog.
         */
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

    /**
     * This method open screen with subcategories of topic which user choose.
     *
     * @param category represent category which user choose.
     */
    private void openChat(String category) {
        Intent intent = new Intent(this, SubCategoriesActivity.class);
        intent.putExtra(CATEGORY, category);
        intent.putExtra(USER, Objects.requireNonNull(mFirebaseUser).getDisplayName());
        intent.setData(mFirebaseUser.getPhotoUrl());
        startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle());
    }

    /**
     * This method check if user is connected to the network.
     *
     * @return boolean which indicate if user is connected to the network.
     * @see ConnectivityManager
     * @see NetworkInfo
     */
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null)
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}