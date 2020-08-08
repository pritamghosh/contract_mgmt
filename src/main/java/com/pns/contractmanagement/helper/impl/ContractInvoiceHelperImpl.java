package com.pns.contractmanagement.helper.impl;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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

/**
 *
 */
@Component
public class ContractInvoiceHelperImpl {

    @Value("${app.print.date.formar:dd-MM-yyyy}")
    private String dateFormat;

    public Report generateInvoice(Contract contract) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("contract", contract);

        parameters.put("formatter", DateTimeFormatter.ofPattern(dateFormat));
        try {
            // JasperReport report;
            // report = (JasperReport)
            // JRLoader.loadObject(this.getClass().getResourceAsStream("/pnsinvoice.jasper"));

            InputStream employeeReportStream = getClass().getResourceAsStream("/pnsinvoice.jrxml");
            JasperReport report = JasperCompileManager.compileReport(employeeReportStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
            byte[] pdfReport = JasperExportManager.exportReportToPdf(jasperPrint);
            return ImmutableReport.builder().content(pdfReport).fileName(contract.getId() + "xyxz.pdf")
                .contentType(MediaType.APPLICATION_PDF).build();
        } catch (JRException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

}
