package com.tool.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {

    private static String mapName = "map.properties";

    private static String apktool;

    private static String apksigner;

    private static String project_path;

    private static String location;

    private static String apkDecodeFolder;

    private static String apkTempFolder;

    private static String unsignedApkFile;

    private static String channelFlag;

    private static String keystore_file_path;

    private static String keystore_pw;

    private static String key_pw;

    private static String unzipalignApkFile;

    private static String alias;

    private static String finalApkFile;

    private static String projectName;

    private static String versionName;

    private static String[] channels;

    private static String outPath;

    private static String content;

    static {
        try {

            location = getProjectPath();

            Properties props = new Properties();

            InputStream in = new BufferedInputStream(new FileInputStream(
                    new File(location, mapName).getAbsolutePath()));
            props.load(in);

            project_path = props.getProperty("project_path");
            channelFlag = props.getProperty("channelFlag");
            keystore_file_path = props.getProperty("keystore_file_path");
            keystore_pw = props.getProperty("keystore_pw");
            key_pw = props.getProperty("key_pw");
            alias = props.getProperty("alias");
            projectName = props.getProperty("projectName");
            versionName = props.getProperty("versionName");
            channels = props.getProperty("channels").split(",");

            apkDecodeFolder = new File(location, "temp").getAbsolutePath();
            apkTempFolder = new File(location, "bins").getAbsolutePath();
            unsignedApkFile = new File(apkTempFolder, "unsigned.apk")
                    .getAbsolutePath();
            unzipalignApkFile = new File(apkTempFolder, "unzipalign.apk")
                    .getAbsolutePath();
            finalApkFile = new File(apkTempFolder, "release.apk")
                    .getAbsolutePath();

            if (isWindows()) {
                apktool = new File(location + File.separator + "windows", "apktool.bat").getAbsolutePath();
                apksigner = "apksigner.bat";
            } else if (isMac()) {
                apktool = new File(location + File.separator + "macosx", "apktool").getAbsolutePath();
                apksigner = "apksigner";
            } else {
                apktool = new File(location + File.separator + "linux", "apktool").getAbsolutePath();
                apksigner = "apksigner";
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        if (args.length != 0) {
            channels = args[0].split(",");
        }

        System.out.println("location:" + location);
        System.out.println("project_path:" + project_path);
        System.out.println("apkDecodeFolder:" + apkDecodeFolder);
        System.out.println("apkTempFolder:" + apkTempFolder);
        System.out.println("unsignedApkFile:" + unsignedApkFile);
        System.out.println("unzipalignApkFile:" + unzipalignApkFile);
        System.out.println("finalApkFile:" + finalApkFile);

        doWorkFromApk();
    }

    private static void doWorkFromApk() {

        long start = System.currentTimeMillis();

        decodeApk();

        for (String channel : channels) {

            System.out.println("\nchannel:" + channel + "\n");

            clean();

            replaceChannel(channel);

            rebuildApk();

            zipAlign();

            signAPK();

            copyToWorkspace(channel);
        }

        long end = System.currentTimeMillis();

        System.out.println("\nTime:" + (end - start) / 1000 + "s");
    }

    private static void clean() {

        System.out.println("clean...");

        deleteDirectory(new File(apkTempFolder));

    }

    private static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    private static void decodeApk() {
        System.out.println("decodeApk...");
        if (!new File(project_path).exists()) {
            System.out.println("Target apk is missing..");
            System.exit(0);
        }

        List<String> cmd = new ArrayList<String>();
        cmd.add(apktool);
        cmd.add("d");
        cmd.add("--no-src");
        cmd.add("-f");
        cmd.add(project_path);
        cmd.add("-o");
        cmd.add(apkDecodeFolder);

        Process process = null;
        try {

            process = Runtime.getRuntime().exec(cmd.toArray(new String[0]),
                    null, null);
            new MyThread(process.getErrorStream()).start();
            new MyThread(process.getInputStream()).start();

            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void replaceChannel(String channel) {
        System.out.println("Add or replcae channel");
        try {
            outPath = new File(apkDecodeFolder, "AndroidManifest.xml")
                    .getAbsolutePath();
            if (content == null) {
                content = read(outPath);
            }
            String tmpContent = content;
            tmpContent = tmpContent.replaceAll(channelFlag, channel);
            write(tmpContent, outPath);
            System.out.println("replace channel name over...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void rebuildApk() {
        System.out.println("rebuildApk...");
        File apkTemp = new File(apkTempFolder);
        try {

            if (!apkTemp.exists()) {
                apkTemp.mkdirs();
                System.out.println("mkdir:bins");
            }

            List<String> cmd = new ArrayList<String>();

            cmd.add(apktool);
            cmd.add("b");
            cmd.add(apkDecodeFolder);
            cmd.add("-o");
            cmd.add(unzipalignApkFile);
            Process process = null;
            process = Runtime.getRuntime().exec(cmd.toArray(new String[0]),
                    null, null);
            new MyThread(process.getErrorStream()).start();
            new MyThread(process.getInputStream()).start();
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void signAPK() {
        System.out.println("signAPK...");
        List<String> cmd = new ArrayList<String>();

        //build-tools 24.0.3
        cmd.add(apksigner);
        cmd.add("sign");

        //V1 方案：基于 JAR 签名
        cmd.add("--v1-signing-enabled");
        cmd.add("true");
        //V2 方案：APK 签名方案 V2，在 Android 7.0 引入
        cmd.add("--v2-signing-enabled");
        cmd.add("true");
        //V3 方案：APK 签名方案 V3，在 Android 9.0 引入(支持轮替签名密钥)
        //build-tools 29.0.1
        cmd.add("--v3-signing-enabled");
        cmd.add("true");
        //V4 方案：APK 签名方案 V4，在 Android 11 引入(会生成.apk.idsig文件 ADB增量APK安装方便大型（2GB 以上）APK测试 adb install --incremental)
        //build-tools 30.0.0
        cmd.add("--v4-signing-enabled");
        cmd.add("false");

        cmd.add("--ks");
        cmd.add(keystore_file_path);
        cmd.add("--ks-pass");
        cmd.add("pass:" + keystore_pw);

        cmd.add("--ks-key-alias");
        cmd.add(alias);
        cmd.add("--key-pass");
        cmd.add("pass:" + key_pw);

        cmd.add("--out");
        cmd.add(finalApkFile);
        cmd.add(unsignedApkFile);

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd.toArray(new String[0]),
                    null, null);
            new MyThread(process.getErrorStream()).start();
            new MyThread(process.getInputStream()).start();

            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void zipAlign() {
        System.out.println("zipAlign...");
        if (!new File(unzipalignApkFile).exists()) {
            //如果您的电脑以前使用过apktool工具请删除工具生成老的framework.jar 路径(windows平台): c:\Documents and Settings%current user%\apktool\framework*
            //Mac ~/Library/apktool/framework/1.apk
            System.out
                    .println("unzipalign apk error .. can't find {0} file for zip align "
                            + unzipalignApkFile);
            System.exit(0);
        }

        List<String> cmd = new ArrayList<String>();
        //cmd.add("D:\\android-sdk-windows\\build-tools\\");
        cmd.add("zipalign");
        cmd.add("-p");
        cmd.add("-f");
        cmd.add("-v");
        cmd.add("4");
        cmd.add(unzipalignApkFile);
        cmd.add(unsignedApkFile);

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd.toArray(new String[0]),
                    null, null);
            new MyThread(process.getErrorStream()).start();
            new MyThread(process.getInputStream()).start();

            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void copyToWorkspace(String channel) {
        System.out.println("copyToWorkspace...");
        String apk_file = finalApkFile;

        if (apk_file == null || !new File(apk_file).exists()) {
            System.out.println("Fail to generate .apk for " + channel);
            return;
        }

        String dst_file = generateDstFile(channel);
        File dstFile = new File(dst_file);
        if (dstFile.exists())
            dstFile.delete();
        copyFile(apk_file, dst_file);
    }

    private static String generateDstFile(String channel) {
        String project_name = projectName;
        String file_name = String.format("%s_%s.apk", project_name, channel);

        File file = new File(location, String.format("%s_%s", project_name, versionName));
        String dst_path = file.getAbsolutePath();

        if (!file.exists()) {
            file.mkdirs();
            System.out.println("mkdir:" + project_name);
        }
        return new File(dst_path, file_name).getAbsolutePath();
    }

    private static void copyFile(String inputFile, String outputFile) {
        System.out.println("copyFile:" + inputFile + "->" + outputFile);
        try {
            FileInputStream fis;

            fis = new FileInputStream(inputFile);

            FileOutputStream fos = new FileOutputStream(outputFile);

            FileChannel fc1 = fis.getChannel();

            FileChannel fc2 = fos.getChannel();

            fc2.transferFrom(fc1, 0, fc1.size());

            fc1.close();

            fc2.close();

            fis.close();

            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String read(String path) {
        StringBuffer res = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path), "UTF-8"));
            while ((line = reader.readLine()) != null) {
                res.append(line + "\n");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    public static boolean write(String cont, String dist) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(new File(dist)), "utf-8");
            writer.write(cont);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class MyThread extends Thread {
        BufferedReader bf;

        public MyThread(InputStream input) {
            bf = new BufferedReader(new InputStreamReader(input));
        }

        public void run() {
            String line;
            try {
                line = bf.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = bf.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getProjectPath() throws UnsupportedEncodingException {
        URL url = Main.class.getProtectionDomain().getCodeSource()
                .getLocation();
        String filePath = URLDecoder.decode(url.getPath(), "utf-8");
        if (filePath.endsWith(".jar")) {//apptools.jar
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        } else if (filePath.endsWith("bin/")) {//eclipse
            filePath = filePath.substring(0, filePath.length() - 4);
        } else if (filePath.endsWith("build/classes/java/main/")) {//android studio
            filePath = filePath.substring(0, filePath.length() - 24);
        }
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    public static boolean isWindows() {

        String os = System.getProperty("os.name").toLowerCase();
        // windows
        return (os.indexOf("win") >= 0);

    }

    public static boolean isMac() {

        String os = System.getProperty("os.name").toLowerCase();
        // Mac
        return (os.indexOf("mac") >= 0);

    }

    public static boolean isUnix() {

        String os = System.getProperty("os.name").toLowerCase();
        // linux or unix
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

    }

}
