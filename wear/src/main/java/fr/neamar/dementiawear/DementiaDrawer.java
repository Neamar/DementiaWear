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
    private final Bitmap rotorImage;
    private final Bitmap handImage;

    private final Paint backgroundPaint;


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
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setFilterBitmap(true);

        scalingRatio = width * DementiaSettings.STATOR_RATIO_IN_WATCH / DementiaSettings.STATOR_SIZE;
        Log.i(TAG, "Scaling ratio is " + scalingRatio);

        // TODO: Why do I need a factor 2?
        statorCircularPitch = 2 * DementiaSettings.STATOR_CIRCULAR_PITCH * scalingRatio;
        rotorCircularPitch = 2 * DementiaSettings.ROTOR_CIRCULAR_PITCH * scalingRatio;
        combinedCircularPitch = statorCircularPitch + rotorCircularPitch + DementiaSettings.TEETH_SIZE;

        this.rotorImage = getRotorImage(context);
        this.handImage = getHandImage(context);

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
    private Bitmap getRotorImage(Context context) {
        Bitmap originalRotor = BitmapFactory.decodeResource(context.getResources(), R.drawable.rotor);
        return Bitmap.createScaledBitmap(originalRotor, (int) (originalRotor.getWidth() * scalingRatio), (int) (originalRotor.getHeight() * scalingRatio), true);
    }

    // Builds the hand
    private Bitmap getHandImage(Context context) {
        Bitmap originalHand = BitmapFactory.decodeResource(context.getResources(), R.drawable.hand);
        return Bitmap.createScaledBitmap(originalHand, (int) (originalHand.getWidth() * scalingRatio), (int) (originalHand.getHeight() * scalingRatio), true);
    }


    void drawOnCanvas(Canvas canvas, Calendar calendar, boolean ambientMode) {
        canvas.drawRect(0, 0, width, height, backgroundPaint);
        // Draw the stator and other static elements
        canvas.drawBitmap(this.backgroundImage, centerX - backgroundImage.getWidth() / 2, centerY - backgroundImage.getHeight() / 2, backgroundPaint);
        canvas.save();

        float statorTheta = (calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000) / 60f * 360;
        Log.e(TAG, "Angle " + statorTheta);

        canvas.rotate(statorTheta, centerX, centerY);

        // Transform the rotor
        float rotorTheta = statorTheta * DementiaSettings.n + (360 / DementiaSettings.t / 2);
        Matrix statorMatrix = new Matrix();

        float dx = centerX - rotorImage.getWidth() / 2;
        float dy = centerY - combinedCircularPitch - rotorImage.getHeight() / 2;
        statorMatrix.setTranslate(dx, dy);
        statorMatrix.postRotate(rotorTheta, dx + rotorImage.getWidth() / 2, dy + rotorImage.getHeight() / 2);

        canvas.drawBitmap(rotorImage, statorMatrix, backgroundPaint);

        // Transform the hand
        float handTheta = statorTheta * DementiaSettings.n;
        Matrix handMatrix = new Matrix();

        dx = centerX - handImage.getWidth() / 2;
        dy = centerY - combinedCircularPitch - DementiaSettings.HAND_Y_CENTER * scalingRatio;
        handMatrix.setTranslate(dx, dy);
        handMatrix.postRotate(handTheta, dx + handImage.getWidth() / 2, dy + DementiaSettings.HAND_Y_CENTER * scalingRatio);

        canvas.drawBitmap(handImage, handMatrix, backgroundPaint);

        canvas.restore();
    }
}
