package com.example.lab5_starter;
//blah
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements CityDialogFragment.CityDialogListener,CityArrayAdapter.DeleteCityListener {

    private Button addCityButton;
    private ListView cityListView;
//    private  Button deleteCityButton;

    private ArrayList<City> cityArrayList;
    private ArrayAdapter<City> cityArrayAdapter;

    private FirebaseFirestore db;

    private CollectionReference citiesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set views
        addCityButton = findViewById(R.id.buttonAddCity);
        cityListView = findViewById(R.id.listviewCities);

        // create city array
        cityArrayList = new ArrayList<>();
        cityArrayAdapter = new CityArrayAdapter(this, cityArrayList,this);
        cityListView.setAdapter(cityArrayAdapter);

//        addDummyData();
        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        //snapshot listener
        //basically clears current cities in the ui and adds cities fetched from db
        citiesRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (value != null) {
                cityArrayList.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    String name = snapshot.getString("name");
                    String province = snapshot.getString("province");
                    cityArrayList.add(new City(name, province));
                    Log.d("Firestore", "City: " + name + " loaded from db");
                }
                cityArrayAdapter.notifyDataSetChanged();
            }
        });


        // set listeners
        addCityButton.setOnClickListener(view -> {
            CityDialogFragment cityDialogFragment = new CityDialogFragment();
            cityDialogFragment.show(getSupportFragmentManager(),"Add City");
        });

        cityListView.setOnItemClickListener((adapterView, view, i, l) -> {
            City city = cityArrayAdapter.getItem(i);
            CityDialogFragment cityDialogFragment = CityDialogFragment.newInstance(city);
            cityDialogFragment.show(getSupportFragmentManager(),"City Details");
        });

    }

    @Override
    public void updateCity(City city, String title, String province) {
        if(title.isEmpty())
        {
            citiesRef.document(city.getName()).delete();
            Log.d("Firestore","City " + city.getName() + " deleted from db!!!");
            return;
        }
        citiesRef.document(city.getName()).delete();
        city.setName(title);
        city.setProvince(province);
//        cityArrayAdapter.notifyDataSetChanged();

        // Updating the database using delete + addition
        City newcity = new City(title,province);
        DocumentReference docref = citiesRef.document(newcity.getName());
        docref.set(newcity);
        Log.d("Firestore","City: " + city.getName() + " Province: " + city.getProvince() + " updated to: " + newcity.getName() + " " + newcity.getProvince());

    }

    @Override
    public void addCity(City city){
        cityArrayList.add(city);
        cityArrayAdapter.notifyDataSetChanged();

        DocumentReference docref = citiesRef.document(city.getName()); //adds a given city by name if doesnt exist in the db
        //listener to log success instances of writing to db
        docref.set(city)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "City" + city.getName() + " added to db");
                    }
                });
    }

    @Override
    public void deleteCity(City city) {
        citiesRef.document(city.getName()).delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", city.getName() + " deleted from db"));
        cityArrayAdapter.notifyDataSetChanged();
    }

//    public void addDummyData(){
//        City m1 = new City("Edmonton", "AB");
//        City m2 = new City("Vancouver", "BC");
//        cityArrayList.add(m1);
//        cityArrayList.add(m2);
//        cityArrayAdapter.notifyDataSetChanged();
//    }
}