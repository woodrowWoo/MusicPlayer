package com.yjy.day12_14;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MyMusicLayout extends LinearLayout {


	public MyMusicLayout(Context context, AttributeSet attrs) {		
		super(context, attrs);		
		ImageButton play=new ImageButton(context);
		play.setImageResource(R.drawable.btn_play_normal);
		play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});		
		play.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		addView(play);
		
		
		
		ImageButton next=new ImageButton(context);
		play.setImageResource(R.drawable.btn_next_normal);
		play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});				
		play.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		addView(play);
		
		
		
		
		
		
		
		ImageButton rew=new ImageButton(context);
		play.setImageResource(R.drawable.btn_rew_normal);
		play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		play.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		addView(play);
	}

	public MyMusicLayout(Context context) {
		super(context);
	}
	
}
