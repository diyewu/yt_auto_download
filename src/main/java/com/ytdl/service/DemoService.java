package com.ytdl.service;

import com.ytdl.entity.AiccUser;

import java.util.List;

public interface DemoService {

    /**
     * 获取角色数据
     */
    List<AiccUser> getUser();

}
