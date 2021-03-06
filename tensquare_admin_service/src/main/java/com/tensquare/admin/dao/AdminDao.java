package com.tensquare.admin.dao;

import com.tensquare.admin.pojo.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * admin数据访问接口
 * @author Administrator
 *
 */
public interface AdminDao extends JpaRepository<Admin,String>,JpaSpecificationExecutor<Admin>{

	/**
	 * 查询用户名
	 * @param loginname
	 * @return
	 */
	Admin findByLoginname(String loginname);
	
}
