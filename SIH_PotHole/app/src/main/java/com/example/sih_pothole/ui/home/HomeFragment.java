package com.example.sih_pothole.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sih_pothole.MyVolleyRequest;
import com.example.sih_pothole.R;

import org.json.JSONObject;

import java.net.HttpURLConnection;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    public static final String SEND_IMAGE="SEND_IMAGE";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        volley(SEND_IMAGE);


        return root;
    }

    private void volley(final String task)
    {

        switch (task) {
            case SEND_IMAGE:

                try{

                    Bitmap bitmap = null;

                    System.out.println("booo");

                    JSONObject object=new JSONObject();
                    Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icon_resource)

                    object.put("image",imagetoString(icon));
                    object.put("vaild","");

                    JsonObjectRequest jSONObjectRequest = new JsonObjectRequest(
                            Request.Method.GET,"http://06e50644.ngrok.io/", object,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    System.out.println("ggg");

                                    if (response != null) {
                                        System.out.println(response);


                                        //   JSONArray array=response;
                                        //   ListDiscover.clear();
/*
                                            for(int i=0;i<array.length();i++)
                                            {

                                                JSONObject jsonObject= (JSONObject) array.get(i);

                                                Gson g=new Gson();
                                                DiscoverRecModel obj =  g.fromJson(jsonObject.toString(), DiscoverRecModel.class);
                                                ListDiscover.add(obj);


                                            }
                                            setTheAdapter();

*/

                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    NetworkResponse networkResponse = error.networkResponse;
                                    if(networkResponse!=null){
                                        int statusCode = networkResponse.statusCode;
                                        if(statusCode== HttpURLConnection.HTTP_UNAUTHORIZED){
                                            //YOU HAVE ALREADY CANCELLED YOUR TRIP


                                        }
                                        else{
                                            //   checkResponse(statusCode);
                                        }
                                        error.printStackTrace();
                                    }
                                }
                            }
                    );
                    MyVolleyRequest.getInstance(getActivity()).addToRequestQueue(jSONObjectRequest);
                }catch(Exception e){ e.printStackTrace(); }

                break;


            default:
                throw new IllegalStateException("Unexpected value: " + task);
        }

    }

    public String imagetoString(Bitmap bitmap)
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}