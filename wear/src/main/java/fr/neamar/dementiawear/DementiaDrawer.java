package fr.neamar.dementiawear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class DementiaDrawer {
    private final Bitmap backgroundImage;
    private final Paint backgroundPaint;

    private final int width;
    private final int height;
    private final float centerX;
    private final float centerY;

    public DementiaDrawer(Context context, int width, int height) {
        this.backgroundImage = getBackgroundImage(context, width, height);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);

        this.width = width;
        this.height = height;
        centerX = width / 2f;
        centerY = height / 2f;
    }

    // Includes the background and the stator
    private Bitmap getBackgroundImage(Context context, int width, int height) {
        Bitmap originalStator = BitmapFactory.decodeResource(context.getResources(), R.drawable.stator);
        return Bitmap.createScaledBitmap(originalStator, (int) (width * DementiaSettings.STATOR_RATIO_IN_WATCH), (int) (height * DementiaSettings.STATOR_RATIO_IN_WATCH), true);
    }

    private Bitmap getRotor() {
        return Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
    }

    public void drawOnCanvas(Canvas canvas, boolean ambientMode) {
        // Draw the stator and other static elements
        canvas.drawBitmap(this.backgroundImage, centerX - backgroundImage.getWidth() / 2, centerY - backgroundImage.getHeight() / 2, backgroundPaint);
    }
}
