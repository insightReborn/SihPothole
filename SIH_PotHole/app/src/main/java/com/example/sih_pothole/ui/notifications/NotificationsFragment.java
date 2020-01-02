package com.example.sih_pothole.ui.notifications;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class NotificationsFragment extends Fragment {


    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_GALLERY_IMAGE = 1;
    private NotificationsViewModel notificationsViewModel;
    private TextView ProfileText;
    private TextView Name, name, Age, age, PhoneNo, EmailId, State, state, Gender, gender;
    private ImageView phoneLogo, emailLogo;
    private CircleImageView ProfileImageView,ProfileImagePlus;
    View mView;

    public interface PickerOptionListener {
        void onTakeCameraSelected();

        void onChooseGallerySelected();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        mView = inflater.inflate(R.layout.fragment_notifications, container, false);



     /*   notificationsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
/*
        ProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });*/
        return mView;
    }

   /* public static void showImagePickerOptions(Context context, final PickerOptionListener listener) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.lbl_set_profile_photo));

        // add a list
        String[] animals = {context.getString(R.string.lbl_take_camera_picture)
                , context.getString(R.string.lbl_choose_from_gallery)};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        listener.onTakeCameraSelected();
                        break;
                    case 1:
                        listener.onChooseGallerySelected();
                        break;
                }
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode== RESULT_OK){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            ProfileImageView.setImageBitmap(bitmap);
        } else if (resultCode == RESULT_CANCELED) {

        }

    }

}