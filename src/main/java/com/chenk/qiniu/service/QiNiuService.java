package com.chenk.qiniu.service;

import com.chenk.qiniu.pojo.FileDBDTO;
import com.chenk.qiniu.pojo.FileDTO;

import java.io.FileInputStream;
import java.util.List;

/**
 * @author: chenke
 * @since: 2021/5/31
 */
public interface QiNiuService {
    String uploadQN(FileInputStream file, String path);

    /**
     *  通过查询七牛云查询列表
     * @return
     */
    List<FileDTO> list();

    List<FileDBDTO> listFromDB();
}
