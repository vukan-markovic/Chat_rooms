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

class MessageAdapter extends ArrayAdapter<Message> {

    MessageAdapter(@NonNull Context context, @NonNull List<Message> objects) {
        super(context, R.layout.item_message, objects);
    }

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