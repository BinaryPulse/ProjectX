package com.BinaryPulse.ProjectX.AcDriveModeling;
import java.lang.Math;
import java.text.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.opengl.GLES20;

import com.BinaryPulse.ProjectX.R;
import com.BinaryPulse.ProjectX.MyFont.MyFont;
import com.BinaryPulse.ProjectX.common.RawResourceReader;
import com.BinaryPulse.ProjectX.common.ShaderHelper;
import com.BinaryPulse.ProjectX.common.TextureHelper;

import android.opengl.Matrix;
import android.util.DisplayMetrics;

public class TorqCtrlLoop {
	
	public final int INDEXT_OFFSET =1; 

	protected float Tc,Tsimu;//Carrier Frequency

    
    protected final float SQRT2 = (float)java.lang.Math.sqrt(2.0);
    protected final float SQRT3 = (float)java.lang.Math.sqrt(3.0);
    protected final float SQRT2DIV3 = SQRT2/SQRT3;
    protected final float SQRT3DIV2 = SQRT3/SQRT2;
    protected final float SQRT1DIV3 = 1.0f/SQRT3;
    protected final float SQRT1DIV2 = 1.0f/SQRT2;
    
    protected final float PI =(float)java.lang.Math.PI;
    protected final float PI1DIV3 =PI/3.0f;

    		
	protected float Rs,	Rr, Ls, Lr, Lm, deltLs,Wsl,ThetaWsl,ThetaFlux,PreThetaFlux,Udc,UsRef,Utheta;
	protected float[] Kp= new float[2];
	protected float[] Ki= new float[2];
	protected float[] CtlLoopIntegral= new float[2];
	protected float[] CtlLoopOut= new float[2];
	protected float[] CtlLoopDeltIn= new float[2];
	protected float[] CtlLoopRefSet= new float[2];
	protected float[] CtlLoopFeedBk= new float[2];
	protected float[] UsInfo= new float[2];
	
	public  float[] yout = new float[4+INDEXT_OFFSET];

	
	protected static int SimuStepIndex;
	protected static int InnerStepIndex;
	public static float realtime,tout, ttemp;
    protected boolean m_CmdRunState =false;

/*##############################################################################
           
		         对象模块功能描述： OscilloScope（示波器）

###############################################################################*/
	public void Stop()
	{
		 m_CmdRunState  = false;

	}

	public void Start()
	{
		 m_CmdRunState  = true;

	}
	
	public boolean IsRun()
	{
		 return m_CmdRunState;

	}
/***********************************************************************************
子函数描述：DrawControlBorder(bool AnimationEnabled), 绘制示波器边框
************************************************************************************/
public TorqCtrlLoop(){//boolean AnimationEnabled ){
	
	SimuStepIndex =0;

	Tc =0.001f;
	Tsimu =0.0005f;
	InnerStepIndex =0;
	tout =0;
	Rs = 0.469f;//1.4f;//Ohm
	Rr = 0.372f;//0.8f;//Ohm
	Ls = 0.067f;//0.134f;//H
	Lr = 0.067f;//134f;//H	
	Lm = 0.064f;//0.124f;//H			
	deltLs = Ls -Lm*Lm/Lr;//H
	Kp[0] = Kp[1] =0.5f*deltLs/(Tc);
	Ki[0] = Ki[1] =0.5f* (Rs+Rr*Lr*Lr/Lm/Lm)/(Tc)*0.5f*Tc;
	CtlLoopIntegral[0] = CtlLoopIntegral[1] =0;
	ThetaFlux = ThetaWsl  = PreThetaFlux =0;
	Udc =540.0f;
}


public void CalculateRealTimeData(float FeedBkIn[],float RotroThetaIn,float RotroSpeedIn){//int Timeindex){

	float MaxOutput;
	MaxOutput = Udc*SQRT1DIV2;

	
	if(m_CmdRunState==false)
		return;
if(InnerStepIndex ==0){

    
	realtime = 0.5f* Tc * (SimuStepIndex - 1);

	if(realtime<=0.6f)
	{
		CtlLoopRefSet[1] =0.0f;
		CtlLoopRefSet[0] =10.0f*SQRT3;
	}
	else if(realtime<=0.8)
	{
		CtlLoopRefSet[1] =15.0f*SQRT3;
		CtlLoopRefSet[0] =10.0f*SQRT3;	
	}	
	else if(realtime<=1.0)
	{
		CtlLoopRefSet[1] =-15.0f*SQRT3;
		CtlLoopRefSet[0] =10.0f*SQRT3;	
	}	
	else if(realtime<=1.2)
	{
		CtlLoopRefSet[1] =-15.0f*SQRT3;
		CtlLoopRefSet[0] =10.0f*SQRT3;	
	}	
	else if(realtime<=1.4)
	{
		CtlLoopRefSet[1] =15.0f*SQRT3;
		CtlLoopRefSet[0] =10.0f*SQRT3;	
	}	
	else
	{
		CtlLoopRefSet[1] =0.0f;
		CtlLoopRefSet[0] =10.0f*SQRT3;
	}

	
	if(CtlLoopRefSet[0] == 0)
	{
		Wsl = 0;
	}
	else
	{
		Wsl = CtlLoopRefSet[1]/CtlLoopRefSet[0]/Lr*Rr;
	}

	ThetaWsl  += Wsl*0.5f*Tc;
	ThetaWsl  =  ThetaWsl%(2.0f*PI);
	
	//ThetaFlux = (PreThetaFlux )%(2.0f*PI);//;
	ThetaFlux = ThetaWsl+ RotroThetaIn+(RotroSpeedIn+Wsl)*0.75f*Tc;
	CtlLoopFeedBk[1] = -(float)java.lang.Math.sin(ThetaFlux)*FeedBkIn[0]+(float)java.lang.Math.cos(ThetaFlux)*FeedBkIn[1];
	CtlLoopFeedBk[0] = (float)java.lang.Math.cos(ThetaFlux)*FeedBkIn[0]+(float)java.lang.Math.sin(ThetaFlux)*FeedBkIn[1];	
	
	//PreThetaFlux = ThetaFlux;
	
	for(int i =0;i<2;i++){
		
		CtlLoopDeltIn[i] = CtlLoopRefSet[i] -CtlLoopFeedBk[i];
		
		CtlLoopOut[i] = Kp[i]*CtlLoopDeltIn[i];
		
		if(CtlLoopIntegral[i]<= MaxOutput && CtlLoopIntegral[i]>= -MaxOutput) 
		{
			CtlLoopIntegral[i] += Ki[i]*CtlLoopDeltIn[i];
		}
		CtlLoopOut[i] += CtlLoopIntegral[i];
		if(i ==1)
		{
			CtlLoopOut[i] +=(RotroSpeedIn+Wsl)*Lm*CtlLoopRefSet[0];
	
		}
		else
			CtlLoopOut[i] -=(RotroSpeedIn+Wsl)*deltLs*CtlLoopRefSet[1];
		
		if(CtlLoopOut[i] > MaxOutput)
			CtlLoopOut[i] = MaxOutput;
		
		if(CtlLoopOut[i] < -MaxOutput)
			CtlLoopOut[i] = -MaxOutput;		
		
	}
	UsRef =  (float)java.lang.Math.sqrt(CtlLoopOut[0]*CtlLoopOut[0] + CtlLoopOut[1]*CtlLoopOut[1]);
	
	if(UsRef > MaxOutput)
		UsRef= MaxOutput;
	

    if(CtlLoopOut[0]==0)
    {
    	Utheta =(float)java.lang.Math.atan(CtlLoopOut[1]/0.00001f);
    }
    else
    {
    	Utheta =(float)java.lang.Math.atan(CtlLoopOut[1]/CtlLoopOut[0]);
    }

    Utheta =Utheta+	ThetaFlux;
	SimuStepIndex++;
	ttemp =0;
}	

UsInfo[0] = UsRef;
UsInfo[1] = Utheta*180.0f/PI;

yout[0] = UsRef;
yout[1] = Utheta*180.0f/PI;
yout[2] = 0;
yout[3] = 0;

tout = (SimuStepIndex)*Tc*0.5f ;

InnerStepIndex++; 
if(InnerStepIndex==4)
{
	InnerStepIndex =0;
}
     
}


public float[] getOutput(){
	
	return yout;
	
}

public float[] getUsInfo(){
	
	return UsInfo;
	
}

public float getFBKIt(){
	
	return CtlLoopFeedBk[1];
	
}
public float getFBKIm(){
	
	return CtlLoopFeedBk[0];
	
}
public float getTime(){
	
	return  tout;
	
}


};