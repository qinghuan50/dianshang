package com.atguigu.gmall.product.utils;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FastDFSUtil {

    /**
     * 静态代码块，类加载的时候会进行初始化，只会初始化一次
     */
    static {
        try {
            //获取配置文件
            ClassPathResource pathResource = new ClassPathResource("tracker.conf");
            //对tracker进行初始化
            ClientGlobal.init(pathResource.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片上传的方法
     * @param file
     * @return
     */
    public static String upload(MultipartFile file){
        try {
            //获取tracker的连接对象信息
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();

            //通过tracker获取storage
            StorageClient storageClient = new StorageClient(trackerServer, null);

            /**
             *  通过storage上传文件
             *  byte[] file_buff, 文件的字节码
             *  String file_ext_name, 文件的后缀名
             *  NameValuePair[] meta_list   文件的附加参数
             */
            String[] uploadAppenderFile = storageClient.upload_appender_file(
                    file.getBytes(),
                    StringUtils.getFilenameExtension(file.getOriginalFilename()),
                    null);
            //返回组名和全量路径名
            return uploadAppenderFile[0] + "/" + uploadAppenderFile[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 出现问题返回空值
         */
        return null;
    }


    /**
     * 图片删除的方法
     * @param groupName
     * @param fileName
     * @return
     */
    public static boolean delFile(String groupName, String fileName){
        try {
            //获取tracker的连接对象信息
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();

            //通过tracker获取storage
            StorageClient storageClient = new StorageClient(trackerServer, null);

            /**
             * 通过组名和文件全量路径删除
             */
            int deleteFile = storageClient.delete_file(groupName, fileName);

            //判断删除是否成功
           return deleteFile >= 0?true:false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 出现问题返回空值
         */
        return false;
    }
}
