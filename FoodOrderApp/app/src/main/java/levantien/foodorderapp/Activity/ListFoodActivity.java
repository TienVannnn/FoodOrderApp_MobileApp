package levantien.foodorderapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import levantien.foodorderapp.Adapter.FoodListAdapter;
import levantien.foodorderapp.Domain.Foods;
import levantien.foodorderapp.R;
import levantien.foodorderapp.databinding.ActivityListFoodBinding;

public class ListFoodActivity extends BaseActivity {
    ActivityListFoodBinding binding;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private boolean isSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityListFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        initList();
    }

    private void initList() {
        DatabaseReference reference = database.getReference("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();
        Query query;
        if(isSearch){
            query = reference.orderByChild("Title").startAt(searchText).endAt(searchText + '\uf8ff');
        }
        else {
            query = reference.orderByChild("CategoryId").equalTo(categoryId);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot issue: snapshot.getChildren()){
                        list.add(issue.getValue(Foods.class));
                    }
                    if(list.size() > 0){
                        binding.recycleFood.setLayoutManager(new LinearLayoutManager(ListFoodActivity.this, LinearLayoutManager.VERTICAL, false));
                        binding.recycleFood.setAdapter(new FoodListAdapter(list, ListFoodActivity.this));
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);
        binding.tvTitle.setText(categoryName);
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}