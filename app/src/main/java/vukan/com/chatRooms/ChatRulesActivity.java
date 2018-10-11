package vukan.com.chatRooms;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

/**
 * <h1>ChatRulesActivity</h1>
 *
 * <p><b>ChatRulesActivity</b> class is responsible for representing user screen which contain chat rules.</p>
 */
public class ChatRulesActivity extends AppCompatActivity {

    /**
     * This method set xml layout file for this activity.
     *
     * @param savedInstanceState potentially contain saved state due to configurations changes.
     * @see AppCompatActivity#onCreate(Bundle, PersistableBundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rules);
    }
}