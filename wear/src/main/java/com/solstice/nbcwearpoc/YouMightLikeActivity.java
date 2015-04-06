package com.solstice.nbcwearpoc;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class YouMightLikeActivity extends Activity implements View.OnClickListener {

    private boolean showed_confirmation = false;
    private Fragment cardFragment, youMightLikeFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_might_like);

        cardFragment = CardFragment.create("Enjoy Constantine?", "You might like Grimm");
        youMightLikeFragment = new YouMightLikeActionsFragment();

        GridViewPager pager = (GridViewPager) findViewById(R.id.you_might_like_grid_view);

        pager.setAdapter(new YouMightLikeGridAdapter(getFragmentManager()));
        pager.setOnPageChangeListener(new GridViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int row, int col, float rowOffset, float colOffset, int rowOffsetPixels, int colOffsetPixels) {
                if (colOffset > 0) {
                    int alpha = Math.round(256 / 2 * colOffset);
                    int color = Color.argb(alpha, 0, 0, 0);

                    youMightLikeFragment.getView().setBackgroundColor(color);
                    cardFragment.getView().setBackgroundColor(color);
                }
            }

            @Override
            public void onPageSelected(int i, int i2) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

        });

        Vibrator vb = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vb.vibrate(250);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (showed_confirmation) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);

        startActivity(intent);

        showed_confirmation = true;
    }

    private class YouMightLikeGridAdapter extends FragmentGridPagerAdapter {
        public YouMightLikeGridAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getFragment(int row, int col) {
            if (col == 0) {
                return cardFragment;
            } else {
                return youMightLikeFragment;
            }
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount(int i) {
            return 2;
        }

        @Override
        public Drawable getBackgroundForRow(int row) {
            return getResources().getDrawable(R.drawable.grimm_bg_640);
        }
    }

    private class YouMightLikeActionsFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.you_might_like_actions, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            view.findViewById(R.id.remind_me).setOnClickListener(YouMightLikeActivity.this);
            view.findViewById(R.id.no_thanks).setOnClickListener(YouMightLikeActivity.this);
        }
    }
}
