package marketplace.spiritlevel2;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by tsunami on 5/12/17.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.text.DecimalFormat;

public class DrawView extends View {
    Paint paint = new Paint();
    private float[] orientation;
    private boolean paused;
    private float lastOrientationX, lastOrientationY, lastOrientationZ;
    private DecimalFormat df;
    float x = 0;
    boolean toggleRotate;
    private float canvasOrientation;

    public DrawView(Context context) {

        super(context);
        orientation = null;
        paused = false;
        df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        toggleRotate = false;
        canvasOrientation = 0;
    }
    @Override
    public void onDraw(Canvas canvas) {

        canvas.save();
        paint.setColor(Color.WHITE);

        /* if the user has paused the graph by tapping the screen, we don't want to continue to
            update orientation */
        if (paused) {
            paint.setTextSize(75);
            canvas.drawText("Locked", 20, 100, paint);
            // if paused, draw with saved orientation, thus freezing the screen */
        } else {
                lastOrientationZ = getZDegrees();
        }
            double diff = Math.abs(canvasOrientation - lastOrientationZ); // how far we are away

           /* If a reduction in the cavas' orientation results in a decrease in the
        distance between canvas and lastZ, then continue decreasing canvas' */
            if (Math.abs(canvasOrientation - 1 - lastOrientationZ) < diff) {
               if (diff > 5) {
                    canvasOrientation -= 1;
                } else {
                    canvasOrientation -= 0.1;
                }
            }
           /* If an increase in the cavas' { ... } then continue decreasing canvas' */
            if (Math.abs(canvasOrientation + 1 - lastOrientationZ) < diff) {
               if (diff > 5) {
                    canvasOrientation += 1;
                } else {
                    canvasOrientation += 0.1;
                }
            }
        /* rotate canvas, color, define rectangle, draw, restore canvas */
        canvas.rotate(canvasOrientation, this.getWidth() / 2, this.getHeight() / 2);
        paint.setColor(Color.argb(150, 45, 140, 254  ));
        Rect r = new Rect(-this.getWidth(), -this.getHeight(), this.getWidth() / 2, this.getHeight()*2);
        canvas.drawRect(r, paint);
        canvas.restore();
        /* display text of angle lastOrientationZ % 90 */
        paint.setColor(Color.argb(90, 255, 255, 255  ));
        paint.setTextSize(300);
        canvas.drawText(df.format(lastOrientationZ % 90), (float) 0.25 * this.getWidth(), (float) 0.25 * this.getHeight(), paint);
        /* draw axis */
        paint.setStrokeWidth(5);
        canvas.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight(), paint);
        canvas.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2, paint);

            invalidate();
        }
    /* toggle boolean paused */
    public void pause() {
        if (this.paused) {
            paused = false;
            System.out.println(paused);
        } else {
            paused = true;
            System.out.println(paused);
        }
    }
    /*return orientation in degrees */
    public float getZDegrees() { return (orientation[2]) % 360; }
    /* called by parent class Level, passing ref to list of orientations */
    public void setOrientation(float[] orientation) {
        this.orientation = orientation;
        System.out.println("Setting Y: " + orientation[1]);
    }

}
