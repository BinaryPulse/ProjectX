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

public class AsychronousMotor {
	
	public final int INDEXT_OFFSET =1; 
	public final int STATE_VAR_NUM =5; 
	public final int OUTPUT_VAR_NUM =5; 
	protected float[][] Cof = new float[4+INDEXT_OFFSET][5+INDEXT_OFFSET];//4, 12), temp As Single
	protected float h;// As Single = 0.1
	protected float ts;// As Single = 20
	protected int   T ;// As Integer = 600
	protected  int  N, Ns;//As Long
	protected float Rs,	Rr, Ls, Lr, Lm, deltLs,Wr,P,J,TL,kL,Tc;
	
	protected float const_a11,const_a21,const_k1,const_ke,const_kl;
	
	protected float Theta;
	
	protected float[]   StateEqVar = new float[STATE_VAR_NUM];
	protected float[]   StateEqVarTemp = new float[STATE_VAR_NUM];
	protected float[][] DerivStateVar = new float[4][STATE_VAR_NUM];
	protected float[]  Input =new float[2];
	
	public  float[] yout = new float[OUTPUT_VAR_NUM];
	
	public  float Iu,Iv,Iw,Te,RotorTheta,Ialph,Ibeta,RotorSpeed;
	public  float Iu_out,Iv_out,Iw_out,Te_out,RotorTheta_out,RotorSpeed_out;
	public  float[]  Ialphbeta_out =new float[2];
	
	protected static int toutIndex,InnerNum;
	public static float realtime;
	public static float PreRealtime;
    protected boolean m_CmdRunState =false;
    protected final float SQRT2 = (float)java.lang.Math.sqrt(2.0);
    protected final float SQRT3 = (float)java.lang.Math.sqrt(3.0);
    protected final float SQRT2DIV3 = SQRT2/SQRT3;
    protected final float PI =(float)java.lang.Math.PI;
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
public AsychronousMotor(){//boolean AnimationEnabled ){
	
	int i, j, k, m;

	toutIndex =0;
	/*

	 '1.将电压方程转化为电流的状态方程
	 '┌
	 '│Is_alph' = a11*Is_alph+a11*Is_beta-a22_11*kx*I0_alph-a22_12*kx*I0_beta+ b1*Us_alph + 0*Us_beta
	 '│Is_beta' = a11*Is_alph+a11*Is_beta-a22_21*kx*I0_alph-a22_22*kx*I0_beta+ 0*Us_alph  + b1*Us_beta
	 '┤I0_alph' = a21*Is_alph+a21*Is_beta+a22_11*I0_alph+a22_12*I0_beta      + 0*Us_alph  + 0*Us_beta
	 '│I0_beta' = a21*Is_alph+a21*Is_beta+a22_21*I0_alph+a22_22*I0_beta      + 0*Us_alph  + 0*Us_beta
	 '│w'       = (3/2*p*Lr*Lr/Lm*(I0_alph*Is_beta-I0_beta*Is_alph) -TL)/J
	 '└*/
   	  
	Rs = 0.469f;//1.4f;//Ohm
	Rr = 0.372f;//0.8f;//Ohm
	Ls = 0.067f;//0.134f;//H
	Lr = 0.067f;//134f;//H	
	Lm = 0.064f;//0.124f;//H			
	deltLs = Ls -Lm*Lm/Lr;//H
	
	Wr =0;//rotor speed;
	P=2;// pole pairs
	TL =0.0f;
	J= 0.152f;//0.032f;
			
	Theta =0;
	
	Tc = h =0.001f;
	T =10; 
	ts =20;
	PreRealtime =0;		
	N = (int)(T / h);
	Ns =(int)(ts / h);
	
	InnerNum =0;
	
	for(j =0; j< 5;j++)
	   StateEqVar[j] = 0;


	const_a11 = -(Rs+Rr*Lm*Lm/Lr/Lr)/deltLs;
	const_a21 = Rr/Lr;
	const_k1  =Lm*Lm/deltLs/Lr;
	
	Cof[0][0] =  Cof[1][1] = const_a11; 
	Cof[2][0] =  Cof[3][1] = const_a21;
	
	 Cof[0][1] = Cof[1][0] = 0;
	 Cof[2][1] = Cof[3][0] = 0;
	
	Cof[2][2] =  -const_a21; Cof[2][3] = -P*Wr;
	Cof[3][2] =  -Cof[2][3]; Cof[3][3] = -const_a21;	
	
	Cof[0][2] =  const_a21*const_k1; Cof[0][3] = P*Wr*const_k1;
	Cof[1][2] =  -Cof[0][3];         Cof[1][3] = Cof[0][2];		
	
	
	Cof[0][4] = Cof[0][5] = Cof[1][4] = Cof[1][5] = 0;  
	Cof[2][4] = Cof[2][5] = Cof[3][4] = Cof[3][5] = 0;  
	Cof[0][4] = Cof[1][5]  = 1.0f/deltLs;
	
	const_ke  =P*Lr*Lr/Lm/J;//1.5f*P*Lr*Lr/Lm/J; 
	kL =TL/J;
	
	Ialphbeta_out[0] = Ialphbeta_out[1] =0;
	RotorTheta_out=RotorSpeed_out =0;
}


public void CalculateRealTimeData(float[] inputVar){//int TimeIndex){//
	
int i, j, k, m,IteNum;
float freq,Amp;
//float realtime;
if(m_CmdRunState==false)
	return;

toutIndex++;
realtime = h * (toutIndex - 1);
 

Input[0] = inputVar[0]; 
Input[1] = inputVar[1]; 

IteNum = (int)inputVar[2];

for(int z =0;z<=IteNum;z++){
     //if(z<IteNum)
     //  h  =0.001f;
     //else
       h=0.001f;//;
	

Wr = StateEqVar[4];

Cof[2][3] = -P*Wr;
Cof[3][2] =  -Cof[2][3]; 

Cof[0][3] = P*Wr*const_k1;
Cof[1][2] =  -Cof[0][3];

for(j =0; j<=4;j++)
  StateEqVarTemp[j] = StateEqVar[j];


for(j =0; j<=3;j++)
	for(k =0; k<=4;k++)
		DerivStateVar[j][k] = 0;//#


for(m =0; m<=3;m++)
{	
	for(k =0; k<=4-1;k++){		
		
		for(j =0; j<= 4 -1;j++){
			DerivStateVar[m][k] = DerivStateVar[m][k] + Cof[k][j] * StateEqVarTemp[j];
		}	
		DerivStateVar[m][k] = DerivStateVar[m][k] +  Cof[k][4] * Input[0] +  Cof[k][5] * Input[1];
	}
	DerivStateVar[m][4] = DerivStateVar[m][4] +const_ke * (StateEqVarTemp[1]*StateEqVarTemp[2] -StateEqVarTemp[0]*StateEqVarTemp[3])-kL;//-0.01f*StateEqVarTemp[4];
	
	for(j =0; j<=4;j++){
		if (m!=2)
			StateEqVarTemp[j] = StateEqVar[j] + h * 0.5f * DerivStateVar[m][j];
		else
			StateEqVarTemp[j] = StateEqVar[j] + h * DerivStateVar[m][ j];
   
	}
/*	
	Cof[2][3] = -P*StateEqVarTemp[4];
	Cof[3][2] =  -Cof[2][3]; 

	Cof[0][3] = P*StateEqVarTemp[4]*const_k1;
	Cof[1][2] =  -Cof[0][3];*/
}

for(j =0; j<=4;j++){
   StateEqVar[j] = StateEqVar[j] + h / 6.0f * (DerivStateVar[0][ j] +  2.0f * DerivStateVar[1][ j]+ 2.0f * DerivStateVar[2][ j] + DerivStateVar[3][j]);
}

Iu = SQRT2DIV3*StateEqVar[0];
Iv = SQRT2DIV3*(-0.5f*StateEqVar[0]+StateEqVar[1]*SQRT3/2);
Iw = SQRT2DIV3*(-0.5f*StateEqVar[0]-StateEqVar[1]*SQRT3/2);


RotorTheta+= (0.5f*P*h*(Wr + StateEqVar[4]));//%(2.0f*PI));

Ialph =  StateEqVar[0];
Ibeta =  StateEqVar[1];
Te    =   const_ke * (StateEqVarTemp[1]*StateEqVarTemp[2] -StateEqVarTemp[0]*StateEqVarTemp[3])*J;
RotorSpeed = P*StateEqVar[4];///2.0f/3.1415f; //r/min


yout[0] = Iu;//StateEqVar[0];
//yout[1] =//StateEqVar[2];
//yout[2] = Iv;//StateEqVar[2];
yout[4] = Iv;//StateEqVar[3];
yout[3] = Iw;//P*StateEqVar[4]/2.0f/3.1415f;


}

InnerNum ++;
if(InnerNum>=4)
{
	InnerNum =0;
	yout[1] =RotorSpeed;//Iu;//StateEqVar[0];

	Iu_out  =Iu;
	Iv_out  =Iv;
	Iw_out  =Iw;
	//yout[0] = Iu_out;

	yout[2] = Iv_out;
	RotorTheta_out =RotorTheta;//+=(0.5f*P*Tc*(StateEqVar[4]));
	Ialphbeta_out[0] =  Ialph;
	Ialphbeta_out[1] =  Ibeta;
	Te_out    = Te;
	RotorSpeed_out = RotorSpeed; //r/min

}


}


public float[] getOutput(){
	
	return yout;
	
}

public float[] getIAB(){
	
	return Ialphbeta_out;
	
}

public float RotorTheta(){
	
	return RotorTheta_out;
	
}

public float RotorSpeed(){
	
	return RotorSpeed_out;
	
}

public float getTime(){
	
	return realtime;
	
}
};