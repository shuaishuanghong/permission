package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.commom.RequestHolder;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysDept;
import com.mmall.param.DeptParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.levelutil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class SysDeptService {
    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    public  void save(DeptParam param){
        BeanValidator.check(param);
        if(chechExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("当前同一层级下已经存在改部门");
        }
        SysDept dept=SysDept.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        dept.setLevel(levelutil.calculateLevel(getLevel(dept.getParentId()),param.getParentId()));
        dept.setOperator(RequestHolder.getCurrentUser().getUsername());//TODO
        /*dept.setOperateIp(RequestHolder.getCurrentHttpServletRequest().getRemoteAddr());//TODO*/
        dept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentHttpServletRequest()));
        dept.setOperateTime(new Date());
        sysDeptMapper.insertSelective(dept);
    }

    private  boolean chechExist(Integer parentId,String deptName,Integer depId){
        //TODO:
        return  sysDeptMapper.countByNameAndParenrId(parentId,deptName,depId)>0;
    }
    private String getLevel(Integer deptId){
        SysDept dept=sysDeptMapper.selectByPrimaryKey(deptId);
        if(dept==null){
            return  null;
        }
        return dept.getLevel();
    }
    public void update(DeptParam param){
        BeanValidator.check(param);
        if(chechExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("当前同一层级下已经存在改部门");
        }
        SysDept before=sysDeptMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的部门不存在");

        SysDept after=SysDept.builder().id(
                param.getId()
        ).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        after.setLevel(levelutil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());//TODO
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentHttpServletRequest()));//TODO
        after.setOperateTime(new Date());
        updateWithChild(before,after);

    }
    @Transactional
    public void updateWithChild(SysDept befor, SysDept after){


        String newLevelPrefix=after.getLevel();
        String oldLevelprefix=befor.getLevel();
        if(!after.getLevel().equals(befor.getLevel())){
                List<SysDept> deptList=sysDeptMapper.getChildDeptListByLevel(befor.getLevel());
                if(CollectionUtils.isNotEmpty(deptList)){
                    for(SysDept dept:deptList){
                        String level=dept.getLevel();
                        if(level.indexOf(oldLevelprefix)==0){
                            level=newLevelPrefix+level.substring(oldLevelprefix.length());
                            dept.setLevel(level);
                        }
                    }
                    sysDeptMapper.batchUpdateLevel(deptList);
                }

        }
        sysDeptMapper.updateByPrimaryKey(after);
    }

    public  void delete(int deptId){
        SysDept dept=sysDeptMapper.selectByPrimaryKey(deptId);
        Preconditions.checkNotNull(dept,"待删除的部门不存在");
        if(sysDeptMapper.countByParentId(dept.getId())>0){
            throw new ParamException("当前部门下有子部门，无法删除");
        }
        if(sysUserMapper.countByDeptId(dept.getId())>0){
            throw new ParamException("当前部门下有用户，无法删除");
        }

        sysDeptMapper.deleteByPrimaryKey(deptId);

    }
}
