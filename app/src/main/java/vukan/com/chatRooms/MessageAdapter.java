package vukan.com.chatRooms;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * <h1>MessageAdapter</h1>
 *
 * <p><b>MessageAdapter</b> class is responsible for loading list of chat messages.</p>
 *
 * @see ArrayAdapter
 */
class MessageAdapter extends ArrayAdapter<Message> {

    MessageAdapter(@NonNull Context context, @NonNull List<Message> objects) {
        super(context, R.layout.item_message, objects);
    }

    /**
     * This method is responsible for fill chat message with all necessary information (message body, profile picture of the author, author name and time when message is sent).
     *
     * @param position    represent position of chat message in list of messages
     * @param convertView represent chat message
     * @param parent      represent parent layout of this view (message)
     * @return View which represent chat message
     * @see ArrayAdapter#getView(int, View, ViewGroup)
     * @see CircleImageView
     * @see Glide
     * @see android.view.LayoutInflater#inflate(int, ViewGroup)
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        CircleImageView profileImageView = convertView.findViewById(R.id.profile_picture_message);
        TextView messageTextView = convertView.findViewById(R.id.message);
        TextView authorTextView = convertView.findViewById(R.id.name);
        TextView timeTextView = convertView.findViewById(R.id.time);
        Message message = getItem(position);
        messageTextView.setText(Objects.requireNonNull(message).getText());
        authorTextView.setText(message.getName());
        timeTextView.setText(message.getDateTime());
        Glide.with(profileImageView.getContext())
                .load(Objects.requireNonNull(message.getProfileUrl()))
                .into(profileImageView);

        return convertView;
    }
}