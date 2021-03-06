/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Core.
 *
 * FenixEdu Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.teacher.service.teacher.professorship;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.service.services.exceptions.NotAuthorizedException;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.academic.util.WeekDay;
import org.fenixedu.bennu.core.i18n.BundleUtil;

import pt.ist.fenixedu.teacher.domain.SupportLesson;
import pt.ist.fenixedu.teacher.domain.teacher.TeacherService;
import pt.ist.fenixedu.teacher.domain.teacher.TeacherServiceLog;
import pt.ist.fenixedu.teacher.dto.teacher.professorship.SupportLessonDTO;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class EditSupportLesson {

    protected void run(SupportLessonDTO supportLessonDTO) {

        Professorship professorship = FenixFramework.getDomainObject(supportLessonDTO.getProfessorshipID());
        ExecutionSemester executionSemester = professorship.getExecutionCourse().getExecutionPeriod();
        Teacher teacher = professorship.getTeacher();
        TeacherService teacherService = TeacherService.getTeacherServiceByExecutionPeriod(teacher, executionSemester);

        if (teacherService == null) {
            teacherService = new TeacherService(teacher, executionSemester);
        }

        final StringBuilder log = new StringBuilder();

        SupportLesson supportLesson = FenixFramework.getDomainObject(supportLessonDTO.getExternalId());
        if (supportLesson == null) {
            supportLesson = new SupportLesson(supportLessonDTO, professorship);
            log.append(BundleUtil.getString(Bundle.TEACHER_CREDITS, "label.teacher.schedule.supportLessons.create"));
        } else {
            supportLesson.update(supportLessonDTO);
            log.append(BundleUtil.getString(Bundle.TEACHER_CREDITS, "label.teacher.schedule.supportLessons.change"));
        }

        log.append(WeekDay.getWeekDay(supportLessonDTO.getWeekDay()).getLabel());
        log.append(" ");
        log.append(supportLessonDTO.getStartTime().getHours());
        log.append(":");
        log.append(supportLessonDTO.getStartTime().getMinutes());
        log.append(" - ");
        log.append(supportLessonDTO.getEndTime().getHours());
        log.append(":");
        log.append(supportLessonDTO.getEndTime().getMinutes());
        log.append(" - ");
        log.append(supportLessonDTO.getPlace());

        new TeacherServiceLog(teacherService, log.toString());
    }

    // Service Invokers migrated from Berserk

    private static final EditSupportLesson serviceInstance = new EditSupportLesson();

    @Atomic
    public static void runEditSupportLesson(SupportLessonDTO supportLessonDTO) throws NotAuthorizedException {
        serviceInstance.run(supportLessonDTO);
    }

}