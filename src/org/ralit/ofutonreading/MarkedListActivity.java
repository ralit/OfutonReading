package org.ralit.ofutonreading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MarkedListActivity extends Activity {
	private ListView listview;
	private String bookName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		if (intent != null) {
			bookName = intent.getStringExtra("bookName");
		}
		LinearLayout root = new LinearLayout(this);
		root.setBackgroundColor(Color.DKGRAY);
		setContentView(root);
		listview = new ListView(this);
		listview.setDividerHeight(0);
		root.addView(listview);
		createImageList();
	}

	void createImageList() {
		File dir = new File(Fun.DIR + bookName + Fun.MARKER);
		File[] filelist = dir.listFiles();
		try {
			ArrayList<ImageItem> array = new ArrayList<ImageItem>();
			for (int i = 0; i < filelist.length; i++) {
				FileInputStream fis = new FileInputStream(filelist[i]);
				Bitmap bmp = BitmapFactory.decodeStream(fis);
				ImageItem item = new ImageItem(bmp);
				array.add(item);
			}
			ImageAdapter adapter = new ImageAdapter(this, 0, array);
			listview.setAdapter(adapter);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


class ImageItem {
	private Bitmap bitmap_;
	public ImageItem(Bitmap bitmap) {
		this.bitmap_ = bitmap;
	}
	public Bitmap getBitmap() {
		return bitmap_;
	}
}


class ImageAdapter extends ArrayAdapter<ImageItem> {
	private LayoutInflater layoutInflater_;

	public ImageAdapter(Context context, int textViewResourceId, List<ImageItem> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 特定の行(position)のデータを得る
		ImageItem item = (ImageItem)getItem(position);
		// convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		if (null == convertView) {
			convertView = layoutInflater_.inflate(R.layout.marked_list, null);
		}
		// ImageItemのデータをViewの各Widgetにセットする
		ImageView imageView;
		imageView = (ImageView)convertView.findViewById(R.id.image);
		imageView.setImageBitmap(item.getBitmap());
		return convertView;
	}
}