package com.solstice.nbcwearpoc;

import android.content.Intent;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by andrewsutherland on 4/1/15.
 */
public class WakeupService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getDataItem().getUri().getPath().equals(Constants.YOU_MIGHT_LIKE_PATH)) {
                startYouMightLike();
            }
        }
    }

    public void startYouMightLike() {
        Intent intent = new Intent(this, YouMightLikeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
}
