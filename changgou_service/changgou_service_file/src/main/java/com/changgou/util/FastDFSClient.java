package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

/**
 * FastDFS入门-文件操作
 * 实现信息获取、文件上传、文件下载、文件删除的相关操作
 * @author Steven
 * @version 1.0
 * @description com.changgou.util
 * @date 2019-9-2
 */
public class FastDFSClient {

    static {
        try {
            //1、获取配置文件路径-filePath = new ClassPathResource("fdfs_client.conf").getPath()
            String conf_filename = new ClassPathResource("fdfs_client.conf").getPath();
            //2、加载配置文件-ClientGlobal.init(配置文件路径)
            ClientGlobal.init(conf_filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取TrackerServer对象
     * @return
     */
    public static TrackerServer getTrackerServer(){
        TrackerServer trackerServer = null;
        try {
            //3、创建一个TrackerClient对象。直接new一个。
            TrackerClient trackerClient = new TrackerClient();
            //4、使用TrackerClient对象创建连接，getConnection获得一个TrackerServer对象。
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trackerServer;
    }

    /**
     * 创建StorageClient对象
     * @return
     */
    public static StorageClient getStorageClient(){
        //5、创建一个StorageClient对象，直接new一个，需要两个参数TrackerServer对象、null
        StorageClient storageClient = new StorageClient(getTrackerServer(), null);
        return storageClient;
    }

    /**
     * 文件上传
     * @param fastDFSFile 文件上传包装内容对象
     * @return String[组名,文件的完整路径]
     */
    public static String[] upload(FastDFSFile fastDFSFile){

        try {
            //创建附加参数
            NameValuePair[] meta_list = new NameValuePair[1];
            meta_list[0] = new NameValuePair("author",fastDFSFile.getAuthor());
            //上传文件-upload_file(字节数组，后缀名,附加参数)
            //fileId=group1/M00/00/00/wKjThF1pAcKAYNhxAA832942OCg928.jpg
            //返回值:String[组名,文件的完整路径]
            String[] uploadResult = getStorageClient().upload_file(fastDFSFile.getContent(), fastDFSFile.getExt(), meta_list);
            return uploadResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取文件信息
     * @param group_name 组名
     * @param remote_filename 文件完整路径
     * @return FileInfo
     */
    public static FileInfo getFileInfo(String group_name, String remote_filename){
        try {
            FileInfo info = getStorageClient().get_file_info(group_name,remote_filename);
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     * @param groupName 组名
     * @param remoteFilename 文件完整路径
     * @return
     */
    public static InputStream downloadFile(String groupName, String remoteFilename){
        try {
            byte[] bytes = getStorageClient().download_file(groupName, remoteFilename);
            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件删除
     * @param groupName 组名
     * @param remoteFilename 文件完整路径
     * @return
     */
    public static void deleteFile(String groupName, String remoteFilename){
        try {
            getStorageClient().delete_file(groupName, remoteFilename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取组(Storage)信息
     * @param groupName 组名
     * @return
     */
    public static StorageServer getStorageServer(String groupName){
        try {
            //3、创建一个TrackerClient对象。直接new一个。
            TrackerClient trackerClient = new TrackerClient();
            //4、使用TrackerClient对象创建连接，getConnection获得一个TrackerServer对象。
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取组信息
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer, groupName);
            return storeStorage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据文件组名和文件存储路径获取Storage服务的IP、端口信息
     * @param groupName 组名
     * @param remoteFilename 完件完整路径
     * @return
     */
    public static ServerInfo[] getServerInfo(String groupName,String remoteFilename){
        try {
            //3、创建一个TrackerClient对象。直接new一个。
            TrackerClient trackerClient = new TrackerClient();
            //4、使用TrackerClient对象创建连接，getConnection获得一个TrackerServer对象。
            TrackerServer trackerServer = trackerClient.getConnection();
            ServerInfo[] infos = trackerClient.getFetchStorages(trackerServer, groupName, remoteFilename);
            return infos;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Tracker服务器地址
     * @return
     */
    public static String getTrackerUrl(){
        try {
            //3、创建一个TrackerClient对象。直接new一个。
            TrackerClient trackerClient = new TrackerClient();
            //4、使用TrackerClient对象创建连接，getConnection获得一个TrackerServer对象。
            TrackerServer trackerServer = trackerClient.getConnection();
            //http://192.168.211.132:8080/group1/M00/00/00/wKjThF1pAcKAYNhxAA832942OCg928.jpg
            String trackerUrl = "http://" + trackerServer.getInetSocketAddress().getHostString() + ":"
                    + ClientGlobal.getG_tracker_http_port() + "/";
            return trackerUrl;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        //获取文件信息
        //group1/M00/00/00/wKjThF1s2xaAUTPIAA832942OCg250.jpg
        /*FileInfo info = getFileInfo("group1", "M00/00/00/wKjThF1s2xaAUTPIAA832942OCg250.jpg");
        System.out.println(info);*/

        //文件下载
        /*try {
            //文件下载，保存到本地
            InputStream is = downloadFile("group1", "M00/00/00/wKjThF1s2xaAUTPIAA832942OCg250.jpg");
            //构建输出流
            OutputStream out = new FileOutputStream("D:/a.jpg");
            //定义缓冲区
            byte[] buff = new byte[1024];
            //循环读取输入流到缓冲区中
            while (is.read(buff) > -1){
                //输出文件
                out.write(buff);
            }
            out.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //文件删除
        //deleteFile("group1", "M00/00/00/wKjThF1s2xaAUTPIAA832942OCg250.jpg");
        //

        //获取组信息
        //StorageServer storageServer = getStorageServer("group1");
        //System.out.println("服务器下标为："+storageServer.getStorePathIndex());
        //System.out.println("服务器IP为："+storageServer.getInetSocketAddress());

        //根据文件组名和文件存储路径获取Storage服务的IP、端口信息
        ServerInfo[] infos = getServerInfo("group1", "M00/00/00/wKjThF1s2xaAUTPIAA832942OCg250.jpg");
        for (ServerInfo serverInfo : infos) {
            System.out.println(serverInfo.getIpAddr() + ":" + serverInfo.getPort());
        }
    }
}
