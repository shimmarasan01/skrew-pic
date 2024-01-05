package com.example.myapplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
class ListAdapter extends SimpleAdapter {
    Context context;
    DBHandler db;
    ArrayList<Bitmap> bitmapArray;
    LayoutInflater layoutInflater;
    ArrayList<HashMap<String, String>> a;
    public ListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, ArrayList<Bitmap> bitmapArray2, DBHandler bloop) {
        super(context, data, resource, from, to);
        this.context = context;
        this.db = bloop;
        a = (ArrayList<HashMap<String, String>>) data;
        layoutInflater = LayoutInflater.from(context);
        this.bitmapArray = bitmapArray2;
    }
    @Override
    public int getCount() {
        return bitmapArray.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = layoutInflater.inflate(R.layout.list_row, parent, false);
        if(Integer.parseInt(a.get(position).get("type"))+1==1) {
            TextView textView = view.findViewById(R.id.id);
            TextView textView2 = view.findViewById(R.id.type);
            TextView textView3 = view.findViewById(R.id.accuracy);
            RoundedImageView imageView = view.findViewById(R.id.imageeee);
            RoundedImageView indicator = view.findViewById(R.id.indicator);
            TextView textView4 = view.findViewById(R.id.date);
            ColorDrawable cd = new ColorDrawable(Color.parseColor("#D9534F"));
            indicator.setImageDrawable(cd);
            textView.setText(a.get(position).get("id"));
            textView2.setText("Unknown");
            textView3.setVisibility(View.GONE);
            textView4.setText(a.get(position).get("date"));
            imageView.setImageBitmap(bitmapArray.get(position));
        }
        if(Integer.parseInt(a.get(position).get("type"))+1!=1) {
            TextView textView = view.findViewById(R.id.id);
            TextView textView2 = view.findViewById(R.id.type);
            TextView textView3 = view.findViewById(R.id.accuracy);
            TextView textView4 = view.findViewById(R.id.date);
            RoundedImageView imageView = view.findViewById(R.id.imageeee);
            textView.setText(a.get(position).get("id"));
            ArrayList<HashMap<String, String>> test = db.getInfoByID(Integer.parseInt(a.get(position).get("type")));
            textView2.setText(test.get(0).get("type"));
            textView4.setText(a.get(0).get("date"));
            String d = String.valueOf((int)Float.parseFloat(a.get(position).get("accuracy")));
            textView3.setText(d+"%");
            RoundedImageView indicator = view.findViewById(R.id.indicator);
            ColorDrawable cd = new ColorDrawable(Color.parseColor("#5BB85C"));
            indicator.setImageDrawable(cd);
            imageView.setImageBitmap(bitmapArray.get(position));
        }
        return view;
    }
}