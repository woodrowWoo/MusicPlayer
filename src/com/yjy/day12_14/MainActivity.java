package com.yjy.day12_14;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.yjy.service.MyMusicService;
import com.yjy.service.MyMusicService.MyBinder;



public class MainActivity extends BaseActivity implements OnClickListener,OnItemClickListener,OnSeekBarChangeListener{

	private ImageButton btnPlay=null; 
	private ImageButton btnNext=null;
	private ImageButton btnRew=null;
	private SeekBar seekBar=null;
	private MyMusicService musicService=null;
	private ListView listViewMusic=null;
	private ArrayList<HashMap<String, Object>> musicList=null;//音乐文件列表
	private Timer timerSeekBar=null;
	private UpdateSeekBar updateSeekBar;
	private boolean isTrackingSeekbar;//是否正在拖动seekBar
	private TextView textViewCurTime=null;
	private TextView textViewTotalTime=null;
	
	private ServiceConnection conn=new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder myBinder= (MyBinder)service;
			//获取到MyMusicService实例
			System.out.println("onServiceConnected===============================");
			musicService=myBinder.getService();
			upDateMusicList();
			setPlayBuutonIcon();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		System.out.println("ActivityonCreate===============================");

		btnPlay=(ImageButton) findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(this);

		btnNext=(ImageButton) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);

		btnRew=(ImageButton) findViewById(R.id.btnRew);
		btnRew.setOnClickListener(this);

		textViewCurTime=(TextView) findViewById(R.id.textViewCurTime);
		textViewTotalTime=(TextView) findViewById(R.id.textViewToatalTime);
		seekBar=(SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(this);
		listViewMusic=(ListView) findViewById(R.id.music_list);
		listViewMusic.setOnItemClickListener(this);

	
		bindService(new Intent(this,MyMusicService.class), conn, Context.BIND_AUTO_CREATE);	

		timerSeekBar=new Timer();
		updateSeekBar=new UpdateSeekBar();
		timerSeekBar.schedule(updateSeekBar, 0, 1);

	}

	@Override
	protected void onResume() {
		System.out.println("ActivityononResume===============================");
		super.onResume();

	}
	public void upDateMusicList() {	
		musicList=musicService.getMusicList();	
		SimpleAdapter adapter=new SimpleAdapter(this,
									 musicList, 
									 R.layout.music_list_item, 
									 new String[]{/**/MyMusicService.MUSIC_TITLE,MyMusicService.MUSIC_ARTIST}, new int[]{/*R.id.music_album,*/R.id.music_title,R.id.music_artist});
//		adapter.setViewBinder(new ViewBinder() {
//			
//			@Override
//			public boolean setViewValue(View view, Object data,
//					String textRepresentation) {
//				if( (view instanceof ImageView) & (data instanceof String) ) {  
//					ImageView iv = (ImageView) view;  
//					FileInputStream is=null;
//					try {
//						is = new FileInputStream((String)data);
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					}
//					Bitmap bm =BitmapFactory.decodeStream(is);
//					iv.setImageBitmap(bm);
//					return true;  
//				}  
//				return false;  			
//			}
//		});
		listViewMusic.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//获取MenuInflater对象，用来对menu进行填充
		MenuInflater menuInflater=getMenuInflater();
		//获取资源对menu进行填充
		menuInflater.inflate(R.menu.main_menu, menu);
		//返回true表示显示菜单  返回false则不显示菜单
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.exit:
			showDialog(DIALOG_EXIT);
			//			Process.killProcess(Process.myPid());
			break;

		default:
			break;
		}
		//返回true表示消息被消耗
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPlay:
			if(musicService.isPlaying()){			
				musicService.pause();
			}else{				
				musicService.play();

			}			
			break;
		case R.id.btnNext:
			musicService.playNext();
			break;
		case R.id.btnRew:
			musicService.playRew();
			break;
		default:
			break;
		}
		setPlayBuutonIcon();
	}	


	@Override
	public void finish() {
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		musicService.play(position);
		setPlayBuutonIcon();
	}
	@Override
	protected void onDestroy() {
		System.out.println("onDestroy====================================");
		super.onDestroy();
	}

	private void setPlayBuutonIcon(){
		if(musicService.isPlaying()){
			btnPlay.setImageResource(R.drawable.btn_pause);
			//获取当前歌曲总时长
			int duration=musicService.getDuration();
			//将seekBar最大值设为当前歌曲时长
			seekBar.setMax(duration);
			//显示歌曲总时长
			textViewTotalTime.setText(timeToString(duration));			
			//开始更新seekBar
			updateSeekBar.run();
		}else{
			btnPlay.setImageResource(R.drawable.btn_play);
		}
	}


	/**
	 * 当seekBar改变时
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
			textViewCurTime.setText(timeToString(progress));
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		isTrackingSeekbar=true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if(musicService.isPlaying()){
			musicService.seekTo(seekBar.getProgress());			
		}
		isTrackingSeekbar=false;

	}
	private String timeToString(int msec){
		int sec=msec/1000;
		int m=sec/60;
		int s=sec%60;		
		String strM=m<10?"0"+m:""+m;
		String strS=s<10?"0"+s:""+s;
		return strM+":"+strS;
	}

	class UpdateSeekBar extends TimerTask{

		@Override
		public void run() {
			if(musicService!=null&&musicService.isPlaying()&&!isTrackingSeekbar){				
				seekBar.setProgress(musicService.getCurrentPosition());
			}
		}

	}
}
