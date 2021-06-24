package com.example.calendar;

import com.example.calendar.business.DateUtitlity;
import com.example.calendar.business.devops.DevopsDataService;
import com.example.calendar.business.domains.DateRange;
import com.example.calendar.business.domains.DevopsData;
import com.example.calendar.business.domains.ProjectType;
import com.example.calendar.business.domains.User;
import com.example.calendar.business.users.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelExportService {

    private final DevopsDataService devopsDataService;
    private final UserService userService;

    @Autowired
    public ExcelExportService(DevopsDataService devopsDataService, UserService userService,
                              @Value("${export.template.file-location}") String templateFileLocation) {
        this.devopsDataService = devopsDataService;
        this.userService = userService;
    }

    public byte[] createExcelExport(ProjectType projectType, LocalDate requestedDate) throws IOException {
        int month = 4;
        int year = 2021;
        DateRange dateRange = DateUtitlity.getDateRangeWithBackDates(requestedDate.getMonth().getValue(), requestedDate.getYear());
        try (
            XSSFWorkbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
        ){
            XSSFSheet sheet = workbook.createSheet("Devops Monthly Effort");
            CellStyle style = workbook.createCellStyle();
            style.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
            style.setFillPattern(FillPatternType.NO_FILL);


            int rowIdx = 0;
            Row row = sheet.createRow(rowIdx);
            int column = 0;
            for (LocalDate date = dateRange.getStartDate(); date.isBefore(dateRange.getEndDate()); date = date.plusDays(1)) {
                Cell cell = row.createCell(++column);
                cell.setCellValue(date.toString());
                sheet.autoSizeColumn(cell.getColumnIndex());
            }

            List<User> userList = userService.findAllUsersForProject(projectType);
            for(User user: userList) {
                row = sheet.createRow(++rowIdx);
                int cellColumn = 0;
                Cell userNameCell = row.createCell(cellColumn++);
                userNameCell.setCellValue(user.getFirstName() + " , " + user.getLastName());
                sheet.autoSizeColumn(userNameCell.getColumnIndex());
                for (LocalDate date = dateRange.getStartDate(); date.isBefore(dateRange.getEndDate()); date = date.plusDays(1)) {
                    Optional<DevopsData>  devopsDataOptional = devopsDataService.getDevopsById(ProjectType.TRAN,date);
                    if(devopsDataOptional.isPresent()){
                        DevopsData data = devopsDataOptional.get();
                        if(user.getUserName().equalsIgnoreCase(data.getPrimaryUserName())){
                            Cell cell = row.createCell(cellColumn++);
                            cell.setCellValue(data.getPrimaryEffort());
                            sheet.autoSizeColumn(cell.getColumnIndex());
                            cell.setCellStyle(style);
                        }else if(user.getUserName().equalsIgnoreCase(data.getSecondaryUserName())){
                           // style.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
                            Cell cell = row.createCell(cellColumn++);
                            sheet.autoSizeColumn(cell.getColumnIndex());
                           // cell.setCellStyle(style);
                            cell.setCellValue(data.getSecondaryEffort());
                        }else {

                            row.createCell(cellColumn++).setCellValue("");

                        }
                    }else {
                        row.createCell(cellColumn++).setCellValue("");
                    }
                }
            }




            workbook.write(out);
            out.flush();
            return out.toByteArray();

        } catch (Exception ex) {
            throw ex;
        }


    }
}
