package fr.neamar.dementiawear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import java.util.Calendar;

public class DementiaDrawer {
    private final static String TAG = "DementiaDrawer";

    private final Bitmap backgroundImage;
    private final Paint backgroundPaint;

    private final Bitmap rotorImage;

    private final int width;
    private final int height;
    private final float centerX;
    private final float centerY;

    private final float scalingRatio;
    private final float statorCircularPitch;
    private final float rotorCircularPitch;
    private final float combinedCircularPitch;

    DementiaDrawer(Context context, int width, int height) {
        this.backgroundImage = getBackgroundImage(context, width, height);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);

        scalingRatio = width * DementiaSettings.STATOR_RATIO_IN_WATCH / DementiaSettings.STATOR_SIZE;
        Log.i(TAG, "Scaling ratio is " + scalingRatio);

        // TODO: Why do I need a factor 2?
        statorCircularPitch = 2 * DementiaSettings.STATOR_CIRCULAR_PITCH * scalingRatio;
        rotorCircularPitch = 2 * DementiaSettings.ROTOR_CIRCULAR_PITCH * scalingRatio;
        combinedCircularPitch = statorCircularPitch + rotorCircularPitch + DementiaSettings.TEETH_SIZE;

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

    void drawOnCanvas(Canvas canvas, Calendar calendar, boolean ambientMode) {
        canvas.drawRect(0, 0, width, height, backgroundPaint);
        // Draw the stator and other static elements
        canvas.drawBitmap(this.backgroundImage, centerX - backgroundImage.getWidth() / 2, centerY - backgroundImage.getHeight() / 2, backgroundPaint);

        float statorTheta = (float) (calendar.get(Calendar.SECOND) / 60f * 360);

        // Transform the angle to be used with cos and sin. Make sure that 0 is at the top, as is standard in a watch.
        float statorThetaRadians = (float) Math.toRadians(statorTheta - 90);
        float rotorTheta = statorTheta * DementiaSettings.n;

        Matrix statorMatrix = new Matrix();

        Log.e(TAG, "Angle " + statorTheta + " rad " + Math.toRadians(statorTheta));
        // Start centered
        float dx = centerX;
        dx += combinedCircularPitch * Math.cos(statorThetaRadians);
        dx -= rotorImage.getWidth() / 2;

        float dy = centerY;
        dy += combinedCircularPitch * Math.sin(statorThetaRadians);
        dy -= rotorImage.getHeight() / 2;

        statorMatrix.setTranslate(dx, dy);
        //statorMatrix.postRotate(rotorTheta, centerX, centerY);
        canvas.drawBitmap(this.rotorImage, statorMatrix, backgroundPaint);
    }
}
