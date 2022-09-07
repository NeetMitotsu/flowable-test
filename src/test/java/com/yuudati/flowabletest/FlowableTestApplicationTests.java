package com.yuudati.flowabletest;

import com.google.common.collect.Maps;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class FlowableTestApplicationTests {

    @Autowired
    private ProcessEngine engine;

    @Test
    void contextLoads() {
        System.out.println("engine = " + engine);
        System.out.println("engine.getName() = " + engine.getName());
        RepositoryService repositoryService = engine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("holiday-request.bpmn20.xml")
                .name("请假审批")
                .deploy();
    }

    @Test
    void selectDeployQuery(){
        RepositoryService repositoryService = engine.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        ProcessDefinition processDefinition = processDefinitionQuery.deploymentId("67be88bd-2ebd-11ed-ac08-24418c901dbf")
                .singleResult();
        System.out.println("processDefinition = " + processDefinition);
        System.out.println("processDefinition.getName() = " + processDefinition.getName());
        System.out.println("processDefinition.getId() = " + processDefinition.getId());
        System.out.println("processDefinition.getDescription() = " + processDefinition.getDescription());
    }

    @Test
    void testDeployDelete(){
        RepositoryService repositoryService = engine.getRepositoryService();
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        List<Deployment> holidayList = deploymentQuery.deploymentName("请假审批").list();
        for (Deployment holiday :
                holidayList) {
            System.out.println("holiday.getId() = " + holiday.getId());
            System.out.println("holiday.getName() = " + holiday.getName());
            repositoryService.deleteDeployment(holiday.getId(), true);

        }

    }

    @Test
    void testRunProcess(){
        RuntimeService runtimeService = engine.getRuntimeService();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("employee", "张三");
        variables.put("nrOfHolidays", "9");
        variables.put("description", "就是请假");

        ProcessInstance holidayRequest = runtimeService.startProcessInstanceByKey("holidayRequest", variables);
        System.out.println("holidayRequest.getId() = " + holidayRequest.getId());
        System.out.println("流程定义id holidayRequest.getProcessDefinitionId() = " + holidayRequest.getProcessDefinitionId());
        System.out.println("holidayRequest.getDeploymentId() = " + holidayRequest.getDeploymentId());
        System.out.println("活跃的id holidayRequest.getActivityId() = " + holidayRequest.getActivityId());
    }

    /**
     * 查询任务
     */
    @Test
    void testSelectCurrentTask(){
        TaskQuery taskQuery = engine.getTaskService().createTaskQuery();
        List<Task> list = taskQuery.processDefinitionKey("holidayRequest")
                .taskAssignee("李四") // 查询任务的处理人
                .list();
        for (Task task :
                list) {
            System.out.println("task.getId() = " + task.getId());
            System.out.println("task.getProcessDefinitionId() = " + task.getProcessDefinitionId());
            System.out.println("task.getName() = " + task.getName());
            System.out.println("task.getAssignee() = " + task.getAssignee());
            System.out.println("task.getDescription() = " + task.getDescription());
        }
    }

    /**
     * 完成任务
     */
    @Test
    void testExecuteTask(){
        TaskService taskService = engine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> list = taskQuery.processDefinitionKey("holidayRequest")
                .taskAssignee("李四") // 查询任务的处理人
                .list();
        for (Task task :
                list) {
            System.out.println("task.getId() = " + task.getId());
            System.out.println("task.getProcessDefinitionId() = " + task.getProcessDefinitionId());
            System.out.println("task.getName() = " + task.getName());
            System.out.println("task.getAssignee() = " + task.getAssignee());
            System.out.println("task.getDescription() = " + task.getDescription());
            // 创建流程变量
            Map<String, Object> variables = Maps.newHashMap();
            variables.put("approved",false);
            // 完成任务
            taskService.complete(task.getId(), variables);
        }
    }

    @Test
    void testHistoryTask(){
        HistoryService historyService = engine.getHistoryService();
        HistoricActivityInstanceQuery historicQuery = historyService.createHistoricActivityInstanceQuery();
        List<HistoricActivityInstance> list = historicQuery
//                .processDefinitionId("holidayRequest")
                .finished()
                .orderByHistoricActivityInstanceEndTime()
                .asc()
                .list();
        list.forEach(item -> {
            System.out.println("item.getActivityId() = " + item.getActivityId());
            System.out.println("item.getProcessDefinitionId() = " + item.getProcessDefinitionId());
            System.out.println("item.getStartTime() = " + item.getStartTime());
            System.out.println("item.getEndTime() = " + item.getEndTime());
            System.out.println("item.getAssignee() = " + item.getAssignee());
            System.out.println("============================================");
        });


    }

}
