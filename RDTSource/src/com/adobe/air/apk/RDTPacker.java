package com.adobe.air.apk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RDTPacker
{
  public static RDTInterface apkPacker = null;
  private static RDTPacker instacne;
  private static String jarFilepath;
  private static String[] resourceIdArr = new String[0];

  public static RDTPacker getInstance()
  {
    if (instacne == null)
      instacne = new RDTPacker();
    return instacne;
  }

  public void ANEFileInRoot(List<File> $RDTFile)
  {
    for (int t = 0; t < $RDTFile.size(); t++)
    {
      File adtFile = (File)$RDTFile.get(t);
      String folderInPackage = adtFile.getPath();

      String[] fileNames = folderInPackage.split("ROOT");
      folderInPackage = "";
      if (fileNames.length == 2)
      {
        folderInPackage = fileNames[1];

        folderInPackage = folderInPackage.substring(1, folderInPackage.length() - adtFile.getName().length() - 1);

        if ('/' != folderInPackage.charAt(folderInPackage.length() - 1)) {
          folderInPackage = folderInPackage + "/";
        }

      }

      if (apkPacker != null)
        apkPacker.addFileToAPKRoot(adtFile, folderInPackage);
      else
        System.out.println("apkPacker is null");
    }
  }

  public void ANEFileInRdt(List<File> $RDTFile)
  {
    for (int t = 0; t < $RDTFile.size(); t++)
    {
      File adtFile = (File)$RDTFile.get(t);
      System.out.println("ANEFileInRdt:" + adtFile.getName());
      String fileName = adtFile.getName();
      String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
      prefix = prefix.toLowerCase();
      prefix = prefix.replace('-', ' ').replace(':', ' ').replaceAll(" ", "");
      System.out.println("prefix:" + prefix.charAt(0));
      if ('x' == prefix.charAt(0))
      {
        try
        {
          domXMl(adtFile);
        }
        catch (Exception e)
        {
          System.out.println("ADT.XML error:" + e.getLocalizedMessage());
        }
      }
      else
      {
        instacne.addExtraResources(adtFile);
      }
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
private void domXMl(File file) {
    try { InputStream input = new FileInputStream(file);
      DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = domBuilder.parse(input);
      Element root = doc.getDocumentElement();
      NodeList students = root.getChildNodes();
      if (students != null) {
        List idList = new ArrayList();
        int i = 0; for (int size = students.getLength(); i < size; i++)
        {
          Node student = students.item(i);
          if ("resourceid" == student.getNodeName())
          {
            idList.add(student.getTextContent());
            System.out.println("resePathID:" + student.getTextContent());
          }
        }

        if (idList.size() > 0)
        {
          resourceIdArr = new String[idList.size()];
          for (int t = 0; t < idList.size(); t++) {
            resourceIdArr[t] = ((String)idList.get(t));
          }
        }
      }
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addExtraResources(File extraResources)
  {
    if (apkPacker != null)
      apkPacker.addJarToClassesDex(extraResources);
    else
      System.out.println("apkPacker is null");
    System.out.println("SDK addJarFile:" + extraResources.getName());
  }

  public void addResourceIDFile(String $jarFilepath)
    throws IOException
  {
    jarFilepath = $jarFilepath;

    for (int t = 0; t < resourceIdArr.length; t++)
    {
      String resourceStr = resourceIdArr[t];
      replacePackageName(jarFilepath, resourceStr, resourceStr + ".java");
      File rJavaFile_sdk = new File(jarFilepath, resourceStr + ".java");
      File resourceJarFile_sdk = ResourceBytecodeGenerator_sdk.generateFromRFile(
        rJavaFile_sdk, new File(jarFilepath), resourceStr + ".jar");

      instacne.addExtraResources(resourceJarFile_sdk);
    }
  }

  private void replacePackageName(String path, String newPackage, String rName)
  {
    try {
      StringBuffer bs = new StringBuffer();
      FileReader fr = new FileReader(path + "\\R.java");

      BufferedReader br = new BufferedReader(fr);
      String line = null;
      while ((line = br.readLine()) != null)
      {
        bs.append(line + "\n");
      }
      br.close();

      String str = bs.toString();
      if (str.indexOf(ResourceBytecodeGenerator_sdk.packageName_air) != -1) {
        str = str.replaceAll(ResourceBytecodeGenerator_sdk.packageName_air, newPackage);
      }

      FileWriter fw = new FileWriter(path + "\\" + rName);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(str);
      bw.flush();
      bw.close();
      System.out.println("SDK资源操作:" + path + "/" + rName);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}