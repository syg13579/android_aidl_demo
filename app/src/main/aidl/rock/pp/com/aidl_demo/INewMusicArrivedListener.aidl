
package rock.pp.com.aidl_demo;

import rock.pp.com.aidl_demo.Music;

interface INewMusicArrivedListener {
    void onNewBookArrived(in Music newMusic);
}
