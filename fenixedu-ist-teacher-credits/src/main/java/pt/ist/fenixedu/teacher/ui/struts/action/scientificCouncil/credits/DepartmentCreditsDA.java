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
package pt.ist.fenixedu.teacher.ui.struts.action.scientificCouncil.credits;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.Department;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

import pt.ist.fenixedu.contracts.domain.Employee;
import pt.ist.fenixedu.teacher.ui.struts.action.ScientificCreditsApp;
import pt.ist.fenixframework.FenixFramework;

@StrutsFunctionality(app = ScientificCreditsApp.class, path = "department-admin-offices", titleKey = "label.departmentAdmOffice",
        bundle = "TeacherCreditsSheetResources")
@Mapping(path = "/departmentCredits", module = "scientificCouncil")
@Forwards(@Forward(name = "departmentCredits", path = "/scientificCouncil/credits/departmentCredits/departmentCredits.jsp"))
public class DepartmentCreditsDA extends FenixDispatchAction {

    @EntryPoint
    public ActionForward prepareDepartmentCredits(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        DepartmentCreditsBean departmentCreditsBean = new DepartmentCreditsBean();
        request.setAttribute("departmentCreditsBean", departmentCreditsBean);
        return mapping.findForward("departmentCredits");
    }

    public ActionForward showEmployeesByDepartment(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        DepartmentCreditsBean departmentCreditsBean = getRenderedObject();
        if (departmentCreditsBean.getDepartment() == null) {
            return mapping.findForward("departmentCredits");
        }
        return forwardDepartmentCredits(mapping, request, departmentCreditsBean);
    }

    public ActionForward addRoleDepartmentCredits(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        DepartmentCreditsBean departmentCreditsBean = getRenderedObject();
        Employee employee = getEmployeeFromBean(departmentCreditsBean);
        if (employee == null) {
            request.setAttribute("success", false);
            return forwardDepartmentCredits(mapping, request, departmentCreditsBean);
        }

        departmentCreditsBean.assignPermission(employee);
        request.setAttribute("success", true);
        return forwardDepartmentCredits(mapping, request, departmentCreditsBean);
    }

    public ActionForward removeRoleDepartmentCredits(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Employee employee = getEmployeeFromRequest(request);
        Department department = getDepartmentFromRequest(request);
        DepartmentCreditsBean departmentCreditsBean = new DepartmentCreditsBean();
        departmentCreditsBean.setDepartment(department);
        departmentCreditsBean.removePermission(employee);
        return forwardDepartmentCredits(mapping, request, null);
    }

    private ActionForward forwardDepartmentCredits(ActionMapping mapping, HttpServletRequest request,
            DepartmentCreditsBean departmentCreditsBean) {
        if (departmentCreditsBean == null) {
            departmentCreditsBean = new DepartmentCreditsBean(getDepartmentFromRequest(request), null);
        }
        request.setAttribute("employeesOfDepartment", getEmployeesFromDepartment(departmentCreditsBean.getDepartment()));
        request.setAttribute("departmentCreditsBean", departmentCreditsBean);
        return mapping.findForward("departmentCredits");
    }

    private Employee getEmployeeFromBean(DepartmentCreditsBean departmentCreditsBean) {
        int employeeNumber = Integer.valueOf(departmentCreditsBean.getEmployeeNumber());
        return Employee.readByNumber(employeeNumber);
    }

    private List<Employee> getEmployeesFromDepartment(Department department) {
        List<Employee> employees = new LinkedList<Employee>();
        for (Person person : department.getAssociatedPersonsSet()) {
            if (hasPersonPermissionCredits(person, department)) {
                employees.add(person.getEmployee());
            }
        }
        return employees;
    }

    private Employee getEmployeeFromRequest(HttpServletRequest request) {
        return FenixFramework.getDomainObject(request.getParameter("employeeId"));
    }

    private Department getDepartmentFromRequest(HttpServletRequest request) {
        return FenixFramework.getDomainObject(request.getParameter("departmentId"));
    }

    private boolean hasPersonPermissionCredits(Person person, Department department) {
        return person.getManageableDepartmentCreditsSet().contains(department);
    }

}
