package com.example.myapplication;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.myapplication.ml.Model;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
public class DisplayImage extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView progressText;
    private TextView result;
    private ImageView imageView;
    private RoundedImageView roundedImageView;
    private TextView one;
    private TextView two;
    private TextView three;
    int i = 0;
    int forBottomSheet = 0;
    boolean isUnknown = false;
    Bitmap bitmap2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        ImageButton buttonShow = findViewById(R.id.buttonShow);
        ImageButton buttonGoogle = findViewById(R.id.buttonGoogle);
        DBHandler dbHandler = new DBHandler(DisplayImage.this);
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        File f = new File(getIntent().getStringExtra("image_path"));
        f.delete();
        //int dimension2 = Math.min(224,224);
        //bitmap = ThumbnailUtils.extractThumbnail(bitmap,dimension2,dimension2);
        classifyImage(bitmap);
        if(isUnknown==true){
            buttonGoogle.setVisibility(View.GONE);
        }
        int dimension = Math.min(600, 600);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
        imageView.setImageBitmap(bitmap);
        ArrayList<HashMap<String, String>> list_params_ = dbHandler.getInfoByID(forBottomSheet);
        buttonShow.setOnClickListener(view -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    DisplayImage.this, R.style.BottomSheetDialogTheme
            );
            if(isUnknown==false) {
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_bottom_sheet,
                                (LinearLayout) findViewById(R.id.bottomSheetContainer)
                        );
                one = bottomSheetView.findViewById(R.id.one);
                two = bottomSheetView.findViewById(R.id.two);
                three = bottomSheetView.findViewById(R.id.three);
                roundedImageView = bottomSheetView.findViewById(R.id.imageee);
                roundedImageView.setImageBitmap(bitmap2);
                one.setText(list_params_.get(0).get("type"));
                two.setText(list_params_.get(0).get("desc"));
                three.setText(list_params_.get(0).get("uses"));
                bottomSheetDialog.setContentView(bottomSheetView);
            }
            else if(isUnknown==true){
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_bottom_sheet_if_unknown,
                                (LinearLayout) findViewById(R.id.bottomSheetContainer)
                        );
                bottomSheetDialog.setContentView(bottomSheetView);
            }
            bottomSheetDialog.show();
        });
        buttonGoogle.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            String term = list_params_.get(0).get("type");
            intent.putExtra(SearchManager.QUERY, term);
            startActivity(intent);
        });
    }
    public void classifyImage(Bitmap bitmap){
        DBHandler dbHandler = new DBHandler(DisplayImage.this);
        double accuracy = 0;
        String type = "";
        int index;
        try{
            Model model = Model.newInstance(getApplicationContext());
            TensorImage image = TensorImage.fromBitmap(bitmap);
            Model.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();
            index = 0;
            for(int i=1;i<probability.size();i++){
                if(probability.get(i).getScore()>probability.get(index).getScore()){
                    index = i;
                }
            }
            accuracy = probability.get(index).getScore() * 100;
            type = probability.get(index).getLabel();
            if(accuracy>=30){
                int typeID = Integer.parseInt(type);
                ArrayList<HashMap<String, String>> list_params_ = dbHandler.getInfoByID(typeID);
                String typeHistory = list_params_.get(0).get("type");
                String b = String.valueOf(accuracy);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                int dimension = Math.min(600, 600);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                String sImage = Base64.encodeToString(bytes,Base64.DEFAULT);
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy");
                String strDate = dateFormat.format(date);
                dbHandler.insertHistory(type, b, sImage, strDate);
                String base64String = "data:image/png;base64," + list_params_.get(0).get("image");
                String base64Image = base64String.split(",")[1];
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                bitmap2 = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                result.setText(typeHistory);
                progressBar(accuracy);
                forBottomSheet = typeID;
                result.setTextColor(Color.rgb(60, 179, 113));
            }
            else if(accuracy<30){
                isUnknown = true;
                type = "0";
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                int dimension = Math.min(600, 600);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();
                String sImage = Base64.encodeToString(bytes, Base64.DEFAULT);
                String b = String.valueOf(accuracy);
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy");
                String strDate = dateFormat.format(date);
                dbHandler.insertHistory(type, b, sImage, strDate);
                progressBar(accuracy);
                result.setText("Unknown");
                result.setTextColor(Color.rgb(217, 83, 79));
            }
            model.close();
        }catch(IOException e){}
    }
    public void progressBar(double x){
        final Handler handler = new Handler();
        int accLimit = (int)x;
        if(accLimit>=30){
            final Runnable runnable = new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void run() {
                    if(i<=accLimit){
                        progressText.setText(""+i+"%");
                        progressBar.setProgress(i);
                        i++;
                        handler.postDelayed(this, 0);
                    }else{
                        handler.removeCallbacks(this);
                    }
                }
            };
            handler.postDelayed(runnable, 0);
            i = 0;
            Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.rgb(60, 179, 113), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
            progressBar.setProgress(0);
            progressText.setText("");
            progressText.setTextColor(Color.rgb(60, 179, 113));
        }
        else if(accLimit<30){
            final Runnable runnable = () -> {
                for(i=0;i<=100;i++){
                    progressText.setText("!");
                    progressBar.setProgress(i);
                }
            };
            handler.postAtTime(runnable, 1);
            i = 0;
            Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.rgb(217, 83, 79), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
            progressBar.setProgress(0);
            progressText.setText("");
            progressText.setTextColor(Color.rgb(217, 83, 79));
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}