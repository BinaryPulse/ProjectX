package com.BinaryPulse.ProjectX.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

public class RawResourceReader
{
	public static String readTextFileFromRawResource(final Context context,
			final int resourceId)
	{
		final InputStream inputStream = context.getResources().openRawResource(
				resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(
				inputStream);
		final BufferedReader bufferedReader = new BufferedReader(
				inputStreamReader);

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try
		{
			while ((nextLine = bufferedReader.readLine()) != null)
			{
				body.append(nextLine);
				body.append('\n');
			}
		}
		catch (IOException e)
		{
			return null;
		}

		return body.toString();
	}
}
/*	
	//public static float[] 
	void MeshDataReader(final Context context,
			final int resourceId, final float[] mVertices,final float[] mNormals,final short[] mIndexes)
	{
		final InputStream inputStream = context.getResources().openRawResource(
				resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(
				inputStream);
		final BufferedReader bufferedReader = new BufferedReader(
				inputStreamReader);

		String nextLine,line;
		final StringBuilder body = new StringBuilder();
		final float[] Vertices = new float[1307*3];
		final float[] Normals = new float[1307*3];
		final short[] Indexes = new short[2148*3];
		try
		{   
			int i;
			while ((nextLine = bufferedReader.readLine()) != null)
			{
				body.append(nextLine);
				//body.append('\n');
			}
			line = body.toString();
			//if(line.startsWith("Vertices") )
			{
				String[] tokens =line.split(":");
				String[] parts1 = tokens[1].split(",");
				String[] parts2 = tokens[2].split(",");
				String[] parts3 = tokens[3].split(",");
				for(i=0;i<1037*3;i++){
					Vertices[i] = Float.parseFloat(parts1[i]);	
					Normals[i] = Float.parseFloat(parts1[i]);
				}
				for(i=0;i<2148*3;i++){
					Indexes[i] = Short.parseShort(parts3[i]);	
				}			     
				//return 	Vertices;	 
				mIndexes = Indexes;
				
			}
			//else
			//	 return Vertices;	
			
		}
		catch (IOException e)
		{
			return null;
		}


	}	
	
	
	
}*/
