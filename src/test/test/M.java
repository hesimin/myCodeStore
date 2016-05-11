package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2016/5/11.
 */
public class M {
	private static final Logger logger = LoggerFactory.getLogger(M.class);
	public static void main(String[] args) {
		logger.trace("trace");
		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.error("error");
	}
}
