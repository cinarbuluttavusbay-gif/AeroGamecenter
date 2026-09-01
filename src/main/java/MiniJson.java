import java.util.*;

/**
 * Minimal, dış bağımlılık gerektirmeyen JSON parser.
 * Roblox API yanıtlarını çözmek için yeterli; Gson/Jackson gibi bir
 * kütüphane indirmene gerek kalmasın diye tek dosyada yazıldı.
 */
public final class MiniJson {

    private final String src;
    private int pos;

    private MiniJson(String src) {
        this.src = src;
        this.pos = 0;
    }

    public static Object parse(String json) {
        MiniJson parser = new MiniJson(json);
        parser.skipWhitespace();
        Object result = parser.parseValue();
        return result;
    }

    private Object parseValue() {
        char c = peek();
        switch (c) {
            case '{': return parseObject();
            case '[': return parseArray();
            case '"': return parseString();
            case 't': expect("true"); return Boolean.TRUE;
            case 'f': expect("false"); return Boolean.FALSE;
            case 'n': expect("null"); return null;
            default: return parseNumber();
        }
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        pos++; // {
        skipWhitespace();
        if (peek() == '}') { pos++; return map; }
        while (true) {
            skipWhitespace();
            String key = parseString();
            skipWhitespace();
            if (peek() != ':') throw new RuntimeException("JSON parse hatasi: ':' bekleniyordu, pos=" + pos);
            pos++; // :
            skipWhitespace();
            Object value = parseValue();
            map.put(key, value);
            skipWhitespace();
            char c = peek();
            if (c == ',') { pos++; continue; }
            if (c == '}') { pos++; break; }
            throw new RuntimeException("JSON parse hatasi: ',' ya da '}' bekleniyordu, pos=" + pos);
        }
        return map;
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        pos++; // [
        skipWhitespace();
        if (peek() == ']') { pos++; return list; }
        while (true) {
            skipWhitespace();
            list.add(parseValue());
            skipWhitespace();
            char c = peek();
            if (c == ',') { pos++; continue; }
            if (c == ']') { pos++; break; }
            throw new RuntimeException("JSON parse hatasi: ',' ya da ']' bekleniyordu, pos=" + pos);
        }
        return list;
    }

    private String parseString() {
        if (peek() != '"') throw new RuntimeException("JSON parse hatasi: '\"' bekleniyordu, pos=" + pos);
        pos++; // "
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = src.charAt(pos++);
            if (c == '"') break;
            if (c == '\\') {
                char esc = src.charAt(pos++);
                switch (esc) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u':
                        String hex = src.substring(pos, pos + 4);
                        sb.append((char) Integer.parseInt(hex, 16));
                        pos += 4;
                        break;
                    default: throw new RuntimeException("Bilinmeyen kacis dizisi: \\" + esc);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private Object parseNumber() {
        int start = pos;
        if (peek() == '-') pos++;
        while (pos < src.length() && (Character.isDigit(src.charAt(pos)) || src.charAt(pos) == '.'
                || src.charAt(pos) == 'e' || src.charAt(pos) == 'E'
                || ((src.charAt(pos) == '+' || src.charAt(pos) == '-') && pos > start))) {
            pos++;
        }
        String numStr = src.substring(start, pos);
        if (numStr.contains(".") || numStr.contains("e") || numStr.contains("E")) {
            return Double.parseDouble(numStr);
        }
        try {
            return Long.parseLong(numStr);
        } catch (NumberFormatException e) {
            return Double.parseDouble(numStr);
        }
    }

    private void expect(String literal) {
        if (!src.startsWith(literal, pos)) {
            throw new RuntimeException("JSON parse hatasi: '" + literal + "' bekleniyordu, pos=" + pos);
        }
        pos += literal.length();
    }

    private char peek() {
        return src.charAt(pos);
    }

    private void skipWhitespace() {
        while (pos < src.length() && Character.isWhitespace(src.charAt(pos))) pos++;
    }

    // ---- Yardimci erisim metodlari ----

    @SuppressWarnings("unchecked")
    public static Map<String, Object> asObject(Object o) {
        return (Map<String, Object>) o;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> asArray(Object o) {
        return (List<Object>) o;
    }

    public static String asString(Object o) {
        return o == null ? null : o.toString();
    }

    public static Long asLong(Object o) {
        if (o == null) return null;
        if (o instanceof Long l) return l;
        if (o instanceof Double d) return d.longValue();
        return Long.parseLong(o.toString());
    }

    public static int asInt(Object o) {
        Long l = asLong(o);
        return l == null ? 0 : l.intValue();
    }
}