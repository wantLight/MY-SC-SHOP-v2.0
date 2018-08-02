package com.imooc.myo2o.util;


import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ImageUtil {

    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
	private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");

	//处理缩略图，调用spring自带的文件处理对象CommonsMultipartFile
	public static String generateThumbnail(InputStream thumbnailInputStream,String fileName,String targetAddr) {
		//生成随机文件名
		String realFileName = FileUtil.getRandomFileName();
		//获取扩展名
		String extension = getFileExtension(fileName);
		//创建生成目录
		makeDirPath(targetAddr);
		String relativeAddr = targetAddr + realFileName + extension;
		File dest = new File(FileUtil.getImgBasePath() + relativeAddr);
		try {
			//Thumbnails.of(thumbnail.getInputStream()).size(200, 200).outputQuality(0.25f).toFile(dest);
			Thumbnails.of(thumbnailInputStream)
					.size(200,200).watermark(Positions.BOTTOM_RIGHT,
					ImageIO.read(new File(basePath + "/watermark.jpg")),0.25f)
					.outputQuality(0.8f).toFile(dest);
		} catch (IOException e) {
		    logger.error(e.toString());
			throw new RuntimeException("创建缩略图失败：" + e.toString());
		}
		return relativeAddr;
	}

	//这里可以替换为ImageHolder
	//生成图片，并返回相对路径
	public static String generateNormalImg(InputStream thumbnailInputStream,String FileName, String targetAddr) {
		String realFileName = FileUtil.getRandomFileName();
		String extension = getFileExtension(FileName);
		makeDirPath(targetAddr);
		String relativeAddr = targetAddr + realFileName + extension;
		File dest = new File(FileUtil.getImgBasePath() + relativeAddr);
		try {
			Thumbnails.of(thumbnailInputStream).size(337, 640).outputQuality(0.5f).toFile(dest);
		} catch (IOException e) {
			throw new RuntimeException("创建缩略图失败：" + e.toString());
		}
		return relativeAddr;
	}

	public static List<String> generateNormalImgs(List<File> imgs, String imgName,String targetAddr) {
		int count = 0;
		List<String> relativeAddrList = new ArrayList<String>();
		if (imgs != null && imgs.size() > 0) {
			makeDirPath(targetAddr);
			for (File img : imgs) {
				String realFileName = FileUtil.getRandomFileName();
				//todo 处理图片名称待修改
				String extension = getFileExtension(imgName);
				String relativeAddr = targetAddr + realFileName + count + extension;
				File dest = new File(FileUtil.getImgBasePath() + relativeAddr);
				count++;
				try {
					Thumbnails.of(img).size(600, 300).outputQuality(0.5f).toFile(dest);
				} catch (IOException e) {
					throw new RuntimeException("创建图片失败：" + e.toString());
				}
				relativeAddrList.add(relativeAddr);
			}
		}
		return relativeAddrList;
	}

	//创建目标路径所涉及到的目录
	private static void makeDirPath(String targetAddr) {
		//由相对路径获取绝对路径
		String realFileParentPath = FileUtil.getImgBasePath() + targetAddr;
		File dirPath = new File(realFileParentPath);
		if (!dirPath.exists()) {
			//创建目录
			dirPath.mkdirs();
		}
	}

	//获取文件名的扩展名
    private static String getFileExtension(String fileName) {
		//获取.后面的字符
		return fileName.substring(fileName.lastIndexOf("."));
	}

	//图片打水印并缩小
	public static void main(String[] args) throws IOException {
		Thumbnails.of(new File("/Users/baidu/work/image/xioahuangren.jpg"))
				.size(200,200).watermark(Positions.BOTTOM_RIGHT,
				ImageIO.read(new File(basePath + "/watermark.jpg")),0.25f)
				.outputQuality(0.8f).toFile("xxxxxx.jpg");
	}
}
