package com.epam.hw.netflix.controllers

import com.epam.hw.netflix.api.WorkspaceAPI
import com.epam.hw.netflix.domain.Workspace
import com.epam.hw.netflix.services.EmployeeService
import feign.Feign
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.netflix.ribbon.RibbonClient
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/employees")
@RibbonClient(name = "workspaces-api")
class EmployeeAPIController {

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate()
    }

    @Autowired
    RestTemplate restTemplate

    @Autowired
    EmployeeService employeeService

    @Autowired
    WorkspaceAPI workspaceAPIClient

    @RequestMapping("/{id}")
    def describeEmployee(@PathVariable("id") String id) {
        def employee = employeeService.findEmployee(id)

        [
                id       : employee.id,
                firstName: employee.firstName,
                lastName : employee.lastName,
                email    : employee.email,
//                workspace: workspaceAPIClient.getWorkspaceById(employee.workspaceId) // null? Nope. Let's request exact workspace by employee.workspaceId from workspaces-api. How? With feign client maybe?
                workspace: this.restTemplate().getForObject("http://workspaces-api/workspaces/{id}", Workspace.class, employee.workspaceId)
        ]
    }
}
