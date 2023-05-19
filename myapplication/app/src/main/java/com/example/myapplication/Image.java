package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageButton;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Image extends AppCompatActivity {
    String currentImagePath = null;
    String one = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageButton pictureGallery = findViewById(R.id.buttonGallery);
        ImageButton pictureCamera = findViewById(R.id.buttonCamera);
        pictureGallery.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
        });
        pictureCamera.setOnClickListener(view -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(cameraIntent.resolveActivity(getPackageManager())!=null){
                File imageFile = null;
                try {
                    imageFile = getImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(imageFile!=null){
                    Uri imageUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", imageFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(cameraIntent, 2);
                    imageFile.delete();
                }
            }
        });
    }
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    public File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "png_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName, ".png", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(data!=null&&resultCode==-1){
                one = getPathFromURI(data.getData());
                Intent intent = new Intent(this, DisplayImage.class);
                intent.putExtra("image_path", one);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            else if(requestCode==1&&data==null&&resultCode==0){
                return;
            }
        }
        if(requestCode==2){
            if(data==null&&resultCode==-1){
                Intent intent = new Intent(this, DisplayImage.class);
                intent.putExtra("image_path", currentImagePath);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            else if(requestCode==2&&data==null&&resultCode==0){
                return;
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}