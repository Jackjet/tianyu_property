package com.vguang.eneity;

import java.io.Serializable;

import javax.activation.FileDataSource;

public class Email implements Serializable {
	private static final long serialVersionUID = 2413945826916154176L;

	private String from; // 发件人
	private String to; // 收件人
	private String cc; // 抄送人
	private String subject; // 邮件主题
	private String content; // 文本内容
	private String picid; // 图片名称
	private FileDataSource img;
	// private MultipartFile[] attachment = new MultipartFile[0];
	
	public Email() {
		super();
	}
	
	public Email(String to, String subject, String content) {
		this(to, subject, content, null, null);
	}
	
	public Email(String to, String subject, String content, String picid, FileDataSource img) {
		super();
		this.to = to;
		this.subject = subject;
		this.content = content;
		this.picid = picid;
		this.img = img;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getCc() {
		return cc;
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}

	public String getPicid() {
		return picid;
	}

	public FileDataSource getImg() {
		return img;
	}

}
