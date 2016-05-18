package uk.ac.reading.sis05kol.mooc;

//Other parts of the android libraries that we use
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;


public class TheGame extends GameThread{

    //Will store the image of a ball
    private Bitmap mBall;

    //The X and Y position of the ball on the screen (middle of ball)
    private float mBallX = 0;
    private float mBallY = 0;

    //The speed (pixel/second) of the ball in direction X and Y
    private float mBallSpeedX = 0;
    private float mBallSpeedY = 0;

    //Image, velocity and position of paddle
    private Bitmap mPaddle;
    private float  mPaddleX;
    private float  mPaddleSpeedX;

    //Image of paddle
    private Bitmap mSmiley;
    private float mSmileyX;
    private float mSmileyY;

    //Variables for sad faces
    private Bitmap mSadBall;
    private float[] mSadBallX = new float[3];
    private float[] mSadBallY = new float[3];

    //Collision distance between paddle and ball
    private float paddleCollisionDistance;
    private float smileyCollisionDistance;
    private float sadCollisionDistance;


    //Set up paint and color for ball trail
    Paint trailPaint = new Paint();

    //Set up arrays to hold collision positions
      //Will use these to draw a trail behind the ball
    private int trailSize= 50;
    private int collisionCounter; //variable to keep track of number of total collisions
    private float[] xTrails = new float[trailSize];
    private float[] yTrails = new float[trailSize];




    //This is run before anything else, so we can prepare things here
    public TheGame(GameView gameView) {
        //House keeping
        super(gameView);

        //Prepare the image so we can draw it on the screen (using a canvas)

        //central ball
        mBall = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.small_red_ball);

        //paddle ball
        mPaddle = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.yellow_ball);

        //smiley face
        mSmiley = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.smiley_ball);

        mSadBall = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.sad_ball);
    }

    //This is run before a new game (also after an old game)
    @Override
    public void setupBeginning() {
        //Initialize speed
        float initialSpeed = calcMagnitude(mCanvasWidth / 3,mCanvasHeight /3);

        //Set ball motion in random direction down the screen
        mBallSpeedX = (float)(Math.random()*.6 - .3); //random number between -.3 and .3
        mBallSpeedY = 1;

        //preserve original speed
        float currentSpeed = calcMagnitude(mBallSpeedX,mBallSpeedY);
        mBallSpeedX *= initialSpeed/currentSpeed;
        mBallSpeedY *= initialSpeed/currentSpeed;

        //Place the ball in the middle of the screen.
        //mBall.Width() and mBall.getHeight() gives us the height and width of the image of the ball
        mBallX = mCanvasWidth / 2;
        mBallY = mCanvasHeight / 2;

        //Set up the position of the paddle
        mPaddleX = mCanvasWidth / 2;
        mPaddleSpeedX = 0;

        //Set up the position of the sad balls
            //Place all SadBalls forming a pyramid underneath the SmileyBall
        mSadBallX[0] = mCanvasWidth / 3;
        mSadBallY[0] = mCanvasHeight / 3;
        mSadBallX[1] = mCanvasWidth - mCanvasWidth / 3;
        mSadBallY[1] = mCanvasHeight / 3;
        mSadBallX[2] = mCanvasWidth / 2;
        mSadBallY[2] = mCanvasHeight / 5;

        //Set up the position of the smiley ball
        //currently set to be the center of the sad balls
        mSmileyX = mean(mSadBallX);
        mSmileyY = mean(mSadBallY);

        //Set up collision distance between ball and paddle
        paddleCollisionDistance = calcCollisionDistance(mBall, mPaddle);
        smileyCollisionDistance = calcCollisionDistance(mBall, mSmiley);
        sadCollisionDistance    = calcCollisionDistance(mBall, mSadBall);

        //Set up paint color
        trailPaint.setColor(Color.WHITE);
        trailPaint.setStrokeWidth(5);

        //Initialize trail counter and set initial trail
        collisionCounter = 0;
        xTrails[0] = mBallX;
        yTrails[0] = mBallY;

    }

    @Override
    protected void doDraw(Canvas canvas) {
        //If there isn't a canvas to draw on do nothing
        //It is ok not understanding what is happening here
        if(canvas == null) return;

        super.doDraw(canvas);

        //draw the trail of the ball
        drawTrail(canvas);


        //draw the image of the ball using the X and Y of the ball
        //drawBitmap uses top left corner as reference, we use middle of picture
        //null means that we will use the image without any extra features (called Paint)
        canvas.drawBitmap(mBall, mBallX - mBall.getWidth() / 2, mBallY - mBall.getHeight() / 2, null);

        //draw the paddle
        canvas.drawBitmap(mPaddle, mPaddleX - mPaddle.getWidth() / 2, mCanvasHeight - mPaddle.getHeight() / 2, null);

        //draw the smiley ball
        canvas.drawBitmap(mSmiley, mSmileyX - mSmiley.getWidth()/2, mSmileyY - mSmiley.getHeight()/2, null);

        //draw the sad balls
        //Loop through all SadBall
        for(int i = 0; i < mSadBallX.length; i++) {
            //Draw SadBall in position i
            canvas.drawBitmap(mSadBall, mSadBallX[i] - mSadBall.getWidth()/2,
                    mSadBallY[i] - mSadBall.getHeight()/2, null);
        }





    }

    //This is run whenever the phone is touched by the user

	@Override
	protected void actionOnTouch(float x, float y) {
		//Increase/decrease the speed of the ball making the ball move towards the touch
		mPaddleX = x;
	}


    //This is run whenever the phone moves around its axises
    @Override
    protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
        //
        mPaddleSpeedX = mPaddleSpeedX + 70f * xDirection;

        if( mPaddleX <= 0 && mPaddleSpeedX < 0 ) {
            mPaddleSpeedX = 0;
            mPaddleX = 0;
        }
        if( mPaddleX >= mCanvasWidth && mPaddleSpeedX >0 ) {
            mPaddleSpeedX = 0;
            mPaddleX = mCanvasWidth;
        }

    }


    //This is run just before the game "scenario" is printed on the screen
    @Override
    protected void updateGame(float secondsElapsed) {

        //Collisions also update total speed of the ball based on points awarded.
        //handle collision with Paddle
        if (mBallY >= mCanvasHeight - mPaddle.getHeight()/2) {
            handleRoundCollision(mPaddleX, mCanvasHeight, paddleCollisionDistance, 0);
        }
        //handle collision with Smiley
        if (mBallY <= mSmileyY + mSmiley.getHeight()/2) {
            handleRoundCollision(mSmileyX, mSmileyY, smileyCollisionDistance, 1);
        }
        //handle collsision with Sad Balls
        if (mBallY <= mSadBallY[0] + mSadBall.getHeight()/2){
            for(int i = 0; i<mSadBallX.length; i++ ){
                handleRoundCollision(mSadBallX[i],mSadBallY[i],sadCollisionDistance,0);
            }
        }


        //Move the ball's X and Y using the speed (pixel/sec)
        mBallX = mBallX + secondsElapsed * mBallSpeedX;
        mBallY = mBallY + secondsElapsed * mBallSpeedY;

        //Reflect ball on boundaries, by flipping value of relevant speed component
        //Check to make sure speed is appropriate value to prevent getting caught outside of the
        // screen if update puts ball outside.
        if(mBallX <= (mBall.getWidth()  / 2 ) && mBallSpeedX < 0 ) mBallSpeedX *= -1;                 //left
        if(mBallX >= mCanvasWidth  - (mBall.getWidth()  / 2 ) && mBallSpeedX > 0 ) mBallSpeedX *= -1; //right
        if(mBallY <= (mBall.getHeight() / 2 ) && mBallSpeedY < 0 ) mBallSpeedY *= -1;                 //top

        mPaddleX = mPaddleX + secondsElapsed * mPaddleSpeedX;


        // Lose if ball hits bottom of screen
        if(mBallY >= mCanvasHeight){
            setState(GameThread.STATE_LOSE);
        }

        //Save the current position of the ball to draw its trail.

        collisionCounter++;

        xTrails[collisionCounter%trailSize] = mBallX;
        yTrails[collisionCounter%trailSize] = mBallY;


    }

    // Function to draw the trail of the ball
    public void drawTrail(Canvas canvas){

        trailPaint.setAlpha(0); //set paint to be completely transparent
        if( collisionCounter < trailSize){  //If buffer of previous ball locations is not yet full
            for (int i = 0; i < collisionCounter; i++) {
                canvas.drawLine(xTrails[i], yTrails[i], xTrails[i + 1], yTrails[i + 1], trailPaint); //draw previous collisions
                trailPaint.setAlpha(trailPaint.getAlpha()+(255/collisionCounter));
            }
            canvas.drawLine(xTrails[collisionCounter], yTrails[collisionCounter],mBallX,mBallY, trailPaint);
        }
        else
        {
            int drawn=1;
            while(drawn < trailSize){
                int i = (collisionCounter+drawn);
                canvas.drawLine(xTrails[(i)%trailSize],yTrails[(i)%trailSize],
                        xTrails[(i+1)%trailSize],yTrails[(i+1)%trailSize],trailPaint);
                trailPaint.setAlpha(trailPaint.getAlpha()+(255/trailSize));
                drawn++;
            }
            canvas.drawLine(xTrails[collisionCounter%trailSize],yTrails[collisionCounter%trailSize], mBallX, mBallY, trailPaint);
        }
    }

    //Function to handle collision between round cental ball and a round object
    private void handleRoundCollision(float pos2x, float pos2y, float collisionDistance, int pointVal){

        //Distance between main ball and other objects
        float D = calcDistance(mBallX,mBallY,pos2x,pos2y);
        if(D <= collisionDistance){


            //calculate speed of the ball to conserve total speed
            float Dx = (mBallX-pos2x); //Separation between ball and object in x direction
            float Dy = (mBallY-pos2y); //Separation between ball and object in y direction
            float Vx = (mBallSpeedX);  //Temporarily save current speeds
            float Vy = -(mBallSpeedY);

            // reposition the ball to prevent overlap
            mBallX = pos2x - (collisionDistance * (-Dx/D) + (float).1); //-Dx/D = sin(theta)
            mBallY = pos2y + (collisionDistance * ( Dy/D) + (float).1); // Dy/D = cos(theta)

            //Copmonents of proper collision derived from long derivation
            mBallSpeedX = (float)( (-1/Math.pow(D,2)) * ( (Math.pow(Dx,2)-Math.pow(Dy,2))*Vx - (2.*Dx*Dy*Vy) ) );
            mBallSpeedY = (float)( (1/Math.pow(D,2)) * ( (Math.pow(Dy,2)-Math.pow(Dx,2))*Vy - (2.*Dx*Dy*Vx) ) );


            //Old method of handling collisions with faulty physics
            /*
            //Calculate absolute velocity of ball
            float ballSpeed = calcMagnitude(mBallSpeedX,mBallSpeedY);

            //Set Velocities in correct direction
            mBallSpeedX = mBallX - pos2x;
            mBallSpeedY = mBallY - pos2y;

            float newBallSpeed = calcMagnitude(mBallSpeedX, mBallSpeedY);

            //Adjust velocities to conserve total speed
            mBallSpeedX *= (ballSpeed / newBallSpeed);
            mBallSpeedY *= (ballSpeed / newBallSpeed);
            */


            //update game score with
            updateScore(pointVal);
            //adjust ball speed as a function of point value
            if (pointVal!=0){
                updateSpeed(pointVal);
            }


        }
    }

    private void updateSpeed(int pointVal){
        float CurrentSpeed = calcMagnitude(mBallSpeedX,mBallSpeedY);
        float newSpeed = CurrentSpeed + pointVal * (CurrentSpeed/10);
        mBallSpeedX *= newSpeed/CurrentSpeed;
        mBallSpeedY *= newSpeed/CurrentSpeed;

    }
}

// This file is part of the course "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist
// It is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// It is is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// 
// You should have received a copy of the GNU General Public License
// along with it.  If not, see <http://www.gnu.org/licenses/>.
