package com.kardabel.go4lunch.usecase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbySearchResponseRepository;

public class getNearbySearchResultsUseCase {

    public LiveData<NearbySearchResults> NearbySearchResultsLiveData;

    // RETRIEVE NEARBY RESULTS FROM LOCATION
    public getNearbySearchResultsUseCase(LocationRepository locationRepository,
                                         NearbySearchResponseRepository nearbySearchResponseRepository) {

        NearbySearchResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), input -> {
             String locationAsText = input.getLatitude() + "," + input.getLongitude();
             return nearbySearchResponseRepository.getRestaurantListLiveData(
                    "restaurant",
                    locationAsText,
                    "1000");

        });
    }
    public LiveData<NearbySearchResults> invoke() { return NearbySearchResultsLiveData; }
}
