package vukan.com.chatRooms;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SubCategoriesHolder> {

    private final int mNumbersItems;
    private String[] mSubcategories;

    SubCategoriesAdapter(int numberOfItems, ListItemClickListener listener, @NonNull String category, @NonNull Context context) {
        mNumbersItems = numberOfItems;
        mOnClickListener = listener;

        switch (category) {
            case "sport":
                mSubcategories = context.getResources().getStringArray(R.array.sports);
                break;
            case "technology":
                mSubcategories = context.getResources().getStringArray(R.array.technologies);
                break;
            case "movies":
                mSubcategories = context.getResources().getStringArray(R.array.movies);
                break;
            case "series":
                mSubcategories = context.getResources().getStringArray(R.array.series);
                break;
            case "economy":
                mSubcategories = context.getResources().getStringArray(R.array.crypto_currency);
                break;
            case "art":
                mSubcategories = context.getResources().getStringArray(R.array.art);
                break;
            case "music":
                mSubcategories = context.getResources().getStringArray(R.array.music);
                break;
            case "games":
                mSubcategories = context.getResources().getStringArray(R.array.games);
                break;
            case "countries":
                mSubcategories = context.getResources().getStringArray(R.array.countries);
                break;
        }

        Arrays.sort(mSubcategories);
    }

    final private ListItemClickListener mOnClickListener;

    String[] getSubcategories() {
        return mSubcategories;
    }

    @NonNull
    @Override
    public SubCategoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubCategoriesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subcategory, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoriesHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumbersItems;
    }

    class SubCategoriesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView subcategory;

        SubCategoriesHolder(@NonNull View view) {
            super(view);
            subcategory = view.findViewById(R.id.subcategory_text);
            view.setOnClickListener(this);
        }

        void bind(int listIndex) {
            subcategory.setText(mSubcategories[listIndex]);
            if (listIndex % 2 == 0)
                subcategory.setBackgroundResource(R.drawable.recycler_view_selector_1);
            else subcategory.setBackgroundResource(R.drawable.recycler_view_selector_2);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
}