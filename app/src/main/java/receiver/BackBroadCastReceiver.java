package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

import utils.Constant;
import utils.MusicUtils;

public class BackBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Constant.STOP.equals(action)) {
            if (MusicUtils.mediaPlayer.isPlaying() && MusicUtils.mediaPlayer != null) {
                MusicUtils.pause();
            }
        } else if (Constant.CANCEL.equals(action)) {
            if (MusicUtils.mediaPlayer.isPlaying() && MusicUtils.mediaPlayer != null) {
                MusicUtils.stop();
                MusicUtils.destoryMedia();
                Process.killProcess(Process.myPid());
            }
        }
    }
}
