package com.example.sih_pothole.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.sih_pothole.BuildConfig;
import com.example.sih_pothole.MainActivity;
import com.example.sih_pothole.R;
import com.example.sih_pothole.login.LoginActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.mindorks.editdrawabletext.DrawablePosition;
import com.mindorks.editdrawabletext.onDrawableClickListener;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class DashboardFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 108;
    private DashboardViewModel dashboardViewModel;

    private ImageView post_images;
   private Bitmap yourSelectedImage;
    //private DiscoverModel discoverModel;
    private int PICK_IMAGE_REQUEST = 2;

    private ArrayList<Bitmap> ListImages;
    //PostImageAdapter postImageAdapter;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int LOCATION_SETTINGS_REQUEST_CODE = 0x2;//OPEN SETTING
    private static final int PHONE_PERMISSION_REQUEST_CODE = 0x3; //Phone

    private static final  int SELECT_PICTURES=011;
    private Toolbar toolbar;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0x1; //LOCATION


    private com.mindorks.editdrawabletext.EditDrawableText loc;
    private ImageView gallery;

    //LOCATION
    private FusedLocationProviderClient myFusedLocationProviderClient;
    private Location myCurrentLocation;
    private LocationRequest myLocationRequest;
    private LocationSettingsRequest myLocationSettingsRequest;
    private LocationCallback myLocationCallback;
    private double myLat, myLon;
    private static final long locationInterval = 2000; //in milliseconds
    private static final long fastestLocationInterval = locationInterval/2; //in milliseconds
    private static final long minimumDisplacement = 5; //in meter

  private   View root;


    @SuppressLint("RestrictedApi")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
      root  = inflater.inflate(R.layout.fragment_dashboard, container, false);

        setHasOptionsMenu(true);
        TextView textViewlogout=root.findViewById(R.id.logout);

        textViewlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i=new Intent(getActivity(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                getActivity().finish();

            }
        });


        requestCameraPermission();
        //LOCATION
        initializeLocationCallback();
        initializeLocationRequest();

        //FusedLocationProviedrClient Initialized
        myFusedLocationProviderClient = new FusedLocationProviderClient(getActivity());


        FloatingActionButton floatingActionButton =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Dialog mDialog = new Dialog(getContext());
                mDialog.setContentView(R.layout.post_card);

                Rect displayRectangle = new Rect();
                Window window = getActivity().getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.post_card, null);
                layout.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
                layout.setMinimumHeight((int)(displayRectangle.height() * 0.9f));

                post_images=layout.findViewById(R.id.post_images);

                DialogElements(layout);

                mDialog.setContentView(layout);
                mDialog.show();


            }
        });
        Toast.makeText(getActivity(), ""+myLat+"  "+myLon, Toast.LENGTH_SHORT).show(); ;


        return root;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            yourSelectedImage= (Bitmap) extras.get("data");
            post_images.setImageBitmap(yourSelectedImage);
            //ListImages.add(imageBitmap);
            //postImageAdapter.UpdatePostAdapter(ListImages);
        }
        else
        if (requestCode == PICK_IMAGE_REQUEST && resultCode==RESULT_OK ) {
            Toast.makeText(getActivity(), "ok", Toast.LENGTH_SHORT).show();
            System.out.println("ok");
            if (data.getData() != null){
                //If uploaded with the new Android Photos gallery
                ClipData clipData = data.getClipData();
                for(int i = 0; i < clipData.getItemCount(); i++){
                    clipData.getItemAt(i);
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    // System.out.println(uri+"dddddddddddddddd");

                    InputStream imageStream;
                    try {
                        imageStream = getActivity().getContentResolver().openInputStream(uri);
                        yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                        ListImages.add(yourSelectedImage);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }

                post_images.setImageBitmap(ListImages.get(0));
               // postImageAdapter.UpdatePostAdapter(ListImages);

            }
        }
    }

    private void DialogElements(View layout) {

        loc=layout.findViewById(R.id.loc);
        gallery=layout.findViewById(R.id.gallery);

        ImageView cam=layout.findViewById(R.id.cam);
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();

            }
        });


        loc.setDrawableClickListener(new onDrawableClickListener() {
            @Override

            public void onClick( DrawablePosition drawablePosition) {

                if(drawablePosition==DrawablePosition.RIGHT){

                    initializeLocationCallback();
                    //   Toast.makeText(getActivity(), "mmmmmmmmmmmm "+myLon+"  "+myLat+" "+myCurrentLocation, Toast.LENGTH_SHORT).show();
                    loc.setText(getAddress(myLat,myLon));

                }
                if(drawablePosition==DrawablePosition.LEFT){

                    initializeLocationCallback();
                    // Toast.makeText(getActivity(), "blaaa "+myLon+"  "+myLat+" "+myCurrentLocation, Toast.LENGTH_SHORT).show();
                    loc.setText(getAddress(myLat,myLon));

                }
            }


        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();

            }
        });


    }

    //LOCATION
    private void initializeLocationRequest(){
        //INITIALIZE
        myLocationRequest = new LocationRequest();
        myLocationRequest.setInterval(locationInterval);
        myLocationRequest.setFastestInterval(fastestLocationInterval);
        myLocationRequest.setSmallestDisplacement(minimumDisplacement);
        myLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //BUILD LOCATION SETTINGS
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(myLocationRequest);
        myLocationSettingsRequest = builder.build();
    }

    private void initializeLocationCallback(){
        myLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                myCurrentLocation = locationResult.getLastLocation();
                myLat = myCurrentLocation.getLatitude();
                myLon = myCurrentLocation.getLongitude();
                String temp = "data : " + myLat +"  " + myLon;
                System.out.println(temp);
           /*     if(connectedToNetwork()){volley(UPDATE_LOCATION);}
                else{NoInternetAlertDialog(UPDATE_LOCATION);}*/


            }
        };
    }

    @Override
    public void onStart() {
        if(checkLocationPermission()){ startLocationUpdate(); }
        else{ requestLocationPermission(); }
        super.onStart();
    }

    @Override
    public void onStop() {
        stopLocationUpdates();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        /*if(connectedToNetwork()){ volley(UPDATE_OFFLINE); }
        else{NoInternetAlertDialog(UPDATE_OFFLINE);}
        */
        super.onDestroy();
    }
    @SuppressWarnings("MissingPermission")
    private void startLocationUpdate(){
        LocationServices.getSettingsClient(getActivity()).checkLocationSettings(myLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        myFusedLocationProviderClient.requestLocationUpdates(myLocationRequest, myLocationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        switch (((ApiException)e).getStatusCode()){
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try{
                                    ResolvableApiException rae = (ResolvableApiException)e;
                                    rae.startResolutionForResult(getActivity(), LOCATION_SETTINGS_REQUEST_CODE);
                                }catch(IntentSender.SendIntentException sie){
                                    //UNABLE TO EXECUTE REQUEST
                                    Toast.makeText(getActivity(),"Location settings are inadequate. Please, fix in settings.", Toast.LENGTH_LONG).show();
                                }
                                break;

                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Toast.makeText(getActivity(),"Location settings are inadequate. Please, fix in settings.", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
    }
    private void stopLocationUpdates(){
        myFusedLocationProviderClient.removeLocationUpdates(myLocationCallback);
    }

    //PERMISSION
    private boolean checkLocationPermission(){
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED;
    }
    private boolean checkPhonePermission(){
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED;
    }
    private void requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
            showSnackbar(R.string.locationPermissionRationale, android.R.string.ok, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
            });
        }
        else{
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private void requestPhonePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)){
            showSnackbar(R.string.phonePermissionRationale, android.R.string.ok, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            PHONE_PERMISSION_REQUEST_CODE);
                }
            });
        }
        else{
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    PHONE_PERMISSION_REQUEST_CODE);
        }
    }
    private void requestCameraPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)){
            showSnackbar(R.string.phonePermissionRationale, android.R.string.ok, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST_CODE);
                }
            });
        }
        else{
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==LOCATION_PERMISSION_REQUEST_CODE){
            if(grantResults.length<=0){
                //USER INTERACTION CANCELLED
            }
            else if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if(checkLocationPermission()){
                    startLocationUpdate();
                    // mMap.setMyLocationEnabled(true);
                }
            }
            else{
                showSnackbar(R.string.locationPermissionDenied, R.string.goToSettings, new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent openSettings = new Intent();
                        openSettings.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                        openSettings.setData(uri);
                        openSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(openSettings);
                    }
                });
            }
        }
        else if(requestCode==PHONE_PERMISSION_REQUEST_CODE){
            if(grantResults.length<=0){
                //USER INTERACTION CANCELLED
            }
            else if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                //Intent call = new Intent(Intent.ACTION_CALL);
                //call.setData(Uri.parse("tel:"+DphoneNumber));
                //startActivity(call);
            }
            else{
                showSnackbar(R.string.phonePermissionDenied, R.string.goToSettings, new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent openSettings = new Intent();
                        openSettings.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                        openSettings.setData(uri);
                        openSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(openSettings);
                    }
                });
            }
        }
    }
    //SNACK BAR
    private void showSnackbar(final int mainTextStringId, final int actionStringId, View.OnClickListener listener) {

        Snackbar.make(getActivity().findViewById(android.R.id.content), getString(mainTextStringId), Snackbar.LENGTH_INDEFINITE).setAction(getString(actionStringId), listener).show();
        //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
    }

    public String getAddress(double lat, double lng) {
        String add = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
            //add = add + " " + obj.getCountryName();
            //add = add + " " + obj.getCountryCode();
            // add = add + " " + obj.getAdminArea();
            // add = add + " " + obj.getPostalCode();
            // add = add + " " + obj.getSubAdminArea();
            // add = add + " " + obj.getLocality();
            // add = add + "\n" + obj.getSubThoroughfare();
            int i=add.indexOf("Tamil");
            //add=add.substring(0,i-2);
            System.out.println(add);
            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        catch (IndexOutOfBoundsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        return add;
    }


    private void showFileChooser() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        //  pickImageIntent.putExtra("aspectX", 1);
        // pickImageIntent.putExtra("aspectY", 1);
        //pickImageIntent.putExtra("scale", true);
        //pickImageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        pickImageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(pickImageIntent, "Select Picture"), SELECT_PICTURES);
    }



    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }



}