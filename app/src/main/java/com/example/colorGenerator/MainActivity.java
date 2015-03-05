package com.example.colorGenerator;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/*
App: Android Color Generator Example
Author: Brandy Lee Camacho
Date: March 4th, 2015
 */


public class MainActivity extends ActionBarActivity {
    String TAG = "ColorGenerator ";
    static LinearLayout ll;
    static boolean paused = false;
    static boolean customColor = false;
    static boolean colorRandom = false;
    static int taskCount = 1;
    static int colorCount = 0;
    private int displayColorCount = 5;
    // this context will use when we work with Alert Dialog
    final Context context = this;

    // Generate getters
    public int getDisplayRange() {
        return displayColorCount;
    }

    // Generate setters
    public void setDisplayRange(int displayRange) {
        if (displayRange > 250) {
            displayRange = 250;
            Toast.makeText(this, "Limit is 250", Toast.LENGTH_SHORT).show();
        }
        this.displayColorCount = displayRange;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.colorGenerator.R.layout.activity_main);
        ll = (LinearLayout) findViewById(R.id.llContentWindow);
        final Button addButton = (Button) findViewById(R.id.addButton);
        final Button removeButton = (Button) findViewById(R.id.removeButton);
        final Button pauseButton = (Button) findViewById(R.id.pauseButton);
        Thread genThread = new Thread(new GenerateColors());
        genThread.start();
/*
        When generating new threads often times you will find your self having the desire
        to pause the thread task. In the code below is an example on how to implement pause feature.

        Within your thread you would add the following code:

            while (paused == true) {
                                    try {
                                            Thread.sleep(1000);
                                    } catch (Exception e) {

                                    }
 */
        pauseButton.setVisibility(View.VISIBLE); // set to View.GONE to remove
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paused = !paused;
                if (paused) {
                    pauseButton.setText("Resume");
                    addButton.setText("Add " + taskCount);
                }
                Toast.makeText(getBaseContext(), "Use the \"add button\" \nto add Tasks to the que  ", Toast.LENGTH_LONG).show();
                if (!paused) {
                    pauseButton.setText("Pause");
                    addButton.setText("Add");
                    Thread genThread = new Thread(new GenerateColors());
                    genThread.start();
                }

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Adding a view will cause a LayoutTransition animation
                if (paused) {
                    if (taskCount * displayColorCount > 250) {
                        Toast.makeText(getBaseContext(), "Way too much data!\nSet your display count lower", Toast.LENGTH_SHORT).show();
                        displayColorCount = 250;
                        taskCount = 1;
                        paused = !paused;
                        addButton.setText("Add ");

                    } else {
                        taskCount++;
                        addButton.setText("Add " + taskCount);
                    }
                }
                if (!paused) {
                    Thread genThread = new Thread(new GenerateColors());
                    genThread.start();
                }
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll.getChildCount() > 0) {
                    Thread genThread = new Thread(new RemoveColors());
                    genThread.start();
                }
            }
        });

//        // Note that this assumes a LayoutTransition is set on the container, which is the
//        // case here because the container has the attribute "animateLayoutChanges" set to true
//        // in the layout file. You can also call setLayoutTransition(new LayoutTransition()) in
//        // code to set a LayoutTransition on any container.
        LayoutTransition transition = ll.getLayoutTransition();
//
//        // New capability as of Jellybean; monitor the container for *all* layout changes
//        // (not just add/remove/visibility changes) and animate these changes as well.
        transition.enableTransitionType(LayoutTransition.CHANGING);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            Thread genThreadRemove = new Thread(new RemoveAllColors());
            genThreadRemove.start();
            return true;
        }
        if (id == R.id.action_reset_color_count) {
            colorCount = 0;
            Thread genThreadRemove = new Thread(new RemoveAllColors());
            Thread genThreadAdd = new Thread(new GenerateColors());
            genThreadRemove.start();
            genThreadAdd.start();
            return true;
        }


        if (id == R.id.action_set_color_display_count) {
    /* Alert Dialog Code Start*/
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Input Amount to Display"); //Set Alert dialog title here
            alert.setMessage("example input \"10\" "); //Message here

            // Set an EditText view to get user input
            final EditText input = new EditText(context);
            input.setText(String.valueOf(displayColorCount));
            input.setInputType(InputType.TYPE_CLASS_NUMBER);

            alert.setView(input);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //You will get as string input data in this variable.
                    // here we convert the input to a string and convert to Interger.
                    String inputData = input.getEditableText().toString();

                    Thread genThreadAdd = new Thread(new GenerateColors());

                    if (inputData.isEmpty()) {
                        input.setText(String.valueOf(displayColorCount));
                        genThreadAdd.start();
                    } else {
                        setDisplayRange(Integer.valueOf(input.getEditableText().toString()));
                        genThreadAdd.start();

                    }
                } // End of onClick(DialogInterface dialog, int whichButton)
            }); //End of alert.setPositiveButton
            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    dialog.cancel();
                }
            }); //End of alert.setNegativeButton
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
       /* Alert Dialog Code End*/
            return true;
        }

        if (id == R.id.action_set_color) {
              /* Alert Dialog Code Start*/
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Input Color Code"); //Set Alert dialog title here
            alert.setMessage("example input \"130001\" for color Red "); //Message here

            // Set an EditText view to get user input
            final EditText input = new EditText(context);
            input.setText(String.valueOf(colorCount));
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            alert.setView(input);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //You will get as string input data in this variable.
                    // here we convert the input to a string and convert to Interger.
                    String inputData = input.getEditableText().toString();

                    Random rand = new Random();
                    Thread genThreadAdd = new Thread(new GenerateColors());

                    if (inputData.isEmpty()) {
                        colorCount = rand.nextInt(16581375);
                        colorRandom = !colorRandom;
                        genThreadAdd.start();
                        Log.e(TAG, String.valueOf("color was not set, default = " + colorCount));
                    } else {
                        colorCount = Integer.valueOf(input.getEditableText().toString());
                        customColor = !customColor;
                        genThreadAdd.start();
                        Log.e(TAG, String.valueOf(colorCount));

                    }
                } // End of onClick(DialogInterface dialog, int whichButton)
            }); //End of alert.setPositiveButton
            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    dialog.cancel();
                }
            }); //End of alert.setNegativeButton
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
       /* Alert Dialog Code End*/

            return true;

        }
        return super.onOptionsItemSelected(item);


    }

    private static class ColoredView extends View {
        Random rand = new Random();
        private boolean mExpanded = false;


        private LayoutParams mCompressedParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 50);

        private LayoutParams mExpandedParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 200);

        private ColoredView(final Context context) {
            super(context);

            /****  Color Generation for RGB should be in range of 255
             we use (int) to eliminates double with decimal point
             In addition by converting to  (int) we are not rounding
             up which keeps our number within range of 255

             Example 1: "Math.random()*10" = 8.26116116563447
             Example 2: "(int)(mathData)" = 8

             Removing decimal by converting to int = 8
             Notice that there is no rounding,
             this is important for keeping numbers within Range
             For example, you need to generate RGB color codes 255,255,255
             There will be an error if you round up to 256
             or by leaving the decimal 255.432453252
             */
            int red = (int) (Math.random() * 128 + 127);
            int green = (int) (Math.random() * 128 + 127);
            int blue = (int) (Math.random() * 128 + 127);
            int white = (int) (0xffffffff);


            if (colorRandom == true) {
                colorRandom = !colorRandom;
            } else if (customColor == true) {
                // set customColor Boolean to false
                customColor = !customColor;
            } else {
                colorCount = colorCount + rand.nextInt(10000);
            }

            /**
            final int color = 0xff << 24 | (red << 16) | (green << 8) | blue;
            0xff = 255
            0xff << 24 is a way to configure a 32bit float which can represent accurately is -16777216 to 16777216.
            Basically, to create green range of colors we need to
               - First we create a 32bit float -16777216
               - We use Math.random() to create a random decimal, i.e. '.49382745'
               - Multiply decimal 128 times
               - Add 127
                  *** Note, 128 + 127 = 255 (truly 256 when counting 0)
               - With a sigma sum of 8 (<< 8)
                  *** Note, sigma sum is a multiplication of itself 'n' times, also known as binary sum, i.e. 2<<8 ==> 2*2=4, 2*4=8, 2*8=16, 2*16=32. 2*32=64, 2*64=128, 2*128=256, 2*256=512
                    Example 1: "Math.random()*10" = 8.26116116563447
                    Example 2: "(int)(mathData)" = 8
                    Removing decimal by converting to int = 8
                    Notice that there is no rounding,
                    this is important for keeping numbers within Range
                    For exmaple, you need to generate RGB color codes 255,255,255
                    There will be an error if you round up to 256
                    or by leaving the decimal 255.432453252

             */

            final int color = colorCount * -1;
            setBackgroundColor(color);


            TextView myTv = new TextView(context);
            myTv.setText("Color code = " + color);
            myTv.setTextColor(color);
            myTv.setTypeface(null, Typeface.BOLD_ITALIC);
            ll.addView(myTv, 0);

            setLayoutParams(mCompressedParams);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), String.valueOf(color) + " | ", Toast.LENGTH_LONG).show();
                    // false boolean ? Params to be removed : Params to be replaced
                    setLayoutParams(mExpanded ? mCompressedParams : mExpandedParams);
                    mExpanded = !mExpanded;
                    requestLayout();
                }
            });
        }
    }

    public class RemoveColors implements Runnable {
        public RemoveColors() {
        }
        @Override
        public void run() {
            int max = displayColorCount * 2;
            int i;
            //perform operations outside user interface layout
            if (displayColorCount >= ll.getChildCount()) {
                for (i = 0; i < ll.getChildCount(); i++) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll.removeViewAt(Math.min(0, ll.getChildCount() - 1));
                        }
                    });
                }
            } else {
                for (i = 0; i < max; i++) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll.removeViewAt(Math.min(0, ll.getChildCount() - 1));
                        }
                    });

                }
            }
        }
    }


    public class GenerateColors implements Runnable {
        public GenerateColors() {
        }
        @Override
        public void run() {
            int max = displayColorCount;
            //perform operations outside user interface layout
            for (int i = 0; i < max; i++) {
                while (paused == true) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                }
                runOnUiThread(new Runnable() {
                    int count;
                    @Override
                    public void run() {
                        if (taskCount == 1) {
                            count = 0;
                        } else {
                            count = taskCount * displayColorCount;
                        }
                        for (int i = 0; i <= count; i++) {
                            ll.addView(new ColoredView(getBaseContext()), 1);
                        }
                        taskCount = 1;
                    }
                });
            }
        }
    }

    public class RemoveAllColors implements Runnable {
       public RemoveAllColors() {
       }
        @Override
        public void run() {
            //perform operations outside user interface layout
            for (int i = 1; i <= ll.getChildCount(); i++) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll.removeViewAt(Math.min(0, ll.getChildCount()));
                    }
                });
            }
        }
    }
}
