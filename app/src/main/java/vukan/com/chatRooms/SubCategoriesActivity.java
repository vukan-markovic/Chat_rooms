package vukan.com.chatRooms;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class SubCategoriesActivity extends AppCompatActivity implements SubCategoriesAdapter.ListItemClickListener {
    public static final String CATEGORY = "category", SUBCATEGORY = "subcategory", USER = "user";
    private static final String PHOTO = "photo";
    private String category, user;
    private Uri photo;
    private SubCategoriesAdapter mSubCategoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategories);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        Resources resources = getResources();
        Intent mIntent = getIntent();
        if (mIntent.hasExtra(MainActivity.USER))
            user = mIntent.getStringExtra(MainActivity.USER);
        if (mIntent.getData() != null) photo = mIntent.getData();

        if (mIntent.hasExtra(MainActivity.CATEGORY)) {
            category = mIntent.getStringExtra(MainActivity.CATEGORY);
            switch (category) {
                case "sport":
                    mSubCategoriesAdapter = new SubCategoriesAdapter(resources.getStringArray(R.array.sports).length, this, category, resources);
                    setTitle("Sports");
                    break;
                case "technology":
                    mSubCategoriesAdapter = new SubCategoriesAdapter(resources.getStringArray(R.array.technologies).length, this, category, resources);
                    setTitle("Technology");
                    break;
                case "movies":
                    mSubCategoriesAdapter = new SubCategoriesAdapter(resources.getStringArray(R.array.movies).length, this, category, resources);
                    setTitle("Movies");
                    break;
                case "series":
                    mSubCategoriesAdapter = new SubCategoriesAdapter(resources.getStringArray(R.array.series).length, this, category, resources);
                    setTitle("Series");
                    break;
                case "economy":
                    setTitle("Crypto currency");
                    mSubCategoriesAdapter = new SubCategoriesAdapter(resources.getStringArray(R.array.crypto_currency).length, this, category, resources);
                    break;
                case "art":
                    mSubCategoriesAdapter = new SubCategoriesAdapter(resources.getStringArray(R.array.art).length, this, category, resources);
                    setTitle("Art");
                    break;
                case "music":
                    mSubCategoriesAdapter = new SubCategoriesAdapter(resources.getStringArray(R.array.music).length, this, category, resources);
                    setTitle("Music");
                    break;
                case "games":
                    setTitle("Games");
                    mSubCategoriesAdapter = new SubCategoriesAdapter(resources.getStringArray(R.array.games).length, this, category, resources);
                    break;
                case "countries":
                    setTitle("Countries");
                    mSubCategoriesAdapter = new SubCategoriesAdapter(resources.getStringArray(R.array.countries).length, this, category, resources);
                    break;
            }
        }

        recyclerView.setAdapter(mSubCategoriesAdapter);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(CATEGORY, category);
        intent.putExtra(SUBCATEGORY, mSubCategoriesAdapter.getSubcategories()[clickedItemIndex]);
        intent.putExtra(USER, user);
        intent.setData(photo);
        startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}