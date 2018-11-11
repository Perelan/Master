package no.uio.ripple;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.sensordroid.ripple.R;

public class RippleEffect extends RelativeLayout {

    private static final int DEFAULT_DURATION_TIME  = 3000;
    private static final float DEFAULT_SCALE        = 6.0f;

    public final int BREATH = getResources().getColor(R.color.breath);
    public final int BATTERY = getResources().getColor(R.color.battery);
    public final int HR = getResources().getColor(R.color.hr);
    public final int IDLE = getResources().getColor(R.color.idle);

    private float rippleStrokeWidth;
    private float rippleRadius;
    private int rippleDurationTime;
    private float rippleScale;
    private Paint paint;
    private LayoutParams rippleParams;

    Context context;
    AttributeSet attrs;

    public RippleEffect(Context context) {
        super(context);
    }

    public RippleEffect(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleEffect(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode()) return;
        if (attrs == null) throw new IllegalArgumentException("Attributes should be provided in this view!");

        this.context = context;
        this.attrs = attrs;

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleBackground);
        rippleRadius = typedArray.getDimension(R.styleable.RippleBackground_rb_radius, getResources().getDimension(R.dimen.rippleRadius));
        rippleDurationTime = typedArray.getInt(R.styleable.RippleBackground_rb_duration, DEFAULT_DURATION_TIME);
        rippleScale = typedArray.getFloat(R.styleable.RippleBackground_rb_scale, DEFAULT_SCALE);
        typedArray.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);

        paint.setStyle(Paint.Style.FILL);

        rippleStrokeWidth = 0;

        rippleParams = new LayoutParams((int) (2 * (rippleRadius + rippleStrokeWidth)),
                (int) (2 * (rippleRadius + rippleStrokeWidth)));

        rippleParams.addRule(CENTER_IN_PARENT, TRUE);
    }

    private class RippleView extends View {

        public RippleView(Context context) {
            super(context);
            this.setVisibility(View.GONE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int radius = (Math.min(getWidth(),getHeight()))/2;
            canvas.drawCircle(radius, radius,radius - rippleStrokeWidth, paint);
        }
    }

    public void pulse(int color) {

        paint.setColor(color);

        AnimatorSet mAnimatorSet;
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        RippleView rippleView = new RippleView(this.context);
        addView(rippleView,0,   rippleParams);

        final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale);
        scaleXAnimator.setDuration(rippleDurationTime);

        final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale);
        scaleYAnimator.setDuration(rippleDurationTime);

        final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f);
        alphaAnimator.setDuration(rippleDurationTime);

        mAnimatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);

        rippleView.setVisibility(VISIBLE);

        mAnimatorSet.start();
    }
}
