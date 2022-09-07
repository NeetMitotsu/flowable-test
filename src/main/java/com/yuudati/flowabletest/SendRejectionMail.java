package com.yuudati.flowabletest;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.job.api.JobInfo;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.spring.job.service.SpringRejectedJobsHandler;

public class SendRejectionMail implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // 进行拒绝后的操作，按流程定义是给拒绝的员工发送邮件
        System.out.println("execution.getCurrentActivityId() = " + execution.getCurrentActivityId());
        System.out.println("execution.getEventName() = " + execution.getEventName());
        System.out.println("=--========================请假被拒绝了==============================");
        System.out.println("execution.getVariable(\"description\") = " + execution.getVariable("description"));
    }
}
