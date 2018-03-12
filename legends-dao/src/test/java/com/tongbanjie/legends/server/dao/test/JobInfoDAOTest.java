package com.tongbanjie.legends.server.dao.test;

import com.tongbanjie.legends.server.dao.JobInfoDAO;
import com.tongbanjie.legends.server.dao.JobSnapshotDAO;
import com.tongbanjie.legends.server.dao.dataobject.JobInfo;
import com.tongbanjie.legends.server.dao.query.JobInfoQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author sunyi
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/legends-dao-test.xml"})
public class JobInfoDAOTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public JobInfoDAO dao;

	@Autowired
	public JobSnapshotDAO jobSnapshotDAO;

	@Test
	public void testMain() {
		Assert.notNull(dao);

		JobInfoQuery query = new JobInfoQuery();
		query.setLikeName("上");
		List<JobInfo> list = dao.findByParam(query);

		int i = dao.countByParam(query);

		Assert.isTrue(list.size() == i);
	}

}
