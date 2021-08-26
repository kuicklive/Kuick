package com.kuick.video_clip_animation.layoutmanager;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class SwipeableLayoutManager extends RecyclerView.LayoutManager {
    private int maxShowCount = 3;
    private float scaleGap = 0.1f;
    private int transYGap = 0;
    private int angle = 10;
    private long animationDuratuion = 500;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int itemCount = getItemCount();
        if (itemCount < 1) {
            return;
        }


        int startPosition = Math.min(maxShowCount, itemCount) - 1;
        startPosition = startPosition > 0 ? startPosition : 0;

        for (int position = startPosition; position >= 0; position--) {
            View view = recycler.getViewForPosition(position);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
            int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
            layoutDecorated(view, widthSpace / 2, heightSpace / 2,
                    widthSpace / 2 + getDecoratedMeasuredWidth(view),
                    heightSpace / 2 + getDecoratedMeasuredHeight(view));
            if (position > 0) {
                view.setScaleX(validateScale(1 - scaleGap * position));
                if (position < maxShowCount - 1) {
                    view.setTranslationY(validateScale(transYGap * position));
                    view.setScaleY(validateScale(1 - scaleGap * position));
                } else {
                    view.setTranslationY(validateTranslation(transYGap * (position - 1)));
                    view.setScaleY(validateScale(1 - scaleGap * (position - 1)));
                }
            }
        }
    }

    private final float validateTranslation(float value) {
        return Math.max(0, value);
    }

    private final float validateScale(float value) {
        return Math.max(0, Math.min(1, value));
    }

    public float getScaleGap() {
        return scaleGap;
    }

    public SwipeableLayoutManager setScaleGap(float scaleGap) {
        this.scaleGap = Math.min(Math.max(0, scaleGap), 1);
        return this;
    }

    public int getMaxShowCount() {
        return maxShowCount;
    }

    public SwipeableLayoutManager setMaxShowCount(int maxShowCount) {
        this.maxShowCount = Math.max(maxShowCount, 1);
        return this;
    }

    public int getTransYGap() {
        return transYGap;
    }

    public SwipeableLayoutManager setTransYGap(int transYGap) {
        this.transYGap = transYGap;
        return this;
    }

    public int getAngle() {
        return angle;
    }

    public SwipeableLayoutManager setAngle(int angle) {
        this.angle = angle;
        return this;
    }

    public long getAnimationDuratuion() {
        return animationDuratuion;
    }

    public SwipeableLayoutManager setAnimationDuratuion(long animationDuratuion) {
        this.animationDuratuion = Math.max(1, animationDuratuion);
        return this;
    }
}
