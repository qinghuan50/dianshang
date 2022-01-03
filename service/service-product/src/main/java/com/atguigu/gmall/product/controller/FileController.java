package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.utils.FastDFSUtil;
import lombok.SneakyThrows;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/admin/product")
public class FileController {

    @Value("${fileServer.url}")
    private String url;

    /**
     * 文件上传的方法
     * @param file
     * @return
     */
    @SneakyThrows
    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestParam("file") MultipartFile file){
        /**
         * 直接调用封装好的静态上传文件方法
         */
        String upload = FastDFSUtil.upload(file);

        return Result.ok(url + upload);
    }


}
