/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report;

import net.vrfun.tasktracker.report.docgen.ReportFormat;
import net.vrfun.tasktracker.report.docgen.ReportGenerator;
import net.vrfun.tasktracker.report.docgen.ReportGeneratorFactory;
import net.vrfun.tasktracker.task.Progress;
import net.vrfun.tasktracker.task.ProgressRepository;
import net.vrfun.tasktracker.task.TaskRepository;
import net.vrfun.tasktracker.user.Team;
import net.vrfun.tasktracker.user.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * Report document composer
 *
 * @author          boto
 * Creation Date    September 2020
 */
@Service
public class ReportComposer {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Value("${company.name: ''}")
    private String companyName;

    public final static String REPORT_DATE_FORMAT = "dd.MM.yyyy";

    private final ProgressRepository progressRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;


    @Autowired
    public ReportComposer(@NonNull final ProgressRepository progressRepository,
                          @NonNull final TeamRepository teamRepository,
                          @NonNull final TaskRepository taskRepository) {

        this.progressRepository = progressRepository;
        this.teamRepository = teamRepository;
        this.taskRepository = taskRepository;
    }

    @NonNull
    public ByteArrayOutputStream createTeamReportCurrentWeek(@NonNull final List<Long> teamIDs,
                                                             @NonNull final ReportFormat reportFormat,
                                                             @NonNull final String title,
                                                             @NonNull final String subTitle) {

        LocalDate currentDate = LocalDate.now();
        return createTeamReport(teamIDs, currentDate.minusWeeks(1L), currentDate, reportFormat, title, subTitle);
    }

    @NonNull
    public ByteArrayOutputStream createTeamReport(@NonNull final List<Long> teamIDs,
                                                  @NonNull final LocalDate fromDate,
                                                  @NonNull final LocalDate toDate,
                                                  @NonNull final ReportFormat reportFormat,
                                                  @NonNull final String title,
                                                  @NonNull final String subTitle) {

        List<Team> teamList = teamRepository.findAllById(teamIDs);
        if (teamList.isEmpty()) {
            throw new IllegalArgumentException("Invalid team IDs!");
        }

        ReportGenerator reportGenerator = ReportGeneratorFactory.build(reportFormat);
        try {
            String footer = companyName + ", Generated by " + appName + " v" + appVersion;

            reportGenerator.begin();
            reportGenerator.setFooter(footer);
            reportGenerator.generateCoverPage(title, subTitle +
                    "\nPeriod: " + fromDate.format(DateTimeFormatter.ofPattern(REPORT_DATE_FORMAT)) + " - " +
                    toDate.format(DateTimeFormatter.ofPattern(REPORT_DATE_FORMAT)) +
                    "\nCreated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(REPORT_DATE_FORMAT + " - HH:mm")));

            teamList.forEach((team -> {
                LOGGER.debug("Generating progress for team: {}", team.getName());

                reportGenerator.sectionBegin(team.getName());

                taskRepository.findTeamTasks(team).forEach((task -> {
                    LOGGER.debug(" Generating progress for task: {}", task.getTitle());

                    List<Progress> progressList = progressRepository.findByTaskIdAndReportWeekBetween(task.getId(), fromDate, toDate);

                    reportGenerator.sectionAppend(progressList);
                }));

                reportGenerator.sectionEnd();
            }));

            return reportGenerator.end();

        } catch (Throwable throwable) {
            LOGGER.error("Could not create report file, reason: {}", throwable.getMessage());
            throw throwable;
        }
    }
}
