/**
 * Created by durban126 on 16/7/21.
 */
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class FileUtil {

    static Logger logger = Logger.getLogger("channel_log");
    private final static String KEY = "AD67EA2F3FQ6E5GDE376DFG0"; // 加解密用到的常量字符串

    /**
     * Str-File
     */
    public static File getFileByBaseStr(String fileName, String str) {
        try {

            // byte[] bytes = new BASE64Decoder().decodeBuffer(str);
            byte[] bytes = new Base64().decode(str);
            // 将字符串转换为byte数组//
            // byte[] bytes = Base64Util.decode(str);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[1024];

            File file = new File(fileName);
            FileOutputStream out = new FileOutputStream(file);
            int bytesum = 0;
            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread); // 文件写操作
            }
            out.close();

            return file;
        } catch (Exception e) {
            logger.error("base64 decode pross error", e);
            return null;
        }
    }

    /**
     * File-Str
     */
    public static String getBaseStrByFile(File file) {
        try {

            InputStream in = new FileInputStream(file);
            // 返回文件的字节长度
            in.available();
            byte[] bytes = new byte[in.available()];
            // 将文件中的内容读入到数组中
            in.read(bytes);
            // String strBase64 = new BASE64Encoder().encode(bytes); //
            String strBase64 = new Base64().encode(bytes); //
            // 将字节流数组转换为字符串
            // String strBase64 = Base64Util.encode(bytes);
            in.close();

            return strBase64;
        } catch (Exception e) {
            logger.error("base64 encode pross error", e);
            return StringUtils.EMPTY;
        }
    }

    public static Properties readProperties(String proName) throws Exception {
        InputStream in = new BufferedInputStream(new FileInputStream(proName));
        Properties p = new Properties();
        p.load(in);
        return p;
    }

    /**
     * 将base64字符解码保存文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static File getFileByBaseStr2(String targetPath, String base64Code)
            throws Exception {
        File file = new File(targetPath);
        byte[] buffer = new Base64().decode(base64Code);
        FileOutputStream out = new FileOutputStream(file);
        out.write(buffer);
        out.close();
        return file;

    }

    /**
     * 将文件转成base64 字符串
     *
     * @param path文件路径
     * @return *
     * @throws Exception
     */
    public static String getBaseStrByFile2(File file) throws Exception {
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new Base64().encode(buffer);

    }

    /**
     * 将文件压缩加密并转成base64 字符串
     *
     * @param path文件路径
     * @return *
     * @throws Exception
     */
    public static String compressEncryptFile(String filePath, String jmFilePath)
            throws Exception {
        // 回盘/兑付文件
        File hpFile = new File(filePath);
        // 压缩文件
        File gzFile = GzipFileUtil.compress(hpFile);
        // 加密文件
        File jmFile = new File(jmFilePath + "jmFile_" + gzFile.getName());
        FileOutputStream fos = null;
        FileInputStream fis = null;

        fis = new FileInputStream(gzFile);
        fos = new FileOutputStream(jmFile);
        DesEncryptUtils.encrypt(KEY.getBytes("utf-8"), fis, fos);
        fos.flush();
        fos.close();
        fis.close();
        String file_content = FileUtil.getBaseStrByFile(jmFile);
        // 删除垃圾文件
        gzFile.delete();
        jmFile.delete();
        return file_content;
    }

    /**
     * 将str解密解压缩并转换成文件
     *
     * @param path文件路径
     * @return *
     * @throws Exception
     */
    public static File dncryptUncompressFile(String file_content,
                                             String file_name, String gzFilePath, String jmFilePath)
            throws Exception {
        // 文件转换
        File jmFile = FileUtil.getFileByBaseStr(jmFilePath + file_name + ".gz",
                file_content);
        File gzFile = new File(gzFilePath + jmFile.getName());
        FileInputStream fis;
        FileOutputStream fos;
        fis = new FileInputStream(jmFile);
        fos = new FileOutputStream(gzFile);
        DesEncryptUtils.decrypt(KEY.getBytes("utf-8"), fis, fos);
        fos.flush();
        fos.close();
        fis.close();

        File sourceFile = GzipFileUtil.deCompress(gzFile); // 解压

        //删除垃圾文件
        gzFile.delete();

        return sourceFile;
    }

    /**
     * 方法名称:transStringToMap 传入参数:mapString 形如 username'chenziwen^password'1234
     * 返回值:Map
     */
    public static Map<String, String> transStringToMap(String mapString) {
        Map map = new HashMap();
        java.util.StringTokenizer items;
        for (StringTokenizer entrys = new StringTokenizer(mapString, "^"); entrys
                .hasMoreTokens(); map.put(items.nextToken(),
                items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))
            items = new StringTokenizer(entrys.nextToken(), "'");
        return map;
    }

    public static void main(String[] args) {
        String str = getBaseStrByFile(new File("e:/a.txt"));
        getFileByBaseStr("e:/b.txt", str);
    }

}
