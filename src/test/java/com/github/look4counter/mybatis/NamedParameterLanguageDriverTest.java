package com.github.look4counter.mybatis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NamedParameterLanguageDriverTest {

	@Test
	void testGuardSql() {
		ReplaceScriptParser parser = new ReplaceScriptParser();
		String sql = makeGuardTestSql("\"", "\"");
		assertEquals(encloseScript(sql), parser.replace(sql));

		sql = makeGuardTestSql("'", "'");
		assertEquals(encloseScript(sql), parser.replace(sql));

		sql = makeGuardTestSql("/*", "*/");
		assertEquals(encloseScript(sql), parser.replace(sql));

		sql = makeGuardTestSql("--", "\n");
		assertEquals(encloseScript(sql), parser.replace(sql));

		sql = makeGuardTestSql("--", "\r\n");
		assertEquals(encloseScript(sql), parser.replace(sql));
	}

	private String makeGuardTestSql(String guardianStart, String guardianEnd) {
		return guardianStart + " Guardian guard: :person " + guardianEnd;
	}

	private String encloseScript(String script) {
		return "<script>" + script + "</script>";
	}
}
