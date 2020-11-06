/**
 * 
 */
package com.pns.contractmanagement.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Pritam Ghosh
 */
@Builder
@Data
@AllArgsConstructor
public class MailVo {
	private List<String> toList;

	private List<String> ccList;
	
	private String form;

	private String subject;

	private Object content;

	private String htmlText;

	private List<Attachment> attachments;

	@Builder
	@Data
	@AllArgsConstructor
	public static class Attachment {

		private String fileName;

		private byte[] content;

		private String type;
	}
}
