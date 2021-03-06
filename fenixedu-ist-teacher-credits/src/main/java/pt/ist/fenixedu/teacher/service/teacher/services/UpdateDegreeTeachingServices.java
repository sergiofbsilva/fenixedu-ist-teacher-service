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
package pt.ist.fenixedu.teacher.service.teacher.services;

import java.util.List;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.service.services.exceptions.NotAuthorizedException;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;

import pt.ist.fenixedu.teacher.domain.teacher.DegreeTeachingService;
import pt.ist.fenixedu.teacher.domain.teacher.TeacherService;
import pt.ist.fenixedu.teacher.domain.teacher.TeacherServiceLog;
import pt.ist.fenixedu.teacher.ui.struts.action.credits.ManageDegreeTeachingServicesDispatchAction.ShiftIDTeachingPercentage;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class UpdateDegreeTeachingServices {

    protected void run(String professorshipID, List<ShiftIDTeachingPercentage> shiftsIDsTeachingPercentages) {

        Professorship professorship = FenixFramework.getDomainObject(professorshipID);
        Teacher teacher = professorship.getTeacher();
        ExecutionSemester executionSemester = professorship.getExecutionCourse().getExecutionPeriod();
        TeacherService teacherService = TeacherService.getTeacherServiceByExecutionPeriod(teacher, executionSemester);
        if (teacherService == null) {
            teacherService = new TeacherService(teacher, executionSemester);
        }

        final StringBuilder log = new StringBuilder();
        log.append(BundleUtil.getString(Bundle.TEACHER_CREDITS, "label.teacher.schedule.change"));

        for (ShiftIDTeachingPercentage shiftIDTeachingPercentage : shiftsIDsTeachingPercentages) {
            Shift shift = FenixFramework.getDomainObject(shiftIDTeachingPercentage.getShiftID());
            DegreeTeachingService degreeTeachingService =
                    teacherService.getDegreeTeachingServiceByShiftAndProfessorship(shift, professorship);
            if (degreeTeachingService != null) {
                degreeTeachingService.updatePercentage(shiftIDTeachingPercentage.getPercentage());
            } else {
                if (shiftIDTeachingPercentage.getPercentage() == null
                        || (shiftIDTeachingPercentage.getPercentage() != null && shiftIDTeachingPercentage.getPercentage() != 0)) {
                    new DegreeTeachingService(teacherService, professorship, shift, shiftIDTeachingPercentage.getPercentage());
                }
            }

            log.append(shift.getPresentationName());
            if (shiftIDTeachingPercentage.getPercentage() != null) {
                log.append("= ");
                log.append(shiftIDTeachingPercentage.getPercentage());
            }
            log.append("% ; ");
        }

        new TeacherServiceLog(teacherService, log.toString());
    }

    // Service Invokers migrated from Berserk

    private static final UpdateDegreeTeachingServices serviceInstance = new UpdateDegreeTeachingServices();

    @Atomic
    public static void runUpdateDegreeTeachingServices(String professorshipID,
            List<ShiftIDTeachingPercentage> shiftsIDsTeachingPercentages) throws NotAuthorizedException {
        serviceInstance.run(professorshipID, shiftsIDsTeachingPercentages);
    }

}