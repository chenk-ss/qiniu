package com.chenk.qiniu.repository;

import com.chenk.qiniu.pojo.bean.FileBean;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: chenke
 * @since: 2021/5/31
 */
public interface FileRepository extends JpaRepository<FileBean, String> {
}
