package com.example.myapplication;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;
import java.util.HashMap;
public class List extends AppCompatActivity {
    ImageButton deleteBtn;
    private ProgressBar progressBaro;
    private TextView progressTexto;
    private TextView one;
    private TextView two;
    private TextView three;
    RoundedImageView roundedImageView;
    boolean isUnknown = false;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent previousIntent = getIntent();
        DBHandler dbHandler = new DBHandler(List.this);
        progressBaro = findViewById(R.id.progress_baro);
        progressTexto = findViewById(R.id.progress_texto);
        TextView lv = findViewById(R.id.typeHistory);
        ImageButton buttonShow = findViewById(R.id.buttonShowo);
        ImageButton buttonGoogle = findViewById(R.id.buttonGoogleo);
        int id = Integer.parseInt(previousIntent.getStringExtra("TEST"));
        ArrayList<HashMap<String, String>> list_params_ = dbHandler.getHistoryByID(id);
        int decide = Integer.parseInt(list_params_.get(0).get("type"));
        if(decide+1==1){
            lv.setText("Unknown");
            buttonGoogle.setVisibility(View.GONE);
        }
        else{
            ArrayList<HashMap<String, String>> list_paramsF_ = dbHandler.getInfoByID(decide);
            lv.setText(list_paramsF_.get(0).get("type"));
        }
        buttonShow.setOnClickListener(view -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    List.this, R.style.BottomSheetDialogTheme
            );
            int typeID = Integer.parseInt(list_params_.get(0).get("type"));
            View bottomSheetView;
            if(typeID+1==1){
                bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_bottom_sheet_if_unknown,
                                (LinearLayout) findViewById(R.id.bottomSheetContainer)
                        );
            }
            else{
                bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_bottom_sheet,
                                (LinearLayout) findViewById(R.id.bottomSheetContainer)
                        );
                one = bottomSheetView.findViewById(R.id.one);
                two = bottomSheetView.findViewById(R.id.two);
                three = bottomSheetView.findViewById(R.id.three);
                roundedImageView = bottomSheetView.findViewById(R.id.imageee);
                ArrayList<HashMap<String, String>> list_paramsF_ = dbHandler.getInfoByID(typeID);
                String base64String = "data:image/png;base64,"+list_paramsF_.get(0).get("image");
                String base64Image = base64String.split(",")[1];
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                roundedImageView.setImageBitmap(bitmap2);
                one.setText(list_paramsF_.get(0).get("type"));
                two.setText(list_paramsF_.get(0).get("desc"));
                three.setText(list_paramsF_.get(0).get("uses"));
            }
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        });
        buttonGoogle.setOnClickListener(view -> {
            int typeID = Integer.parseInt(list_params_.get(0).get("type"));
            ArrayList<HashMap<String, String>> list_paramsF_ = dbHandler.getInfoByID(typeID);
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            String term = list_paramsF_.get(0).get("type");
            intent.putExtra(SearchManager.QUERY, term);
            startActivity(intent);
        });
        roundedImageView = findViewById(R.id.imageView);
        Bitmap bitmap2 = null;
        String base64String = "data:image/png;base64," + list_params_.get(0).get("image");
        String base64Image = base64String.split(",")[1];
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        bitmap2 = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        int dimension = Math.min(600,600);
        bitmap2 = ThumbnailUtils.extractThumbnail(bitmap2, dimension, dimension);
        roundedImageView.setImageBitmap(bitmap2);
        double x = Double.parseDouble(list_params_.get(0).get("accuracy"));
        progressBar(x);
        deleteBtn = (ImageButton)findViewById(R.id.buttonDelete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
                builder.setCancelable(true);
                builder.setTitle("Warning");
                builder.setMessage("History entry will be deleted!");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHandler dbHandler = new DBHandler(List.this);
                                dbHandler.deleteHistory(id);
                                finish();
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    public void progressBar(double x){
        TextView lv = findViewById(R.id.typeHistory);
        final Handler handler = new Handler();
        int accLimit = (int)x;
        if(accLimit>=50){
            lv.setTextColor(Color.rgb(60, 179, 113));
            final Runnable runnable = new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void run() {
                    if(i<=accLimit){
                        progressTexto.setText(""+i+"%");
                        progressBaro.setProgress(i);
                        i++;
                        handler.postAtTime(this, 0);
                    }else{
                        handler.removeCallbacks(this);
                    }
                }
            };
            handler.postAtTime(runnable, 0);
            i = 0;
            Drawable progressDrawable = progressBaro.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.rgb(60, 179, 113), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBaro.setProgressDrawable(progressDrawable);
            progressBaro.setProgress(0);
            progressTexto.setText("");
            progressTexto.setTextColor(Color.rgb(60, 179, 113));
        }
        else if(accLimit<50){
            isUnknown = true;
            lv.setTextColor(Color.rgb(217, 83, 79));
            final Runnable runnable = () -> {
                for(i=0;i<=100;i++){
                    progressTexto.setText("!");
                    progressBaro.setProgress(i);
                }
            };
            handler.postAtTime(runnable, 1);
            i = 0;
            Drawable progressDrawable = progressBaro.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.rgb(217, 83, 79), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBaro.setProgressDrawable(progressDrawable);
            progressBaro.setProgress(0);
            progressTexto.setText("");
            progressTexto.setTextColor(Color.rgb(217, 83, 79));
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}