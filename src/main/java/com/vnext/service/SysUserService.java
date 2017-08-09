package com.vnext.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vnext.service.BaseService;
import com.vnext.pojo.SysUser;

/**
 * Created by CodeGenerator on 2017/08/09.
 */
@Service
public class SysUserService extends BaseService<SysUser> {

	public List<SysUser> savePojo() {
		return queryAll();
	}
}
