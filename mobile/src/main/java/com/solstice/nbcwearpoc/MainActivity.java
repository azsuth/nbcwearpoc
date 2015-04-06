package com.solstice.nbcwearpoc;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends Activity {

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        setupButtonClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        googleApiClient.connect();
        registerReceiver(broadcastReceiver, makeIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();

        googleApiClient.disconnect();
        unregisterReceiver(broadcastReceiver);
    }

    private void setupButtonClickListeners() {
        // silly notification
        findViewById(R.id.voice_1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                voiceNotification("New Episode");
            }

        });

        findViewById(R.id.voice_2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                voiceNotification("Encore Episode");
            }

        });

        findViewById(R.id.you_might_like).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                customNotification(Constants.YOU_MIGHT_LIKE_PATH);
            }

        });

        findViewById(R.id.home_in_time_for).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                homeInTimeFor();
            }

        });
    }

    private void voiceNotification(String title) {
        Intent voice1Intent = new Intent(MainActivity.class.getCanonicalName() + "dismissVoice");
        PendingIntent pendingVoice1Intent = PendingIntent.getBroadcast(this, 0, voice1Intent, 0);

        NotificationCompat.Builder voice1Builder = new NotificationCompat.Builder(this)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("The Voice is about to start.")
                .addAction(R.drawable.small_clock_icon, "Remind me later", pendingVoice1Intent);

        NotificationCompat.WearableExtender voice1Extender = new NotificationCompat.WearableExtender()
                .setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.the_voice_dark));

        voice1Builder.extend(voice1Extender);

        NotificationManagerCompat.from(this).notify(0, voice1Builder.build());
    }

    private void homeInTimeFor() {
        Intent homeInTimeForIntent = new Intent(MainActivity.class.getCanonicalName() + "dismissHome");
        PendingIntent pendingHomeInTimeForIntent = PendingIntent.getBroadcast(this, 0, homeInTimeForIntent, 0);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();

        SpannableStringBuilder text = new SpannableStringBuilder();
        text.append("\u2014\n");
        appendStyled(text, "Grimm\n");
        appendStyled(text, "7pm", new RelativeSizeSpan(.75f), new ForegroundColorSpan(0xffbbbbbb));
        appendStyled(text, " ALL NEW", new RelativeSizeSpan(.75f), new ForegroundColorSpan(Color.RED));
        text.append("\n\u2014\n");
        appendStyled(text, "Dateline NBC\n");
        appendStyled(text, "8pm", new RelativeSizeSpan(.75f), new ForegroundColorSpan(0xffbbbbbb));
        text.append("\n\u2014\n");
        appendStyled(text, "The Tonight Show Starring Jimmy Fallon\n");
        appendStyled(text, "10:30pm", new RelativeSizeSpan(.75f), new ForegroundColorSpan(0xffbbbbbb));
        appendStyled(text, " ALL NEW", new RelativeSizeSpan(.75f), new ForegroundColorSpan(Color.RED));

        style.bigText(text);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("You'll be home in time for...")
                .setStyle(style)
                .setContentIntent(pendingHomeInTimeForIntent)
                .extend(new NotificationCompat.WearableExtender()
                        .setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.schedule_bg_640)));

        NotificationManagerCompat.from(this).notify(1, builder.build());
    }

    private void appendStyled(SpannableStringBuilder builder, String str, Object... spans) {
        builder.append(str);
        for (Object span : spans) {
            builder.setSpan(span, builder.length() - str.length(), builder.length(), 0);
        }
    }

    private void customNotification(String path) {
        if (googleApiClient.isConnected()) {
            Log.d("notification", "putting data item");

            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
            putDataMapRequest.getDataMap().putLong(Constants.UNIQUE_TIME, System.currentTimeMillis());

            Wearable.DataApi.putDataItem(googleApiClient, putDataMapRequest.asPutDataRequest());
        } else {
            Toast.makeText(this, "googleApiClient not connected", Toast.LENGTH_SHORT).show();
        }
    }

    private IntentFilter makeIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(MainActivity.class.getCanonicalName() + "dismissVoice");
        intentFilter.addAction(MainActivity.class.getCanonicalName() + "dismissHome");

        return intentFilter;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(MainActivity.class.getCanonicalName() + "dismissVoice")) {
                NotificationManagerCompat.from(MainActivity.this).cancel(0);
            } else if (action.equals(MainActivity.class.getCanonicalName() + "dismissHome")) {
                NotificationManagerCompat.from(MainActivity.this).cancel(1);
            }
        }

    };
}
