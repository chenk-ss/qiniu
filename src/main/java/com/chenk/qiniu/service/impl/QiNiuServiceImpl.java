package com.chenk.qiniu.service.impl;

import com.chenk.qiniu.config.CloudStorageConfig;
import com.chenk.qiniu.pojo.FileDBDTO;
import com.chenk.qiniu.pojo.FileDTO;
import com.chenk.qiniu.pojo.bean.FileBean;
import com.chenk.qiniu.repository.FileRepository;
import com.chenk.qiniu.service.QiNiuService;
import com.chenk.qiniu.util.TimeUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: chenke
 * @since: 2021/5/31
 */
@Service
public class QiNiuServiceImpl implements QiNiuService {

    @Autowired
    private FileRepository fileRepository;

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
        Zone zone = Zone.zone0();
        uploadManager = new UploadManager(new Configuration(zone));
        auth = Auth.create(config.getACCESS_KEY(), config.getSECRET_KEY());
        // 根据命名空间生成的上传token
        token = auth.uploadToken(config.getBucketName(), null, 60 * 60 * 24 * 30, null, true);
        bucketManager = new BucketManager(auth, new Configuration(zone));
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
            // 添加到MySQL
            {
                FileInfo fileInfo = queryByKey(putRet.key);
                FileBean fileBean = new FileBean();
                fileBean.setFileName(key);
                fileBean.setUrl(config.getDomain() + "/" + putRet.key);
                Date date = new Date();
                fileBean.setCreateTime(date);
                fileBean.setUpdateTime(date);
                fileBean.setSize(fileInfo.fsize);
                fileBean.setType(fileInfo.mimeType);
                fileBean.setStatus(1L);
                fileBean.setRemark(null);
                fileRepository.save(fileBean);
            }

            String path = config.getDomain() + "/" + putRet.key;
            return path;
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        return "";
    }

    public FileInfo queryByKey(String key) throws QiniuException {
        return bucketManager.stat(config.getBucketName(), key);
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
                fileDTO.setPutTime(TimeUtil.secondToDate(item.putTime / 10000, "yyyy-MM-dd HH:mm:ss"));
                fileDTO.setUrl(config.getDomain() + "/" + item.key);
                result.add(fileDTO);
            }
        }
        return result;
    }

    @Override
    public List<FileDBDTO> listFromDB(int pageNum, int size) {
        Pageable page = PageRequest.of(pageNum, size, Sort.by("createTime").descending());
        Page<FileBean> fileBeans = fileRepository.findAll(page);
        List<FileDBDTO> fileDTOList = new ArrayList<>();
        fileBeans.stream().forEach(fileBean -> {
            FileDBDTO fileDTO = new FileDBDTO();
            fileDTO.setId(fileBean.getId());
            fileDTO.setFileName(fileBean.getFileName());
            fileDTO.setUrl(fileBean.getUrl());
            fileDTO.setCreateTime(TimeUtil.dateToStr(fileBean.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            fileDTO.setUpdateTime(TimeUtil.dateToStr(fileBean.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
            fileDTO.setSize(fileBean.getSize());
            fileDTO.setType(fileBean.getType());
            fileDTO.setStatus(fileBean.getStatus());
            fileDTO.setRemark(fileBean.getRemark());
            fileDTOList.add(fileDTO);
        });
        return fileDTOList;
    }
}
