package rock.pp.com.aidl_demo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shiyagang on 2019/12/2.
 */

public class Music implements Parcelable {
    public String name; //书名
    public String author; //作者


    public Music(String name ,String author){
        this.name = name;
        this.author = author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.author);
    }

    public Music() {
    }

    protected Music(Parcel in) {
        this.name = in.readString();
        this.author = in.readString();
    }

    public static final Parcelable.Creator<Music> CREATOR = new Parcelable.Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel source) {
            return new Music(source);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    @Override
    public String toString() {
        return "音乐名称："+name +"  歌手："+author;
    }
}
