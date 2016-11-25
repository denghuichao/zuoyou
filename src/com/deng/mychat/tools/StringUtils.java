package com.deng.mychat.tools;

import java.util.regex.Pattern;

public class StringUtils {

	public static boolean isMobileNO(String string) {
		// TODO Auto-generated method stub
		return string.matches("\\d+");
	}
}
