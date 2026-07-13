package com.github.look4counter.mybatis;

public class ReplaceScriptParser {

	public String replace(String script) {
		if (script == null || script.isEmpty()) {
			return script;
		}

		char[] src = script.toCharArray();
		int len = src.length;
		StringBuilder sb = new StringBuilder();

		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;

		int i = 0;
		while (i < len) {
			char c = src[i];

			if (c == '"' && !inSingleQuote) {
				if (i == 0 || src[i - 1] != '\\') {
					inDoubleQuote = !inDoubleQuote;
				}
				sb.append(c);
				i++;
				continue;
			}

			if (c == '\'' && !inDoubleQuote) {
				if (i == 0 || src[i - 1] != '\\') {
					inSingleQuote = !inSingleQuote;
				}
				sb.append(c);
				i++;
				continue;
			}

			if (inSingleQuote || inDoubleQuote) {
				sb.append(c);
				i++;
				continue;
			}

			if (c == '/' && i + 1 < len && src[i + 1] == '*') {
				if (isMatch(src, i, "/*#")) {
					int start = i + 3;
					int end = findCommentEnd(src, start, len);

					if (end != -1) {
						int exprStart = start;
						int exprEnd = end;

						while (exprStart < exprEnd && Character.isWhitespace(src[exprStart])) {
							exprStart++;
						}
						while (exprEnd > exprStart && Character.isWhitespace(src[exprEnd - 1])) {
							exprEnd--;
						}

						sb.append("<").append(src, exprStart, exprEnd - exprStart).append(">");
						i = end + 2;
						continue;
					}
				}

				int end = findCommentEnd(src, i + 2, len);
				if (end != -1) {
					int commentLen = (end + 2) - i;
					sb.append(src, i, commentLen);
					i = end + 2;
					continue;
				}
			}

			if (c == ':') {
				if (i + 1 < len && src[i + 1] == ':') {
					sb.append("::");
					i += 2;
					continue;
				}

				int start = i + 1;
				int j = start;
				while (j < len && (Character.isLetterOrDigit(src[j]) || src[j] == '_')) {
					j++;
				}

				int paramLen = j - start;
				if (paramLen > 0) {
					sb.append("#{").append(src, start, paramLen).append("}");
					i = j;
					continue;
				}
			}

			sb.append(c);
			i++;
		}

		return sb.toString();
	}

	private int findCommentEnd(char[] src, int from, int len) {
		int j = from;
		while (j < len - 1) {
			if (src[j] == '*' && src[j + 1] == '/') {
				return j;
			}
			j++;
		}
		return -1;
	}

	private boolean isMatch(char[] src, int offset, String target) {
		if (offset + target.length() > src.length)
			return false;
		for (int i = 0; i < target.length(); i++) {
			if (src[offset + i] != target.charAt(i))
				return false;
		}
		return true;
	}
}
