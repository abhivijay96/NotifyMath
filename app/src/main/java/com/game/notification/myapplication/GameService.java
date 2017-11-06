package com.game.notification.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.game.notification.myapplication.Game.Engine;
import com.game.notification.myapplication.Utils.ActionTypes;

public class GameService extends Service
{
    private int notificationId;
    private NotificationManager manager;

    public GameService()
    {
        notificationId = 1;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Engine engine = Engine.getInstance();
        if(intent.getAction() == ActionTypes.START_GAME)
        {
            engine.begin(this, notificationId);
        }

        else if(intent.getAction() == ActionTypes.PAUSE_GAME)
        {
            engine.pause();
        }

        else if(intent.getAction() == ActionTypes.RESUME_GAME)
        {
            engine.resume();
        }

        else if(intent.getAction() == ActionTypes.END_GAME)
        {
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        Engine.getInstance().end();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
