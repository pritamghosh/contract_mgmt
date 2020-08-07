package com.pns.contractmanagement.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pns.contractmanagement.dao.SystemDao;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

/**
 *
 */
@RestController
@RequestMapping("/system")
public class SystemController {

    @Autowired
    SystemDao dao;

    @GetMapping("db")
    public boolean dbsetup() {
        dao.initIndexes();
        return true;
    }
    
    @GetMapping
    public byte[]  jasperTest() throws JRException {
        Map<String, Object> parameters = new HashMap<>();
//        JasperReport report = (JasperReport) JRLoader
//            .loadObject(this.getClass().getResourceAsStream("/test.jasper"));
        
        InputStream employeeReportStream
        = getClass().getResourceAsStream("/test.jrxml");
      JasperReport report
        = JasperCompileManager.compileReport(employeeReportStream);

        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
        byte[] pdfReport = JasperExportManager.exportReportToPdf(jasperPrint);
        JRPdfExporter exporter = new JRPdfExporter();
        /* FontKey keyArial = new FontKey("Arial", false, false);  
        PdfFont fontArial = new PdfFont("Helvetica","Cp1252",false); 
         
        FontKey keyArialBold = new FontKey("Arial", true, false);  
        PdfFont fontArialBold = new PdfFont("Helvetica-Bold","Cp1252",false); 
         
        Map fontMap = new HashMap();
        fontMap.put(keyArial,fontArial);
        fontMap.put(keyArialBold,fontArialBold);
         
        exporter.setParameter(JRExporterParameter.FONT_MAP,fontMap);*/
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(
          new SimpleOutputStreamExporterOutput("test.pdf"));
        exporter.exportReport();
        return pdfReport;
    }
}
