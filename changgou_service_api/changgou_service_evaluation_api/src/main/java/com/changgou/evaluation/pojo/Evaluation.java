package com.changgou.evaluation.pojo;

import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/****
 * @Author:shenkunlin
 * @Description:Evaluation构建
 * @Date 2019/6/14 19:13
 *****/
@Table(name="tb_evaluation")
public class Evaluation implements Serializable{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Integer id;//主键

    @Column(name = "username")
	private String username;//用户名

    @Column(name = "order_id")
	private String orderId;//订单号

    @Column(name = "receipt_time")
	private Date receiptTime;//收货时间

    @Column(name = "sku_id")
	private Long skuId;//sku_id

    @Column(name = "content")
	private String content;//评价内容

    @Column(name = "evaluation_time")
	private Date evaluationTime;//评价时间

    @Column(name = "evaluation_img")
	private String evaluationImg;//评价图片

    @Column(name = "status")
	private String status;//评价状态 0未评价 1已评价

    @Column(name = "star")
	private String star;//星数 0,1差评 2,3中评 4,5好评

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Date getReceiptTime() {
		return receiptTime;
	}

	public void setReceiptTime(Date receiptTime) {
		this.receiptTime = receiptTime;
	}

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getEvaluationTime() {
		return evaluationTime;
	}

	public void setEvaluationTime(Date evaluationTime) {
		this.evaluationTime = evaluationTime;
	}

	public String getEvaluationImg() {
		return evaluationImg;
	}

	public void setEvaluationImg(String evaluationImg) {
		this.evaluationImg = evaluationImg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}
}
