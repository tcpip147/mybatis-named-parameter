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
				if (!isEscaped(src, i)) {
					inDoubleQuote = !inDoubleQuote;
				}
				sb.append(c);
				i++;
				continue;
			}

			if (c == '\'' && !inDoubleQuote) {
				if (!isEscaped(src, i)) {
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

						while (exprStart < exprEnd && Character.isWhitespace(src[exprStart]))
							exprStart++;
						while (exprEnd > exprStart && Character.isWhitespace(src[exprEnd - 1]))
							exprEnd--;

						sb.append("<");
						appendEscapedXml(sb, src, exprStart, exprEnd - exprStart);
						sb.append(">");

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
				} else {
					sb.append(src, i, len - i);
					break;
				}
			}

			if ((c == '-' && i + 1 < len && src[i + 1] == '-') || c == '#') {
				int start = i;
				while (i < len && src[i] != '\n' && src[i] != '\r') {
					i++;
				}
				sb.append(src, start, i - start);
				continue;
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
		if (offset + target.length() > src.length) {
			return false;
		}
		for (int i = 0; i < target.length(); i++) {
			if (src[offset + i] != target.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private void appendEscapedXml(StringBuilder sb, char[] src, int start, int length) {
		int end = start + length;
		for (int k = start; k < end; k++) {
			char ch = src[k];
			if (ch == '<') {
				sb.append("&lt;");
			} else if (ch == '>') {
				sb.append("&gt;");
			} else {
				sb.append(ch);
			}
		}
	}

	private boolean isEscaped(char[] src, int index) {
		int count = 0;
		int p = index - 1;
		while (p >= 0 && src[p] == '\\') {
			count++;
			p--;
		}
		return count % 2 != 0;
	}
}
