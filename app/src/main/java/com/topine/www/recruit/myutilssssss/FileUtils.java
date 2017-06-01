package com.topine.www.recruit.myutilssssss;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
            sdDir = context.getFilesDir(); //返回的是本地的系统路径,当有外部存储卡会转移存储文件
//            sdDir = context.getCacheDir(); //返回的是这个APP目录下的缓存文件夹路径
        }
        return sdDir.getPath();
    }

    /**
     * 在sd目录下创建一个文件夹 并返回这个File
     * @param context
     * @param fileCreateName
     * @return
     */
    public static File createDirs(Context context, String fileCreateName) {
        File file = new File(getSDPath(context) + File.separator + fileCreateName);
        if (!file.exists()) file.mkdirs();
        return file;
    }

    /**
     * 在sd目录下创建一个文件夹 并返回这个File的路径
     * @param context
     * @param fileCreateName
     * @return
     */
    public static String createDirsString(Context context,String fileCreateName) {
        return createDirs(context,fileCreateName).getPath();
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

    /**
     * 解析base64加密的字符串
     * @param source
     * @return
     * @throws Exception
     */
    public static byte[] decodeBase64String(String source) throws Exception {
        return Base64.decode(source, Base64.DEFAULT);
    }
    /** 复制文件，可以选择是否删除源文件 */
    public static boolean copyFile(String srcPath, String destPath,
                                   boolean deleteSrc) {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);
        return copyFile(srcFile, destFile, deleteSrc);
    }

    /** 复制文件，可以选择是否删除源文件 */
    public static boolean copyFile(File srcFile, File destFile,
                                   boolean deleteSrc) {
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int i = -1;
            while ((i = in.read(buffer)) > 0) {
                out.write(buffer, 0, i);
                out.flush();
            }
            if (deleteSrc) {
                srcFile.delete();
            }
        } catch (Exception e) {
            LogUtils.e(e);
            return false;
        } finally {
            IOUtils.close(out);
            IOUtils.close(in);
        }
        return true;
    }
    /** 判断文件是否可写 */
    public static boolean isWriteable(String path) {
        try {
            if (TextUtils.isEmpty(path)) {
                return false;
            }
            File f = new File(path);
            return f.exists() && f.canWrite();
        } catch (Exception e) {
            LogUtils.e(e);
            return false;
        }
    }
    /** 修改文件的权限,例如"777"等 */
    public static void chmod(String path, String mode) {
        try {
            String command = "chmod " + mode + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }
    /**
     * 把数据写入文件
     * @param is   数据流
     * @param path  文件路径
     * @param recreate 如果文件存在，是否需要删除重建
     * @return 是否写入成功
     */
    public static boolean writeFile(InputStream is, String path,
                                    boolean recreate) {
        boolean res = false;
        File f = new File(path);
        FileOutputStream fos = null;
        try {
            if (recreate && f.exists()) {
                f.delete();
            }
            if (!f.exists() && null != is) {
                File parentFile = new File(f.getParent());
                parentFile.mkdirs();
                int count = -1;
                byte[] buffer = new byte[1024];
                fos = new FileOutputStream(f);
                while ((count = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
                res = true;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fos);
            IOUtils.close(is);
        }
        return res;
    }
    /**
     * 把字符串数据写入文件
     *
     * @param content
     *            需要写入的字符串
     * @param path
     *            文件路径名称
     * @param append
     *            是否以添加的模式写入
     * @return 是否写入成功
     */
    public static boolean writeFile(byte[] content, String path, boolean append) {
        boolean res = false;
        File f = new File(path);
        RandomAccessFile raf = null;
        try {
            if (f.exists()) {
                if (!append) {
                    f.delete();
                    f.createNewFile();
                }
            } else {
                f.createNewFile();
            }
            if (f.canWrite()) {
                raf = new RandomAccessFile(f, "rw");
                raf.seek(raf.length());
                raf.write(content);
                res = true;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(raf);
        }
        return res;
    }

    /**
     * 把字符串数据写入文件
     *
     * @param content 需要写入的字符串
     * @param path  文件路径名称
     * @param append    是否以添加的模式写入
     * @return 是否写入成功
     */
    public static boolean writeFile(String content, String path, boolean append) {
        return writeFile(content.getBytes(), path, append);
    }

    /**
     * 把键值对写入文件
     * @param filePath  文件路径
     * @param key 键
     * @param value 值
     * @param comment  该键值对的注释
     */
    public static void writeProperties(String filePath, String key,
                                       String value, String comment) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(filePath)) {
            return;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);// 先读取文件，再把键值对追加到后面
            p.setProperty(key, value);
            fos = new FileOutputStream(f);
            p.store(fos, comment);
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
            IOUtils.close(fos);
        }
    }

    /** 根据值读取 */
    public static String readProperties(String filePath, String key,
                                        String defaultValue) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(filePath)) {
            return null;
        }
        String value = null;
        FileInputStream fis = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);
            value = p.getProperty(key, defaultValue);
        } catch (IOException e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
        }
        return value;
    }

    /** 把字符串键值对的map写入文件 */
    public static void writeMap(String filePath, Map<String, String> map,
                                boolean append, String comment) {
        if (map == null || map.size() == 0 || TextUtils.isEmpty(filePath)) {
            return;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            Properties p = new Properties();
            if (append) {
                fis = new FileInputStream(f);
                p.load(fis);// 先读取文件，再把键值对追加到后面
            }
            p.putAll(map);
            fos = new FileOutputStream(f);
            p.store(fos, comment);
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
            IOUtils.close(fos);
        }
    }

    /** 把字符串键值对的文件读入map */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map<String, String> readMap(String filePath,
                                              String defaultValue) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        Map<String, String> map = null;
        FileInputStream fis = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);
            map = new HashMap<String, String>((Map) p);// 因为properties继承了map，所以直接通过p来构造一个map
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
        }
        return map;
    }

    /**
     * 拷贝文件
     * @param src 源文件路径
     * @param des 目标文件路径
     * @param delete 是否删除源文件
     * @return 返回是否拷贝成功
     */
    public static boolean copy(String src, String des, boolean delete) {
        File file = new File(src);
        if (!file.exists()) {
            return false;
        }
        File desFile = new File(des);
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new FileOutputStream(desFile);
            byte[] buffer = new byte[1024];
            int count = -1;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
                out.flush();
            }
        } catch (Exception e) {
            LogUtils.e(e);
            return false;
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
        }
        if (delete) {
            file.delete();
        }
        return true;
    }
}
