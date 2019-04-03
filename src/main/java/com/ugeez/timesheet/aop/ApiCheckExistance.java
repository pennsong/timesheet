package com.ugeez.timesheet.aop;

import com.ugeez.timesheet.model.Company;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;

@Slf4j
@Aspect
@Component
public class ApiCheckExistance {

    @Autowired
    private ListableBeanFactory listableBeanFactory;

    @Pointcut("@annotation(CheckExistance)")
    public void checkExistance(){}

    @Before("checkExistance()")
    public void doBefore(JoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String[] names = codeSignature.getParameterNames();

        for (int i = 0; i < names.length; i++) {
            if (names[i].equals("id")) {
                // 检查是否存在
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Method method = signature.getMethod();

                CheckExistance checkExistance = method.getAnnotation(CheckExistance.class);
                Class clazz = checkExistance.value();

                Optional repositoryObject = new Repositories(listableBeanFactory).getRepositoryFor(clazz);

                if (!(repositoryObject.isPresent())) {
                    throw new RuntimeException("操作的记录所属的Repository不存在!");
                }

                CrudRepository crudRepository = (CrudRepository) repositoryObject.get();

                if (!(crudRepository.existsById(joinPoint.getArgs()[i]))) {
                    throw new RuntimeException("操作的记录不存在!");
                }
            }
            break;
        }
    }
}