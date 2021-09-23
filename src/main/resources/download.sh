#!/bin/bash

#第一个参数是url，第二个参数是格式（mp3，mp4）

downloadMp3Cmdpre="youtube-dl -o /opt/download/%(playlist)s/%(title)s.%(ext)s -x --audio-format mp3 --playlist-end 1 ";

downloadMp4Cmdpre="youtube-dl -o /opt/download/%(playlist)s/%(title)s.%(ext)s --playlist-end 1 ";


if [ "$2" = "mp3" ]
then
$downloadMp3Cmdpre $1
echo "donwloading mp3"
else
$downloadMp4Cmdpre $1
echo "downloading mp4"
fi
echo "start transport"
rsync -av -e ssh /opt/download/ root@118.24.85.246:/opt/ytdownload
echo "end transport"

mv /opt/download/* /opt/download1

rm -rf /opt/download/*


