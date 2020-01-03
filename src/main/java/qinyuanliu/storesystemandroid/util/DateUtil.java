package qinyuanliu.storesystemandroid.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间工具类     和时间有关的 功能
 *
 */
public class DateUtil {
	
	public static String FILE_NAME = "MMddHHmmssSSS";
	public static String DEFAULT_PATTERN = "yyyy-MM-dd";
	public static String YM_PATTERN = "yyyy-MM";
	public static String DIR_PATTERN = "yyyy/MM/dd/";
	public static String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static String TIMES_PATTERN = "HH:mm:ss";
	public static String TIMES_YMDHM = "yyyy-MM-dd HH:mm";
	public static String NOCHAR_PATTERN = "yyyyMMddHHmmss";
	public static String CHINESE_PATTEN = "yyyy年MM月dd日";
	
	/**
	 * 获取当前时间
	 */
	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}
	/**
	 * 获取当前时间  格式为yyyy-MM-dd HH:mm:ss
	 */
	public static String getCurrentTimeStr() {
		return getCurrentTime("yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 获取当前日期  格式为yyyy-MM-dd
	 */
	public static String getCurrentDateStr() {
		return getCurrentTime(DEFAULT_PATTERN);
	}

	/**
	 * 获取当前日期  格式为yyyy-MM
	 */
	public static String getCurrentYM() {
		return getCurrentTime(YM_PATTERN);
	}

	public static String getNextMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, 1);

		return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1);
	}

	public static String formatDate(String format, Date dt) {
		if (dt == null) {
			return "";
		}
		if (null == format || format.trim().length() == 0) {
			format = "yyyy-MM-dd";
		}
		SimpleDateFormat fmt = new SimpleDateFormat(format);
		return fmt.format(dt);
	}
	/**
	 * 
	 * @param timeMillis
	 * @return    String :  2011-16-45 02:30:11
	 */
	public static String timeMillis2TimeStr( long timeMillis ){
		
		Calendar calendar = Calendar.getInstance() ;
		calendar.setTimeInMillis(timeMillis) ;
		
		return calendar2TimeStr(calendar) ; 
	}
	
	/**
	 * 
	 * @param date
	 * @return    String :  2011-16-45 02:30:11
	 */
	public static String date2TimeStr(Date date){
		
		Calendar calendar = Calendar.getInstance() ;
		calendar.setTimeInMillis(date.getTime()) ;
		
		return calendar2TimeStr(calendar) ;  
	}
	
	/**
	 * 
	 * @param calendar
	 * @return    String :  2011-16-45 02:30:11
	 */
	public static String calendar2TimeStr ( Calendar  calendar){
		String year = String.valueOf(calendar.get(Calendar.YEAR)) ;
		String month  = String.format("%02d",calendar.get(Calendar.MONTH)+1 ) ; //%02d 格式化为至少2位十进制整数
		String day = String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH)) ;
		String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY)) ;
		String minutes = String.format("%02d",calendar.get(Calendar.MINUTE)) ;
		String seconds = String.format("%02d",calendar.get(Calendar.SECOND)) ;
		
		StringBuilder sb = new StringBuilder() ;
		sb.append(year).append('-').append(month).append('-').append(day).append(' ').append(hour).append(':').append(minutes).append(':').append(seconds);
		return  sb.toString() ;
	}
	
	/**
	 * 
	 * @param dateStr  dateStr ：2011-16-45 02:30:11 
	 * @return
	 */
	public static Date timeStr2Date(String dateStr){
		Calendar calendar = timeStr2Calendar(dateStr) ;
		Date date = calendar.getTime() ;
		return date ;
	}
	
	/**
	 * 
	 * @param dateStr  dateStr ：2011-16-45 02:30:11 
	 * @return
	 */
	public static Calendar timeStr2Calendar( String dateStr){
		
		String [] dateTime = dateStr.split(" ") ;
		String date = dateTime[0] ;
		String time = dateTime[1] ;
		String[] dateArra = date.split("-") ;
		String[]  timeArra = time.split(":") ;
		
		int year = Integer.valueOf( dateArra[0] );
		int month = Integer.valueOf(  dateArra[1]) ;
		int day = Integer.valueOf(  dateArra[2]) ;
		int hour = Integer.valueOf(timeArra[0]) ;
		int minute = Integer.valueOf(timeArra[1]) ;
		int second = Integer.valueOf(timeArra[2]) ;
		
		Calendar calendar = Calendar.getInstance() ;
		calendar.set(year, month, day, hour, minute, second) ;
		
		return calendar ;
	}
	
	/**
	 * 检查传入字符串是否为规定格式  ：2011-16-45 02:30:11
	 * @param timeStr 被检查字符串
	 * @return boolean 是：true， 不是：false
	 */
	public static boolean isTimeStr( String timeStr ){
		
		if ( 19 == timeStr.length() ){
			String reg = "^[1-9]\\d{3}-(?:0[1-9]|1[012])-(?:0[1-9]|[12]\\d|3[01])\\s(?:[01]\\d|2[0123]):(?:[0-4]\\d|5[0-9]):(?:[0-4]\\d|5[0-9])$" ;

			Pattern pattern = Pattern.compile(reg) ;
			Matcher matcher = pattern.matcher(timeStr) ;
			if( matcher.find() ){
				return true ;
			}else{
				return false ;
			}
		}else{
			return false ;
		}
	}
	
	public static boolean isImportedStr ( String importedStr ){
		
		if( 19 == importedStr.length() ){
			String reg = "^\\d{4}\\-\\d{1,2}-\\d{1,2}-\\d{1,2}-\\d{1,2}-\\d{1,2}$" ;
			Pattern pattern = Pattern.compile(reg) ;
			Matcher matcher = pattern.matcher(importedStr) ;
			if ( matcher.find() ){
				return true ;
			}else {
				return false ;
			}
		}else {
			return false ;
		}
	}
	
	
	
	/**
	 * // 将导入目标目录名称 2011-01-06-05-40-56 转换为SQLite中支持时间格式   2008-01-03 21:54:06
	 * @param importStr    2011-01-06-05-40-56
	 * @return  String     2008-01-03 21:54:06
	 */
	public static String importStr2TimeStr( String importStr ){
		StringBuilder sb = new StringBuilder( importStr ) ;
        sb.replace(10, 11, " ") ;
        sb.replace(13, 14, ":") ;
        sb.replace(16, 17, ":") ;
		String timeStr = sb.toString() ;
		return timeStr ;
	}
	
	/**
	 * 将时间格式的字符串  2008-01-03 21:54:06 转换为文件系统支持的表现形式 2011-01-06-05-40-56
	 * @param timeStr    2008-01-03 21:54:06
	 * @return  String   2011-01-06-05-40-56
	 */
	public static String timeStrImportStr( String timeStr ){
		timeStr = timeStr.replace(' ', '-') ;
		return timeStr.replace(':', '-') ;
	}
	
	private static final String FAST_FORMAT_HMMSS = "%1$d:%2$02d:%3$02d";

	/**
     * Fast formatting of h:mm:ss
     */
    public static String formatElapsedTime( long elapsedSeconds ) {
    	elapsedSeconds /=1000 ;
    	long hours = 0 ;
    	long minutes = 0 ;
    	long seconds = 0 ;
    	
    	if (elapsedSeconds >= 3600) {
            hours = elapsedSeconds / 3600;
            elapsedSeconds -= hours * 3600;
        }
        if (elapsedSeconds >= 60) {
            minutes = elapsedSeconds / 60;
            elapsedSeconds -= minutes * 60;
        }
        seconds = elapsedSeconds;
        
        return formatElapsedTime( "%1$d:%2$02d:%3$02d" , hours, minutes, seconds) ;
    }
	
    /**
     * Fast formatting of h:mm:ss
     */
    private static String formatElapsedTime( String format, long hours, long minutes, long seconds ) {
    	StringBuilder sb = new StringBuilder() ;
    	
    	if( format.equals(FAST_FORMAT_HMMSS) ){
    		if( hours < 10 ){
    			sb.append('0').append(hours) ;
    		}else{
    			sb.append(hours) ;
    		}
    		sb.append(':') ;
    		if( minutes < 10 ){
    			sb.append('0').append(minutes) ;
    		}else{
    			sb.append(minutes) ;
    		}
    		sb.append(':') ;
    		if( seconds < 10 ){
    			sb.append('0').append(seconds) ;
    		}else{
    			sb.append(seconds) ;
    		}
    		return sb.toString();
    	}else{
    		return null ;
    	}
    }
    
    /**
	 * 计算过去到现在有多久
	 * @param spaceTime
	 * 			过去距现在的毫秒数
	 */
	public static String getSpaceTime(long spaceTime){
		long minuteTime = 1000*60;
        long hourTime = 60*minuteTime;
        long dateTime = 24*hourTime ;//一天的毫秒数
        long minute = spaceTime/1000;
        String space = "";
        if(spaceTime<minuteTime){
            space = "刚刚";
        }
        else if(spaceTime<hourTime){
            space = (int)Math.floor(minute/60)+"分钟前";
        }
        else if(spaceTime<dateTime){
            space = (int)Math.floor(minute/60/60)+"小时前";
        }
        else if(spaceTime<7*dateTime){
            space = (int)Math.floor(minute/60/60/24)+"天前";
        }
        else{
            space = formatDate(DEFAULT_PATTERN, new Date(new Date().getTime()-spaceTime));
        }
        
        return space;
	}
	
	/**
	 * 日期格式字符串转换为日期对象
	 * 
	 * @param strDate
	 *            日期格式字符串
	 * @param pattern
	 *            日期对象
	 * @return
	 */
	public static Date parseDate(String strDate, String pattern) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			Date nowDate = format.parse(strDate);
			return nowDate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 字符串转换为默认格式(yyyy-MM-dd)日期对象
	 * 
	 * @param date
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static Date parseDefaultDate(String date) {
		return parseDate(date, DEFAULT_PATTERN);
	}

	/**
	 * 字符串转换为完整格式(yyyy-MM-dd HH:mm:ss)日期对象
	 * 
	 * @param date
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static Date parseTimesTampDate(String date) {
		return parseDate(date, TIMESTAMP_PATTERN);
	}
	
	/** 
     * 计算两个日期之间相差的天数 
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
	 * @throws ParseException 
     */  
    public static int daysBetween(Date smdate,Date bdate) {  
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	try {
			smdate=sdf.parse(sdf.format(smdate));
			bdate=sdf.parse(sdf.format(bdate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
        Calendar cal = Calendar.getInstance();  
        cal.setTime(smdate);  
        long time1 = cal.getTimeInMillis();               
        cal.setTime(bdate);  
        long time2 = cal.getTimeInMillis();       
        long between_days=(time2-time1)/(1000*3600*24);
          
       return Integer.parseInt(String.valueOf(between_days));         
    }  
    
    /**
     *字符串的日期格式的计算
     */
    public static int daysBetween(String smdate,String bdate) {
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();  
        try {
			cal.setTime(sdf.parse(smdate));
		} catch (ParseException e) {
			e.printStackTrace();
		}  
        long time1 = cal.getTimeInMillis();               
        try {
			cal.setTime(sdf.parse(bdate));
		} catch (ParseException e) {
			e.printStackTrace();
		}  
        long time2 = cal.getTimeInMillis();       
        long between_days=(time2-time1)/(1000*3600*24);
          
       return Integer.parseInt(String.valueOf(between_days));   
    }

	/**
	 * 给指定日期加上或减去指定天数
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDay(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);

		return calendar.getTime();
	}
	public static String GetDateString(int y, int m, int d){
		String yy = String.valueOf(y);
		String mm = "";
		String dd = "";

		if (m <= 9) {
			mm = "0" + m;
		} else {
			mm = String.valueOf(m);
		}
		if (d <= 9) {
			dd = "0" + d;
		} else {
			dd = String.valueOf(d);
		}
		String str = String.format("%s-%s-%s", yy, mm, dd);
		return str;
	}
}
