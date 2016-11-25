package com.minlu.fosterpig.customview;


import android.media.MediaPlayer;

import com.minlu.fosterpig.observer.Observers;

public class MyMediaPlayer extends MediaPlayer implements Observers {

	public MyMediaPlayer() {
		super();
	}

	@Override
	public void update(int mPosition, int distinguishBeNotified, int cancelOrderBid) {
		if(mPosition==3){
			this.pause();
		}
	}
}
