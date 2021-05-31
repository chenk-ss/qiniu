package com.chenk.qiniu.service;

import com.chenk.qiniu.pojo.FileDTO;

import java.io.FileInputStream;
import java.util.List;

/**
 * @author: chenke
 * @since: 2021/5/31
 */
public interface QiNiuService {
    String uploadQN(FileInputStream file, String path);

    List<FileDTO> list();
}
