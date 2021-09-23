package com.ytdl.util;

public class YoutubeUtil {

    /**
     * 判断当前系统是否windows
     * @return
     */
    public static boolean osIsWin() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return true;
        }
        return false;
    }

    /**
     * 获取引号
     * @return
     */
    public static String getQuotationMark(){
        if(osIsWin()){
            return "\"";
        }
        return "'";
    }

    /**
     * 获取百分号
     * @return
     */
    public static String getPercentSign(){
        if(osIsWin()){
            return "%%";
        }
        return "%";
    }


}
