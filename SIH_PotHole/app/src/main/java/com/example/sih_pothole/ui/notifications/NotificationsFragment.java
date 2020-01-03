package com.example.sih_pothole.ui.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.sih_pothole.R;
import com.google.android.material.textfield.TextInputLayout;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class NotificationsFragment extends Fragment {


    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_GALLERY_IMAGE = 1;

    private NotificationsViewModel notificationsViewModel;
    private TextView ProfileText;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*" + "@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private TextInputLayout Name,Email,PhoneNumber;
    private CircleImageView ProfileImageView, ProfileImagePlus;
    View mView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        mView = inflater.inflate(R.layout.fragment_notifications, container, false);
        ProfileImageView = (CircleImageView) mView.findViewById(R.id.img_profile);
        ProfileImagePlus = (CircleImageView) mView.findViewById(R.id.img_plus);
        Name = mView.findViewById(R.id.name);
        Email = mView.findViewById(R.id.email_id);
        PhoneNumber = mView.findViewById(R.id.phone_number);

        Name.getEditText().addTextChangedListener(new CustomTextWatcher(Name));
        Email.getEditText().addTextChangedListener((TextWatcher) new CustomTextWatcher(Email));
        PhoneNumber.getEditText().addTextChangedListener(new CustomTextWatcher(PhoneNumber));

     /*   notificationsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        ProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Profile photo");

                // add a list
                String[] animals = {"Camera","Gallery"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
                                break;
                            case 1:
                                //Create an Intent with action as ACTION_PICK
                                Intent intent1 = new Intent(Intent.ACTION_PICK);
                                // Sets the type as image/*. This ensures only components of type image are selected
                                intent1.setType("image/*");
                                //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                                String[] mimeTypes = {"image/jpeg", "image/png"};
                                intent1.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                                // Launching the Intent
                                startActivityForResult(intent1, REQUEST_GALLERY_IMAGE);
                                break;
                        }
                    }
                });
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return mView;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ProfileImageView.setImageBitmap(bitmap);
                case REQUEST_GALLERY_IMAGE:
                    Uri selectedImage = data.getData();
                    ProfileImageView.setImageURI(selectedImage);


            }
        }
    }

    private class CustomTextWatcher implements TextWatcher {
        private View view;

        private CustomTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            String text = s.toString();
            switch (view.getId()){
                case R.id.name:

            }
        }

    }

    public static boolean emailValidator(String email) {

        if (email == null) {
            return false;
        }

        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }


}