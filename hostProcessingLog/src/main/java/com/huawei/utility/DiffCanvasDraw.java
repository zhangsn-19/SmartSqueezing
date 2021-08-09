package huawei.utility;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import huawei.shared.CapData;
import huawei.shared.CmdOper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.huawei.hostprocessinglog.THPActivity;
import com.huawei.utility.THPDiffData;


import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.rgb;

//添加
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DiffCanvasDraw extends View {
	private Paint mpaint = new Paint();
	private boolean israw = false;
	private int[][] arrOne = null;
	private int GETX = 0;
	private int GETY = 0;
	private int[] AccXp = null;
	private int[] AccXn = null;
	private int[] AccYp = null;
	private int[] AccYn = null;
	private boolean drawEdge = false;
	public int rawBase = 0;
	public int step=1;
	public int ifdrawnum=1;

	public THPActivity father;

	public PrintWriter pw = null;
	public float xx =0;
	public float yy =0;

	private void init(int[][] initdata) {
		arrOne = initdata;
		GETX = arrOne[0].length;
		GETY = arrOne.length;

		AccXp = new int[GETX];
		AccXn = new int[GETX];
		AccYp = new int[GETY];
		AccYn = new int[GETY];
	}

	public DiffCanvasDraw(Context context, int[][] initdata) {
		super(context);
		father = (THPActivity) context;
		init(initdata);

	}

	public void updateData(int[][] data, boolean israw) {
		arrOne = data;
		this.israw = israw;
	}

	//初始坐标 光标（lx, ly, ry）
	float lx = 80 ;
	float ly = 200;
	float cursor_len = 90;

	Paint b = new Paint();
	Paint c = new Paint();

	float ux, dx, mx=1200;
	float width, height;
	float L, R, U, D;
	int slidingWindowNumber;
	float alpha = 0.4f;
	float beta = 0.7f;

	float count = 0;
	int can_move = 0;
	int can_step = 0;
	int can_move_bar1=0;
	int can_move_bar2=0;
	int can_move_bar3=0;

	int flag = 0;
	boolean startSignal = false;
	public int mode=1;
	public int bar_mode = 0;
	public int move_mode = 0;
	public int v_mode = 0;
	public int f1 = 0;


//	可拖动bar
	float bar1 = 300, bar2 = 1800;


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (israw) {
			canvas.drawColor(rgb(0, 0, 0));//黑色
		} else {
			canvas.drawColor(rgb(255, 255, 255));//白色
		}

		Paint p = new Paint();
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		width = dm.widthPixels; // 屏幕宽（像素，如：1176px）
		height = dm.heightPixels; // 屏幕高（像素，如：2400px）
		System.out.print(width);

		//      文本编辑区域的边界
		L = 80;
		R = width - 80;
		U = 200;
		D = height - 200;

		// draw pressure bumping line
		p.setColor(Color.rgb(255, 150, 150));
		canvas.drawRect(L + 80,U + 830, R - 80, D - 830, p);

		CapData data;
		THPDiffData diff = new THPDiffData(CmdOper.getDiffData());
		data = diff.getData();
		int[][] array = data.getDataArray();
		int maxData = 0;
		int staticMaxData = 8000;
		for(int row = 0; row < array.length; ++row){
			for(int col = 0; col < array[0].length; ++col){
				if(array[row][col] > maxData) maxData = array[row][col];
			}
		}
		p.setColor(Color.RED);
		float dynamicRectUpperBound = U + 830;
		float dynamicRectLowerBound = D - 830;
		float dynamicRectLeftBound = L + 80;
		float staticRectRightBound = R - 40;
		float dynamicRectRightBound;
		if(maxData > staticMaxData) {
			dynamicRectRightBound = staticRectRightBound;
		}else {
			dynamicRectRightBound = (staticRectRightBound - dynamicRectLeftBound) *
					((float) (maxData * alpha + slidingWindowNumber * (1 - alpha)) / staticMaxData)
					+ dynamicRectLeftBound;
		}
		slidingWindowNumber = (int)(maxData * beta + slidingWindowNumber * (1 - beta));
		Paint pressureNumber = new Paint();
		pressureNumber.setTextSize(60);
		pressureNumber.setColor(rgb(0, 0, 0));
		if(startSignal){
			canvas.drawRect(
					dynamicRectLeftBound,
					dynamicRectUpperBound,
					dynamicRectRightBound,
					dynamicRectLowerBound, p);
			canvas.drawText(Integer.toString(maxData/100), 520, 1000, pressureNumber);
		} else {
			canvas.drawRect(
					dynamicRectLeftBound,
					dynamicRectUpperBound,
					dynamicRectLeftBound,
					dynamicRectLowerBound, p);
			canvas.drawText("Null", 520, 1000, pressureNumber);
		}

//      光标绘制
		/*p.setColor(Color.BLACK);// 设置红色
		p.setStrokeWidth(4);
		canvas.drawLine(lx, ly, lx, ly + cursor_len, p);*/

//      触发区域的边界

		//居中模式
		/*if(bar_mode==0)
			mx = (bar1+bar2)/2;
		ux = mx-80;
		dx = mx+70;*/

//      绘制触发区域
/*		b.setColor(Color.BLUE);//
		canvas.drawRect(width - 40, ux, width, dx, b);
		c.setColor(Color.BLUE);//
		canvas.drawRect(0, ux, 40, dx, c);*/

//		绘制控制条：
		/*Paint p2 = new Paint();
		p2.setColor(Color.GREEN);// 设置绿色
		p2.setStrokeWidth(20);
		canvas.drawLine(10, bar1, 70, bar1, p2);
		p2.setColor(Color.BLACK);//
		canvas.drawLine(10, bar2, 70, bar2, p2);*/

		drawbuttons(canvas);
	}


	public static float log(float basement, float n){
		return (float) (Math.log(n) / Math.log(basement));
	}

	int Lindex = -1;
	int Rindex = -1;
	int index_bar1 = -1;
	int index_bar2 = -1;
	int index_bar3 = -1;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		float nowx = event.getX();
		float nowy = event.getY();

		// start judgement
		if(nowx >= width/2 - 150 && nowy >= 40 && nowx <= width/2 + 90 && nowy <= 150) {
			startSignal = !startSignal;
		}

		switch (action){
			case MotionEvent.ACTION_DOWN:
				onbutton(event.getX(),event.getY());

				int index = event.getActionIndex();
				if (event.getY() > ux && nowy < dx && event.getX() > R) {//按到蓝色触发位置
					if(move_mode==0){ Rindex = index; can_move = 1; }
					else {Lindex = index; can_step = 1;}
				}
//				else if (f1==2 && click(nowx,nowy)==-4) {//触发下侧开关
//					Lindex = index;
//					can_step = 1;
//				}


				if (onbar(nowx,nowy)==1) {//上bar
					index_bar1 = index;
					can_move_bar1 = 1;
				} else if (onbar(nowx,nowy)==2) {//触发bar2
					index_bar2 = index;
					can_move_bar2 = 1;
				}else if(bar_mode==1&&onbar(nowx,nowy)==3){
					index_bar3 = index;
					can_move_bar3 = 1;
				}
				break;

			case MotionEvent.ACTION_POINTER_DOWN:
				onbutton(event.getX(),event.getY());
				index = event.getActionIndex();
				if (event.getY(index) > ux && event.getY(index) < dx && event.getX(index) > R) {//按到蓝色触发位置
					if(move_mode==0){ Rindex = index; can_move = 1; }
					else {Lindex = index; can_step = 1;}
				}
//				else if (event.getY(index) > ux && event.getY(index) < dx && event.getX(index) < L) {//触发左侧开关
//					Lindex = index;
//					can_step = 1;
//				}


				if (onbar(nowx,nowy)==1) {//上bar
					index_bar1 = index;
					can_move_bar1 = 1;
				} else if (onbar(nowx,nowy)==2) {//触发bar2
					index_bar2 = index;
					can_move_bar2 = 1;
				}else if(bar_mode==1&&onbar(nowx,nowy)==3){
					index_bar3 = index;
					can_move_bar3 = 1;
				}

				break;

			case MotionEvent.ACTION_MOVE:
				float x1,y1,x2,y2,distance;

				if (can_move_bar1 == 1 && index_bar1 >=0  ){
					bar1 = (event.getY(index_bar1)+400)<bar2? event.getY(index_bar1): bar1;
				}
				if (can_move_bar2 == 1 && index_bar2 >=0 ){
					bar2 = (event.getY(index_bar2)-400)>bar1? event.getY(index_bar2): bar2;
				}
				if(can_move_bar3 ==1&& index_bar3>=0){
					if(event.getY(index_bar3)<bar2-80 && event.getY(index_bar3)>bar1+80)
						mx = event.getY(index_bar3);
				}

				if (can_move == 1 && Rindex>=0 ) {
					x2 = event.getX(Rindex);
					y2 = event.getY(Rindex);
					y2 = y2<bar1? bar1:y2;
					y2 = y2>bar2? bar2:y2;
					//绝对距离
					if(v_mode==0) {
						distance = y2 - mx;//可以为负
					}
					//相对距离
					else {
						distance = (y2 - mx)/(bar2-bar1)*height;
					}


					if(mode == 1) {

						if(f1==11) {
							if (distance < 0 && x2 > R)
								lx -= distance * distance / 5000 - distance / 20;

							else if (distance > 0 && x2 > R)
								lx += distance * distance / 5000 + distance / 20;
						}

						if(f1==12){
							if (distance < 0 && x2 > R)
								lx += distance / 5;

							else if (distance > 0 && x2 > R)
								lx += distance / 5;
						}

						if(f1==13){
							if (distance < 0 && x2 > R) {
								distance = (float) log(2, -distance);
								lx -= distance * 10;
							}
							else if (distance > 0 && x2 > R) {
								distance = (float) log(2, distance);
								lx += distance * 10;
							}
						}

						if(f1==14){
							if (distance < 0 && x2 > R) {
								float x = -distance;
								if(x<600) lx -= x/5;
								else lx -= 180* Math.exp((x-1000)/1000);
							}
							else if (distance > 0 && x2 > R) {
								if(distance<600) lx += distance/5;
								else lx += 180* Math.exp((distance-1000)/1000);
							}
						}

						if(f1==15){
							if (distance < 0 && x2 > R) {
								float x = -distance;
								if(x<600) lx -= distance * distance / 5000 - distance / 20;
								else lx -= x/5;
							}
							else if (distance > 0 && x2 > R) {
								if(distance<600) lx += distance * distance / 5000 + distance / 20;
								else lx += distance/5;
							}
						}
					}

					//【bar1,bar2】映射位置
					if(mode == 0){
						y2 = y2 - bar1;
						if(y2<0) y2=0;
						if(y2>(bar2-bar1)) y2=bar2-bar1;
						float dis = y2/(bar2-bar1) * (R-L) ;

//						x-y模式
						if(f1==2) {
							lx = dis + L;
						}

						if(f1==3){
							dis = dis * (int)((D-U)/100);
							ly = U + (int)(dis/(R-L))*100;
							lx = dis - (ly-U)/100*(R-L);
							if(dis == 0){
								lx=L;
							}
						}

					}
					//              换行机制
					if (lx > R) { lx = L; ly += 100; }
					else if (lx < L) { lx = R; ly -= 100; }

					//              判断是否超出编辑区域
					if (ly < U) { lx = R; ly = U; }
					else if (ly > D) { lx = L; ly = D; }
//					invalidate();
				}

				if (can_step == 1 && Lindex >=0) {
					x1 = event.getX(Lindex);
					y1 = event.getY(Lindex);
					distance = Math.abs(y1 - mx);//可以为负
					count += 1 + distance / 800;

					if (count > 10) {
						if (y1 - mx < 0 && x1 < L) {
							ly -= 100; count = 0;
						} else if (y1 - mx > 0 && x1 < L) {
							ly += 100; count = 0;
						}
						count = 0;
					}
					//判断是否超出编辑区域
					if (ly < U) ly = U;
					else if (ly > D) ly = D;

					if(f1==2){
						y1 = y1 - bar1;
						if(y1<0) y1=0;
						if(y1>(bar2-bar1)) y1=bar2-bar1;
						float dis = y1/(bar2-bar1) * (D-U)/100 ;
						ly = U + (int)(dis)*100;
					}
//					invalidate();
				}
				break;

			case MotionEvent.ACTION_UP:
				can_move = 0;
				can_step = 0;
				Lindex = -1;
				Rindex = -1;
				index_bar1 = -1;
				index_bar2 = -1;
				index_bar3 = -1;
				can_move_bar1=0;
				can_move_bar2=0;
				can_move_bar3=0;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				Log.v("YX", "ACTION_POINTER_UP!!");
				index = event.getActionIndex();
				Log.v("YX","index=:"+Integer.toString(index));
				if(index == Lindex) {
					can_step = 0;
					Lindex = -1;
					if(Lindex < Rindex) Rindex --;
					if(Lindex < index_bar1) index_bar1--;
					if(Lindex < index_bar2) index_bar2--;
					if(Lindex<index_bar3) index_bar3--;

				}
				else if(index == Rindex){
					can_move = 0;
					Rindex = -1;
					if(Rindex < Lindex) Lindex--;
					if(Rindex < index_bar1) index_bar1--;
					if(Rindex < index_bar2) index_bar2--;
					if(Rindex<index_bar3) index_bar3--;
				}
				else if(index==index_bar1){
					can_move_bar1 = 0;
					index_bar1 = -1;
					if(index_bar1<Rindex) Rindex--;
					if(index_bar1<Lindex) Lindex--;
					if(index_bar1<index_bar2) index_bar2--;
					if(index_bar1<index_bar3) index_bar3--;
				}
				else if(index==index_bar2){
					can_move_bar2 = 0;
					index_bar2 = -1;
					if(index_bar2<Rindex) Rindex--;
					if(index_bar2<Lindex) Lindex--;
					if(index_bar2<index_bar1) index_bar1--;
					if(index_bar2<index_bar3) index_bar3--;
				}
				else if(index==index_bar3){
					can_move_bar3 = 0;
					index_bar3 = -1;
					if(index_bar3<Rindex) Rindex--;
					if(index_bar3<Lindex) Lindex--;
					if(index_bar3<index_bar1) index_bar1--;
					if(index_bar3<index_bar2) index_bar2--;
				}

				else{
					if(index<Rindex) Rindex--;
					if(index<Lindex) Lindex--;
					if(index<index_bar2) index_bar2--;
					if(index<index_bar1) index_bar1--;
					if(index<index_bar3) index_bar3--;
				}
				break;

			default:
				break;
		}
		return true;
	}

	private int  onbar(float x, float y){
		if(flag == 0 && x>=0 && x<=80 && y>=bar1-40 && y<=bar1+40){
			return 1;
		}
		if(flag == 0 && x>=0 && x<=80 && y>=bar2-40 && y<=bar2+40){
			return 2;
		}
		if(flag == 0 && x>=0 && x<=50 && y>=ux && y<=dx){
			return 3;
		}
		flag = 0;
		return 0;
	}

	private int click(float x,float y){
		if(x>=170&&x<=450&&y>=0&&y<=170)
			return 1;
		if(x>=470&&y>=0&&x<=1100&&y<=170)
			return -2;
		if(x>=170&&x<=400&&y>=2220&&y<=2400)
			return -3;
		if(x>=770&&x<=1070&&y>=2220&&y<=2400) //触发切换x-y
			return -4;
		if(x>=450&&x<=730&&y>=2220&&y<=2400)
			return -5;
		if(mode==1&&x>=240&&y>=300&&x<=940&&y<=500)
			return 11;
		if(mode==1&&x>=240&&y>=600&&x<=940&&y<=800)
			return 12;
		if(mode==1&&x>=240&&y>=900&&x<=940&&y<=1100)
			return 13;
		if(mode==1&&x>=240&&y>=1200&&x<=940&&y<=1400)
			return 14;
		if(mode==1&&x>=240&&y>=1500&&x<=940&&y<=1700)
			return 15;

		if(mode==0&&x>=240&&y>=300&&x<=940&&y<=500)
			return 2;
		if(mode==0&&x>=240&&y>=600&&x<=940&&y<=800)
			return 3;
//		if(mode==0&&x>=240&&y>=900&&x<=940&&y<=1100)
//			return 4;
		return 0;
	}

	public void onbutton(float x,float y){
		if(click(x,y)==1)	{
			mode = 1-mode;
			f1=0;
		}
		if(click(x,y)==-3){
			bar_mode =1-bar_mode;
		}
		if(click(x,y)==-4 && f1==2){
			move_mode=1-move_mode;
		}
		if(click(x,y)==-5 && mode == 1){
			v_mode = 1-v_mode;
		}

		if(click(x,y)==-2)	f1=0;

		if(click(x,y)==11)	f1=11;
		if(click(x,y)==12)	f1=12;
		if(click(x,y)==13)	f1=13;
		if(click(x,y)==14)	f1=14;
		if(click(x,y)==15)	f1=15;

		if(click(x,y)==2)	f1=2;
		if(click(x,y)==3)	f1=3;
		if(click(x,y)==4)	f1=4;

	}

    private int valuetoColor(int val) {//调整原始数据大小转换成颜色
		int MaxVal = 1800;
		int MinVal = -1500;
		int BaseVal = 0;

		if (val >= BaseVal) {//大于0
			if (val > MaxVal) {//大于最大值1800
				val = MaxVal;
			}
			val = ((val - BaseVal) * 128) / (MaxVal - (BaseVal + 1));
			val += 127;
		} else {
			if (val < MinVal) {
				val = MinVal;
			}
			val = ((val - MinVal) * 128) / (BaseVal - (MinVal + 1));
		}
		int r = val;
		int g = val > 127 ? 2 * (255 - val) : 2 * val;
		int b = 255 - val;
		return rgb(r, b, g);
	}
	private void setColor(Paint mpaint, int value) {//数字范围对应的颜色
		if ((value >= 0) && (value < 30)) {
			mpaint.setColor(rgb(0xBD, 0xBD, 0xBD));
		} else if ((value >= 30)) {
			mpaint.setColor(rgb(0xFF, 0xEB, 0x3B));
		} else if ((value < 0) && (value > -30)) {
			mpaint.setColor(rgb(0xC5, 0xCA, 0xE9));
		} else if ((value <= -30)) {
			mpaint.setColor(rgb(0x28, 0x35, 0x93));
		}
		mpaint.setColor(valuetoColor(value));
	}

	void drawbuttons(Canvas canvas){

		//bar 模式
		/*Paint b1= new Paint();
		b1.setColor(Color.rgb(192,192,192));
		canvas.drawRect(170,2220,400,2400,b1);
		b1.setStyle(Paint.Style.STROKE);
		b1.setStrokeWidth(5);
		b1.setColor(rgb(255,255,255));
		canvas.drawRect(170,2220,400,2400,b1);
*/
/*
		Paint z1 = new Paint();
		z1.setTextSize(60);//设置字体大小
		z1.setColor(rgb(105,105,105));
		if(bar_mode == 0) canvas.drawText("居中",220,2300,z1);
		if(bar_mode == 1) canvas.drawText("拖动",220,2300,z1);
*/
		Paint pressureText = new Paint();
		pressureText.setTextSize(60);
		pressureText.setColor(rgb(0, 0, 0));
		canvas.drawText("压力条:", 220, 1000, pressureText);
/*
		//切换x-y模式
		Paint b2= new Paint();
		b2.setColor(Color.rgb(192,192,192));
		canvas.drawRect(770,2220,1070,2400,b2);
		b2.setStyle(Paint.Style.STROKE);
		b2.setStrokeWidth(5);
		b2.setColor(rgb(255,255,255));
		canvas.drawRect(770,2220,1070,2400,b2);
		Paint z2 = new Paint();
		z2.setTextSize(60);//设置字体大小
		z2.setColor(rgb(105,105,105));
		if(move_mode == 0) canvas.drawText("水平方向",800,2300,z2);
		if(move_mode == 1)canvas.drawText("竖直方向",800,2300,z2);
*/

/*
		//切换速度模式（相对/绝对）
		Paint b3= new Paint();
		b3.setColor(Color.rgb(192,192,192));
		canvas.drawRect(450,2220,730,2400,b3);
		b3.setStyle(Paint.Style.STROKE);
		b3.setStrokeWidth(5);
		b3.setColor(rgb(255,255,255));
		canvas.drawRect(450,2220,730,2400,b3);
*/

/*
		Paint z3 = new Paint();
		z3.setTextSize(60);//设置字体大小
		z3.setColor(rgb(105,105,105));
		if(v_mode == 0) canvas.drawText("绝对速度",470,2300,z3);
		if(v_mode == 1)canvas.drawText("相对速度",470,2300,z3);
*/



/*
		//切换模式按钮
		Paint chan = new Paint();
		chan.setColor(Color.rgb(192,192,192));
		canvas.drawRect(170,0,450,170,chan);
		chan.setStyle(Paint.Style.STROKE);
		chan.setStrokeWidth(5);
		chan.setColor(rgb(255,255,255));
		canvas.drawRect(470,0,750,170,chan);
		Paint d = new Paint();
		d.setTextSize(60);//设置字体大小
		d.setColor(rgb(105,105,105));
		if(mode == 0) canvas.drawText("位置",240,110,d);
		if(mode == 1)canvas.drawText("速度",240,110,d);
*/

/*
		//当前函数按钮
		Paint chan1 = new Paint();
		chan1.setColor(Color.rgb(192,192,192));
		canvas.drawRect(470,0,1100,170,chan1);
		chan1.setStyle(Paint.Style.STROKE);
		chan1.setStrokeWidth(5);
		chan1.setColor(rgb(255,255,255));
		canvas.drawRect(470,0,1100,170,chan1);
*/

/*
		Paint chan2 = new Paint();
		chan2.setTextSize(60);//设置字体大小
		chan2.setColor(rgb(105,105,105));

		if(f1==0)   canvas.drawText("函数：空",490,110,chan2);
		if(f1==11)   canvas.drawText("v = x^2/5000 + x/20",490,110,chan2);
		if(f1==12)   canvas.drawText("v = x/5",490,110,chan2);
		if(f1==13)   canvas.drawText("v = 10 * log(2,x)",490,110,chan2);
		if(f1==14) {
			chan2.setTextSize(38);//设置字体大小
			canvas.drawText("v=x/5				  ,x<=600", 490, 50, chan2);
			canvas.drawText("v=180*exp((x-1000)/1000),x>600", 490, 120, chan2);
		}
		if(f1==15) {
			chan2.setTextSize(38);//设置字体大小
			canvas.drawText("v=x^2/5000 + x/20 , x<=600", 490, 50, chan2);
			canvas.drawText("v=x/5			, x>600", 490, 120, chan2);
		}

		if(f1==3)   canvas.drawText("d = kx (一维模式) ",490,110,chan2);
		if(f1==4)   canvas.drawText("函数：",490,110,chan2);
*/
		Paint startSignalStroke = new Paint();
		startSignalStroke.setColor(rgb(255, 200, 200));
		canvas.drawRect(width/2 - 150, 40, width/2 + 90, 150, startSignalStroke);
		startSignalStroke.setTextSize(60);//设置字体大小
		startSignalStroke.setColor(rgb(0, 0, 0));
		if(!startSignal)
			canvas.drawText("开始",width/2 - 100,110, startSignalStroke);
		else
			canvas.drawText("结束",width/2 - 100, 110, startSignalStroke);


/*
		//速度
		if(mode == 1 && f1 == 0){
			//1
			Paint mid = new Paint();
			mid.setColor(rgb(192,192,192));
			canvas.drawRect(240,300,940,500,mid);
			mid.setStyle(Paint.Style.STROKE);
			mid.setStrokeWidth(5);
			mid.setColor(rgb(255,255,255));
			canvas.drawRect(240,300,940,500,mid);
*/

/*
			//2
			Paint we = new Paint();
			we.setColor(Color.rgb(192,192,192));
			canvas.drawRect(240,600,940,800,we);
			we.setStyle(Paint.Style.STROKE);
			we.setStrokeWidth(5);
			we.setColor(rgb(255,255,255));
			canvas.drawRect(240,600,940,800,we);
*/

/*
			//3
			Paint del = new Paint();
			del.setColor(Color.rgb(192,192,192));
			canvas.drawRect(240,900,940,1100,del);
			del.setStyle(Paint.Style.STROKE);
			del.setStrokeWidth(5);
			del.setColor(rgb(255,255,255));
			canvas.drawRect(240,900,940,1100,del);
*/

/*
			//4
			Paint p4 = new Paint();
			p4.setColor(Color.rgb(192,192,192));
			canvas.drawRect(240,1200,940,1400,p4);
			p4.setStyle(Paint.Style.STROKE);
			p4.setStrokeWidth(5);
			p4.setColor(rgb(255,255,255));
			canvas.drawRect(240,1200,940,1400,p4);
*/

/*
			Paint p5 = new Paint();
			p5.setColor(Color.rgb(192,192,192));
			canvas.drawRect(240,1500,940,1700,p5);
			p5.setStyle(Paint.Style.STROKE);
			p5.setStrokeWidth(5);
			p5.setColor(rgb(255,255,255));
			canvas.drawRect(240,1500,940,1700,p5);
*/

/*
			//
			Paint a = new Paint();
			a.setTextSize(60);//设置字体大小
			a.setColor(rgb(105,105,105));
			canvas.drawText("v = x^2/5000 + x/20",260,420,a);
			//
*/
/*
			Paint b = new Paint();
			b.setTextSize(60);//设置字体大小
			b.setColor(rgb(105,105,105));
			canvas.drawText("v = x/5",260,720,b);
			//
*/
/*
			Paint c = new Paint();
			c.setTextSize(60);//设置字体大小
			c.setColor(rgb(105,105,105));
			canvas.drawText("v = 10 * log(2,x)",260,1020,c);
			//
*/
/*
			Paint c4 = new Paint();
			c4.setTextSize(40);//设置字体大小
			c4.setColor(rgb(105,105,105));
			canvas.drawText("v = x/5    				  , x<=600",260,1260,c4);
			canvas.drawText("v = 180*exp((x-1000)/1000) , x>600",260,1360,c4);
*/

/*
			Paint c5 = new Paint();
			c5.setTextSize(40);//设置字体大小
			c5.setColor(rgb(105,105,105));
			canvas.drawText("v = x^2/5000 + x/20    	 , x<=600",260,1560,c5);
			canvas.drawText("v = v = x/5 , 	x>600",260,1660,c5);
*/

		/*}*/
		if(mode == 0 && f1 == 0){
			//1
			Paint mid = new Paint();
			mid.setColor(rgb(192,192,192));
			canvas.drawRect(240,300,940,500,mid);
			mid.setStyle(Paint.Style.STROKE);
			mid.setStrokeWidth(5);
			mid.setColor(rgb(255,255,255));
			canvas.drawRect(240,300,940,500,mid);

			//2
			Paint we = new Paint();
			we.setColor(Color.rgb(192,192,192));
			canvas.drawRect(240,600,940,800,we);
			we.setStyle(Paint.Style.STROKE);
			we.setStrokeWidth(5);
			we.setColor(rgb(255,255,255));
			canvas.drawRect(240,600,940,800,we);

//			//3
//			Paint del = new Paint();
//			del.setColor(Color.rgb(192,192,192));
//			canvas.drawRect(240,900,940,1100,del);
//			del.setStyle(Paint.Style.STROKE);
//			del.setStrokeWidth(5);
//			del.setColor(rgb(255,255,255));
//			canvas.drawRect(240,900,940,1100,del);


			//
			Paint a = new Paint();
			a.setTextSize(60);//设置字体大小
			a.setColor(rgb(105,105,105));
			canvas.drawText("d = kx (x-y模式)",260,420,a);
			//
			Paint b = new Paint();
			b.setTextSize(60);//设置字体大小
			b.setColor(rgb(105,105,105));
			canvas.drawText("d = kx (一维模式)",260,720,b);

		}


	}

	private void drawEdgeNum(Canvas canvas) {
		// line color
		mpaint.setColor(rgb(255, 255, 255));
		canvas.drawLine((GETX * (float) getWidth() / (GETX + 2)), 0, (GETX
				* (float) getWidth() / (GETX + 2)), getHeight(), mpaint);
		canvas.drawLine(0, GETY * (float) getHeight() / (GETY + 2), getWidth(),
				GETY * (float) getHeight() / (GETY + 2), mpaint);
		// show acc data X&Y
		for (int ii = 0; ii < GETY; ii++) {
			int tmpData = AccYp[ii];
			setColor(mpaint, tmpData);
			canvas.drawText(String.format("%03d", tmpData), 6 + (GETX
					* (float) getWidth() / (GETX + 2)), (ii + (float) 0.8)
					* getHeight() / (GETY + 2), mpaint);
			tmpData = AccYn[ii];
			setColor(mpaint, tmpData);
			canvas.drawText(String.format("%03d", tmpData), 6 + ((GETX + 1)
					* (float) getWidth() / (GETX + 2)), (ii + (float) 0.8)
					* getHeight() / (GETY + 2), mpaint);
		}
		for (int jj = 0; jj < GETX; jj++) {
			int tmpData = AccXp[jj];
			setColor(mpaint, tmpData);
			canvas.drawText(String.format("%03d", tmpData), 2 + (jj
					* (float) getWidth() / (GETX + 2)), ((GETY + 1))
					* (float) getHeight() / (GETY + 2), mpaint);
			tmpData = AccXn[jj];
			setColor(mpaint, tmpData);
			canvas.drawText(String.format("%03d", tmpData), 2 + (jj
					* (float) getWidth() / (GETX + 2)), ((GETY + 2))
					* (float) getHeight() / (GETY + 2), mpaint);
		}
	}
}
