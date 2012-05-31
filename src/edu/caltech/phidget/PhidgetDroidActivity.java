package edu.caltech.phidget;

import com.phidgets.*;
import com.phidgets.event.*;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PhidgetDroidActivity extends Activity{
    /** Called when the activity is first created. */
    
	OurView v;
	
	public float ch1_data, ch1_data1,ch1_data2;
	public float var=0,var1=0,var2=0;
	
	public int i=0,j;
	public int x=0;
	public float z;
	
	private SpatialPhidget spatial;
	
	String out;
	
	boolean flag = false;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v= new OurView(this);
        setContentView(v);
        
        com.phidgets.usb.Manager.Initialize(this);
        
        
        try {
			spatial = new SpatialPhidget();			
			spatial.addAttachListener(new AttachListener() {
				
				@Override
				public void attached(AttachEvent ae) {
					// TODO Auto-generated method stub
					try {
						((SpatialPhidget)ae.getSource()).setDataRate(4);
					} catch (PhidgetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			spatial.addSpatialDataListener(new SpatialDataListener() {
				
				@Override
				public void data(SpatialDataEvent sde) {
					// TODO Auto-generated method stub
					for(j=0;j<sde.getData().length;j++)
					{
						if(sde.getData()[j].getAcceleration().length>0)
						{
							out = "Acceleration: ";
							for(i=0;i<sde.getData()[j].getAcceleration().length;i++){
								ch1_data = 700 - (float)((sde.getData()[j].getAcceleration()[0])*10);
								ch1_data1 = 500 - (float)((sde.getData()[j].getAcceleration()[1])*10);
								ch1_data2 = 300 - (float)((sde.getData()[j].getAcceleration()[2])*10);
								out = out + sde.getData()[j].getAcceleration()[i] + ((i==sde.getData()[j].getAcceleration().length-1)?"":",");
							}
						}
						;
						//Log.d(TAG, out);
						
					}
				}
			});			
			spatial.openAny();
		} catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		v.pause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		v.resume();
	}
	
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		com.phidgets.usb.Manager.Uninitialize();
	}

	public class OurView extends SurfaceView implements Runnable
    {

		Thread t = null;
		SurfaceHolder holder;
		String accelerometer;
		
		private final int width =805;
		private final int heigth = 280*4;
		private Paint grid_paint = new Paint();
		private Paint cross_paint = new Paint();
		private Paint outline_paint = new Paint();
		private Paint ch_1 = new Paint();
		private Paint ch_2 = new Paint();
		private Paint ch_3 = new Paint();
		
		
		public OurView(Context context) {
			super(context);
			holder = getHolder();
		}

		
		public void callDraw(Canvas canvas)
		{
			grid_paint.setColor(Color.rgb(100, 100, 100));
			cross_paint.setColor(Color.rgb(70, 100, 70));
			outline_paint.setColor(Color.GREEN);
			ch_1.setColor(Color.RED);
			ch_2.setColor(Color.BLUE);
			ch_3.setColor(Color.GREEN);
			for(int vertical = 1; vertical<10; vertical++){
		    	canvas.drawLine(
		    			vertical*(width/10)+1, 1,
		    			vertical*(width/10)+1, heigth+1,
		    			grid_paint);
		    }	    	
		    for(int horizontal = 1; horizontal<10; horizontal++){
		    	canvas.drawLine(
		    			1, horizontal*(heigth/10)+1,
		    			width+1, horizontal*(heigth/10)+1,
		    			grid_paint);
		    }	    
		    
		    
		    canvas.drawLine(0, (heigth/2)+1, width+1, (heigth/2)+1, cross_paint);
			canvas.drawLine((width/2)+1, 0, (width/2)+1, heigth+1, cross_paint);
			
			// draw outline
			canvas.drawLine(0, 0, (width+1), 0, outline_paint);	// top
			canvas.drawLine((width+1), 0, (width+1), (heigth+1), outline_paint); //right
			canvas.drawLine(0, (heigth+1), (width+1), (heigth+1), outline_paint); // bottom
			canvas.drawLine(0, 0, 0, (heigth+1), outline_paint); //left
		}
		
		
		public void run() {
			// TODO Auto-generated method stub
			while(flag==true)
			{
				if(!holder.getSurface().isValid())
				{
					continue;
				}
				Canvas canvas = holder.lockCanvas();
				callDraw(canvas);
				
				
				{	
					if(x+5>(240*3))
						{
							canvas.drawColor(Color.BLACK);
							callDraw(canvas);
							if(x>721)
							x=0;	
							var=0;
							var1=0;
							var2=0;
							x++;
						}
					else
					{
					canvas.drawLine(x, var, x+1, ch1_data, ch_1);
					canvas.drawLine(x, var1, x+1, ch1_data1, ch_2);
					canvas.drawLine(x, var2, x+1, ch1_data2, ch_3);
					var = ch1_data;
					var1 = ch1_data1;
					var2 = ch1_data2;
					x+=1;
					}
					
					
			}
				holder.unlockCanvasAndPost(canvas);
		}
	}
		public void pause()
		{
			flag= false;
			while(true)
			{
				try{
				t.join();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				break;
			}
			t=null;
		}
		public void resume()
		{
			flag= true;
			t=new Thread(this);
			t.start();
		}
    	
    }
}