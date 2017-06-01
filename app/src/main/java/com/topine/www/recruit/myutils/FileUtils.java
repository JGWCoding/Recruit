package com.topine.www.recruit.myutils;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 操作文件的 ,注意的是要配置sd权限
 * 里面有获取SD卡状态的getSDstate等方法
 */
public class FileUtils {
    /**
     * 得到sd卡的状态
     * @return
     */
    public static boolean getSDState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    /**
     * 得到sd路径
     * @param context
     * @return
     */
    public static String getSDPath(Context context) {
        File sdDir = null;
        boolean sdCardExist = getSDState();//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        } else {
            sdDir = context.getFilesDir();
        }
        return sdDir.getPath();
    }

    /**
     * 在sd目录下创建一个文件夹 并返回这个File
     * @param context
     * @param fileCreateName
     * @return
     */
    public static File createDirectory(Context context,String fileCreateName) {
        File file = new File(getSDPath(context) + File.separator + fileCreateName);
        if (!file.exists()) file.mkdirs();
        return file;
    }
    /**
     * 在sd目录下创建一个文件 并返回这个File
     * @param context
     * @param fileCreateName
     * @return
     */
    public static File createFile(Context context,String fileCreateName) throws IOException {
        File file = new File(getSDPath(context) + File.separator + fileCreateName);
        if (!file.exists()) file.createNewFile();
        return file;
    }
    /**
     * 在sd目录下创建一个文件夹 并返回这个File的路径
     * @param context
     * @param fileCreateName
     * @return
     */
    public static String createDirectoryString(Context context,String fileCreateName) {
        return createDirectory(context,fileCreateName).getPath();
    }

    /**
     *如果文件夹过多的话可以自己拼接 例如 directory="directory/directory/directory"
     * @param context
     * @param directory
     * @param fileCreateName
     * @return
     * @throws IOException
     */
    public static File createFile(Context context,String directory,String fileCreateName) throws IOException {
        File file = new File(getSDPath(context)+ File.separator+ directory + File.separator + fileCreateName);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists())
            parentFile.mkdirs();
            file.createNewFile();
        }
        return file;
    }
    /**
     * 文件转base64
     * @param file
     * @throws Exception
     */
    public static String encodeBase64File(File file) throws Exception {
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }
    /**
     * 字符串转base64
     * @param source
     * @return
     * @throws Exception
     */
    public static String encodeBase64String(String source) throws Exception {
        byte[] bytes = source.getBytes("UTF-8");
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    //TODO 解析 base64 还有md5  创建一个目录
}
