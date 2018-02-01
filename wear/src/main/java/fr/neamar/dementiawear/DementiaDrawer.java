package fr.neamar.dementiawear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.Calendar;

public class DementiaDrawer {
    private final Bitmap backgroundImage;
    private final Paint backgroundPaint;

    private final Bitmap rotorImage;

    private final int width;
    private final int height;
    private final float centerX;
    private final float centerY;

    public DementiaDrawer(Context context, int width, int height) {
        this.backgroundImage = getBackgroundImage(context, width, height);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);

        this.rotorImage = getRotorImage(context, width, height);

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

    // Builds the rotor
    private Bitmap getRotorImage(Context context, int width, int height) {
        Bitmap originalRotor = BitmapFactory.decodeResource(context.getResources(), R.drawable.rotor);
        return Bitmap.createScaledBitmap(originalRotor, (int) (backgroundImage.getWidth() / DementiaSettings.n), (int) (backgroundImage.getHeight() / DementiaSettings.n), true);
    }

    public void drawOnCanvas(Canvas canvas, Calendar calendar, boolean ambientMode) {
        // Draw the stator and other static elements
        canvas.drawBitmap(this.backgroundImage, centerX - backgroundImage.getWidth() / 2, centerY - backgroundImage.getHeight() / 2, backgroundPaint);

        float angle = 90; //(float) (calendar.get(Calendar.SECOND) / 60f * 360);
        Matrix statorMatrix = new Matrix();
        statorMatrix.setRotate(angle, rotorImage.getWidth() / 2, rotorImage.getHeight() / 2);
        statorMatrix.postTranslate(centerX - rotorImage.getWidth() / 2, centerY - rotorImage.getHeight() / 2);
        canvas.drawBitmap(this.rotorImage, statorMatrix, backgroundPaint);
    }
}
