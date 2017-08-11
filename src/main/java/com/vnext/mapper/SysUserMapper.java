package com.vnext.mapper;

import java.util.List;

import com.vnext.core.MyMapper;
import com.vnext.pojo.SysUser;

public interface SysUserMapper extends MyMapper<SysUser> {
	public void updateForBatch(List<SysUser> list);
}