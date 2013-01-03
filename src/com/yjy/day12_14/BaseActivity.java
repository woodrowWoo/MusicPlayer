package com.yjy.day12_14;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class BaseActivity extends Activity {
	protected final int DIALOG_EXIT=0;

	@SuppressLint("NewApi")
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		switch (id) {
		case DIALOG_EXIT:
			View view=getLayoutInflater().inflate(R.layout.exit_dialog_layout,null);
			
			Button btnOk=(Button) view.findViewById(R.id.ok);
			//为确认按钮添加响应事件
			btnOk.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
			Button btnCancel=(Button) view.findViewById(R.id.cancel);
			//为取消按钮添加响应事件
			btnCancel.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					dismissDialog(DIALOG_EXIT);
				}
			});
			builder.setView(view);
			break;

		default:
			break;
		}
		
		return builder.create();
		
	}
}
