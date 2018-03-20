package com.mmall.dto;

import com.google.common.collect.Lists;
import com.mmall.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@ToString
public class DeptlevelDto extends SysDept {

    private List<DeptlevelDto> deptList= Lists.newArrayList();

    public  static  DeptlevelDto  adapt(SysDept dept){
        DeptlevelDto dto=new DeptlevelDto();
        BeanUtils.copyProperties(dept,dto);
        return dto;
    }
}
