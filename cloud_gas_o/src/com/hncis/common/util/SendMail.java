package com.hncis.common.util;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

import com.hncis.common.Constant;
import com.hncis.common.vo.BgabGascZ011Dto;

/******************************************************************
 * 1.Project Name : Kia Motors Slovakia
 * 2.File Name    : SendMail.java
 * 3.Content      : sending mail
 ******************************************************************/
public class SendMail {

	final private String _ID	= "hncis@hncis.co.kr";
	final private String _PWD	= "human1025";
	private String _HOST = "smtp.daum.net";
	private String _PORT = "465";
	private boolean isReal = false;
	
	static Logger logger = Logger.getLogger("SendMail.class");
	
	public SendMail() {
		String area = StringUtil.getSystemArea().toUpperCase();
		try {
//			if (area.equals("REAL")){
//				_HOST = "smtp.gmail.com";
//				isReal = true;
//			} else {
//				_HOST = "smtp.gmail.com";
//				isReal = false;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SendMail(String str) {
		this._HOST = str;
	}
	
	public String getHost() {
		return this._HOST;
	}
	
	/**
	 * mail sedding
	 * 
	 * ex) oMail.sendMail("E500455@kia.sk","gasc@kia.sk",
	 * "This is test", "asdfasdf", 0, false);
	 * 
	 * @param to       - 받는사람 메일주소
	 * @param from     - 보내는 사람 메일주소
	 * @param subject  - 메일 제목
	 * @param body     - 메일 내용
	 * @param flag     - 메일 보낼 형식 text:0, html:1
	 * @param debug    - 디버그 여부 디버그:false, 안함:true, debug 가 true 이면 운영자에게 보내기
	 * @return success - 성공여부, 성공:true, 실패:false
	 */
	public boolean sendMail(String to, String from, String subject, String body, int flag, boolean debug) throws Exception {
		boolean success = true;
		
		//if(false){
		//	to = "gas@hncis.co.kr";
		//}
		
		System.out.println("발송메일 : " + to);
		if(to.equals("")){
			return false;
		}
		
		try {
			Properties props = System.getProperties();
//			props.put("mail.transport.protocel", "smtp");
//			props.put("mail.smtp.host", "smtp.daum.net");
//			props.put("mail.smtp.port", "465");
//			props.put("mail.smtp.ssl_enable", "true");
//			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.host", _HOST);
			props.put("mail.smtp.port", _PORT);
			props.put("mail.smtp.starttls.enable","true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.debug", "true");
			props.put("mail.smtp.socketFactory.port", "465"); 
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
			props.put("mail.smtp.socketFactory.fallback", "false");
			
			Authenticator auth = new SMTPAuthenticator("hncis",_PWD);
			Session session = Session.getDefaultInstance(props, auth);
			
			if(debug){
				session.setDebug(true);
			}
			
			MimeMessage message = new MimeMessage(session);
			message.setSubject(subject, "utf-8");
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			if(flag == 0){
				message.setContent(body, "text/plain; charset=utf-8");
			}else{
				message.setContent(body, "text/html; charset=utf-8");
			}
//			System.out.println("_HOST:"+_HOST);
			message.setFrom(new InternetAddress(from , MimeUtility.encodeText("GAS ADMIN","UTF-8","B")));
			
			Transport.send(message);
			
			logger.debug("method : sendEmail ");
			logger.debug("area : " + StringUtil.getSystemArea().toUpperCase());
			logger.debug("mail server : " + _HOST);
			logger.debug("from : " + from);
			logger.debug("mailto : " + to);
			logger.debug("body : "  + body);
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
			logger.error("method : sendEmail ");
			logger.error("area : " + StringUtil.getSystemArea().toUpperCase());
			logger.error("mail server : " + _HOST);
			logger.error("from : " + from);
			logger.error("mailto : " + to);
			logger.error("body : "  + body);
			logger.error(e.getMessage());
			//throw e;
		}
		
		return success;
	}
	
	/**
	 * 파일 첨부도 할수 있는 메일보내기
	 * 
	 * @attention 글로벌 총무만 사용 함!
	 * @param to       - 받는사람 메일주소
	 * @param from     - 보내는 사람 메일주소
	 * @param fromName - 보내는 사람 메일주소
	 * @param subject  - 메일 제목
	 * @param body     - 메일 내용
	 * @param filUrl   - 파일 경로
	 * @param flag     - 메일 보낼 형식 text:0, html:1
	 * @param debug    - 디버그 여부 디버그:false, 안함:true, debug 가 true 이면 조대리님 앞으로 메일 보내기
	 * @return success - 성공여부, 성공:true, 실패:false
	 */
	public boolean sendMailFileGlobal(String to, String from, String fromStr, String subject, String body, String fileUrl, int flag, boolean debug) throws MessagingException, Exception {
		boolean success = true;
		
		if(false){
			to = "hncis@hncis.co.kr";
		}
		
		System.out.println("발송메일 : " + to);
		if(to.equals("")){
			return false;
		}
		
		try {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", _HOST);
			
			Session session = Session.getDefaultInstance(props, null);
			if(debug){
				session.setDebug(true);
			}
			
			MimeMessage message = new MimeMessage(session);
			try {
				message.setFrom(new InternetAddress(from, MimeUtility.encodeText(fromStr, "utf-8", "B")));
			}catch(UnsupportedEncodingException uee) {
				message.setFrom(new InternetAddress(from));
			}
			
			message.setSubject(subject, "utf-8");
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			if (flag == 0) {
				message.setContent(body, "text/plain; charset=utf-8");
			} else {
				message.setContent(body, "text/html; charset=utf-8");
			}
			
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setContent(body, "text/html; charset=utf-8");
			MimeBodyPart mbp2 = new MimeBodyPart();
			FileDataSource fds = new FileDataSource(fileUrl);
			mbp2.setDataHandler(new DataHandler(fds));
			mbp2.setFileName(MimeUtility.encodeText(fds.getName(), "KSC5601", "Q"));
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);
			mp.addBodyPart(mbp2);
			message.setContent(mp);
			
			Transport.send(message);
		}catch(MessagingException mex){
			mex.printStackTrace();
			logger.error("method : sendMailFileGlobal ");
			logger.error("area : " + StringUtil.getSystemArea().toUpperCase());
			logger.error("mail server : " + _HOST);
			logger.error("from : " + from);
			logger.error("mailto : " + to);
			logger.error("body : "  + body);
			logger.error(mex.getMessage());
			success = false;
			throw mex;
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("method : sendMailFileGlobal ");
			logger.error("area : " + StringUtil.getSystemArea().toUpperCase());
			logger.error("mail server : " + _HOST);
			logger.error("from : " + from);
			logger.error("mailto : " + to);
			logger.error("body : "  + body);
			logger.error(e.getMessage());
			success = false;
			throw e;
		}
		
		return success;
	}
	
	/**
	 * 파일 첨부도 할수 있는 메일보내기
	 * 
	 * @attention 글로벌 총무만 사용 함!
	 * @param to       - 받는사람 메일주소
	 * @param from     - 보내는 사람 메일주소
	 * @param fromName - 보내는 사람 메일주소
	 * @param subject  - 메일 제목
	 * @param body     - 메일 내용
	 * @param filUrl   - 파일 경로
	 * @param flag     - 메일 보낼 형식 text:0, html:1
	 * @param debug    - 디버그 여부 디버그:false, 안함:true, debug 가 true 이면 조대리님 앞으로 메일 보내기
	 * @return success - 성공여부, 성공:true, 실패:false
	 */
	public boolean sendMailFileConfrimation(String to, String from, String fromStr, String subject, String body, List<BgabGascZ011Dto> rsList, int flag, boolean debug) throws MessagingException, Exception {
		boolean success = true;
		
		if(false){
			to = "hncis@hncis.co.kr";
		}
		
		System.out.println("발송메일 : " + to);
		if(to.equals("")){
			return false;
		}
		
		try {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", _HOST);
			
			Session session = Session.getDefaultInstance(props, null);
			if(debug){
				session.setDebug(true);
			}
			
			MimeMessage message = new MimeMessage(session);
			try {
				message.setFrom(new InternetAddress(from, MimeUtility.encodeText(fromStr, "utf-8", "B")));
			}catch(UnsupportedEncodingException uee) {
				message.setFrom(new InternetAddress(from));
			}
			
			message.setSubject(subject, "utf-8");
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			if (flag == 0) {
				message.setContent(body, "text/plain; charset=utf-8");
			} else {
				message.setContent(body, "text/html; charset=utf-8");
			}
			
			Multipart mp = new MimeMultipart();
			MimeBodyPart mbp1 = new MimeBodyPart();
			
			mbp1.setContent(body, "text/html; charset=utf-8");
			mp.addBodyPart(mbp1);
			
			for(BgabGascZ011Dto vo : rsList){
				MimeBodyPart mbp2 = new MimeBodyPart();
				
				String fileUrl = "";
				if(StringUtil.getSystemArea().equals("LOCAL")){
					fileUrl = Constant.UPLOAD_LOCAL_PATH;
				}else if(StringUtil.getSystemArea().equals("TEST")){
					fileUrl = Constant.UPLOAD_TEST_PATH;
				}else{
					fileUrl = Constant.UPLOAD_REAL_PATH;
				}
				fileUrl += "/businessTravel/"+vo.getOgc_fil_nm();
				
				FileDataSource fds = new FileDataSource(fileUrl);
				mbp2.setDataHandler(new DataHandler(fds));
				//mbp2.setFileName(MimeUtility.encodeText(fds.getName(), "utf-8", "Q"));
				mbp2.setFileName(MimeUtility.encodeText(vo.getFil_nm(), "utf-8", "Q"));
				
				mp.addBodyPart(mbp2);
			}
			message.setContent(mp);
			
			Transport.send(message);
		}catch(MessagingException mex){
			mex.printStackTrace();
			logger.error("method : sendMailFileGlobal ");
			logger.error("area : " + StringUtil.getSystemArea().toUpperCase());
			logger.error("mail server : " + _HOST);
			logger.error("from : " + from);
			logger.error("mailto : " + to);
			logger.error("body : "  + body);
			logger.error(mex.getMessage());
			success = false;
			throw mex;
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("method : sendMailFileGlobal ");
			logger.error("area : " + StringUtil.getSystemArea().toUpperCase());
			logger.error("mail server : " + _HOST);
			logger.error("from : " + from);
			logger.error("mailto : " + to);
			logger.error("body : "  + body);
			logger.error(e.getMessage());
			success = false;
			throw e;
		}
		
		return success;
	}
	
	/**
	 * 파일 첨부도 할수 있는 메일보내기
	 * 
	 * @attention 글로벌 총무만 사용 함!
	 * @param to       - 받는사람 메일주소
	 * @param from     - 보내는 사람 메일주소
	 * @param fromName - 보내는 사람 메일주소
	 * @param subject  - 메일 제목
	 * @param body     - 메일 내용
	 * @param filUrl   - 파일 경로
	 * @param flag     - 메일 보낼 형식 text:0, html:1
	 * @param debug    - 디버그 여부 디버그:false, 안함:true, debug 가 true 이면 조대리님 앞으로 메일 보내기
	 * @return success - 성공여부, 성공:true, 실패:false
	 */
	public boolean sendMailShuttleBusFile(String to, String from, String fromStr, String subject, String body, List<BgabGascZ011Dto> rsList, int flag, boolean debug) throws MessagingException, Exception {
		boolean success = true;
		
		if(false){
			to = "hondaman@hyundai-autoever.com";
		}
		
		System.out.println("발송메일 : " + to);
		if(to.equals("")){
			return false;
		}
		
		try {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", _HOST);
			
			Session session = Session.getDefaultInstance(props, null);
			if(debug){
				session.setDebug(true);
			}
			
			MimeMessage message = new MimeMessage(session);
			try {
				message.setFrom(new InternetAddress(from, MimeUtility.encodeText(fromStr, "utf-8", "B")));
			}catch(UnsupportedEncodingException uee) {
				message.setFrom(new InternetAddress(from));
			}
			
			message.setSubject(subject, "utf-8");
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			if (flag == 0) {
				message.setContent(body, "text/plain; charset=utf-8");
			} else {
				message.setContent(body, "text/html; charset=utf-8");
			}
			
			Multipart mp = new MimeMultipart();
			MimeBodyPart mbp1 = new MimeBodyPart();
			
			mbp1.setContent(body, "text/html; charset=utf-8");
			mp.addBodyPart(mbp1);
			
			for(BgabGascZ011Dto vo : rsList){
				MimeBodyPart mbp2 = new MimeBodyPart();
				
				String fileUrl = "";
				if(StringUtil.getSystemArea().equals("LOCAL")){
					fileUrl = Constant.UPLOAD_LOCAL_PATH;
				}else if(StringUtil.getSystemArea().equals("TEST")){
					fileUrl = Constant.UPLOAD_TEST_PATH;
				}else{
					fileUrl = Constant.UPLOAD_REAL_PATH;
				}
				fileUrl += "/shuttleBus/"+vo.getOgc_fil_nm();
				
				FileDataSource fds = new FileDataSource(fileUrl);
				mbp2.setDataHandler(new DataHandler(fds));
				//mbp2.setFileName(MimeUtility.encodeText(fds.getName(), "utf-8", "Q"));
				mbp2.setFileName(MimeUtility.encodeText(vo.getFil_nm(), "utf-8", "Q"));
				
				mp.addBodyPart(mbp2);
			}
			message.setContent(mp);
			
			Transport.send(message);
		}catch(MessagingException mex){
			mex.printStackTrace();
			logger.error("method : sendMailFileGlobal ");
			logger.error("area : " + StringUtil.getSystemArea().toUpperCase());
			logger.error("mail server : " + _HOST);
			logger.error("from : " + from);
			logger.error("mailto : " + to);
			logger.error("body : "  + body);
			logger.error(mex.getMessage());
			success = false;
			throw mex;
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("method : sendMailFileGlobal ");
			logger.error("area : " + StringUtil.getSystemArea().toUpperCase());
			logger.error("mail server : " + _HOST);
			logger.error("from : " + from);
			logger.error("mailto : " + to);
			logger.error("body : "  + body);
			logger.error(e.getMessage());
			success = false;
			throw e;
		}
		
		return success;
	}
	
	
	
	
	
	
	static final String FROM = "SENDER@EXAMPLE.COM";   // Replace with your "From" address. This address must be verified.
    static final String TO = "RECIPIENT@EXAMPLE.COM";  // Replace with a "To" address. If your account is still in the 
                                                       // sandbox, this address must be verified.
    
    static final String BODY = "This email was sent through the Amazon SES SMTP interface by using Java.";
    static final String SUBJECT = "Amazon SES test (SMTP interface accessed using Java)";
    
    static final String SMTP_USERNAME = "AKIAJV7G455PGC6V2JNA";  // Replace with your SMTP username.
    static final String SMTP_PASSWORD = "ApByzJLy/3PZOBZAgyU17N9ZDhRy09/rjG6cSnvHnXo5";  // Replace with your SMTP password.
    
    static final int PORT = 25;

	
	
	
    @SuppressWarnings("unused")
	public boolean sendMailAmz(String to, String from, String subject, String body, int flag, boolean debug) throws Exception {
		boolean success = true;
		
		Transport transport = null;
		
		
		if(to.equals("")){
			return false;
		}
		
		to = "hncis@hncis.co.kr";
		
		System.out.println("발송메일 : " + from);
		System.out.println("수신메일 : " + to);
		
		try {
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", "smtp");
	    	props.put("mail.smtp.port", PORT); 
	    	
	    	props.put("mail.smtp.auth", "true");
	    	props.put("mail.smtp.starttls.enable", "true");
	    	props.put("mail.smtp.starttls.required", "true");
	    	
	    	Session session = Session.getDefaultInstance(props);
			
			if(debug){
				session.setDebug(true);
			}
			
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setSubject(subject, "utf-8");
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			if(flag == 0){
				message.setContent(body, "text/plain; charset=utf-8");
			}else{
				message.setContent(body, "text/html; charset=utf-8");
			}
			
			System.out.println("_HOST:"+_HOST);
			
			transport = session.getTransport();
			
			transport.connect(_HOST, SMTP_USERNAME, SMTP_PASSWORD);
			
			 transport.sendMessage(message, message.getAllRecipients());
			
			logger.info("method : sendEmail ");
			logger.info("area : " + StringUtil.getSystemArea().toUpperCase());
			logger.info("mail server : " + _HOST);
			logger.info("from : " + from);
			logger.info("mailto : " + to);
			logger.info("body : "  + body);
		} catch (Exception e) {
			success = false;
			logger.error("method : sendEmail ");
			logger.error("area : " + StringUtil.getSystemArea().toUpperCase());
			logger.error("mail server : " + _HOST);
			logger.error("from : " + from);
			logger.error("mailto : " + to);
			logger.error("body : "  + body);
			logger.error(e.getMessage());
			throw e;
		}finally {
			transport.close();
		}
		
		return success;
	}
	
    private class SMTPAuthenticator extends javax.mail.Authenticator {

		String id;
		String pwd;

		SMTPAuthenticator(String id , String pwd){
			this.id = id;
			this.pwd = pwd;
		}
	
			public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(this.id, this.pwd); //구글아이디는 구글계정에서 @이후를 제외한 값이다. (예: abcd@gmail.com --> abcd)
		}
	} 	
}