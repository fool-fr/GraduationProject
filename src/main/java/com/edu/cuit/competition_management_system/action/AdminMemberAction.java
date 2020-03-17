package com.edu.cuit.competition_management_system.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.edu.cuit.competition_management_system.dao.userdao.FindUser;
import com.edu.cuit.competition_management_system.entity.Competition;
import com.edu.cuit.competition_management_system.entity.Users;
import com.edu.cuit.competition_management_system.json.LayuiTable;
import com.edu.cuit.competition_management_system.json.Tablejson;
import com.edu.cuit.competition_management_system.service.ComTpService;
import com.edu.cuit.competition_management_system.service.CompetitionService;
import com.edu.cuit.competition_management_system.service.UserSign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author yuanck 2016051230
 * 描述：管理页面用户模块相关控制器
 */
@Controller
@RequestMapping("AdminMember")
public class AdminMemberAction {
    @Autowired
    UserSign userSign;
    @Autowired
    CompetitionService competitionService;
    @Autowired
    ComTpService comTpService;
    @Autowired
    FindUser findUser;

    @RequestMapping("member_list")
    public String member_list(){
        return "admin/member/member-tea";
    }
    @RequestMapping("member_list1")
    public String member_list1(){
        return "admin/member/member_stu";
    }
    @RequestMapping("member_del")
    public String member_del(){
        return "admin/member/member-del";
    }

    /**
     * 查询所有有效状态用户数据
     * @param type 获取查询的用户类型
     * @return 如下json格式的学生列表
     * {
     *   "code": 0,
     *   "msg": "",
     *   "count": 1000,
     *   "data": [{}, {}]
     * }
     */
    @RequestMapping("user")
    @ResponseBody
    public String student(String type){
        Tablejson tb = new Tablejson();
        tb.setData(userSign.findAllByType(Integer.parseInt(type)));
        return tb.getDate();
    }

    /**
     * 分页查询所有学生或老师用户
     * @param type 1学生 2老师
     * @param limit 每页数据
     * @param page 当前页面
     * @return
     */
    @RequestMapping("pageUser")
    @ResponseBody
    public LayuiTable pageUser(String type,String limit,String page){
        LayuiTable layuiTable = new LayuiTable();
        //定义查询模板
        Users users = new Users();
        users.setType(Integer.parseInt(type));
        users.setState(1);
      //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching(); //构建对象
        Example<Users> ex = Example.of(users, matcher);
        Pageable pager = PageRequest.of(Integer.parseInt(page)-1,Integer.parseInt(limit));
        Page<Users> pagerlist = findUser.findAll(ex,pager);
        //封装数据
        List<Users> usersList = pagerlist.getContent();
        layuiTable.setCode(0);
        layuiTable.setMsg("ok");
        layuiTable.setCount(pagerlist.getTotalElements());
        layuiTable.setData(usersList);
        return layuiTable;
    }

    /**
     * 查询所有无效状态数据
     *
     * @return
     */
    @RequestMapping("findDel")
    @ResponseBody
    public String findDel(){
        Tablejson tb = new Tablejson();
        tb.setData(userSign.findAllDelUser());
        return tb.getDate();
    }
    /**
     * 根据用户id修改用户状态
     * @param id
     * @param response
     * @throws IOException
     */
    @RequestMapping("setUserState")
    public void setUserState(String id,String state,HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        String msg="";
        try {
            userSign.setUserState(Integer.parseInt(id),Integer.parseInt(state));
            msg="ok";
            out.print(msg);
        } catch (Exception e) {
            msg="error";
            out.print(msg);
        }
    }
    @RequestMapping("memberAdd")
    public String memberAdd(){
        return "admin/member/member-add";
    }

    /**
     * 根据用户id重置用户密码
     * @param id
     * @param response
     * @throws IOException
     */
    @RequestMapping("resetPw")
    public void resetPw(String id,HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        String msg="";
        try {
            userSign.setUserPass(Integer.parseInt(id),"000000");
            msg="ok";
            out.print(msg);
        } catch (Exception e) {
            msg="error";
            out.print(msg);
        }
    }

    /**
     * 修改用户信息
     * @param param
     * @param response
     * @throws IOException
     */
    @RequestMapping("update")
    public void update(String param,HttpServletResponse response)throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        String msg="";
        Users user = JSON.parseObject(param, new TypeReference<Users>() {});
        try {
            userSign.save(user);
            msg="ok";
            out.print(msg);
        } catch (Exception e) {
            msg="error";
            out.print(msg);
        }
    }

    /**
     * 获得所有的竞赛列表
     * @return
     */
    @ResponseBody
    @RequestMapping("selectCom")
    public String selectCom(){
        Tablejson tb = new Tablejson();
        tb.setData(competitionService.findAll());
        return tb.getDate();
    }

    /**
     * 更新用户选择的竞赛
     * @param id 用户id
     * @param comid 竞赛id
     * @param response
     * @throws IOException
     */
    @RequestMapping("updateCOm")
    public void updateCOm(String id,String comid,HttpServletResponse response)throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        String msg="";
        try {
            userSign.setUserCom(Integer.parseInt(id),Integer.parseInt(comid));
            msg="ok";
            out.print(msg);
        } catch (Exception e) {
            msg="error";
            out.print(msg);
        }
    }

    /**
     * 更新老师指导的竞赛类型
     * @param id 用户id
     * @param comtpid 竞赛类型id
     * @param response
     * @throws IOException
     */
    @RequestMapping("updateCOmtp")
    public void updatecomty(String id,String comtpid,HttpServletResponse response) throws IOException{
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        String msg="";
        try {
            userSign.setUserComtp(Integer.parseInt(id),Integer.parseInt(comtpid));
            msg="ok";
            out.print(msg);
        } catch (Exception e) {
            msg="error";
            out.print(msg);
        }
    }
    /**
     * 获得所有的竞赛种类列表
     * @return
     */
    @ResponseBody
    @RequestMapping("selectComtp")
    public String selectComtp(){
        Tablejson tb = new Tablejson();
        tb.setData(comTpService.findAllComTp());
        return tb.getDate();
    }
}