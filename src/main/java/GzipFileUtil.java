/**
 * Created by durban126 on 16/7/21.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

public class GzipFileUtil {

    private static Logger logger = Logger.getLogger(GzipFileUtil.class);
    public static final String GZ_SUFFIX = ".gz";
    public static final String SEPARATOR_POINT = ".";


    /**
     * 压缩文件
     * 压缩好的文件生成在输入文件同一目录，以.gz结尾
     *
     * @param fileIn
     * @return
     */
    public static File compress(File fileIn) {
        System.out.println(fileIn.getName());
        try {
            //目标输出文件
            String outName = fileIn.getParent() + File.separator + fileIn.getName() + GZ_SUFFIX;
            File fileOut = new File(outName);
            //打开需压缩文件作为文件输入流
            FileInputStream fins = new FileInputStream(fileIn);
            //建立压缩文件输出流
            FileOutputStream fouts = new FileOutputStream(fileOut);
            GZIPOutputStream gzouts = new GZIPOutputStream(fouts);
            //设定读入缓冲区尺寸
            byte[] buf = new byte[1024];
            int num;
            while ((num = fins.read(buf)) != -1) {
                gzouts.write(buf, 0, num);
            }
            //!!!关闭流,必须关闭所有输入输出流.保证输入输出完整和释放系统资源.
            gzouts.close();
            fouts.close();
            fins.close();

            return fileOut;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解压文件
     * 解压好的文件生成在输入文件同一目录，假设输入文件是test.txt.gz,输出为文件为test__UNGZIP.txt
     *
     * @param fileIn
     * @return
     */
    public static File deCompress(File fileIn) {
        try {
            //目标输出文件
            String inName = fileIn.getName();
            String inSuffix = inName.substring(inName.indexOf(SEPARATOR_POINT), inName.indexOf(GZ_SUFFIX));
            String outName = fileIn.getParent() + File.separator + inName.substring(0, inName.indexOf(SEPARATOR_POINT)) + "_UNGZIP" + inSuffix;
            logger.info("解压缩文件地址：" + outName);
            //目标输出文件
            File fileOut = new File(outName);
            //建立gzip压缩文件输入流
            FileInputStream fins = new FileInputStream(fileIn);
            //建立gzip解压工作流
            GZIPInputStream gzins = new GZIPInputStream(fins);
            //建立解压文件输出流
            FileOutputStream fouts = new FileOutputStream(fileOut);
            byte[] buf = new byte[1024];
            int num;
            while ((num = gzins.read(buf, 0, buf.length)) != -1) {
                fouts.write(buf, 0, num);
            }
            gzins.close();
            fouts.close();
            fins.close();

            return fileOut;
        } catch (IOException e) {

            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
