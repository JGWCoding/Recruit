package com.topine.www.recruit.bean;

/**
 * Created by Administrator on 2017/2/22.
 */
public class UserBean {
    private int id;
    private String workId;
    private int workState;
    private String creatDate;
    private String imgUrls;//影相保存地址,多张以 ， 号分割开；仅当workState为4时才有值；
    private int upload; //是否已上传，0未上传，1已上传;
    private String time;
    private String tag;
    private String desc;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public int getWorkState() {
        return workState;
    }

    public void setWorkState(int workState) {
        this.workState = workState;
    }

    public String getCreatDate() {
        return creatDate;
    }

    public void setCreatDate(String creatDate) {
        this.creatDate = creatDate;
    }

    public String getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(String imgUrls) {
        this.imgUrls = imgUrls;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public void setUpload(int tag){
        this.upload=tag;
    }
    public int getUpload(){
        return this.upload;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
