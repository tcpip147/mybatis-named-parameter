package com.github.look4counter.mybatis;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

public class NamedParameterLanguageDriver extends XMLLanguageDriver {

	private ReplaceScriptParser parser = new ReplaceScriptParser();

	@Override
	public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
		return super.createSqlSource(configuration, parser.replace(script), parameterType);
	}
}
