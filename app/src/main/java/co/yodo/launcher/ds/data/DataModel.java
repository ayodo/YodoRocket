package co.yodo.launcher.ds.data;

import sunmi.ds.data.DSData.DataType;

/**
 * 数据模型
 * 
 * @author longtao.li
 */
public enum DataModel {
	// DATA
	RESULT(DataType.DATA, 0x011), // 显示结果
	NORMOL(DataType.DATA, 0x021), // 显示正常
	TEXT(DataType.CMD, 0x031), // 显示文字，包括title content
	TEXT_SINGLE(DataType.CMD, 0x031),//显示单行，居中文字

	// FILE
	FILE(DataType.FILE, 0x041),

	// CMD
	APK(DataType.CMD, 0x051), // 安装apk
	OTA(DataType.CMD, 0x061), // 刷OTA包
	IMAGE(DataType.CMD, 0x071), // 显示图片
	IMAGES(DataType.CMD, 0x072), // 显示图片轮播
	VIDEO(DataType.CMD, 0x081), // 播放视频
	AUDIO(DataType.CMD, 0x091), // 播放音频
	READ_BRIGHTNESS(DataType.CMD, 0x101), // 读取副屏亮度
	SET_BRIGHTNESS(DataType.CMD, 0x111), // 设置副屏亮度
	SHUTDOWN(DataType.CMD, 0x121), // 控制副屏关机
	SCREEN_UNLOCK(DataType.CMD, 0x131), // 解锁副屏
	QRCODE(DataType.CMD, 0x141),//显示二维码
	GET_SECOND_SCREEN_DATA(DataType.CMD, 0x151),//获取副屏设置应用数据
	SET_MUSIC_VOLUME(DataType.CMD, 0x161),//设置媒体音量
	OPEN_APP(DataType.CMD, 0x171),//打开一个app
	REBOOT(DataType.CMD, 0x181), // 控制副屏关机

	SHOW_IMG_WELCOME(DataType.CMD, 0x191),//显示全屏图片的welcom界面
	SHOW_IMG_LIST(DataType.CMD, 0x201),//显示图和列表页面
	CLEAN_FILES(DataType.CMD,0x206);
	DataType dataType;

	int modelCode;// 模型描述code

	int dataModelCode;// 对数据类型与模型的描述code

	DataModel(DataType type, int code) {
		this.dataType = type;
		this.modelCode = code;
		this.dataModelCode = dataType.typeCode & modelCode;
	}

}