package com.BinaryPulse.ProjectX.switchinglivewallpaper;

import android.opengl.GLSurfaceView.Renderer;

import com.BinaryPulse.ProjectX.lesson3.LessonThreeRenderer;

public class LessonThreeWallpaperService extends OpenGLES2WallpaperService {
	@Override
	Renderer getNewRenderer() {
		return new LessonThreeRenderer();
	}
}
