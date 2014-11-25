package com.adobe.air.apk;

import android.annotation.SuppressLint;
import com.adobe.air.ANEFile;
import com.adobe.air.ApplicationDescriptor;
import com.adobe.air.ApplicationPackager;
import com.adobe.air.InvalidInputException;
import com.adobe.air.Utils;
import com.adobe.air.android.AndroidDeviceSDK;
import com.adobe.air.validator.DescriptorValidationException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SuppressLint("DefaultLocale")
public final class APKPackager extends ApplicationPackager implements RDTInterface
{
	public static final ApplicationDescriptor.ValidationParams APK_VALIDATION_PARAMS = new ApplicationDescriptor.ValidationParams("http://ns.adobe.com/air/application/2.5", "http://ns.adobe.com/air/application/3.0", new String[] { "mobileDevice" }, false, false);

	public static final ApplicationDescriptor.ValidationParams APK_CAPTIVE_RUNTIME_VALIDATION_PARAMS = new ApplicationDescriptor.ValidationParams("http://ns.adobe.com/air/application/3.0", "http://ns.adobe.com/air/application/3.0", new String[] { "mobileDevice", "mobileDeviceBase" }, false, false);
	@SuppressWarnings("unused")
	private static final String AIR_ANDROID_SHARED_RUNTIME = "AIR_ANDROID_SHARED_RUNTIME";
	@SuppressWarnings("unused")
	private static final String JAR_FILE_EXTENSION = "jar";
	@SuppressWarnings("unused")
	private static final String LIB_FILE_EXTENSION = "so";

	public static File[] jarFiles;
	public static File[] newJarFiles;
	public APKPackager()
	{
		super("AIR", "Android-ARM", true, false);
		setStream(new APKOutputStream());
		setIsSharedRuntime();
	}

	public void setPrivateKey(PrivateKey key) {
		super.setPrivateKey(key);
	}

	public void setApplicationDescriptor(File descriptorFile) {
		try {
			super.setApplicationDescriptor(descriptorFile);
			getAPKStream().addApplicationDescriptorFile(descriptorFile);
		}
		catch (IOException ex)
		{
		}
	}

	protected void validateApplicationDescriptor() throws DescriptorValidationException {
		super.validateApplicationDescriptor();
		getAPKStream().setSources(getSources());
	}

	protected ApplicationDescriptor.ValidationParams getValidationParams(ApplicationDescriptor appDescriptor) {
		return getAPKStream().hasCaptiveRuntime() ? APK_CAPTIVE_RUNTIME_VALIDATION_PARAMS : APK_VALIDATION_PARAMS;
	}

	private static String getFileExtension(String f) {
		String ext = "";
		int i = f.lastIndexOf('.');
		if ((i > 0) && (i < f.length() - 1)) {
			ext = f.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	@SuppressWarnings({ "rawtypes", "unchecked"})
	protected void processExtensions()
	throws IOException, DescriptorValidationException, InvalidInputException
	{
		super.processExtensions();

		File tempDir = Utils.createTempDirectory();
		addFileForCleanup(tempDir);

		List SDKLibs = new LinkedList();
		List RootFiles = new LinkedList();
		
		for (ANEFile ane : getExtensions()) {
			try
			{
				String platformPath = ane.getPlatformPath("Android-ARM");
				String libPlatformPath;
				if ((getAPKStream().getConfigType().equals("apk")) || (getAPKStream().getConfigType().equals("apk-debug")) || (getAPKStream().getConfigType().equals("apk-captive-runtime")))
					libPlatformPath = platformPath + "/libs/armeabi-v7a/";
				else {
					libPlatformPath = platformPath + "/libs/armeabi/";
				}

				String resPlatformPath = platformPath + "/res/";

				String SDKPlatformPath = platformPath + "/RDT/";
				String RootPlatformPath = platformPath + "/ROOT/";
				
				String nativeLibraryPath = ane.getNativeLibraryPath("Android-ARM");
				if (nativeLibraryPath != null)
				{
					String nativeLibraryFileExtension = getFileExtension(nativeLibraryPath);
					if ((nativeLibraryFileExtension.equals("jar")) || (nativeLibraryFileExtension.equals("so")))
					{
						ZipFile zipFile = new ZipFile(ane.getFile());

						List dependencies = Collections.EMPTY_LIST;
						List resources = Collections.EMPTY_LIST;
						String platformOptionsPath = ane.getPlatformPath(this.m_targetPlatform) + "/" + "platform.xml";
						ZipEntry platformOptionsFile = zipFile.getEntry(platformOptionsPath);
						if (platformOptionsFile != null)
						{
							AndroidExtensionDescriptor desc = new AndroidExtensionDescriptor(zipFile.getInputStream(platformOptionsFile), true, platformOptionsFile.getName());
							dependencies = desc.dependencies();
							resources = desc.resourceFolders();
							getAPKStream().setPackages(desc.resourcePackages());
						}

						LinkedHashMap extraResources = new LinkedHashMap();
						List extraLibs = new LinkedList();
						for (Enumeration e = zipFile.entries(); e.hasMoreElements(); ) {
							ZipEntry currentEntry = (ZipEntry)e.nextElement();
							String currentEntryName = currentEntry.getName();
							File outFile = null;
							String itemRelativeName;
							if (currentEntryName.startsWith(libPlatformPath))
							{
								itemRelativeName = currentEntryName.substring(libPlatformPath.length());
								outFile = new File(tempDir, "libs" + File.separator + itemRelativeName);
								extraLibs.add(outFile);
							}
							else if (currentEntry.getName().startsWith(resPlatformPath))
							{
								itemRelativeName = currentEntryName.substring(resPlatformPath.length());
								if (validateANEResourcesAgainstSupportedLanguages(itemRelativeName)) {
									outFile = new File(tempDir, "res" + File.separator + itemRelativeName);
									extraResources.put(outFile, "res");
								}
							}
							else if (currentEntry.getName().startsWith(platformPath))
							{
								itemRelativeName = currentEntryName.substring(platformPath.length() + 1);

								for (Object resource_ : resources)
								{
									String resource = (String)resource_;
									if ((itemRelativeName.startsWith(resource + "/")) && (validateANEResourcesAgainstSupportedLanguages(itemRelativeName)))
									{
										outFile = new File(tempDir, itemRelativeName);
										extraResources.put(outFile, resource);
										break;
									}
								}
								for (Object dependency_ : dependencies)
								{
									String dependency = (String)dependency_;
									if (itemRelativeName.equals(dependency))
									{
										outFile = new File(tempDir, itemRelativeName);
										break;
									}
								}
							}

							if (currentEntryName.startsWith(SDKPlatformPath))
							{
								 itemRelativeName = currentEntryName.substring(SDKPlatformPath.length());
								outFile = new File(tempDir, "RDT" + File.separator + itemRelativeName);
								SDKLibs.add(outFile);
								System.out.println("RDT:RDT" + File.separator + itemRelativeName);
							}

							if (currentEntryName.startsWith(RootPlatformPath))
							{
								 itemRelativeName = currentEntryName.substring(RootPlatformPath.length());
								outFile = new File(tempDir, "ROOT" + File.separator + itemRelativeName);
								RootFiles.add(outFile);
								System.out.println("ROOT:ROOT" + File.separator + itemRelativeName);
							}
							
							if (outFile != null)
							{
								InputStream in = zipFile.getInputStream(currentEntry);
								outFile.getParentFile().mkdirs();
								OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
								Utils.writeThrough(in, out);
								out.close();
							}
						}
						if (extraResources.size() > 0)
						{
							getAPKStream().addExtraResources(extraResources);
						}
						if (extraLibs.size() > 0)
						{
							getAPKStream().addExtraLibs(extraLibs);
						}

						InputStream in = zipFile.getInputStream(zipFile.getEntry(nativeLibraryPath));
						OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(tempDir, ane.id() + "." + nativeLibraryFileExtension)));
						Utils.writeThrough(in, out);

						in.close();
						out.close();
						zipFile.close();
					}
				}
			} catch (IOException e) { throw new InvalidInputException(ane.getFile().getAbsoluteFile() + " is not a valid native extension file."); }

		}

		jarFiles = tempDir.listFiles(new FileExtensionFilter("jar"));
		getAPKStream().setExtensionsJars(jarFiles);
		RDTPacker.apkPacker = this;
		
		File[] libFile = tempDir.listFiles(new FileExtensionFilter("so"));
		getAPKStream().setExtensionsLibs(libFile);
		
		if (SDKLibs.size() > 0)
		{
			System.out.println("SDKLibs:" + SDKLibs.size());
			RDTPacker.getInstance().ANEFileInRdt(SDKLibs);
		}

		if (RootFiles.size() > 0)
		{
			System.out.println("RootFiles:" + RootFiles.size());
			RDTPacker.getInstance().ANEFileInRoot(RootFiles);
		}
	}
	//***************************************************
	public void addFileToAPKRoot(File extraResources, String folderInPackage)
	{
		try {
			InputStream inClsDexStream = new FileInputStream(extraResources);
			getAPKStream().addFileFromStream(extraResources.getName(), inClsDexStream, -2119958528L, true, folderInPackage);
		}
		catch (Exception e)
		{
			System.out.println("addFileToAPKRoot Error:" + extraResources.getName());
		}
	}
	
	public void addJarToClassesDex(File extraResources)
	{
		System.out.println("jarFiles:" + jarFiles.length);
		newJarFiles = new File[jarFiles.length + 1];
		for (int t = 0; t < jarFiles.length; t++)
			newJarFiles[t] = jarFiles[t];
		newJarFiles[(newJarFiles.length - 1)] = extraResources;
		System.out.println("newJarFiles:" + newJarFiles.length);
		jarFiles = newJarFiles;
		getAPKStream().setExtensionsJars(newJarFiles);
	}
	//***************************************************
	protected void addExtensions()
	throws IOException, DescriptorValidationException, InvalidInputException
	{
		super.addExtensions();
		getAPKStream().addExtensionsNativeLibs();
	}

	public void setOutput(File output) throws FileNotFoundException, IOException {
		super.setOutput(output);
		initTempFile("apk");
	}

	public void setPackageConfiguration(String configType) {
		getAPKStream().setConfigType(configType);
	}

	public void setForOutgoingDebuggingConnection(String host, int port) throws IOException {
		getAPKStream().setForOutgoingDebuggingConnection(host, port);
	}

	public void setPreloadSWFPath(String path) {
		getAPKStream().setPreloadSWFPath(path);
	}

	public void setForIncomingDebuggerConnection(int portToListenOn) throws IOException {
		getAPKStream().setForIncomingDebuggerConnection(portToListenOn);
	}

	public void setAIRDownloadURL(String airDownloadURL)
	{
		getAPKStream().setAIRDownloadURL(airDownloadURL);
	}

	protected APKOutputStream getStream() {
		return (APKOutputStream)super.getStream();
	}

	private APKOutputStream getAPKStream() {
		return getStream();
	}

	public void setDeviceSDKDirectory(File sdkDir) throws InvalidInputException
	{
		getAPKStream().setAndroidDeviceSDK(new AndroidDeviceSDK(sdkDir));
	}

	public void setTimestampURL(String url) {
		throw new UnsupportedOperationException("timestamp not supported for APK");
	}

	public boolean allowAIRDownloadURL()
	{
		return (getAPKStream().getConfigType().equals("apk")) || (getAPKStream().getConfigType().equals("apk-debug"));
	}

	private void setIsSharedRuntime()
	{
		String airSharedRuntimeVar = System.getenv("AIR_ANDROID_SHARED_RUNTIME");
		boolean isShared = (airSharedRuntimeVar != null) && (airSharedRuntimeVar.indexOf("true") != -1);
		getAPKStream().setIsSharedRuntime(isShared);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean validateANEResourcesAgainstSupportedLanguages(String relativeName)
	{
		int folderSeparatorIndex = relativeName.indexOf('/');
		if ((folderSeparatorIndex != -1) && (relativeName.lastIndexOf('-', folderSeparatorIndex) != -1)) {
			String folderName = relativeName.substring(0, folderSeparatorIndex);
			String[] qualifiers = folderName.split("-");

			if ((qualifiers[1].length() == 2) && (qualifiers[1].matches("[a-z]{2}"))) {
				String lang = qualifiers[1];
				List supportedLanguages = getApplicationDescriptor().supportedLanguages();

				if (supportedLanguages == null) {
					getApplicationDescriptor(); supportedLanguages = new ArrayList(ApplicationDescriptor.ALL_SUPPORTED_LANGUAGES);
				}
				if ((!supportedLanguages.contains(lang.toLowerCase())) && (!lang.equals("en"))) {
					System.out.println("Warning: Resource " + relativeName + " has been skipped because of mismatch with supported languages information in application descriptor.");
					return false;
				}
			}
		}
		return true;
	}

	class FileExtensionFilter
	implements FilenameFilter
	{
		private String extension;

		public FileExtensionFilter(String extension)
		{
			this.extension = extension.toLowerCase();
		}

		public boolean accept(File directory, String filename) {
			boolean fileOK = true;

			if (this.extension != null) {
				fileOK &= filename.toLowerCase().endsWith('.' + this.extension);
			}
			return fileOK;
		}
	}
}