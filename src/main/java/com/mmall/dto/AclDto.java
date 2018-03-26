package com.mmall.dto;

import com.mmall.model.SysAcl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

@Setter
@Getter
@ToString
public class AclDto extends SysAcl {
    //是否默认选中
    private boolean checked=false;
    //是否有权操作
    private  boolean hasAcl=false;

    public  static  AclDto adapt(SysAcl acl){
        AclDto aclDto=new AclDto();
        BeanUtils.copyProperties(acl,aclDto);
        return  aclDto;
    }
}
