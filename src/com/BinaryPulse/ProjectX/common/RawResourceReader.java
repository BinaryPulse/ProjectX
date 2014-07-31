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
	
	public static float[] MeshDataReader(final Context context,
			final int resourceId)
	{
		final InputStream inputStream = context.getResources().openRawResource(
				resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(
				inputStream);
		final BufferedReader bufferedReader = new BufferedReader(
				inputStreamReader);

		String nextLine,line;
		final StringBuilder body = new StringBuilder();
        float[] Vertices = new float[21];
        
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
				String[] parts = tokens[1].split(",");
				for(i=0;i<21;i++){
					Vertices[i] = Float.parseFloat(parts[i]);					
				}
			     
				return 	Vertices;	 
				
			}
			//else
			//	 return Vertices;	
			
		}
		catch (IOException e)
		{
			return null;
		}


	}	
	
	
	
}
