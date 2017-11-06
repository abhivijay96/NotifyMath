package com.game.notification.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.game.notification.myapplication.Game.Engine;
import com.game.notification.myapplication.Utils.ActionTypes;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Engine engine = Engine.getInstance();

        if(!engine.isRunning())
        {
            setStartFab(fab);
        }

        else
        {
            setStopFab(fab);
        }
    }

    private void setStartFab(final FloatingActionButton fab)
    {
        fab.setImageResource(android.R.drawable.ic_media_play);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeScreen.this, GameService.class);
                i.setAction(ActionTypes.START_GAME);
                startService(i);
                Snackbar.make(view, "Open Notification Bar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                setStopFab((FloatingActionButton) view);
            }
        });
    }

    private void setStopFab(final FloatingActionButton fab)
    {
        fab.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(HomeScreen.this, GameService.class));
                Snackbar.make(view, "All notifications are cleared", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                setStartFab((FloatingActionButton) view);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
