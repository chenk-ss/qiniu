package com.chenk.qiniu.service.impl;

import com.chenk.qiniu.config.CloudStorageConfig;
import com.chenk.qiniu.pojo.FileDTO;
import com.chenk.qiniu.service.QiNiuService;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: chenke
 * @since: 2021/5/31
 */
@Service
public class QiNiuServiceImpl implements QiNiuService {

    private CloudStorageConfig config;
    // 七牛文件上传管理器
    private UploadManager uploadManager;
    private BucketManager bucketManager;
    private String token;
    // 七牛认证管理
    private Auth auth;

    public QiNiuServiceImpl(CloudStorageConfig config) {
        this.config = config;
        init();
    }

    private void init() {
        // 构造一个带指定Zone对象的配置类, 注意这里的Zone.zone0需要根据主机选择
        uploadManager = new UploadManager(new Configuration(Zone.zone0()));
        auth = Auth.create(config.getACCESS_KEY(), config.getSECRET_KEY());
        // 根据命名空间生成的上传token
        token = auth.uploadToken(config.getBucketName());
        bucketManager = new BucketManager(auth, new Configuration(Zone.zone0()));
    }

    @Override
    public String uploadQN(FileInputStream file, String key) {
        try {
            // 上传文件
            Response res = uploadManager.put(file, key, token, null, null);
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res.toString());
            }
            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(res.bodyString(), DefaultPutRet.class);

            String path = config.getDomain() + "/" + putRet.key;
            // 这个returnPath是获得到的外链地址,通过这个地址可以直接打开
            return path;
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        return "";
    }

    public List<FileDTO> list() {
        //文件名前缀
        String prefix = "";
        //每次迭代的长度限制，最大1000，推荐值 1000
        int limit = 10;
        //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";

        List<FileDTO> result = new ArrayList<>();
        //列举空间文件列表
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(config.getBucketName(), prefix, limit, delimiter);
        while (fileListIterator.hasNext()) {
            //处理获取的file list结果
            FileInfo[] items = fileListIterator.next();
            for (FileInfo item : items) {
                FileDTO fileDTO = new FileDTO();
                fileDTO.setFsize(item.fsize);
                fileDTO.setHash(item.hash);
                fileDTO.setEndUser(item.endUser);
                fileDTO.setKey(item.key);
                fileDTO.setMimeType(item.mimeType);
                fileDTO.setPutTime(item.putTime);
                fileDTO.setUrl(config.getDomain() + "/" + item.key);
                result.add(fileDTO);
            }
        }
        return result;
    }
}
