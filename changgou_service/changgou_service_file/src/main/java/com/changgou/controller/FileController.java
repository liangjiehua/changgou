package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSClient;
import entity.Result;
import entity.StatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.controller
 * @date 2019-9-2
 */
@RestController
@CrossOrigin
public class FileController {

    @RequestMapping("upload")
    public Result upload(MultipartFile file) throws IOException {
        //1、包装FastDFS上传文件对象
        FastDFSFile dfsFile = new FastDFSFile(
                file.getOriginalFilename(),  //原来文件名
                file.getBytes(),  //文件字节数组
                StringUtils.getFilenameExtension(file.getOriginalFilename())  //后缀
         );
        //2、上传文件
        String[] uploadResult = FastDFSClient.upload(dfsFile);
        //3、拼接图片的返回url，返回结果
        //http://192.168.211.132:8080/group1/M00/00/00/wKjThF1pAcKAYNhxAA832942OCg928.jpg
        String url = FastDFSClient.getTrackerUrl() + uploadResult[0] + "/" + uploadResult[1];
        return new Result(true, StatusCode.OK,url);
    }
}
