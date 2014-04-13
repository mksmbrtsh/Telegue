package maximsblog.blogspot.com.telegue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TrackJoystickView extends View implements Runnable {
	// Constants
	public final static long DEFAULT_LOOP_INTERVAL = 100; // 100 ms
	// Variables
	private OnTrackJoystickViewMoveListener onTrackJoystickViewMoveListener; // Listener
	private Thread mThread = new Thread(this);
	private long mLoopInterval = DEFAULT_LOOP_INTERVAL;
	private int mYPosition1 = 0; // Touch y1 track position
	private int mYPosition2 = 0; // Touch y2 track position
	private double mCenterX1 = 0; // Center view x1 position
	private double mCenterY1 = 0; // Center view y1 position
	private double mCenterX2 = 0; // Center view x2 position
	private double mCenterY2 = 0; // Center view y2 position
	private Paint mButton;
	private Paint mLine;
	private int mWorkInterval;
	private int mButtonRadius;
	private int mLeftTrackTouchPointer = -1;
	private int mRightTrackTouchPointer = -1;

	public TrackJoystickView(Context context) {
		super(context);
	}

	public TrackJoystickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initJoystickView();
	}

	public TrackJoystickView(Context context, AttributeSet attrs,
			int defaultStyle) {
		super(context, attrs, defaultStyle);
		initJoystickView();
	}

	protected void initJoystickView() {
		mLine = new Paint();
		mLine.setStrokeWidth(10);
		mLine.setColor(Color.DKGRAY);

		mButton = new Paint(Paint.ANTI_ALIAS_FLAG);
		mButton.setColor(Color.DKGRAY);
		mButton.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onFinishInflate() {
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		mButtonRadius = (int) (yNew / 2 * 0.25);
		mWorkInterval = yNew - 2 * mButtonRadius;
		// before measure, get the center of view
		mYPosition1 = (int) yNew / 2;
		mYPosition2 = (int) yNew / 2;
		mCenterX1 = getWidth() - 1 * mButtonRadius;
		mCenterY1 = (getHeight()) / 2;

		mCenterX2 = 1 * mButtonRadius;
		mCenterY2 = (getHeight()) / 2;

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// setting the measured values to resize the view to a certain width and
		// height
		int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));

		setMeasuredDimension(d, d);

	}

	private int measure(int measureSpec) {
		int result = 0;

		// Decode the measurement specifications.
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Return a default size of 200 if no bounds are specified.
			result = 200;
		} else {
			// As you want to fill the available space
			// always return the full available bounds.
			result = specSize;
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// buttons
		canvas.drawCircle((float) mCenterX1, mYPosition1, mButtonRadius, mButton);
		canvas.drawCircle((float) mCenterX2, mYPosition2, mButtonRadius, mButton);
		// vertical lines
		canvas.drawLine((float) mCenterX1,
				(float) mCenterY1 + mWorkInterval / 2, (float) mCenterX1,
				(float) (mCenterY1 - mWorkInterval / 2), mLine);
		canvas.drawLine((float) mCenterX2,
				(float) mCenterY2 + mWorkInterval / 2, (float) mCenterX2,
				(float) (mCenterY2 - mWorkInterval / 2), mLine);
		// main horisontal lines
		// neuntral
		canvas.drawLine((float) (mCenterX1 - mButtonRadius * 0.75),
				(float) mCenterY1, (float) (mCenterX1 + mButtonRadius * 0.75),
				(float) mCenterY1, mLine);
		canvas.drawLine((float) (mCenterX2 - mButtonRadius * 0.75),
				(float) mCenterY2, (float) (mCenterX2 + mButtonRadius * 0.75),
				(float) mCenterY2, mLine);
		// low
		canvas.drawLine((float) (mCenterX1 - mButtonRadius * 0.6),
				(float) mCenterY1 + mWorkInterval / 2,
				(float) (mCenterX1 + mButtonRadius * 0.6), (float) mCenterY1
						+ mWorkInterval / 2, mLine);
		canvas.drawLine((float) (mCenterX2 - mButtonRadius * 0.6),
				(float) mCenterY2 + mWorkInterval / 2,
				(float) (mCenterX2 + mButtonRadius * 0.6), (float) mCenterY2
						+ mWorkInterval / 2, mLine);
		// high
		canvas.drawLine((float) (mCenterX1 - mButtonRadius * 0.6),
				(float) mCenterY1 - mWorkInterval / 2,
				(float) (mCenterX1 + mButtonRadius * 0.6), (float) mCenterY1
						- mWorkInterval / 2, mLine);
		canvas.drawLine((float) (mCenterX2 - mButtonRadius * 0.6),
				(float) mCenterY2 - mWorkInterval / 2,
				(float) (mCenterX2 + mButtonRadius * 0.6), (float) mCenterY2
						- mWorkInterval / 2, mLine);
		// second horisontal lines
		canvas.drawLine((float) (mCenterX1 - 0.4 * mButtonRadius),
				(float) (mCenterY1 + (mWorkInterval) / 4),
				(float) (mCenterX1 + 0.4 * mButtonRadius),
				(float) (mCenterY1 + (mWorkInterval) / 4), mLine);
		canvas.drawLine((float) (mCenterX2 - 0.4 * mButtonRadius),
				(float) (mCenterY2 + (mWorkInterval) / 4),
				(float) (mCenterX2 + 0.4 * mButtonRadius),
				(float) (mCenterY2 + (mWorkInterval) / 4), mLine);

		canvas.drawLine((float) (mCenterX1 - 0.4 * mButtonRadius),
				(float) (mCenterY1 - (mWorkInterval) / 4),
				(float) (mCenterX1 + 0.4 * mButtonRadius),
				(float) (mCenterY1 - (mWorkInterval) / 4), mLine);
		canvas.drawLine((float) (mCenterX2 - 0.4 * mButtonRadius),
				(float) (mCenterY2 - (mWorkInterval) / 4),
				(float) (mCenterX2 + 0.4 * mButtonRadius),
				(float) (mCenterY2 - (mWorkInterval) / 4), mLine);
		//
		canvas.drawLine((float) (mCenterX1 - 0.4 * mButtonRadius),
				(float) (mCenterY1 - (mWorkInterval) / 8),
				(float) (mCenterX1 + 0.4 * mButtonRadius),
				(float) (mCenterY1 - (mWorkInterval) / 8), mLine);
		canvas.drawLine((float) (mCenterX2 - 0.4 * mButtonRadius),
				(float) (mCenterY2 - (mWorkInterval) / 8),
				(float) (mCenterX2 + 0.4 * mButtonRadius),
				(float) (mCenterY2 - (mWorkInterval) / 8), mLine);

		canvas.drawLine((float) (mCenterX1 - 0.4 * mButtonRadius),
				(float) (mCenterY1 - 3 * (mWorkInterval) / 8),
				(float) (mCenterX1 + 0.4 * mButtonRadius),
				(float) (mCenterY1 - 3 * (mWorkInterval) / 8), mLine);
		canvas.drawLine((float) (mCenterX2 - 0.4 * mButtonRadius),
				(float) (mCenterY2 - 3 * (mWorkInterval) / 8),
				(float) (mCenterX2 + 0.4 * mButtonRadius),
				(float) (mCenterY2 - 3 * (mWorkInterval) / 8), mLine);
		//
		canvas.drawLine((float) (mCenterX1 - 0.4 * mButtonRadius),
				(float) (mCenterY1 + (mWorkInterval) / 8),
				(float) (mCenterX1 + 0.4 * mButtonRadius),
				(float) (mCenterY1 + (mWorkInterval) / 8), mLine);
		canvas.drawLine((float) (mCenterX2 - 0.4 * mButtonRadius),
				(float) (mCenterY2 + (mWorkInterval) / 8),
				(float) (mCenterX2 + 0.4 * mButtonRadius),
				(float) (mCenterY2 + (mWorkInterval) / 8), mLine);

		canvas.drawLine((float) (mCenterX1 - 0.4 * mButtonRadius),
				(float) (mCenterY1 + 3 * (mWorkInterval) / 8),
				(float) (mCenterX1 + 0.4 * mButtonRadius),
				(float) (mCenterY1 + 3 * (mWorkInterval) / 8), mLine);
		canvas.drawLine((float) (mCenterX2 - 0.4 * mButtonRadius),
				(float) (mCenterY2 + 3 * (mWorkInterval) / 8),
				(float) (mCenterX2 + 0.4 * mButtonRadius),
				(float) (mCenterY2 + 3 * (mWorkInterval) / 8), mLine);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//
		int actionMask = event.getActionMasked();

		switch (actionMask) {
		case MotionEvent.ACTION_DOWN: { // first down
			int i1 = event.getActionIndex();
			int y = (int) event.getY(i1);
			int x = (int) event.getX(i1);
			double abs1 = Math.sqrt((x - mCenterX1) * (x - mCenterX1)
					+ (y - mCenterY1) * (y - mCenterY1));
			double abs2 = Math.sqrt((x - mCenterX2) * (x - mCenterX2)
					+ (y - mCenterY2) * (y - mCenterY2));
			if (abs1 < abs2) {
				mYPosition1 = y;
				mLeftTrackTouchPointer = event.getPointerId(i1);
				if (abs1 > mWorkInterval / 2) {
					mYPosition1 = (int) ((mYPosition1 - mCenterY1)
							* mWorkInterval / 2 / abs1 + mCenterY1);
				}
			} else {
				mYPosition2 = y;
				mRightTrackTouchPointer = event.getPointerId(i1);
				if (abs2 > mWorkInterval / 2) {
					mYPosition2 = (int) ((mYPosition2 - mCenterY2)
							* mWorkInterval / 2 / abs2 + mCenterY2);
				}
			}
			invalidate();
			if (mThread != null && mThread.isAlive()) {
				mThread.interrupt();
			}
			mThread = new Thread(this);
			mThread.start();
		}
			break;
		case MotionEvent.ACTION_POINTER_DOWN: { // next downs
			for (int i1 = 0; i1 < event.getPointerCount(); i1++) {
				int y = (int) event.getY(i1);
				int x = (int) event.getX(i1);

				if (mLeftTrackTouchPointer == -1 && mRightTrackTouchPointer != event.getPointerId(i1)) {
					mLeftTrackTouchPointer = event.getPointerId(i1);
				} else if (mRightTrackTouchPointer ==  -1 && mLeftTrackTouchPointer != event.getPointerId(i1)) {
					mRightTrackTouchPointer = event.getPointerId(i1);
				}

				if (mLeftTrackTouchPointer == event.getPointerId(i1)) {
					double abs1 = Math.sqrt((x - mCenterX1) * (x - mCenterX1)
							+ (y - mCenterY1) * (y - mCenterY1));
					mYPosition1 = y;
					if (abs1 > mWorkInterval / 2) {
						mYPosition1 = (int) ((mYPosition1 - mCenterY1)
								* mWorkInterval / 2 / abs1 + mCenterY1);
					}
				} else if (mRightTrackTouchPointer == event.getPointerId(i1)) {
					double abs2 = Math.sqrt((x - mCenterX2) * (x - mCenterX2)
							+ (y - mCenterY2) * (y - mCenterY2));
					mYPosition2 = y;
					if (abs2 > mWorkInterval / 2) {
						mYPosition2 = (int) ((mYPosition2 - mCenterY2)
								* mWorkInterval / 2 / abs2 + mCenterY2);
					}
				}
			}
			invalidate();
		}
			break;
		case MotionEvent.ACTION_UP: { // last up
			mYPosition1 = (int) mCenterY1;
			mYPosition2 = (int) mCenterY2;
			mRightTrackTouchPointer =  -1;
			mLeftTrackTouchPointer =  -1;
			invalidate();
			mThread.interrupt();
			onTrackJoystickViewMoveListener.onValueChanged(getPower2(),
					getPower1());
		}
			break;
		case MotionEvent.ACTION_POINTER_UP: { // next up
				int i = event.getPointerId(event.getActionIndex());
				if (mLeftTrackTouchPointer == i) {
					mYPosition1 = (int) mCenterY1;
					mLeftTrackTouchPointer =  -1;

				} else if (mRightTrackTouchPointer == i) {
					mYPosition2 = (int) mCenterY2;
					mRightTrackTouchPointer =  -1;
				}
			invalidate();

		}
			break;

		case MotionEvent.ACTION_MOVE: { // moving
			for (int i1 = 0; i1 < event.getPointerCount(); i1++) {
				int y = (int) event.getY(i1);
				int x = (int) event.getX(i1);
				if (mLeftTrackTouchPointer == event.getPointerId(i1)) {
					double abs1 = Math.sqrt((x - mCenterX1) * (x - mCenterX1)
							+ (y - mCenterY1) * (y - mCenterY1));
					mYPosition1 = y;
					if (abs1 > mWorkInterval / 2) {
						mYPosition1 = (int) ((mYPosition1 - mCenterY1)
								* mWorkInterval / abs1 / 2 + mCenterY1);
					}
				} else if (mRightTrackTouchPointer == event.getPointerId(i1)) {
					double abs2 = Math.sqrt((x - mCenterX2) * (x - mCenterX2)
							+ (y - mCenterY2) * (y - mCenterY2));
					mYPosition2 = y;
					if (abs2 > mWorkInterval / 2) {
						mYPosition2 = (int) ((mYPosition2 - mCenterY2)
								* mWorkInterval / abs2 / 2 + mCenterY2);
					}
				}
			}
			invalidate();
		}
			break;
		}
		return true;
	}

	private int getPower1() {
		int y = mYPosition1 - mButtonRadius;

		return (int) Math.round(100.0 - y * 200.0 / (getHeight() - 2.0 * mButtonRadius));
	}

	private int getPower2() {
		int y = mYPosition2 - mButtonRadius;

		return (int)Math.round(100.0 - y * 200.0 / (getHeight() - 2.0 * mButtonRadius));
	}

	public void setOnTrackJoystickViewMoveListener(
			OnTrackJoystickViewMoveListener listener, long repeatInterval) {
		this.onTrackJoystickViewMoveListener = listener;
		this.mLoopInterval = repeatInterval;
	}

	public static interface OnTrackJoystickViewMoveListener {
		public void onValueChanged(int y1, int y2);
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			post(new Runnable() {
				public void run() {
					onTrackJoystickViewMoveListener.onValueChanged(getPower2(),
							getPower1());
				}
			});
			try {
				Thread.sleep(mLoopInterval);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}