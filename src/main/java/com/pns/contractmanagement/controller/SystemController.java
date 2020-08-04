package com.pns.contractmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pns.contractmanagement.dao.SystemDao;

/**
 *
 */
@RestController
@RequestMapping("/system")
public class SystemController {

    @Autowired
    SystemDao dao;

    @GetMapping("db")
    public boolean dbsetup() {
        dao.initIndexes();
        return true;
    }
}
