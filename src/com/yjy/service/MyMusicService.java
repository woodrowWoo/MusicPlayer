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
	//private ArrayList<String> mMusicList=new ArrayList<String>();//�����ļ��б�
	//private ArrayList<String> mMusiNameList=new ArrayList<String>();//�����ļ������б�
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
	 * ��ȡ��ǰ����ʱ��
	 * @return
	 */
	public int getCurrentPosition(){
		return mMediaPlayer.getCurrentPosition() ;
	}
	
	/**
	 * ��ȡ��ǰ��Ŀ��ʱ��
	 * @return
	 */
	public int getDuration(){
		return mMediaPlayer.getDuration();
	}
	/**
	 * ָ�����ŵ�λ�ã��Ժ���Ϊ��λ��ʱ�䣩
	 * @param msec
	 */
	public void seekTo(int msec){
		mMediaPlayer.seekTo(msec);
	}
	
	
	
	public void play(){
		play(nowPlay);
	}
	
	/**
	 * ����
	 * @param index �����mMusicList������
	 */
	public void play(int index){
		if(!isPause||nowPlay!=index){//����ͣ״̬
			if(index<0||index>=mMusicList.size())//�����󲥷���Ŀ�����б�Χ
				return;
			if(mMediaPlayer!=null)mMediaPlayer.stop();//�����ǵ�һ�β�����ֹͣ��ǰ����
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
	 * ������һ��
	 */
	public void playNext(){
		play(nowPlay+1);
	}
	
	/**
	 * ������һ��
	 */
	public void playRew(){
		play(nowPlay-1);
	}
	
	/**
	 * ��ͣ
	 */
	public void pause(){
		isPause=true;
		mMediaPlayer.pause();
		myNotification(android.R.drawable.ic_media_pause, (String)mMusicList.get(nowPlay).get(MUSIC_TITLE),(String)mMusicList.get(nowPlay).get(MUSIC_ARTIST));
	}

	/**
	 * �Զ���֪ͨ
	 * @param icon
	 * @param text
	 */
	public void myNotification(int icon,String text,String des){
		NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification=new Notification(icon,text,System.currentTimeMillis());
		//����֪ͨʱ����Ϣ����ͼ
		Intent intent=new Intent(getApplicationContext(),MainActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent contentIntent=PendingIntent.getActivity(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);		
		notification.setLatestEventInfo(getApplicationContext(), text, des ,contentIntent);		
		notification.flags|=Notification.FLAG_NO_CLEAR;//�޷�ͨ�������ť���
//		notification.flags|=Notification.FLAG_AUTO_CANCEL;//�޷�ͨ�������ť���
		//��NotificationManager����֪ͨ
		notificationManager.notify(10,notification);
	}
	/**
	 * ɨ�������ļ�
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
		//���ɶ�̬���飬����ת������  
		ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();  

		//��ѯý�����ݿ�
		Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		//����ý�����ݿ�
		if(cursor.moveToFirst()){

			while (!cursor.isAfterLast()) { 

				//�������
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));  
				//��������
				String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));  
				//������ר������MediaStore.Audio.Media.ALBUM
				String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));  
				//�����ĸ������� MediaStore.Audio.Media.ARTIST
				String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));  
				//�����ļ���·�� ��MediaStore.Audio.Media.DATA
				String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));    
				//�������ܲ���ʱ�� ��MediaStore.Audio.Media.DURATION
				int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));    
				//�����ļ��Ĵ�С ��MediaStore.Audio.Media.SIZE
				Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
				
				if(size>1024*800){//����300K
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
	 * �ж��Ƿ����ڲ���
	 * @return
	 */
	public boolean isPlaying(){
		if(mMediaPlayer==null)
			return false;
		return mMediaPlayer.isPlaying();
	}
	/**
	 * �Զ���Binder ������ȡMyMusicService�����ݶ�����󶨵�Activity
	 * @author YangJinyang
	 *
	 */
	public class MyBinder extends Binder{
		public MyMusicService getService(){
			return MyMusicService.this;
		}
	}
}
