package com.yjy.service;





import java.util.ArrayList;
import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;

import com.yjy.day12_14.MainActivity;

public class MyMusicService extends Service {
	private MediaPlayer mMediaPlayer;
	private MyBinder myBinder=new MyBinder();
	//private ArrayList<String> mMusicList=new ArrayList<String>();//音乐文件列表
	//private ArrayList<String> mMusiNameList=new ArrayList<String>();//音乐文件名称列表
	private ArrayList<HashMap<String, Object>> mMusicList;
	public static String MUSIC_TITLE="musicTitle";
	public static String MUSIC_DIR="musicDir";
	public static String MUSIC_ARTIST="musicArtist";
	public static String MUSIC_ALBUM="musicAlbum";
	private boolean isPause;
	private int nowPlay=0;
	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("onBind===============================");
		return myBinder;
	}

	@Override
	public void onCreate() {
		System.out.println("onCreate===============================");
		mMusicList=scanMusic();
		super.onCreate();		
	}
	
	/**
	 * 获取当前播放时间
	 * @return
	 */
	public int getCurrentPosition(){
		return mMediaPlayer.getCurrentPosition() ;
	}
	
	/**
	 * 获取当前曲目的时间
	 * @return
	 */
	public int getDuration(){
		return mMediaPlayer.getDuration();
	}
	/**
	 * 指定播放的位置（以毫秒为单位的时间）
	 * @param msec
	 */
	public void seekTo(int msec){
		mMediaPlayer.seekTo(msec);
	}
	
	
	
	public void play(){
		play(nowPlay);
	}
	
	/**
	 * 播放
	 * @param index 相对于mMusicList的索引
	 */
	public void play(int index){
		if(!isPause||nowPlay!=index){//非暂停状态
			if(index<0||index>=mMusicList.size())//当请求播放曲目超过列表范围
				return;
			if(mMediaPlayer!=null)mMediaPlayer.stop();//若不是第一次播放先停止当前播放
			nowPlay=index;
			mMediaPlayer = MediaPlayer.create(this, Uri.parse((String)mMusicList.get(nowPlay).get(MUSIC_DIR)));
		}
		mMediaPlayer.start();
		myNotification(android.R.drawable.ic_media_play, (String)mMusicList.get(nowPlay).get(MUSIC_TITLE),(String)mMusicList.get(nowPlay).get(MUSIC_ARTIST));
		
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				play(nowPlay+1);
			}
		});
	}
	
	/**
	 * 播放下一首
	 */
	public void playNext(){
		play(nowPlay+1);
	}
	
	/**
	 * 播放上一首
	 */
	public void playRew(){
		play(nowPlay-1);
	}
	
	/**
	 * 暂停
	 */
	public void pause(){
		isPause=true;
		mMediaPlayer.pause();
		myNotification(android.R.drawable.ic_media_pause, (String)mMusicList.get(nowPlay).get(MUSIC_TITLE),(String)mMusicList.get(nowPlay).get(MUSIC_ARTIST));
	}

	/**
	 * 自定义通知
	 * @param icon
	 * @param text
	 */
	public void myNotification(int icon,String text,String des){
		NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification=new Notification(icon,text,System.currentTimeMillis());
		//定义通知时的信息和意图
		Intent intent=new Intent(getApplicationContext(),MainActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent contentIntent=PendingIntent.getActivity(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);		
		notification.setLatestEventInfo(getApplicationContext(), text, des ,contentIntent);		
		notification.flags|=Notification.FLAG_NO_CLEAR;//无法通过清除按钮清除
//		notification.flags|=Notification.FLAG_AUTO_CANCEL;//无法通过清除按钮清除
		//向NotificationManager传送通知
		notificationManager.notify(10,notification);
	}
	/**
	 * 扫描音乐文件
	 */
//	public void scanMusic(){
//		File dir = Environment.getExternalStorageDirectory();
//		File mp3 = new File(dir, "wandoujia/music");		
//		mMusicList.clear();
//		mMusiNameList.clear();		
//		for (String s : mp3.list()) {
//			if (s.endsWith(".mp3")) {
//				mMusiNameList.add(s);				
//				mMusicList.add(mp3.getAbsolutePath() + "/" + s);
//			}
//		}		
//	}
	public ArrayList<HashMap<String, Object>> scanMusic() { 
		//生成动态数组，并且转载数据  
		ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();  

		//查询媒体数据库
		Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		//遍历媒体数据库
		if(cursor.moveToFirst()){

			while (!cursor.isAfterLast()) { 

				//歌曲编号
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));  
				//歌曲标题
				String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));  
				//歌曲的专辑名：MediaStore.Audio.Media.ALBUM
				String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));  
				//歌曲的歌手名： MediaStore.Audio.Media.ARTIST
				String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));  
				//歌曲文件的路径 ：MediaStore.Audio.Media.DATA
				String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));    
				//歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
				int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));    
				//歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
				Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
				
				if(size>1024*800){//大于300K
					HashMap<String, Object> map = new HashMap<String, Object>();					
					map.put(MUSIC_TITLE, tilte);  
					map.put(MUSIC_DIR, url);
					map.put(MUSIC_ARTIST, artist);
					map.put(MUSIC_ALBUM, album);
					mylist.add(map);  
				}
				cursor.moveToNext(); 
			} 
		}
		return mylist;
	}

	public ArrayList<HashMap<String, Object>> getMusicList(){
		return mMusicList;
	}
	/**
	 * 判断是否正在播放
	 * @return
	 */
	public boolean isPlaying(){
		if(mMediaPlayer==null)
			return false;
		return mMediaPlayer.isPlaying();
	}
	/**
	 * 自定义Binder 用来获取MyMusicService并传递都与其绑定的Activity
	 * @author YangJinyang
	 *
	 */
	public class MyBinder extends Binder{
		public MyMusicService getService(){
			return MyMusicService.this;
		}
	}
}
