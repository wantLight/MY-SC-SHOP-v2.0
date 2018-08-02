package com.imooc.myo2o.dao;


import com.imooc.myo2o.entity.Area;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AreaDaoTest{
	@Autowired
	private AreaDao areaDao;

	/**
	 * 遇到错误待解决  c3p0连接池问题？
	 * java.lang.AbstractMethodError:
	 * Method com/mchange/v2/c3p0/impl/NewProxyPreparedStatement.isClosed()Z is abstract
	 *
	 * 已解决- - c3p0版本太老了
	 */
	@Test
	public void testQueryArea(){
		List <Area> areaList = areaDao.queryArea();
		assertEquals(2,areaList.size());
	}
	@Test
	public void testAInsertArea() throws Exception {
		Area area = new Area();
		area.setAreaName("区域1");
		area.setAreaDesc("区域1");
		area.setPriority(1);
		area.setCreateTime(new Date());
		area.setLastEditTime(new Date());
		int effectedNum = areaDao.insertArea(area);
		assertEquals(1, effectedNum);
	}

	@Test
	public void testBQueryArea() throws Exception {
		List<Area> areaList = areaDao.queryArea();
		assertEquals(3, areaList.size());
	}

	@Test
	public void testCUpdateArea() throws Exception {
		Area area = new Area();
		area.setAreaId(1L);
		area.setAreaName("南苑");
		area.setLastEditTime(new Date());
		int effectedNum = areaDao.updateArea(area);
		assertEquals(1, effectedNum);
	}

	@Test
	public void testDDeleteArea() throws Exception {
		long areaId = -1;
		List<Area> areaList = areaDao.queryArea();
		for (Area myArea : areaList) {
			if ("区域1".equals(myArea.getAreaName())) {
				areaId = myArea.getAreaId();
			}
		}
		List<Long> areaIdList = new ArrayList<Long>();
		areaIdList.add(areaId);
		int effectedNum = areaDao.batchDeleteArea(areaIdList);
		assertEquals(1, effectedNum);
	}
}
