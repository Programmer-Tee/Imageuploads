package com.example.tobeisun.image_upload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //changed the view to scroll view in activity_main.xml which allows you to scroll down should the image be very large
    // can only have one child in a scroll view , linear layout was added to it
    //IMAGE VIEW WAS ADDED TO HOLD THE IMAGE
    // EDITTEXT WAS ALSO ADDED TO ALLOW USERS TO ENTER TEXT


    // MATCH PARENT IS Used so it fills up the complete page
    // wrap content, vertically because its not too big

    //orientation was added because we need to tell android if the views are going to be added in a vertical or horizontal way in the linear layout

    // add padding around everything so it looks good ?

    // add uses permission to the manifest for internet ,
    // second permission is for external storage , so as to be able to access the gallery of the user

    private static final int IMAGERESULT = 1; //IT IS STATIC BECAUSE IT IS GOING TO REMAIN CONSTANT AND PRIVATE BECAUSE IT IS JUST FOR THAT "CLASS"
    ImageView toupload;
    ImageView todownload;
    EditText upload;
    EditText download;
    Button uploadimage;
    Button downloadimage;
    ProgressDialog pd;

    Uri selectedimage;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ref = storage.getReferenceFromUrl("gs://imageupload-25b8b.appspot.com");    //got the URI from firebase storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toupload = (ImageView) findViewById(R.id.Imagetoupload);
        todownload = (ImageView) findViewById(R.id.Imagetodownload);

        upload = (EditText) findViewById(R.id.Uploadname);
        download = (EditText) findViewById(R.id.Downloadname);
        uploadimage = (Button) findViewById(R.id.uploadimage);
        downloadimage = (Button) findViewById(R.id.downloadimage);


// an eror first pops up because the main activity has not been made an onclick listener .. so to correct that , add implements view . onclicklistener to the top of the code
        toupload.setOnClickListener(this);
        uploadimage.setOnClickListener(this);
        downloadimage.setOnClickListener(this);
        ///IMAGE TO DOWNLOAD CANNOT BE HERE BECAUSE ALL THESE VIEWS DONT SAVE , THEY REQUIRE THE GALLERY TO OPEN
    }

    // and override the view by rightclicking , going to generate , override and generate an onclick (view v), so the method below will be called when any of the 3 views are clicked
// .... now main activity is a listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Imagetoupload:
// allows the gallery to be opened and for you to be able to select from the gallery

                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                //startactivity for result because when the user selects a picture ,  we get the result of the image we select

                // then give the image selected a unique id so we know how to call it or something , the id is given upppp
                startActivityForResult(gallery, IMAGERESULT);


                break;


            case R.id.uploadimage:


                if (selectedimage != null) {
                    Toast.makeText(MainActivity.this, "test", Toast.LENGTH_SHORT).show();
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    ref = ref.child("images/" + UUID.randomUUID().toString());
                    ref.putFile(selectedimage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
                }


                break;


            case R.id.downloadimage:

                if (ref != null) {
                    pd.setTitle("Downloading...");
                    pd.setMessage(null);
                    pd.show();

                    final long ONE_MEGABYTE = 1024 * 1024;
                    ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            todownload.setImageBitmap(bmp);
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pd.dismiss();
                            Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Upload file before downloading", Toast.LENGTH_LONG).show();
                }
                break ;

        }
    }









    // CREATE A METHOD THAT WILL BE CALLED WHEN A USER HAS SELECTED A PICTURE

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // CHECK IF IT IS THE GALLERY GANGAN THAT IS CALLED , IF THE RESULT IS OKAY AND THAT AN IMAGE HAS BEEN SELECTED
        if(requestCode==IMAGERESULT && resultCode==RESULT_OK && data !=null)
        {
          // to display the image selected , create an URI which is a uniform resource indicator ,

            selectedimage = data.getData();               // this shows the address of the image on the phone


            toupload.setImageURI(selectedimage); // displays the image in the image view
        }




    }
}
