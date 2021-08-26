package com.kuick.video_clip_animation.layoutmanager;

public interface OnItemSwiped {
  void onItemSwiped();
  void onItemSwipedLeft();
  void onItemSwipedRight();
  void onItemSwipedUp();
  void onItemSwipedDown();
  void onItemTransactionAnimation(float dX);
}
