package cn.hesimin.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 日期工具类
 * <p>继承org.apache.commons.lang3.time.DateUtils类
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	private static String[] parsePatterns = {"yyyy-MM-dd",
			"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd",
			"yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyyMMdd"};

	/**
	 * 格式化当前日期
	 *
	 * @return yyyy-MM-dd
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}

	/**
	 * 得到当前日期字符串
	 *
	 * @param pattern 日期格式可以为："yyyy-MM-dd" "HH:mm:ss" "E"等
	 * @return 格式化后的单曲日期
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}

	/**
	 * 获取当前时间字符串
	 *
	 * @return HH:mm:ss
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 获取当前时间和日期字符串
	 *
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 格式花日期
	 *
	 * @param date    日期
	 * @param pattern 格式
	 * @return 格式化后的日期
	 */
	public static String formatDate(Date date, String pattern) {
		if (date == null || StringUtils.isBlank(pattern)) {
			throw new IllegalArgumentException();
		}
		return DateFormatUtils.format(date, pattern);
	}

	/**
	 * 格式化日期
	 *
	 * @param date 被格式化日期
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}


	/**
	 * 获取当前年份
	 *
	 * @return yyyy
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 获取当前月份
	 *
	 * @return MM
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 获取当前是月的第几日
	 *
	 * @return dd
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 获取星期字符串
	 *
	 * @return E
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}

	/**
	 * 日期型字符串转化为日期
	 * <p>格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyyMMdd" }
	 */
	public static Date parseDate(String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		try {
			return parseDate(str, parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 *
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	/**
	 * 获取过去的小时
	 *
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (60 * 60 * 1000);
	}

	/**
	 * 获取过去的分钟
	 *
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (60 * 1000);
	}

	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 *
	 * @param timeMillis
	 * @return
	 */
	public static String formatDateTime(long timeMillis) {
		long day = timeMillis / (24 * 60 * 60 * 1000);
		long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
		long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
		return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
	}

	/**
	 * 获取某一天的开始时间（0点）
	 *
	 * @param date
	 * @return
	 */
	public static Date getDateStart(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获取某一天的结束时间(23:59)
	 *
	 * @param date
	 * @return
	 */
	public static Date getDateEnd(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 比较两个日期时间的大小,反回1表示preDateStr > nextDateStr，0就相等，-1为小于
	 *
	 * @param preDateStr
	 * @param nextDateStr
	 * @return result
	 */
	public static int compareDate(String preDateStr, String nextDateStr) {
		Date preDate = parseDate(preDateStr);
		Date nextDate = parseDate(nextDateStr);
		return preDate.compareTo(nextDate);
	}

	/**
	 * 获取某一天的前几天或者后几天，根据数字符号决定天数
	 *
	 * @param dateStr
	 * @param days
	 * @return
	 */
	public static String getPastDayStr(String dateStr, int days) {
		Date date = parseDate(dateStr);
		if (date == null) {
			throw new IllegalArgumentException("dateStr error.");
		}
		long time = date.getTime() + days * (long) (24 * 60 * 60 * 1000);
		return formatDate(new Date(time), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * preDateStr - nextDateStr 返回秒数
	 *
	 * @param preDateStr
	 * @param nextDateStr
	 * @return
	 */
	public static long getSubactDate(String preDateStr, String nextDateStr) {
		Date preDate = parseDate(preDateStr);
		Date nextDate = parseDate(nextDateStr);
		return (preDate.getTime() - nextDate.getTime()) / 1000L;
	}

	/**
	 * 返回过去的天数： preDateStr - nextDateStr
	 *
	 * @param preDateStr
	 * @param nextDateStr
	 * @return
	 */
	public static long getDifferDate(String preDateStr, String nextDateStr) {
		return getSubactDate(preDateStr, nextDateStr) / (60 * 60 * 24L);
	}

	/**
	 * 传入日期时间与当前系统日期时间的比较,
	 * 若日期相同，则根据时分秒来返回 ,
	 * 否则返回具体日期
	 *
	 * @param updateDate 传入日期
	 * @param updateTime 传入时间
	 * @return 日期或者 xx小时前||xx分钟前||xx秒前
	 */
	public static String getNewUpdateDateString(String updateDate, String updateTime) {
		String result = updateDate;
		if (updateDate.equals(DateUtils.getDate())) {
			long time = DateUtils.getSubactDate(DateUtils.getDateTime(), updateDate
					+ " " + updateTime);
			if (time >= 3600) {
				result = time / 3600 + "小时前";
			} else if (time >= 60) {
				result = time / 60 + "分钟前";
			} else if (time >= 1) {
				result = time + "秒前";
			} else {
				result = "刚刚";
			}
		} else if (result.length() >= 10) {
			result = result.substring(5);
		}
		return result;
	}
}