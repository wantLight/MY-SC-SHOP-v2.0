package com.imooc.myo2o.util;

public class PageCalculator {


	public static int calculatePageCount(int totalCount, int pageSize) {
		int idealPage = totalCount / pageSize;
		int totalPage = (totalCount % pageSize == 0) ? idealPage
				: (idealPage + 1);
		return totalPage;
	}

	//将pageIndex转换成rowIndex
	public static int calculateRowIndex(int pageIndex, int pageSize) {
		return (pageIndex > 0) ? (pageIndex - 1) * pageSize : 0;
	}
}
