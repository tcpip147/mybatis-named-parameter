package com.github.look4counter.mybatis;

import org.junit.jupiter.api.Test;

public class NamedParameterLanguageDriverTest {

	@Test
	void testCreateSqlSource() {
		String originalScript = """
				/* :grade */
				/*#if test="grade != null"*/
				/*#/if*/
				SELECT *
				  FROM TB_USER
				 WHERE NAME = ":name"
				   AND AGE = :age
				   AND K = 1
				""";

		System.out.println(new ReplaceScriptParser().replace(originalScript));
	}
}
