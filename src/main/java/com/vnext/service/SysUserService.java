package com.vnext.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vnext.mapper.SysUserMapper;
import com.vnext.pojo.SysUser;

/**
 * Created by CodeGenerator on 2017/08/09.
 */
@Service
public class SysUserService extends BaseService<SysUser> {

	@Autowired
	private SysUserMapper sysUserMapper;
	public List<SysUser> savePojo() {
		return queryAll();
	}
	
	public void updateForBatch(List<SysUser> list) {
		sysUserMapper.updateForBatch(list);
	}
}
