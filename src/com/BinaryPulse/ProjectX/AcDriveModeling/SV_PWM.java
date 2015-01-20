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

public class SV_PWM {
	
	public final int INDEXT_OFFSET =1; 

	protected float Tc,Tsimu;//Carrier Frequency
	protected float t0,t7,t1,t2;
	protected float tCmpA,tCmpB,tCmpC;
	protected float tSimuStep;//simulation step time
	protected float[] tFrac=new float[4];
	protected float[] NumofSimStep=new float[4];// num of simulation step for each vector
	protected float[] Ualph =new float[4]; //  Ualph for each vector
	protected float[] Ubeta =new float[4]; //  Ubeta for each vector
	protected float[] Uu =new float[4]; //  Ubeta for each vector
	
	protected float UsAmp,ThetaUs,ThetaUsMod,Udc;
	protected int PeroidOrZeroFlag;
    protected boolean PeroidOrZeroTrigger;
    
    protected final float SQRT2 = (float)java.lang.Math.sqrt(2.0);
    protected final float SQRT3 = (float)java.lang.Math.sqrt(3.0);
    protected final float SQRT2DIV3 = SQRT2/SQRT3;
    protected final float SQRT3DIV2 = SQRT3/SQRT2;
    protected final float SQRT1DIV3 = 1.0f/SQRT3;
    protected final float SQRT1DIV2 = 1.0f/SQRT2;
    
    protected final float PI =(float)java.lang.Math.PI;
    protected final float PI1DIV3 =PI/3.0f;

    		
    protected  final int  SectionToVector1[] = {4,6,2,3,1,5};
    protected  final int  SectionToVector2[] = {6,2,3,1,5,4};
    protected  final int  SectionSwip[] = {0,1,0,1,0,1};
    protected  final int  SectionToCmpAValidVect1[] = {0,0,1,1,1,0};
    protected  final int  SectionToCmpAValidVect2[] = {0,1,1,1,0,0};
    protected  final int  SectionToCmpBValidVect1[] = {1,0,0,0,1,1};
    protected  final int  SectionToCmpBValidVect2[] = {0,0,0,1,1,1};
    protected  final int  SectionToCmpCValidVect1[] = {1,1,1,0,0,0};
    protected  final int  SectionToCmpCValidVect2[] = {1,1,0,0,0,1};
    
    protected final float F1DIV3 = 1.0f/3.0f;
    protected final float F2DIV3 = 2.0f/3.0f;
    protected final float VectorToUuCof[] ={-F1DIV3,-F1DIV3,-F2DIV3, F2DIV3, F1DIV3, F1DIV3};
    protected final float VectorToUvCof[] ={-F1DIV3, F2DIV3, F1DIV3,-F1DIV3,-F2DIV3, F1DIV3};
    protected final float VectorToUwCof[] ={ F2DIV3,-F1DIV3, F1DIV3,-F1DIV3, F1DIV3,-F2DIV3};
    
    
	protected float[] VetorToUalphCof =new float[6]; //  Ualph for each vector
	protected float[] VetorToUbetaCof =new float[6]; //  Ubeta for each vector
    
	public  float[] yout = new float[4+INDEXT_OFFSET];
	public  float[] youtAlph = new float[4];
	public  float[] youtBeta = new float[4];
	
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
public SV_PWM(){//boolean AnimationEnabled ){
	
	SimuStepIndex =0;
	PeroidOrZeroTrigger =false;	
	UsAmp = ThetaUs =0;
	Udc =540;
	Tc =0.001f;
	Tsimu =0.0005f;
	InnerStepIndex =0;
	tout =0;
	for(int i =0;i<6;i++){
		VetorToUalphCof[i] = VectorToUuCof[i]+(float)java.lang.Math.cos(120.0f*PI/180.0f)*VectorToUvCof[i]+(float)java.lang.Math.cos(120.0f*PI/180.0f)*VectorToUwCof[i];
		VetorToUbetaCof[i] = (float)java.lang.Math.cos(30.0f*PI/180.0f)*VectorToUvCof[i]-(float)java.lang.Math.cos(30.0f*PI/180.0f)*VectorToUwCof[i];
		VetorToUalphCof[i] =VetorToUalphCof[i]*SQRT2DIV3;
		VetorToUbetaCof[i] =VetorToUbetaCof[i]*SQRT2DIV3;
	}
	
}


public void CalculateRealTimeData(int Timeindex){
	float freq,Amp,tempresult;
	float ThetaToVec1,ThetaToVec2;
	float AmpVec1,AmpVec2,FracVec1,FracVec2;
	int SectionIndex,NumofSimStepVect1,NumofSimStepVect2;
	int vector1,vector2;

	
	if(m_CmdRunState==false)
		return;
if(InnerStepIndex ==0){


	realtime = 0.5f* Tc * (SimuStepIndex - 1);
	freq =  realtime*48.0f/2.0f;
	if(freq >48.0f)
		freq =48.0f;
	UsAmp = 380.0f*freq/50.0f;
	//ThetaUs = ThetaUs + 0.5f* Tc *2*PI*freq;
	ThetaUs = ThetaUs + 0.5f* Tc *360.0f*freq;
	
	
	tempresult = Udc*SQRT1DIV2;
	if(UsAmp >=tempresult)
		UsAmp =tempresult;
	
	ThetaUsMod = (int)(ThetaUs%360.0f);//(int)(ThetaUs - (float)((int)(ThetaUs/360.0f))*360.0f);	
	SectionIndex = (int)(ThetaUsMod/60.0f);
	
	if(SectionIndex>=6)
		SectionIndex =0;
	
	ThetaToVec1 = ThetaUsMod - (float)(SectionIndex*60);
	ThetaToVec2 = 60.0f -ThetaToVec1;
	
	AmpVec1 = UsAmp*(float)java.lang.Math.sin(ThetaToVec2*PI/180.0f)/(float)java.lang.Math.sin(PI/3);	
	AmpVec2 = UsAmp*(float)java.lang.Math.sin(ThetaToVec1*PI/180.0f)/(float)java.lang.Math.sin(PI/3);
	
	t1 = AmpVec1/Udc*SQRT3DIV2*Tc*0.5f;
	t2 = AmpVec2/Udc*SQRT3DIV2*Tc*0.5f;
	t0 = t7 =Tc*0.25f-0.5f*(t1+t2);
	
	tCmpA = t1*SectionToCmpAValidVect1[SectionIndex] + t2*SectionToCmpAValidVect2[SectionIndex]+t0;
	tCmpB = t1*SectionToCmpBValidVect1[SectionIndex] + t2*SectionToCmpBValidVect2[SectionIndex]+t0;
	tCmpC = t1*SectionToCmpCValidVect1[SectionIndex] + t2*SectionToCmpCValidVect2[SectionIndex]+t0;
	
	NumofSimStep[0] = NumofSimStep[3]= 0;// (int)(t0/Tsimu);
	tFrac[0] = tFrac[3] =t0%Tsimu;// t0 -(float)NumofSimStep[0]*Tsimu;
	
	if(SectionSwip[SectionIndex] == 0){
		vector1 = SectionToVector1[SectionIndex]-1;
		vector2 = SectionToVector2[SectionIndex]-1;
		
		NumofSimStepVect1= (int)(t1/Tsimu);
		NumofSimStepVect2 = (int)(t2/Tsimu);		
		FracVec1 = t1%Tsimu;//t1 -(float)NumofSimStepVect1*Tsimu;
		FracVec2 = t2%Tsimu;//t2 -(float)NumofSimStepVect2*Tsimu;
		
		
	}
	else
	{
		vector2 = SectionToVector1[SectionIndex]-1;
		vector1 = SectionToVector2[SectionIndex]-1;		
		NumofSimStepVect1= (int)(t2/Tsimu);
		NumofSimStepVect2 = (int)(t1/Tsimu);		
		FracVec1 = t2%Tsimu;//t2 -(float)NumofSimStepVect1*Tsimu;
		FracVec2 = t1%Tsimu;//t1 -(float)NumofSimStepVect2*Tsimu;	
		
	}
	youtAlph[0] = youtAlph[3] =0;	
	youtBeta[0] = youtBeta[3] =0;
	Uu[0] = Uu[3] =0;
	
	if(PeroidOrZeroTrigger){
		youtAlph[1] = VetorToUalphCof[vector2]*Udc;
		youtAlph[2] = VetorToUalphCof[vector1]*Udc;
		youtBeta[1] = VetorToUbetaCof[vector2]*Udc;
		youtBeta[2] = VetorToUbetaCof[vector1]*Udc;	
		Uu[1] = VectorToUuCof[vector2]*Udc;
		Uu[2] = VectorToUuCof[vector1]*Udc;
		/*
		youtAlph[1] = VectorToUuCof[vector2]*Udc;
		youtAlph[2] = VectorToUuCof[vector1]*Udc;
	
		youtBeta[1] = VectorToUvCof[vector2]*Udc;
		youtBeta[2] = VectorToUvCof[vector1]*Udc;*/
		
		NumofSimStep[1] = 0;//NumofSimStepVect2;
		NumofSimStep[2] = 0;//NumofSimStepVect1;		
		tFrac[1]        = FracVec2;
		tFrac[2]        = FracVec1;
	}
	else{
		youtAlph[1] = VetorToUalphCof[vector1]*Udc;
		youtAlph[2] = VetorToUalphCof[vector2]*Udc;
		youtBeta[1] = VetorToUbetaCof[vector1]*Udc;
		youtBeta[2] = VetorToUbetaCof[vector2]*Udc;	
		Uu[1] = VectorToUuCof[vector1]*Udc;
		Uu[2] = VectorToUuCof[vector2]*Udc;
		/*
		youtAlph[1] = VectorToUuCof[vector1]*Udc;
		youtAlph[2] = VectorToUuCof[vector2]*Udc;
	
		youtBeta[1] = VectorToUvCof[vector1]*Udc;
		youtBeta[2] = VectorToUvCof[vector2]*Udc;*/	
		
		NumofSimStep[1] = 0;//NumofSimStepVect1;
		NumofSimStep[2] = 0;//NumofSimStepVect2;		
		tFrac[1]        = FracVec1;
		tFrac[2]        = FracVec2;		
	}
	
	
		
	SimuStepIndex++;
	ttemp =0;
}	

yout[0] = youtAlph[InnerStepIndex];
yout[1] = youtBeta[InnerStepIndex];
yout[2] = NumofSimStep[InnerStepIndex];
yout[3] = tFrac[InnerStepIndex];
//yout[4] = Uu[InnerStepIndex];


ttemp +=tFrac[InnerStepIndex];//(tFrac[InnerStepIndex]+NumofSimStep[InnerStepIndex]*Tsimu);
tout = (SimuStepIndex)*Tc*0.5f +ttemp;

InnerStepIndex++; 
if(InnerStepIndex==4)
{
	InnerStepIndex =0;
	if(PeroidOrZeroTrigger)
		PeroidOrZeroFlag= 100;
	else
		PeroidOrZeroFlag=-100;
	
	PeroidOrZeroTrigger =!PeroidOrZeroTrigger;	
}
/*	
	yout[0] = tCmpA*1000000.0f;
	yout[1] = tCmpB*1000000.0f;
	yout[2] = UsAmp;
	yout[3] = ThetaUsMod;
*/
     
}


public float[] getOutput(){
	
	return yout;
	
}

public float getTime(){
	
	return  tout;//(SimuStepIndex)*4*Tc;//*1000.0f;tout;//
	
}

public float getPeroidZeroTrigger(){
	  return  PeroidOrZeroFlag;
	
}

public float getUu(){
	  return  Uu[InnerStepIndex];
	
}
};