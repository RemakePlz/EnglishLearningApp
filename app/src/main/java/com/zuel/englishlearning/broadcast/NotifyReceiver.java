package com.zuel.englishlearning.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zuel.englishlearning.activity.service.NotifyLearnService;

public class NotifyReceiver extends BroadcastReceiver {

    private static final String TAG = "NotifyReceiver";

    public static final String UPDATE_ACTION = "intent_update_word";

    public static final String SOUND_ACTION = "intent_sound_word";

    public static final String STAR_ACTION = "intent_star_word";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        switch (intent.getAction()) {
            case UPDATE_ACTION:
                NotifyLearnService.currentIndex = NotifyLearnService.currentIndex + 1;
                NotifyLearnService.updateNotification();
                break;
            case SOUND_ACTION:
                NotifyLearnService.playSound();
                break;
            case STAR_ACTION:
                NotifyLearnService.setStarStatus();
                NotifyLearnService.updateNotification();
                break;
        }
    }
}
