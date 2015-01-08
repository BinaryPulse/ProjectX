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

public class SychronousMotor {
	
	public final int INDEXT_OFFSET =1; 

	protected float[][] Cof = new float[4+INDEXT_OFFSET][12+INDEXT_OFFSET];//4, 12), temp As Single
	protected float h;// As Single = 0.1
	protected float ts;// As Single = 20
	protected int   T ;// As Integer = 600
	protected  int  N, Ns;//As Long
	protected float[]   StateEqVar = new float[7+INDEXT_OFFSET];
	protected float[]   StateEqVarTemp = new float[7+INDEXT_OFFSET];
	protected float[][] DerivStateVar = new float[3+INDEXT_OFFSET][4+INDEXT_OFFSET];
	
	public  float[] yout = new float[4+INDEXT_OFFSET];
	
	protected static int toutIndex;
	public static float realtime;
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
public SychronousMotor(){//boolean AnimationEnabled ){
	
	int i, j, k, m;

	toutIndex =0;
	/*
	'电压方程系数
	'
	'│Ud = -Rs * iD - xd * iD'+Xad*(if'+iD')+Xq*iq-Xaq*iQ
	'│Uq = -Rs*iq-Xd*id+Xad*(if+iD)-Xq*iq'+Xaq*iQ'
	'┤Uf = Rf*if+Xf*if'+Xad*(-id'+iD')
	'│0  = RD * iD + xd * iD'+Xad*(-id'+if')
	'│0  = RQ * iQ + XQ * iQ'-Xaq*iq'
	*/


	 Cof[0][0] = -2; Cof[0][2] = 1.9f; 	     Cof[0][3] = 1.9f; 		Cof[0][5] = -0.025f; Cof[0][ 6] = 2;  Cof[0][9] = -1.9f;Cof[0][10] = -1;
	 Cof[1][1] = -2; Cof[1][4] = 1.9f; 		 Cof[1][5] = -2; 		Cof[1][6] = -0.025f; Cof[1][ 7] = 1.9f;Cof[1][8] = 1.9f; Cof[1][ 11] = -1;
	 Cof[2][0]= -1.9f;Cof[2][2] = 2.0036f;   Cof[2][3]= 1.9f; 		Cof[2][7] = 0.0038f; Cof[2][12] = -1;
	 Cof[3][0] = -1.9f; Cof[3][2] = 1.9f;	 Cof[3][3] = 1.9914f; 	Cof[3][8] = 0.0185f;
	 Cof[4][1] = -1.9f; Cof[4][4] = 1.9914f; Cof[4][9] = 0.0185f;
	/*
	 '1.将电压方程转化为电流的状态方程
	 '┌
	 '│id' = c11*id+c12*iq+c13*if+c14*iD+c15*iQ+c16*Ud+c17*Uq+c18*Uf
	 '│iq' = c21*id+c22*iq+c23*if+c24*iD+c25*iQ+c26*Ud+c27*Uq+c28*Uf
	 '┤if' = c31*id+c32*iq+c33*if+c34*iD+c35*iQ+c36*Ud+c37*Uq+c38*Uf
	 '│iD' = c41*id+c42*iq+c43*if+c44*iD+c45*iQ+c46*Ud+c47*Uq+c48*Uf
	 '│iQ' = c51*id+c52*iq+c53*if+c54*iD+c55*iQ+c56*Ud+c57*Uq+c58*Uf
	 '└*/
	for (i = 0; i<= 4;i++)
	{
		for (k = 0; k<=3-i;k++)
		{
			if( Cof[k][4 - i] != 0.0f)
			{
				float temp = Cof[k][4 - i];
				for(j = 0; j<=12;j++)
					Cof[k][ j] = Cof[k][j] * Cof[4 - i][4 - i] - Cof[4 - i][j] * temp;
			}
	    
		}
	 
	} 
	
	
	for (i = 0; i<= 4;i++)
	{
		if(i>0)
		{
			for (k = 0; k<=i-1;k++)
			{
				for(j =5; j<=12;j++)
					Cof[i][j] = Cof[i][j] + Cof[i][k] * Cof[k][ j];
			    Cof[i][k] = 0;
			}
		}
		for(j =5; j<=12;j++)
		{
			Cof[i][j]= -Cof[i][j] / Cof[i][i];
		}
		 Cof[i][i] = 1;
	}		  
	
	h =0.1f;
	T =600;
	ts =20;
			
	N = (int)(T / h);
	Ns =(int)(ts / h);
	for(j =0; j<=7;j++)
	   StateEqVar[j] = 0;


	StateEqVar[7] = 0.002f;                 //Uf=0.002
	StateEqVar[2]= 0.002f / 0.0038f;        //If=Uf/Rf
	StateEqVar[6] = 1.9f * StateEqVar[2];   //Uq=Xad*If
		
	
}


public void CalculateRealTimeData(int Timeindex){
	
int i, j, k, m;
//float realtime;
if(m_CmdRunState==false)
	return;
toutIndex++;

if(toutIndex > Ns)
   StateEqVar[6] = 0;//# '突然短路发生时刻

realtime = h * (toutIndex - 1);
/* xd(0) = h * (i - 1)
 xd(1) = h * (i - 1)
 xd(2) = h * (i - 1)
 xd(3) = h * (i - 1)
 xd(4) = h * (i - 1)*/
 
yout[1] = StateEqVar[0];// ' StateEqVar(0) * Cos(xd(0) + 1) - StateEqVar(1) * Sin(xd(0) + 1) '
yout[2] = StateEqVar[2];// 'StateEqVar(0) * Cos(xd(0) + 2) - StateEqVar(1) * Sin(xd(0) + 2) '
yout[3] = StateEqVar[3];// 'StateEqVar(0) * Cos(xd(0) + 3) - StateEqVar(1) * Sin(xd(0) + 3) '
yout[0] = (StateEqVar[0] * (float)java.lang.Math.cos(realtime) - StateEqVar[1] * (float)java.lang.Math.sin(realtime));//
yout[4] = 1.9f* (StateEqVar[1] * StateEqVar[2] + StateEqVar[3] * StateEqVar[1] - StateEqVar[4] * StateEqVar[0]);// 'StateEqVar(0) * Cos(xd(0) + 4) - StateEqVar(1) * Sin(xd(0) + 4) '
 
// objScope.RecieveData xd, yd, DCurve_Num
// objScope.Draw_Curves
    
for(j =0; j<=7;j++)
  StateEqVarTemp[j] = StateEqVar[j];


for(j =0; j<=3;j++)
	for(k =0; k<=4;k++)
		DerivStateVar[j][k] = 0;//#


for(m =0; m<=3;m++)
{	
	for(k =0; k<=4;k++){
		for(j =0; j<=7;j++){
			DerivStateVar[m][k] = DerivStateVar[m][k] + Cof[k][j + 5] * StateEqVarTemp[j];
		}	
	}
	for(j =0; j<=4;j++){
		if (m!=2)
			StateEqVarTemp[j] = StateEqVar[j] + h * 0.5f * DerivStateVar[m][j];
		else
			StateEqVarTemp[j] = StateEqVar[j] + h * DerivStateVar[m][ j];
   
	}
}

for(j =0; j<=4;j++){
   StateEqVar[j] = StateEqVar[j] + h / 6 * (DerivStateVar[0][ j] +  2 * DerivStateVar[1][ j]+ 2 * DerivStateVar[2][ j] + DerivStateVar[3][j]);
}




}


public float[] getOutput(){
	
	return yout;
	
}

public float getTime(){
	
	return realtime;
	
}
};