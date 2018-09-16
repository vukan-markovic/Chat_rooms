package vukan.com.chatRooms;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class SubCategoriesActivity extends AppCompatActivity implements SubCategoriesAdapter.ListItemClickListener {
    public static final String SUBCATEGORY = "subcategory";
    public static final String CATEGORY = "category";
    public static final String USER = "user";
    private SubCategoriesAdapter mSubCategoriesAdapter;
    private Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategories);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        intent1 = getIntent();

        if (intent1.hasExtra(MainActivity.CATEGORY)) {
            switch (intent1.getStringExtra(MainActivity.CATEGORY)) {
                case "sport":
                    mSubCategoriesAdapter = new SubCategoriesAdapter(getResources().getStringArray(R.array.sports).length, this, intent1.getStringExtra(MainActivity.CATEGORY), this);
                    setTitle("Sports");
                    break;
                case "technology":
                    mSubCategoriesAdapter = new SubCategoriesAdapter(getResources().getStringArray(R.array.technologies).length, this, intent1.getStringExtra(MainActivity.CATEGORY), this);
                    setTitle("Technology");
                    break;
                case "movies":
                    mSubCategoriesAdapter = new SubCategoriesAdapter(getResources().getStringArray(R.array.movies).length, this, intent1.getStringExtra(MainActivity.CATEGORY), this);
                    setTitle("Movies");
                    break;
                case "series":
                    mSubCategoriesAdapter = new SubCategoriesAdapter(getResources().getStringArray(R.array.series).length, this, intent1.getStringExtra(MainActivity.CATEGORY), this);
                    setTitle("Series");
                    break;
                case "economy":
                    setTitle("Crypto currency");
                    mSubCategoriesAdapter = new SubCategoriesAdapter(getResources().getStringArray(R.array.crypto_currency).length, this, intent1.getStringExtra(MainActivity.CATEGORY), this);
                    break;
                case "art":
                    mSubCategoriesAdapter = new SubCategoriesAdapter(getResources().getStringArray(R.array.art).length, this, intent1.getStringExtra(MainActivity.CATEGORY), this);
                    setTitle("Art");
                    break;
                case "music":
                    mSubCategoriesAdapter = new SubCategoriesAdapter(getResources().getStringArray(R.array.music).length, this, intent1.getStringExtra(MainActivity.CATEGORY), this);
                    setTitle("Music");
                    break;
                case "games":
                    setTitle("Games");
                    mSubCategoriesAdapter = new SubCategoriesAdapter(getResources().getStringArray(R.array.games).length, this, intent1.getStringExtra(MainActivity.CATEGORY), this);
                    break;
                case "countries":
                    setTitle("Countries");
                    mSubCategoriesAdapter = new SubCategoriesAdapter(getResources().getStringArray(R.array.countries).length, this, intent1.getStringExtra(MainActivity.CATEGORY), this);
                    break;
            }
        }
        recyclerView.setAdapter(mSubCategoriesAdapter);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(CATEGORY, intent1.getStringExtra(MainActivity.CATEGORY));
        intent.putExtra(SUBCATEGORY, mSubCategoriesAdapter.getSubcategories()[clickedItemIndex]);
        intent.putExtra(USER, intent1.getStringExtra(MainActivity.USER));
        intent.setData(intent1.getData());
        startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

}