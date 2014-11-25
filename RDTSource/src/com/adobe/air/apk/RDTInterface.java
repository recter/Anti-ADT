package com.adobe.air.apk;

import java.io.File;

/**
 * @author Rect
 * @see rectvv@gmail.com
 * @see www.shadowkong.com
 * @see github.com/platformanes
 * @version 2013-12-26
 */
public interface RDTInterface {
	public void addJarToClassesDex(File extraResources);
	public abstract void addFileToAPKRoot(File paramFile, String paramString);
}
