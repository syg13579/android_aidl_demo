package rock.pp.com.aidl_demo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final String TAG = MainActivity.class.getSimpleName();
    private static final int MESSAGE_ARRIVED = 1;

    private TextView music_list;

    private IMusicManager mRemoteMusicManager;


    private INewMusicArrivedListener musicArrivedListener = new INewMusicArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Music newMusic) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_ARRIVED, newMusic ).sendToTarget();
        }
    };

    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_ARRIVED:
                    Log.e(TAG, "收到的新书: " + msg.obj);
                    new BookListAsyncTask().execute();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            IMusicManager musicManager = IMusicManager.Stub.asInterface(service);

            try {
                mRemoteMusicManager = musicManager;
                Music newMusic = new Music("《客户端音乐》", "rock");
                musicManager.addMusic(newMusic);
                musicManager.registerListener(musicArrivedListener);
                new BookListAsyncTask().execute();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override public void onServiceDisconnected(ComponentName name) {
            mRemoteMusicManager = null;
            Log.e(TAG, "绑定结束");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        music_list = findViewById(R.id.music_list);
    }

    /**
     * 获取music列表
     *
     * @param view 视图
     */
    public void getMusicList(View view) {
        if (mRemoteMusicManager !=null){
            List<Music> list = null;
            try {
                list = mRemoteMusicManager.getMusicList();
            }catch (Exception e){

            }
            if (list!=null){
                String content = "";
                for (int i = 0; i < list.size(); ++i) {
                    content += list.get(i).toString() + "\n";
                }
                music_list.setText(content);
            }
        }
        Toast.makeText(getApplicationContext(), "正在获取中...", Toast.LENGTH_SHORT).show();
    }


    /**
     * 绑定服务按钮的点击事件
     *
     * @param view 视图
     */
    public void bindService(View view) {
        Intent intent = new Intent(this, MusicManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    /**
     * 解绑服务
     */
    public void unbindService(View view){
        unbindService(mConnection);
    }


    private class BookListAsyncTask extends AsyncTask<Void, Void, List<Music>> {
        @Override
        protected List<Music> doInBackground(Void... params) {
            List<Music> list = null;
            try {
                list = mRemoteMusicManager.getMusicList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Music> musicList) {
            String content = "";
            for (int i = 0; i < musicList.size(); ++i) {
                content += musicList.get(i).toString() + "\n";
            }
            music_list.setText(content);
        }
    }
}
