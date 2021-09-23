package com.ytdl.task;

import com.ytdl.task.detail.DownloadVideo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@EnableScheduling
public class DownloadYoutubeSchedule {
    @Autowired
    DownloadVideo downloadVideo;

    /**
     * 定时下载
     */
    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void ytdl() {
        try {
            downloadVideo.exec();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定时更新
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void ytUpdate() {
        try {
            downloadVideo.dlUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
