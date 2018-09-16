package vukan.com.chatRooms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ChatActivity extends AppCompatActivity {
    private static final String ANONYMOUS = "anonymous";
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 500;
    private MessageAdapter mMessageAdapter;
    private EditText messageEnter;
    private Button mSendButton;
    private String mUsername;
    private SoundHelper sound;
    private DatabaseReference mMessagesDatabaseReference;
    @Nullable
    private ChildEventListener mChildEventListener;
    private Intent intent;
    private String message;
    private List<String> words;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = getIntent();
        mUsername = ANONYMOUS;
        words = Arrays.asList(getResources().getStringArray(R.array.bad_words));
        if (intent.hasExtra(SubCategoriesActivity.CATEGORY) && intent.hasExtra(SubCategoriesActivity.SUBCATEGORY))
            mMessagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child(intent.getStringExtra(SubCategoriesActivity.CATEGORY) + "/" + intent.getStringExtra(SubCategoriesActivity.SUBCATEGORY));
        setTitle(intent.getStringExtra(SubCategoriesActivity.SUBCATEGORY));
        ListView mMessageListView = findViewById(R.id.message_list_view);
        ViewGroup relativeLayout = findViewById(R.id.relative_layout);
        messageEnter = findViewById(R.id.messageEnter);
        mSendButton = findViewById(R.id.send);
        List<Message> messages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, messages);
        mMessageListView.setAdapter(mMessageAdapter);
        if (intent.hasExtra(SubCategoriesActivity.USER))
            mUsername = intent.getStringExtra(SubCategoriesActivity.USER);
        sound = new SoundHelper(this);
        attachDatabaseReadListener();

        messageEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(@NonNull CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) mSendButton.setEnabled(true);
                else mSendButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        messageEnter.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        mSendButton.setOnClickListener(view -> {
            animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
            view.startAnimation(animation);
            animation.setDuration(200);
            if (messageEnter.length() != 0 && !messageEnter.getText().toString().equals("") && !messageEnter.getText().toString().isEmpty()) {
                message = messageEnter.getText().toString().trim();
                for (String word : words)
                    message = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE).matcher(message).replaceAll(new String(new char[word.length()]).replace('\0', '*'));
                mMessagesDatabaseReference.push().setValue(new Message(message, mUsername, Objects.requireNonNull(intent.getData()).toString(), Calendar.getInstance().getTime().toString()));
                sound.playSound();
                messageEnter.setText("");
            }
        });

        switch (intent.getStringExtra(SubCategoriesActivity.CATEGORY)) {
            case "sport":
                relativeLayout.setBackgroundResource(R.drawable.chat_background_6);
                break;
            case "economy":
                relativeLayout.setBackgroundResource(R.drawable.chat_background_1);
                break;
            case "movies":
                relativeLayout.setBackgroundResource(R.drawable.chat_background_2);
                break;
            case "technology":
                relativeLayout.setBackgroundResource(R.drawable.chat_background_3);
                break;
            case "art":
                relativeLayout.setBackgroundResource(R.drawable.chat_background_4);
                break;
            case "music":
                relativeLayout.setBackgroundResource(R.drawable.chat_background_5);
                break;
            case "games":
                relativeLayout.setBackgroundResource(R.drawable.chat_background);
                break;
            case "series":
                relativeLayout.setBackgroundResource(R.drawable.chat_background_7);
                break;
            case "countries":
                relativeLayout.setBackgroundResource(R.drawable.chat_background_8);
                break;
            default:
                relativeLayout.setBackgroundResource(R.drawable.chat_background);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    mMessageAdapter.add(dataSnapshot.getValue(Message.class));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}