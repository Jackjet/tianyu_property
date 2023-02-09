package com.vguang.service.impl;

//@Service("mailService")
//public class MailService implements IMailService{
//	private static final Logger log = LoggerFactory.getLogger(MailService.class);
//	@Autowired
//    private JavaMailSender javaMailSender;
//	@Autowired
//    private SimpleMailMessage simpleMailMessage; 
//    
//    /**
//     * 发送简单文本邮件
//     */
//    @Override
//    public void sendEmail(Email email) {
//    	log.info("发送简单文本邮件");
//		try {
//			// MimeMessages为复杂邮件模板，支持文本、附件、html、图片等。
//			MimeMessage message = javaMailSender.createMimeMessage();
//			// 创建MimeMessageHelper对象，处理MimeMessage的辅助类
//			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
//
//			messageHelper.setFrom(simpleMailMessage.getFrom());
//
//			String subject = email.getSubject();
//			String to = email.getTo();
//			String content = email.getContent();
//			if (subject != null) {
//				messageHelper.setSubject(subject);
//			} else {
//				messageHelper.setSubject(simpleMailMessage.getSubject());
//			}
//			messageHelper.setTo(to);
////			messageHelper.setText(content, true);
//			messageHelper.setText(content, true);
//			
//			javaMailSender.send(message);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//    
//    /**
//     * 发送富文本邮件
//     * @param mail
//     */
//    @Override
//	public void sendRichEmail(Email mail) {
//		// 建立邮件消息
//		MimeMessage message = javaMailSender.createMimeMessage();
//
//		try {
//			// 设置邮件的属性
//			String to = mail.getTo();
//			String subject = mail.getSubject();
//			String content = mail.getContent();
//			String picid = mail.getPicid();
//			FileDataSource simg = mail.getImg();
//			// 设置邮件的发件人
//			message.setFrom(new InternetAddress(simpleMailMessage.getFrom()));
//			// 设置邮件的收件人 cc表示抄送 bcc 表示暗送
//			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
//			// 设置邮件的主题
//			message.setSubject(subject);
//			MimeBodyPart text = new MimeBodyPart();
//			MimeBodyPart img = new MimeBodyPart();
//
//			MimeMultipart mm = new MimeMultipart();
//			DataHandler dh = new DataHandler(simg);
//			text.setContent(content + " </br> <img src='cid:" + picid + "'>", "text/html;charset=utf8");
//			img.setDataHandler(dh);
//			img.setContentID(picid);
//
//			mm.addBodyPart(text);
//			mm.addBodyPart(img);
//			mm.setSubType("related");
//			
//			message.setContent(mm);
//			message.saveChanges();
//		} catch (javax.mail.MessagingException e) {
//			e.printStackTrace();
//		}
//
//		javaMailSender.send(message);
//	}
//    public void sendRichEmail2(Email mail)  {
//        // 建立邮件消息
//        MimeMessage message = javaMailSender.createMimeMessage();
//        MimeMessageHelper messageHelper;
//        try {
//            messageHelper = new MimeMessageHelper(message, true, "UTF-8");
//            // 设置发件人邮箱
//            messageHelper.setFrom(simpleMailMessage.getFrom());
//            // 设置收件人
//            messageHelper.setTo(mail.getTo());
//            // 设置邮件主题
//            if (mail.getSubject()!=null) {
//            	//从mail对象获取邮件主题
//                messageHelper.setSubject(mail.getSubject());
//            } else {
//                //从xml配置文件获取邮件主题
//                messageHelper.setSubject(mail.getSubject());
//            }
//            
//            // true 表示启动HTML格式的邮件
//            messageHelper.setText(mail.getContent(), true);
//            
//            // 设置图片
//            String cid = mail.getPicid();
//            FileDataSource img = mail.getImg();
////            FileSystemResource img = new FileSystemResource(file);
//            messageHelper.addInline(cid, img);
//            
//            // 设置发送时间
//            messageHelper.setSentDate(new Date());
//            // 发送邮件
//            javaMailSender.send(message);
//            
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }
//
//	
//}
