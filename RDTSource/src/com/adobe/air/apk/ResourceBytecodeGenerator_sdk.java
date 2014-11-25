package com.adobe.air.apk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.bcel.generic.ConstantPoolGen;

@SuppressWarnings("unused")
public class ResourceBytecodeGenerator_sdk
{
	public static String packageName_air = null;
  @SuppressWarnings({ "rawtypes", "unchecked" })
public static File generateFromRFile(File theRFile, File outputDirectory,String fileName)
    throws IOException
  {
    File resourceJarFile = null;
    try
    {
      FileReader f = new FileReader(theRFile);
      Scanner scanner = new Scanner(f);

      Pattern rscClassPattern = Pattern.compile("^\\s*public\\s*static\\s*final\\s*class\\s*(\\S*)");
      Pattern rscIdPattern = Pattern.compile("^\\s*public\\s*static\\s*final\\s*int\\s*(\\S*?)=0x(\\S*?);");
      Pattern packagePattern = Pattern.compile("^\\s*package\\s*(\\S*?);");

      String packageName = null;
      RClassCreator classCreator = null;
      RClassCreator outerClassCreator = null;
      List classCreators = new LinkedList();

      ConstantPoolGen constantPool = new ConstantPoolGen();

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        Matcher packageMatcher = packagePattern.matcher(line);
        Matcher rscClassMatcher = rscClassPattern.matcher(line);
        Matcher rscIdMatcher = rscIdPattern.matcher(line);
        if (packageMatcher.find()) {
          packageName = packageMatcher.group(1);
          classCreator = new RClassCreator(packageName, "R", constantPool);
          outerClassCreator = classCreator;
          classCreators.add(classCreator);
        } else if (rscClassMatcher.find()) {
          String currentResourceType = rscClassMatcher.group(1);
          String innerClassName = String.format("R$%s", new Object[] { currentResourceType });
          classCreator = new RClassCreator(packageName, innerClassName, constantPool, outerClassCreator);
          classCreators.add(classCreator);
        } else if (rscIdMatcher.find()) {
          String resourceName = rscIdMatcher.group(1);
          String resourceIdInHex = rscIdMatcher.group(2);
          int resourceId = Integer.parseInt(resourceIdInHex, 16);
          classCreator.appendResourceId(resourceName, resourceId);
        }

      }

      if (classCreators.size() > 0) {
        resourceJarFile = new File(outputDirectory, fileName);
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        JarOutputStream targetJar = new JarOutputStream(new FileOutputStream(resourceJarFile));

        for (Object _item : classCreators) {
        	RClassCreator item = (RClassCreator)_item;
          String classFilePath = item.getClassFilePath().replace("\\", "/");
          JarEntry entry = new JarEntry(classFilePath);
          entry.setTime(new Date().getTime());

          targetJar.putNextEntry(entry);
          item.create(targetJar);
          targetJar.closeEntry();
        }
        targetJar.close();
      }
    }
    catch (FileNotFoundException e) {
    }
    return resourceJarFile;
  }
}