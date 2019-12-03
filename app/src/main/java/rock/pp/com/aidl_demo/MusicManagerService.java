package rock.pp.com.aidl_demo;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 音乐管理的服务类
 */
public class MusicManagerService extends Service {

    private static final String TAG =  MusicManagerService.class.getSimpleName();

    private ArrayList<Music> mMusicList = new ArrayList<>();  //生成的音乐列表
    private List<INewMusicArrivedListener> mListenerList = new ArrayList<>(); //客户端注册的接口列表
    private boolean isServiceDestroy = false; //当前服务是否结束
    private int num = 0;

    /**
     * 解绑服务
     * @param conn
     */
    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Log.e(TAG,"unbindService-----");
    }

    /**
     * 服务端通过Binder实现AIDL的IMusicManager.Stub接口
     * 这个类需要实现IMusicManager相关的抽象方法
     */
    private Binder mBinder = new IMusicManager.Stub() {
        @Override
        public List<Music> getMusicList() throws RemoteException {
            SystemClock.sleep(1000); // 延迟加载
            return mMusicList;
        }

        @Override
        public void addMusic(Music music) throws RemoteException {
            mMusicList.add(music);
        }

        @Override
        public void registerListener(INewMusicArrivedListener listener) throws RemoteException {
            mListenerList.add(listener);
            int num = mListenerList.size();
            Log.e(TAG, "添加完成, 注册接口数: " + num);
        }

        @Override
        public void unregisterListener(INewMusicArrivedListener listener) throws RemoteException {
            mListenerList.add(listener);
            int num = mListenerList.size();
            Log.e(TAG, "添加完成, 注册接口数: " + num);
        }
    };


    //新音乐到达后给客户端发送相关通知
    private void onNewMusicArrived(Music music) throws Exception {
        mMusicList.add(music);
        Log.e(TAG, "发送通知的数量: " + mMusicList.size());
        int num = mListenerList.size();
        for (int i = 0; i < num; ++i) {
            INewMusicArrivedListener listener = mListenerList.get(i);
            listener.onNewBookArrived(music);
        }
        for (Music b : mMusicList){
            Log.e(TAG,b.name+"  "+b.author);
        }
    }

    @Override public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate-------------");
        //首先添加两首歌曲
        mMusicList.add(new Music("《封锁我一生》", "王杰"));
        mMusicList.add(new Music("《稻香》", "周杰伦"));
        //音乐制造机器
        new Thread(new ServiceWorker()).start();
    }

    @Override public void onDestroy() {
        isServiceDestroy = true;
        super.onDestroy();
        Log.e(TAG,"onDestroy-----");
    }

    //音乐制造机
    //每5秒生产一首音乐
    private class ServiceWorker implements Runnable {
        @Override public void run() {
            while (!isServiceDestroy) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                num++;
                if (num == 5) {
                    isServiceDestroy= true;
                }
                Message msg = new Message();
                mHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            }
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int id = 1 + mMusicList.size();
            Music music = new Music("《张学友——新歌》"+id, "张学友");
            try {
                onNewMusicArrived(music);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Nullable @Override public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
