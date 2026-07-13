package com.github.look4counter.mybatis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NamedParameterLanguageDriverTest {

	@Test
	void testGuardSql() {
		ReplaceScriptParser parser = new ReplaceScriptParser();
		String sql = makeGuardTestSql("\"", "\"");
		assertEquals(sql, parser.replace(sql));

		sql = makeGuardTestSql("'", "'");
		assertEquals(sql, parser.replace(sql));

		sql = makeGuardTestSql("/*", "*/");
		assertEquals(sql, parser.replace(sql));

		sql = makeGuardTestSql("--", "\n");
		assertEquals(sql, parser.replace(sql));

		sql = makeGuardTestSql("--", "\r\n");
		assertEquals(sql, parser.replace(sql));
	}

	private String makeGuardTestSql(String guardianStart, String guardianEnd) {
		return guardianStart + " Guardian guard: :person " + guardianEnd;
	}
}
