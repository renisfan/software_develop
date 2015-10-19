package com.fdusoft.matchcards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class CardAdapter extends BaseAdapter {
	
	private Context mContext;
	
	private CardItem[] items;
	
	private int itemSize;
		
	public CardAdapter(Context context, int col, int size, int width) {
		this.mContext = context;
		this.items = new CardItem[size];
		for (int i=0; i<size; ++i)
			items[i] = new CardItem();
		this.itemSize = width / (col+1);
	}
	
	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView==null) {
			imageView=new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(itemSize, itemSize));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(5, 5, 5, 5);
		} else {
			imageView = (ImageView) convertView;
		}
		imageView.setImageResource(items[position].getImage());
		return imageView;  
	}

}
