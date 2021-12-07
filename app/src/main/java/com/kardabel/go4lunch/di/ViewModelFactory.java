package com.kardabel.go4lunch.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kardabel.go4lunch.MainActivityViewModel;
import com.kardabel.go4lunch.MainApplication;
import com.kardabel.go4lunch.repository.AutocompleteRepository;
import com.kardabel.go4lunch.repository.ChatMessageRepository;
import com.kardabel.go4lunch.repository.FavoriteRestaurantsRepository;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbySearchResponseRepository;
import com.kardabel.go4lunch.repository.RestaurantDetailsResponseRepository;
import com.kardabel.go4lunch.repository.UserSearchRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.repository.UsersWhoMadeRestaurantChoiceRepository;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;
import com.kardabel.go4lunch.ui.chat.ChatViewModel;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsViewModel;
import com.kardabel.go4lunch.ui.mapview.MapViewModel;
import com.kardabel.go4lunch.ui.restaurants.RestaurantsViewModel;
import com.kardabel.go4lunch.ui.workmates.WorkMatesViewModel;
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsByIdUseCase;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsUseCase;
import com.kardabel.go4lunch.usecase.GetPredictionsUseCase;
import com.kardabel.go4lunch.usecase.GetRestaurantDetailsResultsByIdUseCase;
import com.kardabel.go4lunch.usecase.GetRestaurantDetailsResultsUseCase;

import java.time.Clock;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;
    private final Application application;

    private final LocationRepository locationRepository;
    private final WorkmatesRepository workmatesRepository;
    private final UserSearchRepository mUserSearchRepository;
    private final FavoriteRestaurantsRepository favoriteRestaurantsRepository;
    private final UsersWhoMadeRestaurantChoiceRepository mUsersWhoMadeRestaurantChoiceRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final GetNearbySearchResultsUseCase getNearbySearchResultsUseCase;
    private final GetNearbySearchResultsByIdUseCase getNearbySearchResultsByIdUseCase;
    private final GetRestaurantDetailsResultsUseCase getRestaurantDetailsResultsUseCase;
    private final GetRestaurantDetailsResultsByIdUseCase getRestaurantDetailsResultsByIdUseCase;
    private final GetPredictionsUseCase getPredictionsUseCase;
    private final GetCurrentUserIdUseCase getCurrentUserIdUseCase;

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();

                }
            }
        }
        return factory;

    }

    public ViewModelFactory() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GoogleMapsApi googleMapsApi = retrofit.create(GoogleMapsApi.class);
        this.application = MainApplication.getApplication();
        NearbySearchResponseRepository nearbySearchResponseRepository = new NearbySearchResponseRepository(
                googleMapsApi,
                application);
        RestaurantDetailsResponseRepository restaurantDetailsResponseRepository = new RestaurantDetailsResponseRepository(
                googleMapsApi,
                application);
        AutocompleteRepository autocompleteRepository = new AutocompleteRepository(
                googleMapsApi,
                application);
        this.locationRepository = new LocationRepository();
        this.workmatesRepository = new WorkmatesRepository();
        this.mUserSearchRepository = new UserSearchRepository();
        this.favoriteRestaurantsRepository = new FavoriteRestaurantsRepository();
        this.mUsersWhoMadeRestaurantChoiceRepository = new UsersWhoMadeRestaurantChoiceRepository(Clock.systemDefaultZone());
        this.chatMessageRepository = new ChatMessageRepository();

        this.getNearbySearchResultsUseCase = new GetNearbySearchResultsUseCase(
                locationRepository,
                nearbySearchResponseRepository);
        this.getNearbySearchResultsByIdUseCase = new GetNearbySearchResultsByIdUseCase(
                locationRepository,
                nearbySearchResponseRepository);
        this.getRestaurantDetailsResultsUseCase = new GetRestaurantDetailsResultsUseCase(
                locationRepository,
                nearbySearchResponseRepository,
                restaurantDetailsResponseRepository);
        this.getRestaurantDetailsResultsByIdUseCase = new GetRestaurantDetailsResultsByIdUseCase(
                restaurantDetailsResponseRepository
        );
        this.getPredictionsUseCase = new GetPredictionsUseCase(
                locationRepository,
                autocompleteRepository);
        this.getCurrentUserIdUseCase = new GetCurrentUserIdUseCase();
    }


    // CREATE INSTANCE FOR EACH VIEWMODEL
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantsViewModel.class)) {
            return (T) new RestaurantsViewModel(
                    application,
                    locationRepository,
                    getNearbySearchResultsUseCase,
                    getRestaurantDetailsResultsUseCase,
                    mUsersWhoMadeRestaurantChoiceRepository,
                    mUserSearchRepository,
                    Clock.systemDefaultZone());
        } else if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(
                    locationRepository,
                    getNearbySearchResultsUseCase,
                    mUsersWhoMadeRestaurantChoiceRepository,
                    mUserSearchRepository);
        } else if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(
                    application,
                    locationRepository,
                    getPredictionsUseCase,
                    mUserSearchRepository,
                    mUsersWhoMadeRestaurantChoiceRepository,
                    getCurrentUserIdUseCase
            );
        } else if (modelClass.isAssignableFrom(RestaurantDetailsViewModel.class)) {
            return (T) new RestaurantDetailsViewModel(
                    application,
                    getNearbySearchResultsByIdUseCase,
                    getRestaurantDetailsResultsByIdUseCase,
                    mUsersWhoMadeRestaurantChoiceRepository,
                    workmatesRepository,
                    favoriteRestaurantsRepository,
                    getCurrentUserIdUseCase);
        } else if (modelClass.isAssignableFrom(WorkMatesViewModel.class)) {
            return (T) new WorkMatesViewModel(
                    application,
                    workmatesRepository,
                    mUsersWhoMadeRestaurantChoiceRepository
            );
        } else if (modelClass.isAssignableFrom(ChatViewModel.class)) {
            return (T) new ChatViewModel(
                    chatMessageRepository,
                    getCurrentUserIdUseCase
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");

    }
}
