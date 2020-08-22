package com.pns.contractmanagement.helper.impl;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.pns.contractmanagement.exceptions.PnsError;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Contract;
import com.pns.contractmanagement.model.ImmutableReport;
import com.pns.contractmanagement.model.Report;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;

/**
 *
 */
@Component
public class ContractInvoiceHelperImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractInvoiceHelperImpl.class);
	@Value("${app.print.date.formar:dd-MM-yyyy}")
	private String dateFormat;

	@Value("${app.print.jasper.compiled:false}")
	private boolean isCompiled;
	@Value("${app.print.jasper.file.url:/pnsinvoice.jasper}")
	private String jasperFileUrl;

	public Report generateInvoice(Contract contract) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("contract", contract);

		parameters.put("formatter", DateTimeFormatter.ofPattern(dateFormat));
		try {
			JasperReport report;
			if (isCompiled) {
				report = (JasperReport) JRLoader.loadObjectFromFile(jasperFileUrl);
			} else {
				InputStream employeeReportStream = getClass().getResourceAsStream("/pnsinvoice.jrxml");
				report = JasperCompileManager.compileReport(employeeReportStream);
			}

			JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
			byte[] pdfReport = JasperExportManager.exportReportToPdf(jasperPrint);
			// JasperExportManager.exportReportToPdfFile(jasperPrint, contract.getId() +
			// ".pdf");
			return ImmutableReport.builder().content(pdfReport).fileName(contract.getId() + ".pdf")
					.contentType(MediaType.APPLICATION_PDF).build();
		} catch (JRException ex) {
			LOGGER.error("Exception occured while generating jasper report.");
			throw new PnsException("Unable to Create Pdf", ex, PnsError.WARNING);
		}
	}

	public void compileJasper() {
		InputStream employeeReportStream = getClass().getResourceAsStream("/pnsinvoice.jrxml");

		try {
			JasperReport report = JasperCompileManager.compileReport(employeeReportStream);
			JRSaver.saveObject(report, jasperFileUrl);

		} catch (JRException ex) {
			LOGGER.error("Exception occured while compiling jasper report.");
			throw new PnsException("Unable to compile pnsinvoice.jrxml", ex, PnsError.SYSTEM_ERROR);
		}
	}

}
