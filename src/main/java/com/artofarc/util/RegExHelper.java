/*
 * Copyright 2022 Andre Karalus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.artofarc.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.florianingerl.util.regex.Pattern;

/**
 * This class helps to get the minLength and maxLength of a possible match to a regular expression.
 * The core Java Pattern class has this functionality implemented but not public so this is a <b>hack</b>
 * in order to avoid a complete re-implementation of a regular expression parser.
 * <p><u>Limitation:</u> Does not support back references.
 * @see java.util.regex.Pattern.TreeInfo
 */
public final class RegExHelper {

	private final static Constructor<?> conTreeInfo;
	private final static Method Node$study;
	private final static Field Pattern$matchRoot;
	private final static Field TreeInfo$maxValid;
	private final static Field TreeInfo$maxLength;
	private final static Field TreeInfo$minLength;

	static {
		String PatternClassName = Pattern.class.getName();
		try {
			Class<?> Node = Class.forName(PatternClassName + "$Node");
			Class<?> TreeInfo = Class.forName(PatternClassName + "$TreeInfo");
			conTreeInfo = TreeInfo.getDeclaredConstructor();
			conTreeInfo.setAccessible(true);
			Node$study = Node.getDeclaredMethod("study", TreeInfo);
			Node$study.setAccessible(true);
			Pattern$matchRoot = Pattern.class.getDeclaredField("matchRoot");
			Pattern$matchRoot.setAccessible(true);
			TreeInfo$maxValid = TreeInfo.getDeclaredField("maxValid");
			TreeInfo$maxValid.setAccessible(true);
			TreeInfo$maxLength = TreeInfo.getDeclaredField("maxLength");
			TreeInfo$maxLength.setAccessible(true);
			TreeInfo$minLength = TreeInfo.getDeclaredField("minLength");
			TreeInfo$minLength.setAccessible(true);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Incompatible Pattern implementation " + PatternClassName);
		}
	}

	private final Pattern pattern;
	private final Object treeInfo;

	private RegExHelper(String regex) {
		// https://stackoverflow.com/questions/4304928/unicode-equivalents-for-w-and-b-in-java-regular-expressions/4307261#4307261
		pattern = Pattern.compile(convertXMLSchemaRegEx(regex), Pattern.UNICODE_CHARACTER_CLASS);
		try {
			treeInfo = conTreeInfo.newInstance();
			Node$study.invoke(Pattern$matchRoot.get(pattern), treeInfo);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException());
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public int getMaxLength() {
		try {
			return TreeInfo$maxValid.getBoolean(treeInfo) ? TreeInfo$maxLength.getInt(treeInfo) : Integer.MAX_VALUE;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public int getMinLength() {
		try {
			return TreeInfo$minLength.getInt(treeInfo);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean matches(String s) {
		return pattern.matcher(s).matches();
	}

	public static RegExHelper newRegExHelper(String regex) {
		return new RegExHelper(regex);
	}

	private static final Pattern _I = Pattern.compile("(^|[^\\\\])\\\\I");
	private static final String $_I = "$1[\\^\\\\i]";
	private static final Pattern _C = Pattern.compile("(^|[^\\\\])\\\\C");
	private static final String $_C = "$1[\\^\\\\c]";
	private static final Pattern _i = Pattern.compile("(^|[^\\\\])\\\\i");
	private static final String $_i = "$1[\\\\p{Alpha}:_]";
	private static final Pattern _c = Pattern.compile("(^|[^\\\\])\\\\c");
	private static final String $_c = "$1[\\\\p{Alnum}:_\\-\\.]";

	/**
	 * @param regex
	 * @return regex
	 * @see <a href="https://web.archive.org/web/20190305094825/http://www.xmlschemareference.com/regularExpression.html"/>XML schema regular expression</a>
	 */
	private static String convertXMLSchemaRegEx(String regex) {
		return _c.matcher(_i.matcher(_C.matcher(_I.matcher(regex).replaceAll($_I)).replaceAll($_C)).replaceAll($_i)).replaceAll($_c);
	}

}
