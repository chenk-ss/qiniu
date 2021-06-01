package com.chenk.qiniu.controller;

import com.chenk.qiniu.pojo.FileDTO;
import com.chenk.qiniu.service.QiNiuService;
import com.chenk.qiniu.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author: chenke
 * @since: 2021/5/31
 */
@RestController
@RequestMapping("file")
public class FileController {

    @Autowired
    private QiNiuService qiNiuService;

    @GetMapping("query/list")
    public List<FileDTO> queryList() {
        return qiNiuService.list();
    }


    @PostMapping(value = "/upload")
    private String upload(@RequestParam("file") MultipartFile file) throws IOException {
        // 获取文件的名称
        String fileName = file.getOriginalFilename();
        // 使用工具类根据上传文件生成唯一文件名称
        String imgName = StringUtil.getRandomFileName(fileName);
        if (!file.isEmpty()) {
            FileInputStream inputStream = (FileInputStream) file.getInputStream();
            String path = qiNiuService.uploadQN(inputStream, imgName);
            System.out.print("七牛云返回的链接:" + path);
            return path;
        }
        return "上传失败";
    }

}
