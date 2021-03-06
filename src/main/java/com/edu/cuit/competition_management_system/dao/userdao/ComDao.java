package com.edu.cuit.competition_management_system.dao.userdao;

import com.edu.cuit.competition_management_system.entity.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComDao extends JpaRepository<Competition,Integer> {
    //根据竞赛种类查询当前报名还没有结束的竞赛
    List<Competition> findAllByComtpidAndEndtimeIsAfter(Integer comTpid,String time);
    //根据竞赛种类查询当前还没开始的竞赛
    List<Competition> findAllByComtpidAndComtimeIsAfter(Integer comTpid,String time);
    //查询报名结束之前的6个竞赛并且宣传图片不是空的
    Page<Competition> findAllByEndtimeIsAfterAndPicIsNotNull(String time,Pageable pageable);
}
