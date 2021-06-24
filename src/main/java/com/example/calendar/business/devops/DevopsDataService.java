package com.example.calendar.business.devops;

import com.example.calendar.business.DateUtitlity;
import com.example.calendar.business.domains.*;
import com.example.calendar.exceptions.GenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DevopsDataService {

    private final DevopsDataRepo devopsDataRepo;
    private final MongoTemplate mongoTemplate;

    private static final String PRIMARY_USERNAME = "primaryUserName";
    private static final String SECONDARY_USERNAME = "secondaryUserName";

    @Autowired
    public DevopsDataService(DevopsDataRepo devopsDataRepo, MongoTemplate mongoTemplate) {
        this.devopsDataRepo = devopsDataRepo;
        this.mongoTemplate = mongoTemplate;
    }

    public List<List<DevopsDataDto>> getCalendarData(int month, int year, ProjectType projectType) {
        List<List<DevopsDataDto>> listList = new ArrayList<>();

        DateRange dateRange = DateUtitlity.getDateRange(month, year);
        int breakCount = 0;
        List<DevopsDataDto> tempList = new ArrayList<>();
        LocalDate firstDayOfRequestedMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfRequestedMonth = firstDayOfRequestedMonth.withDayOfMonth(DateUtitlity.numberOfDaysInMonth(month, year));

        for (LocalDate date = dateRange.getStartDate(); date.isBefore(dateRange.getEndDate()); date = date.plusDays(1)) {
            breakCount++;
            tempList.add(new DevopsDataDto(date, date.getDayOfMonth(), projectType,
                    devopsDataRepo.findById(getDevopsId(projectType, date)).orElse(null),
                    isOutsideMonthRange(date, firstDayOfRequestedMonth, lastDayOfRequestedMonth)));
            if (breakCount % 7 == 0) {
                listList.add(new ArrayList<>(tempList));
                tempList.clear();
            }
        }
        return listList;
    }

    private boolean isOutsideMonthRange(LocalDate date, LocalDate firstDayOfRequestedMonth, LocalDate lastDayOfRequestedMonth) {
        return date.isBefore(firstDayOfRequestedMonth) || date.isAfter(lastDayOfRequestedMonth);
    }

    public Double getEfforts(User user, ProjectType projectType) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where(PRIMARY_USERNAME).is(user.getUserName()), Criteria.where(SECONDARY_USERNAME).is(user.getUserName()));
        criteria.andOperator(Criteria.where("_id").regex(projectType.toString()));
        query.addCriteria(criteria);
        return mongoTemplate.find(query, DevopsData.class).stream().mapToDouble(devopsData -> {
            if (devopsData.getPrimaryUserName().equalsIgnoreCase(user.getUserName())) {
                return devopsData.getPrimaryEffort() != null ? devopsData.getPrimaryEffort() : 0;
            }
            return devopsData.getSecondaryEffort() != null ? devopsData.getSecondaryEffort() : 0;
        }).sum();
    }

    public DevopsData save(DevopsData devopsData) {
        if (devopsData != null && devopsData.getPrimaryUserName() != null && devopsData.getSecondaryUserName() != null) {
            if(devopsData.getPrimaryUserName().equalsIgnoreCase(devopsData.getSecondaryUserName())){
                throw new GenericException("Primary and secondary can't be same");
            }
        }
        return devopsDataRepo.save(devopsData);
    }

    public void deleteById(String devopsId) {
        devopsDataRepo.deleteById(devopsId);
    }

    public String getDevopsId(ProjectType projectType, LocalDate devOpsDate) {
        return projectType.toString() + "-" + devOpsDate;
    }

    public Optional<DevopsData> getDevopsById(ProjectType projectType, LocalDate date) {
        return devopsDataRepo.findById(getDevopsId(projectType, date));
    }
}
