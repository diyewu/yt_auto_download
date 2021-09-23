package com.ytdl.task.detail;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DownloadVideo {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadVideo.class);
    private final static String FORMAT_MP3 = "mp3";
    private final static String FORMAT_MP4 = "mp4";
    private File taskFile;
    private File cacheFile;

    private final static String taskFileName = "videoTask.txt";
    private final static String cacheFileName = "downloadcache.txt";

    private static Map<String, String> lastVideoCache = new HashMap<>();

    private final static String checkCmdpre = "youtube-dl --get-id --playlist-end 1 ";
    private final static String downloadMp3Cmdpre = "youtube-dl -o /opt/download/%(playlist)s/%(title)s.%(ext)s -x --audio-format mp3 --audio-quality 0 --playlist-end 1 ";
    private final static String downloadMp4Cmdpre = "youtube-dl -o /opt/download/%(playlist)s/%(title)s.%(ext)s --playlist-end 1 ";
    private final static String rsyncCmd = "rsync -auvzP -e ssh /opt/download/ root@118.24.85.246:/opt/ytdownload";//rsync结合ssh同步文件
    private final static String deleteFileCmd = "rm -rf /opt/download/*";//同步完成后删除文件


    /**
     * 自动更新
     */
    public void dlUpdate() {
        try {
            Runtime.getRuntime().exec("youtube-dl -U");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exec() throws InterruptedException {
        if (lastVideoCache.isEmpty()) {
            lastVideoCache = readTaskFile(cacheFileName);
            LOGGER.info("读取cache..." + lastVideoCache);
        }
        Map<String, String> taskMap = readTaskFile(taskFileName);
        for (Map.Entry<String, String> entry : taskMap.entrySet()) {
            String url = entry.getKey();
            String format = entry.getValue();
            String preCmd;
            switch (format) {
                case FORMAT_MP3:
                    preCmd = downloadMp3Cmdpre;
                    break;
                case FORMAT_MP4:
                    preCmd = downloadMp4Cmdpre;
                    break;
                default:
                    preCmd = downloadMp4Cmdpre;
            }

            String cmd = checkCmdpre + url;
            String result = execCmd(cmd);
            if (StringUtils.isNotBlank(result) && !result.equals(lastVideoCache.get(url))) {
                /*调用脚本*/
                cmd = "sh download.sh " + url + " " + execCmd(cmd);
                LOGGER.info("executing ...." + cmd);
                execCmd(cmd);
                /*执行命令行
                //下载
                cmd = preCmd + url;
                LOGGER.info("Downloading ..."+cmd);
                String cmd1 = execCmd(cmd);
                LOGGER.info(cmd1);
                //同步到腾讯服务器，下载快
                String cmd2 = execCmd(rsyncCmd);
                LOGGER.info(cmd2);
                //同步完成后，删除文件
                *//*String cmd3 = execCmd(deleteFileCmd);
                LOGGER.info(cmd3);*//*
                Thread.sleep(60*1000);*/
                //put&writelocalfile
                lastVideoCache.put(url, result);
                write2File(cacheFileName, url + "," + result);
            }

        }
    }

    private static String execCmd(String command) {
        String resp = "";
        Process process;
        List<String> processList = new ArrayList<>();
        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                processList.add(line);
                LOGGER.info(line);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            resp = StringUtils.join(processList.toArray(), ",\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

    /**
     * 获取jar包所在的路径
     *
     * @return
     */
    private String getJarPath() {
        ApplicationHome h = new ApplicationHome(getClass());
        File jarF = h.getSource();
        return jarF.getParentFile().toString();

    }

    private String getFolderPath() {
        return getJarPath() + File.separator + "taskfile";
    }


    /**
     * 读取本地文件
     *
     * @return
     */
    private Map<String, String> readTaskFile(String fileName) {
        Map<String, String> resp = new HashMap<>();
        BufferedReader in = null;
        try {
            String taskFilePath = getFolderPath() + File.separator + fileName;
            File tmpFile = new File(taskFilePath);
            if (!tmpFile.exists()) {
                File pathFile = new File(getFolderPath());
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                boolean fileCreate = tmpFile.createNewFile();
                LOGGER.info("----------" + fileCreate);
                if (!fileCreate) {
                    throw new RuntimeException("Create file error");
                }
            }
            in = new BufferedReader(new FileReader(tmpFile));
            String tmp;
            while ((tmp = in.readLine()) != null) {
                String[] split = tmp.split(",");
                resp.put(split[0], split[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return resp;
    }

    public void write2File(String fileName, String content) {
        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            if (cacheFile == null) {
                String taskFilePath = getFolderPath() + File.separator + fileName;
                cacheFile = new File(taskFilePath);
            }
            if (!cacheFile.exists()) {
                File pathFile = new File(getFolderPath());
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                boolean fileCreate = cacheFile.createNewFile();
                if (!fileCreate) {
                    throw new RuntimeException("Create file error");
                }
            }
            fw = new FileWriter(cacheFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(content);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
