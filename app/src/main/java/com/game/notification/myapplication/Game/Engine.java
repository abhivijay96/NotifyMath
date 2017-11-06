package com.game.notification.myapplication.Game;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.game.notification.myapplication.GameService;
import com.game.notification.myapplication.Utils.ActionTypes;
import com.game.notification.myapplication.Utils.GetOperands;
import com.game.notification.myapplication.Utils.Operands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Abhishek on 06-11-2017.
 */

//Singleton class

public class Engine
{
    private static Engine singleton;
    private static Random random;

    private int stage;
    private int score;
    private int health;

    private boolean running;
    private boolean paused;
    private Context context;
    private NotificationManager manager;
    private int notificationId;

    private Engine()
    {
    }

    static
    {
        singleton = new Engine();
        random = new Random();
    }

    public static Engine getInstance()
    {
        return singleton;
    }

    public void begin(Context context, int id)
    {
        this.context = context;
        manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.notificationId = id;
        this.stage = 1;
        running = true;
        paused = false;

        Thread test = new Thread(){
            @Override
            public void run() {
                handleGame();
            }
        };

        test.start();
        // This should go inside the thread

    }

    private void handleGame()
    {
        while(running)
        {
            int level = 2;

            Log.v("Stage", stage + "");

            int result = (random.nextInt(100) + 1) + ((stage - 1) * 100);
            int wrongResult = result + random.nextInt(20);
            showNotification(getNotification(ActionTypes.PAUSE_GAME, result));

            List<String> correct = new ArrayList<>();
            List<String> wrong = new ArrayList<>();

            while(level < 4)
            {
                correct.add(getOutputString(result, level));
                wrong.add(getOutputString(wrongResult, level));
                ++level;
            }

            List<Notification> correctNotifications = new ArrayList<>();
            List<Notification> wrongNotifications = new ArrayList<>();

            for(String c: correct)
            {
                correctNotifications.add(getChoiceNotification(c));
            }

            for(String w: wrong)
            {
                wrongNotifications.add(getChoiceNotification(w));
            }

            List<Notification> all = new ArrayList<>(correctNotifications);
            all.addAll(wrongNotifications);
            Collections.shuffle(all);

            int notifIds = 2 * stage;
            List<Integer> correctIds = new ArrayList<>();
            for (Notification n: all)
            {
                if(correctNotifications.contains(n))
                    correctIds.add(notifIds);

                showChoiceNotification(n, notifIds++);
            }

            try
            {
                Thread.sleep(15000);
            }

            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            while(paused && running)
            {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            StatusBarNotification[] active = manager.getActiveNotifications();
            for (StatusBarNotification n: active)
            {
                if(n.getId() != notificationId)
                {
                    boolean found = false;
                    for(int e: correctIds)
                    {
                        if(e == n.getId())
                        {
                            found = true;
                        }
                    }

                    if(!found)
                        score -= 2;

                    else
                        ++score;

                    manager.cancel(n.getId());
                }
            }


            if(stage < 3)
                ++stage;
        }
    }

    private void showChoiceNotification(Notification notification, int id)
    {
        manager.notify(id, notification);
    }

    private Notification getChoiceNotification(String output)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(output)
                .setSmallIcon(android.R.drawable.alert_dark_frame)
                .setPriority(Notification.PRIORITY_DEFAULT);
        return builder.build();
    }

    private String getOutputString(int result, int level)
    {
        String output;

        Operands operands = GetOperands.addition(result);
        output = operands.left + " + " + operands.right;

        if(level == 2 && operands.left != 0)
        {
            Operands two = GetOperands.multiplication(operands.left);
            output = two.left + " * " + two.right + " + " + operands.right;
        }

        if(level == 3 && operands.left != 0 && operands.right != 0)
        {
            Operands two = GetOperands.multiplication(operands.left);
            Operands three = GetOperands.division(operands.right);
            output = two.left + " * " + two.right + " + " + three.left + " / " + three.right;
        }

        return output;
    }

    private void showNotification(Notification notification)
    {
        manager.notify(notificationId, notification);
    }

    private Notification getNotification(String pendinIntentActionType, int result)
    {
        Intent i = new Intent(context, GameService.class);
        i.setAction(pendinIntentActionType);
        Intent exit = new Intent(context, GameService.class);
        exit.setAction(ActionTypes.END_GAME);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        PendingIntent exitPendingIntent = PendingIntent.getService(context, 0, exit, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Result : " + result + " Score: " + this.score)
                .setSmallIcon(android.R.drawable.ic_input_add)
                .addAction(android.R.drawable.ic_media_rew,
                        pendinIntentActionType == ActionTypes.PAUSE_GAME ? "Pause" : "Resume",
                        pendingIntent)
                .addAction(android.R.drawable.ic_input_delete, "End", exitPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX);
        return builder.build();
    }

    public void pause()
    {
        paused = true;
        showNotification(getNotification(ActionTypes.RESUME_GAME, 0));
    }

    public void resume()
    {
        paused = false;
        showNotification(getNotification(ActionTypes.PAUSE_GAME, 0));
    }

    public void end()
    {
        running = false;
        manager.cancelAll();
    }

    public boolean isRunning()
    {
        return running;
    }

}
