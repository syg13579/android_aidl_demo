
package rock.pp.com.aidl_demo;

// Declare any non-default types here with import statements
import rock.pp.com.aidl_demo.Music;
import rock.pp.com.aidl_demo.INewMusicArrivedListener;

interface IMusicManager {

    List<Music> getMusicList(); // 返回书籍列表
    void addMusic(in Music music); // 添加书籍

    void registerListener(INewMusicArrivedListener listener); // 注册接口
    void unregisterListener(INewMusicArrivedListener listener); // 注销接口

}
