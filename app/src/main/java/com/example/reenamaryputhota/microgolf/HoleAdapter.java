package com.example.reenamaryputhota.microgolf;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by reenamaryputhota on 4/14/18.
 */

public class HoleAdapter extends ArrayAdapter {

    Context context;
    int resourceID;
    List<Hole> holeItems;
    LayoutInflater layoutInflater;


    public HoleAdapter(Context context, int resourceID, List<Hole> holeItems){
        super(context,resourceID,holeItems);
        this.context = context;
        this.resourceID = resourceID;
        this.holeItems = holeItems;
    }

    private class ViewHolder{
        ImageView hole_image;
        TextView number;
        TextView separator;

        public ViewHolder(View convertView){
            hole_image = convertView.findViewById(R.id.hole_image_view);
            number = convertView.findViewById(R.id.number);
            separator = convertView.findViewById(R.id.separator);

            //ViewHolder contains hole image, number and a separator after every 10 holes

        }
    }


    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {


        ViewHolder viewHolder = null;
        layoutInflater = LayoutInflater.from(context);
        if(convertView == null){
            convertView = layoutInflater.inflate(resourceID,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Hole holeItem = holeItems.get(position);
        viewHolder.hole_image.setImageResource(holeItem.getImageId());
        viewHolder.number.setText(holeItem.getNumber()+"");

        if((position+1)%10 == 0 ){
            viewHolder.separator.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.separator.setVisibility(View.GONE);
        }
        return convertView;
    }

}
