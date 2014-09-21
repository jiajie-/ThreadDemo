package com.sise.threaddemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	public static final int UPDATE_TEXT = 1;
	
	private int step=0;

	private Button btn_change_text;
	private Button btn_download;
	private TextView textView;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn_change_text = (Button) findViewById(R.id.btn_change_text);
		btn_download = (Button) findViewById(R.id.btn_download);
		textView = (TextView) findViewById(R.id.tv_text);

		dialog = new ProgressDialog(this);
		dialog.setTitle("Downloading");
		dialog.setMessage("Downloading....");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// dialog.setCancelable(false);

		btn_change_text.setOnClickListener(this);
		btn_download.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_change_text:
			new Thread(new Runnable() {
				@Override
				public void run() {
					Message message = Message.obtain();
					message.what = UPDATE_TEXT;
					// 将Message对象发送出去
					handler.sendMessage(message);
				}
			}).start();
			break;
		case R.id.btn_download:
			new DownloadTask().execute();
			break;
		default:
			break;
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case UPDATE_TEXT:
				// 进行UI操作
				textView.setText("Nice to meet you");
				break;
			default:
				break;
			}
		}
	};

	class DownloadTask extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			// 显示进度对话框
			dialog.show();
		}
		
		/**
		 * 执行具体的耗时任务
		 */
		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				while (true) {
					Thread.sleep(1000);
					// 计算下载进度
					int downloadPercent = doDownload();
					//publishProgress()将进度传到onProgressUpdate()中，
					publishProgress(downloadPercent);
					if (downloadPercent >= 100) {
						step=0;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		private int doDownload() {
			step+=10;
			return step;
		}

		/**
		 * 进行UI操作
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			dialog.setProgress(values[0]);
		}

		/**
		 * 执行任务收尾工作
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			// 关闭进度对话框
			dialog.dismiss();
			
			if (result) {
				Toast.makeText(MainActivity.this, "Download succeeded", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MainActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
			}
			
		}
	}

}
