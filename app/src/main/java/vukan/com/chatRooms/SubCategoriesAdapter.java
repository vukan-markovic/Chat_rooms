package vukan.com.chatRooms;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

/**
 * <h1>SubCategoriesAdapter</h1>
 *
 * <p><b>SubCategoriesAdapter</b> class is responsible to fill RecyclerView with appropriate subcategories of selected topic.</p>
 *
 * @see RecyclerView.Adapter
 */
public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SubCategoriesHolder> {
    private final int mNumbersItems;
    private String[] mSubcategories;
    final private ListItemClickListener mOnClickListener;

    SubCategoriesAdapter(int numberOfItems, ListItemClickListener listener, @NonNull String category, @NonNull Resources resources) {
        mNumbersItems = numberOfItems;
        mOnClickListener = listener;

        switch (category) {
            case "sport":
                mSubcategories = resources.getStringArray(R.array.sports);
                break;
            case "technology":
                mSubcategories = resources.getStringArray(R.array.technologies);
                break;
            case "movies":
                mSubcategories = resources.getStringArray(R.array.movies);
                break;
            case "series":
                mSubcategories = resources.getStringArray(R.array.series);
                break;
            case "economy":
                mSubcategories = resources.getStringArray(R.array.crypto_currency);
                break;
            case "art":
                mSubcategories = resources.getStringArray(R.array.art);
                break;
            case "music":
                mSubcategories = resources.getStringArray(R.array.music);
                break;
            case "games":
                mSubcategories = resources.getStringArray(R.array.games);
                break;
            case "countries":
                mSubcategories = resources.getStringArray(R.array.countries);
                break;
        }

        Arrays.sort(mSubcategories);
    }

    /**
     * This method return string array of subcategories.
     *
     * @return String[] which represent array of subcategories.
     */
    String[] getSubcategories() {
        return mSubcategories;
    }

    /**
     * This method create view holder which represent one subcategory.
     *
     * @param parent   represent parent layout.
     * @param viewType type of the view of the recycler view item.
     * @return SubCategoriesHolder which represent one subcategory.
     * @see RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)
     */
    @NonNull
    @Override
    public SubCategoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubCategoriesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subcategory, parent, false));
    }

    /**
     * This method bind holder to appropriate position.
     *
     * @param holder   represent holder to bind
     * @param position represent position at which to bind holder.
     * @see RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(@NonNull SubCategoriesHolder holder, int position) {
        holder.bind(position);
    }

    /**
     * This method return number of subcategories.
     *
     * @return int which represent number of subcategories for chosen topic.
     * @see RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        return mNumbersItems;
    }

    /**
     * This class represent holder for subcategory.
     *
     * @see RecyclerView.ViewHolder
     * @see View.OnClickListener
     */
    class SubCategoriesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView subcategory;

        SubCategoriesHolder(@NonNull View view) {
            super(view);
            subcategory = view.findViewById(R.id.subcategory_text);
            view.setOnClickListener(this);
        }

        /**
         * This method set name of each subcategory and it's background color.
         *
         * @param listIndex represent position of subcategory in recycler view.
         */
        void bind(int listIndex) {
            subcategory.setText(mSubcategories[listIndex]);
            if (listIndex % 2 == 0)
                subcategory.setBackgroundResource(R.drawable.recycler_view_selector_1);
            else subcategory.setBackgroundResource(R.drawable.recycler_view_selector_2);
        }

        /**
         * This method is called when user tap on some subcategory.
         *
         * @param v represent subcategory which user chose.
         */
        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }

    /**
     * This interface define method which will be called when user tap on some subcategory.
     * This interface implement SubCategoryActivity.
     *
     * @see View.OnClickListener#onClick(View)\
     * @see SubCategoriesActivity#onListItemClick(int)
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
}