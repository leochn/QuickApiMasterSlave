package com.vnext.controller;

import com.github.pagehelper.PageInfo;
import com.vnext.core.Result;
import com.vnext.core.ResultGenerator;
import com.vnext.pojo.SysAdmin;
import com.vnext.service.SysAdminService;

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

import java.util.List;

/**
 * Created by CodeGenerator on 2017/08/11.
 */
@RestController
@RequestMapping("/api")
public class SysAdminController {

    @Autowired
    private SysAdminService sysAdminService;  
    
    /**
     * 分页获取数据
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/sysAdminList")
    public Result getPageList(
    		@RequestParam(value = "page") Integer page,
			@RequestParam(value = "rows") Integer rows) {
    	PageInfo<SysAdmin> pageInfo = this.sysAdminService.queryPageListByWhere(new SysAdmin(), page, rows);
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
    @GetMapping("/sysAdminListOrderBy")
    public Result getPageListAndOrderBy(
    		@RequestParam(value = "page") Integer page,
			@RequestParam(value = "rows") Integer rows,
			@RequestParam(value = "sortField", defaultValue = "ID") String sortField,
			@RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {
    	PageInfo<SysAdmin> pageInfo = this.sysAdminService.queryPageListByWhereAndOrderBy(new SysAdmin(), page, rows, sortField, sortOrder);
		if (pageInfo != null) {
			return ResultGenerator.genSuccessResult(pageInfo.getTotal(), pageInfo.getList());
		}
		return ResultGenerator.genNotFoundResult();
    }
    
    /**
     * 获取全部数据
     * @return
     */
	@GetMapping("/sysAdmins")
	public Result all() {
		List<SysAdmin> list = this.sysAdminService.queryAll();
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
    @GetMapping("/sysAdmin/{id}")
    public Result detail(@PathVariable String id) {
        SysAdmin record = this.sysAdminService.queryById(id);
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
	@GetMapping("/sysAdmin/{fieldName}/{value}")
	public Result queryByFieldName(
			@PathVariable("fieldName") String fieldName,
			@PathVariable("value") String value) {
		SysAdmin record = this.sysAdminService.queryByFieldName(fieldName, value);
		if (record != null) {
			return ResultGenerator.genSuccessResult(1, record);
		}
		return ResultGenerator.genFailResult();
	}
    
    /**
	 * 新增一条信息
	 * @return
	 */
	@PostMapping("/sysAdmin")
	public Result add(@RequestBody SysAdmin record) {
		Integer num = this.sysAdminService.saveSelective(record);
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
	@PutMapping("/sysAdmin/{id}")
	public Result update(@PathVariable("id") String id, @RequestBody SysAdmin record) {
		Integer num = this.sysAdminService.updateSelective(record);
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
	@DeleteMapping("/sysAdmin/{id}")
	public Result delete(@PathVariable("id") String id) {
		Integer num = this.sysAdminService.deleteById(id);
		if (num == 1) {
			return ResultGenerator.genSuccessResult();
		}
		return ResultGenerator.genFailResult();
	}

}
