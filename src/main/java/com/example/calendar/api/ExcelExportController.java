package com.example.calendar.api;

import com.example.calendar.ExcelExportService;
import com.example.calendar.business.domains.ProjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
public class ExcelExportController {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelExportController.class);

    private static final String APPLICATION_EXCEL_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private ExcelExportService excelExportService;

    @Autowired
    public ExcelExportController(ExcelExportService excelExportService) {
        this.excelExportService = excelExportService;
    }

    @PostMapping(value = "/download-report/{projectType}")
    private ResponseEntity<byte[]> downloadExcel(@PathVariable ProjectType projectType, @RequestBody LocalDate requestedDate) {
        try {
            byte[] bytes = excelExportService.createExcelExport(projectType, requestedDate);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(APPLICATION_EXCEL_XLSX))
                    .contentLength(bytes.length)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"devops-export" + ".xlsx\"")
                    .body(bytes);

        } catch (Exception ex) {
            LOG.error("**** Error :: Something went wrong.", ex);
            return ResponseEntity.noContent().build();
        }
    }

}
