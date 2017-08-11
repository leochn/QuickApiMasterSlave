package com.vnext.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.vnext.core.Result;
import com.vnext.core.ResultGenerator;
import com.vnext.pojo.SysUser;
import com.vnext.service.SysUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by CodeGenerator on 2017/08/09.
 */
@Api(value = "sysUser信息")
@RestController
@RequestMapping("/api")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;  
    
    /**
     * 分页获取数据
     * @param page
     * @param rows
     * @return
     */
    @ApiOperation("分页查询数据,page页数,rows每页显示条数")
    @GetMapping("/sysUserList")
    public Result getPageList(
    		@RequestParam(value = "page") Integer page,
			@RequestParam(value = "rows") Integer rows) {
    	PageInfo<SysUser> pageInfo = this.sysUserService.queryPageListByWhere(new SysUser(), page, rows);
		if (pageInfo != null) {
			return ResultGenerator.genSuccessResult(pageInfo.getTotal(), pageInfo.getList());
		}
		return ResultGenerator.genNotFoundResult();
    }

    /**
     * 分页查询,并根据字段排序
     * @param page
     * @param rows
     * @param sortField
     * @param sortOrder
     * @return
     */
    @GetMapping("/sysUserListOrderBy")
    public Result getPageListAndOrderBy(
    		@RequestParam(value = "page") Integer page,
			@RequestParam(value = "rows") Integer rows,
			@RequestParam(value = "sortField", defaultValue = "ID") String sortField,
			@RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {
    	PageInfo<SysUser> pageInfo = this.sysUserService.queryPageListByWhereAndOrderBy(new SysUser(), page, rows, sortField, sortOrder);
		if (pageInfo != null) {
			return ResultGenerator.genSuccessResult(pageInfo.getTotal(), pageInfo.getList());
		}
		return ResultGenerator.genNotFoundResult();
    }
    
    /**
     * 获取全部数据
     * @return
     */
	@GetMapping("/sysUsers")
	public Result all() {
		List<SysUser> list = this.sysUserService.queryAll();
		if (list != null) {
			return ResultGenerator.genSuccessResult(list.size(), list);
		}
		return ResultGenerator.genNotFoundResult();
	}
	
	/**
     * 根据id,获取单个数据
     * @param id
     * @return
     */
    @GetMapping("/sysUser/{id}")
    public Result detail(@PathVariable String id) {
        SysUser record = this.sysUserService.queryById(id);
        if (record != null) {
        	return ResultGenerator.genSuccessResult(1,record);
		}
        return ResultGenerator.genNotFoundResult();
    }
    
    /**
	 * 根据某个字段值获取一条数据
	 * @param fieldName
	 * @param value
	 * @return
	 */
	@GetMapping("/sysUser/{fieldName}/{value}")
	public Result queryByFieldName(
			@PathVariable("fieldName") String fieldName,
			@PathVariable("value") String value) {
		SysUser record = this.sysUserService.queryByFieldName(fieldName, value);
		if (record != null) {
			return ResultGenerator.genSuccessResult(1, record);
		}
		return ResultGenerator.genFailResult();
	}
    
    /**
	 * 新增一条信息
	 * @return
	 */
	@PostMapping("/sysUser")
	public Result add(@RequestBody SysUser record) {
		Integer num = this.sysUserService.saveSelective(record);
		if (num == 1) {
			return ResultGenerator.genSuccessResult();
		}
		return ResultGenerator.genFailResult();
	}
	
	/**
	 * 更新数据
	 * @param id
	 * @param record
	 * @return
	 */
	@PutMapping("/sysUser/{id}")
	public Result update(@PathVariable("id") String id, @RequestBody SysUser record) {
		Integer num = this.sysUserService.updateSelective(record);
		if (num == 1) {
			return ResultGenerator.genSuccessResult();
		}
		return ResultGenerator.genFailResult();
	}
	
	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	@DeleteMapping("/sysUser/{id}")
	public Result delete(@PathVariable("id") String id) {
		Integer num = this.sysUserService.deleteById(id);
		if (num == 1) {
			return ResultGenerator.genSuccessResult();
		}
		return ResultGenerator.genFailResult();
	}
	
	@GetMapping("/sysUser/savePojo")
	public Result savePojo() {
		List<SysUser> list = this.sysUserService.savePojo();
		if (list != null) {
			return ResultGenerator.genSuccessResult(list.size(), list);
		}
		return ResultGenerator.genNotFoundResult();
	}
	@GetMapping("/sysUser/updateForBatch")
	public Result updateForBatch() {
		ArrayList<SysUser> list = new ArrayList<SysUser>();
		SysUser sysUser0 = new SysUser();
		sysUser0.setUserName("hello0");
		sysUser0.setEmail("hello0@163.com");
		SysUser sysUser1 = new SysUser();
		sysUser1.setUserName("hello1");
		sysUser1.setEmail("hello1@163.com");
		SysUser sysUser2 = new SysUser();
		sysUser2.setUserName("hello2");
		sysUser2.setEmail("hello2@163.com");
		SysUser sysUser3 = new SysUser();
		sysUser3.setUserName("hello3");
		sysUser3.setEmail("hello3@163.com");
		sysUser0.setId("01");
		sysUser1.setId("02");
		sysUser2.setId("03");
		sysUser3.setId("04");
		list.add(sysUser0);
		list.add(sysUser1);
		list.add(sysUser2);
		list.add(sysUser3);
		this.sysUserService.updateForBatch(list);
		
		return ResultGenerator.genSuccessResult();
		
	}
	
	
	@GetMapping("/sysUser/savePojo1")
	public Result savePojo1() {
		SysUser sysUser = new SysUser();
		if (sysUser != null) {
			System.out.println("queryOne00000000000000000000000000000");
			System.out.println(sysUser.getUserName());
		}
		sysUser.setId("0001");
		SysUser queryOne = this.sysUserService.queryOne(sysUser);
		//System.out.println(queryOne.getUserName());
		System.out.println("queryOne============");
		if (queryOne != null) {
			System.out.println("queryOne========================+++++++++++++++++++++");
			return ResultGenerator.genSuccessResult();
		}
		return ResultGenerator.genNotFoundResult();
	}
	
	
	
	

}
