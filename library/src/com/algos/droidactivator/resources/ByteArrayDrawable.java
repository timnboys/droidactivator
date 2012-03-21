package com.algos.droidactivator.resources;

import java.io.ByteArrayInputStream;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


public abstract class ByteArrayDrawable {
	
	public static Drawable bytesToDrawable(byte[] bytes) {
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		BitmapDrawable bDrawable = new BitmapDrawable(stream);
		return bDrawable;
	}

}
