/*
 *  License and Copyright:
 *
 *  The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may
 *  not use this file except in compliance with the License. You should have received a copy of the License
 *  along with this software; if not, you may obtain a copy of the License at
 *  http://www.opensource.org/licenses/ecl1.php.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 *  either express or implied. See the License for the specific language governing rights and limitations
 *  under the License.
 *
 *  Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */
package edu.ucar.dls.repository.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import edu.ucar.dls.email.SendEmail;


/**
 Class that emails out a report of the Index Summary
 */

public class SummaryEmail {
	
	/**
	 * Email index summary to admins
	 * @param indexSummary
	 * @param passedThresholdTests
	 * @param servletContext
	 */
	public static void emailIndexDataBean(IndexDataBean indexSummary, boolean passedThresholdTests,
			ServletContext servletContext)
	{
		ArrayList<String> msgList = new ArrayList<String>();
		
		String subject = null;
		String indexName = (String)indexSummary.getIndexDetail().get(IndexDataBean.NAME);
		
		String ddsDomain = servletContext.getInitParameter("ddsDomain");
		if(ddsDomain==null)
			ddsDomain = "";
		String contextName = servletContext.getContextPath().replace("/", "");
		String maintenanceUrl = String.format("%s/%s/admin/maintenance.do", 
				ddsDomain, contextName);
		
		// If it didn't pass threshold tests its an action required
		if(passedThresholdTests)
		{
			subject = String.format("(%s) Repository index was successfully committed. ",contextName);
			msgList.add("A new repository index was successfully committed.");
			msgList.add("");
			msgList.add("Details about this index and others may be viewed at:");
			msgList.add(maintenanceUrl);
			msgList.add("");
		}
		else{
			subject = String.format("(%s) ACTION REQUIRED: Repository index needs review ",
					contextName);
			msgList.add("ACTION REQUIRED: Repository index needs review");
			msgList.add("");
			msgList.addAll(createThresholdErrorsMsgList(indexSummary));
			msgList.add("");
			msgList.add("All background indexing is turned off until action is resolved. Action can be resolved here:");
			msgList.add(maintenanceUrl);
			msgList.add("");
		}
		
		msgList.addAll(createIndexMsgList("", indexSummary.getIndexDetail()));
		msgList.add("");
		msgList.addAll(createIndexMsgList("Prior Index - ", indexSummary.getPriorIndexDetail()));
		
		msgList.add("");
		
		msgList.addAll(createIndexDiffMsgList(indexSummary));
		
		
		String msg = StringUtils.join(msgList, "\n");
		
		try
		{
			String [] toEmails = servletContext.getInitParameter("validationToEmails").split(",");
			String fromEmail = servletContext.getInitParameter("fromEmail");
			String mailType = servletContext.getInitParameter("mailType");
			String mailServer = servletContext.getInitParameter("mailServer");
			
			if(mailType.equals("dev"))
				sendDevEmail(subject, msg, toEmails, fromEmail);
			else
			{
				SendEmail emailer = new SendEmail(mailType, mailServer);
				try {
					emailer.doSendEmail(toEmails, fromEmail, subject, msg);
				} catch (Throwable t) {
					System.out.println("Unable to send e-mail: " + t);
				}
			}
		}
		catch(NullPointerException e)
		{
			// summary emails were not setup correctly
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Create the index diff msg list for emailing. This is sent out both during
	 * successful commits and error ones.
	 * @param indexSummary
	 * @return
	 */
	private static List<String> createIndexDiffMsgList(
			IndexDataBean indexSummary) {
		List<String> msgList = new ArrayList<String>();
		
		msgList.add("Changes Since Prior Index");
		String collectionDetailMsg = "%s %s\nRecord Changes: %s(%s)\n";
		Map<String, String> recordDetail = indexSummary.getRecordDetail();
		int allCountChanged = Integer.parseInt((String)recordDetail.get(IndexDataBean.COUNT_CHANGED));
		double allPercentChanged = Double.parseDouble((String)indexSummary.getRecordDetail().get(IndexDataBean.PERCENT_CHANGED));

		msgList.add((String.format(collectionDetailMsg, "All", "", formatChangeCount(allCountChanged), formatChangePercent(allPercentChanged))));

		
		for(Map<String, String> collectionDetail: indexSummary.getSortedCollections().values() )
		{
			String collectionMsg = "";
			if(!IndexValidation.passedThresholdTest(collectionDetail, IndexDataBean.PASSED))
				collectionMsg = "(Exceeded Threshold)";
			else if(collectionDetail.containsKey(IndexDataBean.ADDED))
				collectionMsg = "(Added)";
			else if(collectionDetail.containsKey(IndexDataBean.REMOVED))
				collectionMsg = "(Removed)";
			
			int countChanged = Integer.parseInt((String)collectionDetail.get(
					IndexDataBean.COUNT_CHANGED));

			// We only want to report on collections that have changed 
			if(collectionMsg.equals("") && countChanged==0)
				continue;
			
			String collectionName = collectionDetail.get(IndexDataBean.NAME);
			double percentChanged = Double.parseDouble((String)collectionDetail.get(
					IndexDataBean.PERCENT_CHANGED));

			
			msgList.add(String.format(collectionDetailMsg, collectionName, 
					collectionMsg, formatChangeCount(countChanged), formatChangePercent(percentChanged)));
		}
		return msgList;
	}

	/*
	 * Create an error threshold error msg list. This is only used when its an errored out repos
	 * do to threshold errors 
	 */
	private static  List<String> createThresholdErrorsMsgList(
			IndexDataBean indexSummary) {
		Map collectionsDetails = indexSummary.getCollectionsDetails();
		List<String> msgList = new ArrayList<String>();
		if(!Boolean.parseBoolean(
				(String)collectionsDetails.get(IndexDataBean.PASSED_MAX_THRESHOLD_FOR_COLLECTION_COUNT_CHANGES)))
		{
			String msg = "Collection added/removed (%d/%d) exceeeded the max threshold of %d";
			int addedCount = Integer.parseInt((String)collectionsDetails.get(IndexDataBean.COLLECIONS_ADDED));
			int removedCount = Integer.parseInt((String)collectionsDetails.get(IndexDataBean.COLLECIONS_REMOVED));
			int threshold = Integer.parseInt((String)collectionsDetails.get(IndexDataBean.MAX_THRESHOLD_FOR_COLLECTION_COUNT_CHANGES));

			msgList.add(String.format(msg, addedCount, removedCount, threshold));
			
		}
		if(!Boolean.parseBoolean(
				(String)collectionsDetails.get(IndexDataBean.PASSED_MAX_THRESHOLD_FOR_RECORD_REMOVALS)))
		{
			String msg = "Collection records removed exceeded the max threshold of %s";
			double threshold = Double.parseDouble((String)collectionsDetails.get(IndexDataBean.MAX_THRESHOLD_FOR_RECORD_REMOVALS));

			msgList.add(String.format(msg, formatChangePercent(threshold)));
		}
		return msgList;
	}

	/*
	 * Create a msg list for general info of a index info
	 */
	private static List<String> createIndexMsgList(String namePrefix, Map indexInfo)
	{
		ArrayList<String> msgList = new ArrayList<String>();
		msgList.add(namePrefix+(String)indexInfo.get(IndexDataBean.NAME)+":");
		msgList.add("Path: "+(String)indexInfo.get(IndexDataBean.INDEX_PATH));
		msgList.add("Started: "+(String)indexInfo.get(IndexDataBean.STARTED));
		msgList.add("Finished: "+(String)indexInfo.get(IndexDataBean.FINISHED));
		msgList.add("Number of Collections: "+ indexInfo.get(IndexDataBean.COLLECTIONS));
		msgList.add("Number of Records: "+ indexInfo.get(IndexDataBean.RECORDS));
		return msgList;
	}
	
	/*
	 * Helper method for how to format a double in the email. Which we want as 
	 * a percent
	 */
	private static String formatChangePercent(double d)
	{
		String sign = "";
		if(d>0)
			sign="+";
		return sign+String.valueOf(Math.round(d*100))+"%";
	}
	
	/*
	 * Helper method for how to format a change count in the email. Which we
	 * want to force to have a sign
	 */
	private static String formatChangeCount(int d)
	{
		String sign = "";
		if(d>0)
			sign="+";
		return sign+String.valueOf(d);
	}
	
	/*
	 * Helper method during testing. So emails can be sent locally
	 */
	private static void sendDevEmail(String subject, String message, 
			String[] toEmails, String fromEmail)
	{
		// Set the next 4 lines to your external exchange server. Example here
		// is for gmail
		String emailHost = "smtp.gmail.com";
		String emailPort = "587";
		final String emailUser = "finke.dave@gmail.com";
		final String emailPassword = "ducati999r";
		
		Properties props = System.getProperties();
		props.put("mail.smtp.host", emailHost);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", emailPort);
		props.put("mail.store.protocol", "pop3");
	    props.put("mail.transport.protocol", "smtp");
	    
		Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(emailUser, emailPassword);
					}
				  });
		
		Message mailMessage = new MimeMessage(session);
		try {
			mailMessage.setFrom(new InternetAddress(fromEmail));
		
			InternetAddress[] addressTo = 
				new javax.mail.internet.InternetAddress[toEmails.length];

			for (int i = 0; i < toEmails.length; i++)
			{
			    addressTo[i] = new javax.mail.internet.InternetAddress(toEmails[i]);
			}
			
			mailMessage.setRecipients(Message.RecipientType.TO,
					addressTo);
			mailMessage.setSubject(subject);
			mailMessage.setText(message);
			Transport.send(mailMessage);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}