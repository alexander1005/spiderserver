package server.spider.impl;

import lombok.Data;

import java.util.Date;

@Data
public class Model {
  private Integer id;
  private String sourceId;
  private String name;
  // 型号
  private String model;
  // 商标
  private String branch;
  // 价格
  private String price;
  // 详情描述
  private String detail;
  // 标题
  private String title;
  // 图表
  private String image;
  // 时间
  private Date time;
  // 详情url
  private String url;
  // 时间
  private Date createTime;
  // 时间
  private String source;
}
