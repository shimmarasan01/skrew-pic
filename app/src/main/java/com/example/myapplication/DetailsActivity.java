package com.example.myapplication;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
public class DetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        setOnClickListener();
    }
    private void setOnClickListener(){
        DBHandler db = new DBHandler(this);
        ArrayList<HashMap<String, String>> userList = db.getAllHistory();
        ListView lv = (ListView) findViewById(R.id.user_list);
        ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
        ArrayList<HashMap<String, String>> userListImage = db.getAllImagesFromHistory();
        for(int i=0;i<userListImage.size();i++) {
            String base64String = "data:image/png;base64," + userListImage.get(i).get("image");
            String base64Image = base64String.split(",")[1];
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap2 = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            bitmapArray.add(bitmap2);
        }
        ListAdapter adapter = new ListAdapter(DetailsActivity.this, userList, R.layout.list_row, new String[]{"id", "type", "accuracy", "date"}, new int[]{R.id.id, R.id.type, R.id.accuracy, R.id.date}, bitmapArray, db);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                TextView textView = (TextView) view.findViewById(R.id.id);
                Intent editNoteIntent = new Intent(getApplicationContext(), List.class);
                editNoteIntent.putExtra("TEST", textView.getText());
                startActivity(editNoteIntent);
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        DBHandler db = new DBHandler(this);
        ArrayList<HashMap<String, String>> userList = db.getAllHistory();
        ListView lv = (ListView) findViewById(R.id.user_list);
        android.widget.ListAdapter adapter = new SimpleAdapter(DetailsActivity.this, userList, R.layout.list_row, new String[]{"id", "type", "accuracy", "date"}, new int[]{R.id.id, R.id.type, R.id.accuracy, R.id.date});
        lv.setAdapter(adapter);
        setOnClickListener();
    }
    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(0, 0);
    }
}