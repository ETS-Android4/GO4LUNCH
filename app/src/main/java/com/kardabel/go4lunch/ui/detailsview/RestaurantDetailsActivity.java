package com.kardabel.go4lunch.ui.detailsview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.kardabel.go4lunch.databinding.RestaurantDetailsBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;
import com.kardabel.go4lunch.repository.UserRepository;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private static final String RESTAURANT_ID = "RESTAURANT_ID";
    private String restaurantId;
    private String restaurantName;
    private UserRepository userRepository;


    private RestaurantDetailsBinding binding;
    private RestaurantDetailsViewModel restaurantDetailsViewModel;

    public static Intent navigate(Context context, String placeId){
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra(RestaurantDetailsActivity.RESTAURANT_ID, placeId);
        return intent;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RestaurantDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userRepository = new UserRepository();

        // CONFIGURE VIEWMODEL
        ViewModelFactory listViewModelFactory = ViewModelFactory.getInstance();
        restaurantDetailsViewModel =
                new ViewModelProvider(this, listViewModelFactory).get(RestaurantDetailsViewModel.class);

        Intent intent = getIntent();
        restaurantDetailsViewModel.init(intent.getStringExtra(RESTAURANT_ID));



        restaurantDetailsViewModel.getRestaurantDetailsViewStateLiveData().observe(this, new Observer<RestaurantDetailsViewState>() {
            @Override
            public void onChanged(RestaurantDetailsViewState details) {
                restaurantId = details.getDetailsRestaurantId();
                restaurantName = details.getDetailsRestaurantName();
                binding.detailRestaurantName.setText(details.getDetailsRestaurantName());
                binding.detailRestaurantAddress.setText(details.getDetailsRestaurantAddress());
                String urlPhoto = RestaurantDetailsViewState.urlPhotoDetails(details);
                Glide.with(binding.detailPicture.getContext())
                        .load(urlPhoto)
                        .into(binding.detailPicture);
            }
        });

        binding.addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRepository.createUserInFavoriteRestaurant(restaurantId, restaurantName);

            }
        });



    }
}
