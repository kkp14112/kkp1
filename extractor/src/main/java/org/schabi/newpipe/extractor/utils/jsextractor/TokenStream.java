package org.schabi.newpipe.extractor.utils.jsextractor;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.ScriptRuntime;
import org.schabi.newpipe.extractor.exceptions.ParsingException;

import java.io.IOException;
import java.io.Reader;

/*
Source: org.mozilla.javascript.TokenStream (private class)
 */
class TokenStream {
    /*
     * For chars - because we need something out-of-range
     * to check.  (And checking EOF by exception is annoying.)
     * Note distinction from EOF token type!
     */
    private static final int EOF_CHAR = -1;

    /*
     * Return value for readDigits() to signal the caller has
     * to return an number format problem.
     */
    private static final int REPORT_NUMBER_FORMAT_ERROR = -2;

    private static final char BYTE_ORDER_MARK = '\uFEFF';
    private static final char NUMERIC_SEPARATOR = '_';

    TokenStream(final Reader sourceReader, final String sourceString, final int lineno) {
        this.lineno = lineno;
        if (sourceReader != null) {
            if (sourceString != null) {
                Kit.codeBug();
            }
            this.sourceReader = sourceReader;
            this.sourceBuffer = new char[512];
            this.sourceEnd = 0;
        } else {
            if (sourceString == null) {
                Kit.codeBug();
            }
            this.sourceString = sourceString;
            this.sourceEnd = sourceString.length();
        }
        this.sourceCursor = 0;
        this.cursor = 0;
    }

    static boolean isKeyword(final String s, final int version, final boolean isStrict) {
        return Token.EOF != stringToKeyword(s, version, isStrict);
    }

    private static int stringToKeyword(final String name, final int version,
                                       final boolean isStrict) {
        if (version < Context.VERSION_ES6) {
            return stringToKeywordForJS(name);
        }
        return stringToKeywordForES(name, isStrict);
    }

    /** JavaScript 1.8 and earlier */
    @SuppressWarnings({"checkstyle:LocalFinalVariableName",
            "checkstyle:MultipleVariableDeclarations", "MethodLength"})
    private static int stringToKeywordForJS(final String name) {
        // The following assumes that Token.EOF == 0
        final int Id_break = Token.BREAK,
                Id_case = Token.CASE,
                Id_continue = Token.CONTINUE,
                Id_default = Token.DEFAULT,
                Id_delete = Token.DELPROP,
                Id_do = Token.DO,
                Id_else = Token.ELSE,
                Id_export = Token.RESERVED,
                Id_false = Token.FALSE,
                Id_for = Token.FOR,
                Id_function = Token.FUNCTION,
                Id_if = Token.IF,
                Id_in = Token.IN,
                Id_let = Token.LET, // reserved ES5 strict
                Id_new = Token.NEW,
                Id_null = Token.NULL,
                Id_return = Token.RETURN,
                Id_switch = Token.SWITCH,
                Id_this = Token.THIS,
                Id_true = Token.TRUE,
                Id_typeof = Token.TYPEOF,
                Id_var = Token.VAR,
                Id_void = Token.VOID,
                Id_while = Token.WHILE,
                Id_with = Token.WITH,
                Id_yield = Token.YIELD, // reserved ES5 strict

                // the following are #ifdef RESERVE_JAVA_KEYWORDS in jsscan.c
                Id_abstract = Token.RESERVED, // ES3 only
                Id_boolean = Token.RESERVED, // ES3 only
                Id_byte = Token.RESERVED, // ES3 only
                Id_catch = Token.CATCH,
                Id_char = Token.RESERVED, // ES3 only
                Id_class = Token.RESERVED,
                Id_const = Token.CONST, // reserved
                Id_debugger = Token.DEBUGGER,
                Id_double = Token.RESERVED, // ES3 only
                Id_enum = Token.RESERVED,
                Id_extends = Token.RESERVED,
                Id_final = Token.RESERVED, // ES3 only
                Id_finally = Token.FINALLY,
                Id_float = Token.RESERVED, // ES3 only
                Id_goto = Token.RESERVED, // ES3 only
                Id_implements = Token.RESERVED, // ES3, ES5 strict
                Id_import = Token.RESERVED,
                Id_instanceof = Token.INSTANCEOF,
                Id_int = Token.RESERVED, // ES3
                Id_interface = Token.RESERVED, // ES3, ES5 strict
                Id_long = Token.RESERVED, // ES3 only
                Id_native = Token.RESERVED, // ES3 only
                Id_package = Token.RESERVED, // ES3, ES5 strict
                Id_private = Token.RESERVED, // ES3, ES5 strict
                Id_protected = Token.RESERVED, // ES3, ES5 strict
                Id_public = Token.RESERVED, // ES3, ES5 strict
                Id_short = Token.RESERVED, // ES3 only
                Id_static = Token.RESERVED, // ES3, ES5 strict
                Id_super = Token.RESERVED,
                Id_synchronized = Token.RESERVED, // ES3 only
                Id_throw = Token.THROW,
                Id_throws = Token.RESERVED, // ES3 only
                Id_transient = Token.RESERVED, // ES3 only
                Id_try = Token.TRY,
                Id_volatile = Token.RESERVED; // ES3 only

        int id = 0;
        switch (name) {
            case "break":
                id = Id_break;
                break;
            case "case":
                id = Id_case;
                break;
            case "continue":
                id = Id_continue;
                break;
            case "default":
                id = Id_default;
                break;
            case "delete":
                id = Id_delete;
                break;
            case "do":
                id = Id_do;
                break;
            case "else":
                id = Id_else;
                break;
            case "export":
                id = Id_export;
                break;
            case "false":
                id = Id_false;
                break;
            case "for":
                id = Id_for;
                break;
            case "function":
                id = Id_function;
                break;
            case "if":
                id = Id_if;
                break;
            case "in":
                id = Id_in;
                break;
            case "let":
                id = Id_let;
                break;
            case "new":
                id = Id_new;
                break;
            case "null":
                id = Id_null;
                break;
            case "return":
                id = Id_return;
                break;
            case "switch":
                id = Id_switch;
                break;
            case "this":
                id = Id_this;
                break;
            case "true":
                id = Id_true;
                break;
            case "typeof":
                id = Id_typeof;
                break;
            case "var":
                id = Id_var;
                break;
            case "void":
                id = Id_void;
                break;
            case "while":
                id = Id_while;
                break;
            case "with":
                id = Id_with;
                break;
            case "yield":
                id = Id_yield;
                break;
            case "abstract":
                id = Id_abstract;
                break;
            case "boolean":
                id = Id_boolean;
                break;
            case "byte":
                id = Id_byte;
                break;
            case "catch":
                id = Id_catch;
                break;
            case "char":
                id = Id_char;
                break;
            case "class":
                id = Id_class;
                break;
            case "const":
                id = Id_const;
                break;
            case "debugger":
                id = Id_debugger;
                break;
            case "double":
                id = Id_double;
                break;
            case "enum":
                id = Id_enum;
                break;
            case "extends":
                id = Id_extends;
                break;
            case "final":
                id = Id_final;
                break;
            case "finally":
                id = Id_finally;
                break;
            case "float":
                id = Id_float;
                break;
            case "goto":
                id = Id_goto;
                break;
            case "implements":
                id = Id_implements;
                break;
            case "import":
                id = Id_import;
                break;
            case "instanceof":
                id = Id_instanceof;
                break;
            case "int":
                id = Id_int;
                break;
            case "interface":
                id = Id_interface;
                break;
            case "long":
                id = Id_long;
                break;
            case "native":
                id = Id_native;
                break;
            case "package":
                id = Id_package;
                break;
            case "private":
                id = Id_private;
                break;
            case "protected":
                id = Id_protected;
                break;
            case "public":
                id = Id_public;
                break;
            case "short":
                id = Id_short;
                break;
            case "static":
                id = Id_static;
                break;
            case "super":
                id = Id_super;
                break;
            case "synchronized":
                id = Id_synchronized;
                break;
            case "throw":
                id = Id_throw;
                break;
            case "throws":
                id = Id_throws;
                break;
            case "transient":
                id = Id_transient;
                break;
            case "try":
                id = Id_try;
                break;
            case "volatile":
                id = Id_volatile;
                break;
        }
        if (id == 0) {
            return Token.EOF;
        }
        return id & 0xff;
    }

    /** ECMAScript 6. */
    @SuppressWarnings({"checkstyle:LocalFinalVariableName",
            "checkstyle:MultipleVariableDeclarations", "MethodLength"})
    private static int stringToKeywordForES(final String name, final boolean isStrict) {
        // The following assumes that Token.EOF == 0
        final int
                // 11.6.2.1 Keywords (ECMAScript2015)
                Id_break = Token.BREAK,
                Id_case = Token.CASE,
                Id_catch = Token.CATCH,
                Id_class = Token.RESERVED,
                Id_const = Token.CONST,
                Id_continue = Token.CONTINUE,
                Id_debugger = Token.DEBUGGER,
                Id_default = Token.DEFAULT,
                Id_delete = Token.DELPROP,
                Id_do = Token.DO,
                Id_else = Token.ELSE,
                Id_export = Token.RESERVED,
                Id_extends = Token.RESERVED,
                Id_finally = Token.FINALLY,
                Id_for = Token.FOR,
                Id_function = Token.FUNCTION,
                Id_if = Token.IF,
                Id_import = Token.RESERVED,
                Id_in = Token.IN,
                Id_instanceof = Token.INSTANCEOF,
                Id_new = Token.NEW,
                Id_return = Token.RETURN,
                Id_super = Token.RESERVED,
                Id_switch = Token.SWITCH,
                Id_this = Token.THIS,
                Id_throw = Token.THROW,
                Id_try = Token.TRY,
                Id_typeof = Token.TYPEOF,
                Id_var = Token.VAR,
                Id_void = Token.VOID,
                Id_while = Token.WHILE,
                Id_with = Token.WITH,
                Id_yield = Token.YIELD,

                // 11.6.2.2 Future Reserved Words
                Id_await = Token.RESERVED,
                Id_enum = Token.RESERVED,

                // 11.6.2.2 NOTE Strict Future Reserved Words
                Id_implements = Token.RESERVED,
                Id_interface = Token.RESERVED,
                Id_package = Token.RESERVED,
                Id_private = Token.RESERVED,
                Id_protected = Token.RESERVED,
                Id_public = Token.RESERVED,

                // 11.8 Literals
                Id_false = Token.FALSE,
                Id_null = Token.NULL,
                Id_true = Token.TRUE,

                // Non ReservedWord, but Non IdentifierName in strict mode code.
                // 12.1.1 Static Semantics: Early Errors
                Id_let = Token.LET, // TODO : Valid IdentifierName in non-strict mode.
                Id_static = Token.RESERVED;

        int id = 0;
        switch (name) {
            case "break":
                id = Id_break;
                break;
            case "case":
                id = Id_case;
                break;
            case "catch":
                id = Id_catch;
                break;
            case "class":
                id = Id_class;
                break;
            case "const":
                id = Id_const;
                break;
            case "continue":
                id = Id_continue;
                break;
            case "debugger":
                id = Id_debugger;
                break;
            case "default":
                id = Id_default;
                break;
            case "delete":
                id = Id_delete;
                break;
            case "do":
                id = Id_do;
                break;
            case "else":
                id = Id_else;
                break;
            case "export":
                id = Id_export;
                break;
            case "extends":
                id = Id_extends;
                break;
            case "finally":
                id = Id_finally;
                break;
            case "for":
                id = Id_for;
                break;
            case "function":
                id = Id_function;
                break;
            case "if":
                id = Id_if;
                break;
            case "import":
                id = Id_import;
                break;
            case "in":
                id = Id_in;
                break;
            case "instanceof":
                id = Id_instanceof;
                break;
            case "new":
                id = Id_new;
                break;
            case "return":
                id = Id_return;
                break;
            case "super":
                id = Id_super;
                break;
            case "switch":
                id = Id_switch;
                break;
            case "this":
                id = Id_this;
                break;
            case "throw":
                id = Id_throw;
                break;
            case "try":
                id = Id_try;
                break;
            case "typeof":
                id = Id_typeof;
                break;
            case "var":
                id = Id_var;
                break;
            case "void":
                id = Id_void;
                break;
            case "while":
                id = Id_while;
                break;
            case "with":
                id = Id_with;
                break;
            case "yield":
                id = Id_yield;
                break;
            case "await":
                id = Id_await;
                break;
            case "enum":
                id = Id_enum;
                break;
            case "implements":
                if (isStrict) {
                    id = Id_implements;
                }
                break;
            case "interface":
                if (isStrict) {
                    id = Id_interface;
                }
                break;
            case "package":
                if (isStrict) {
                    id = Id_package;
                }
                break;
            case "private":
                if (isStrict) {
                    id = Id_private;
                }
                break;
            case "protected":
                if (isStrict) {
                    id = Id_protected;
                }
                break;
            case "public":
                if (isStrict) {
                    id = Id_public;
                }
                break;
            case "false":
                id = Id_false;
                break;
            case "null":
                id = Id_null;
                break;
            case "true":
                id = Id_true;
                break;
            case "let":
                id = Id_let;
                break;
            case "static":
                if (isStrict) {
                    id = Id_static;
                }
                break;
        }
        if (id == 0) {
            return Token.EOF;
        }
        return id & 0xff;
    }

    @SuppressWarnings("checkstyle:MethodLength")
    final int getToken() throws IOException, ParsingException {
        int c;

        for (;;) {
            // Eat whitespace, possibly sensitive to newlines.
            for (;;) {
                c = getChar();
                if (c == EOF_CHAR) {
                    tokenBeg = cursor - 1;
                    tokenEnd = cursor;
                    return Token.EOF;
                } else if (c == '\n') {
                    dirtyLine = false;
                    tokenBeg = cursor - 1;
                    tokenEnd = cursor;
                    return Token.EOL;
                } else if (!isJSSpace(c)) {
                    if (c != '-') {
                        dirtyLine = true;
                    }
                    break;
                }
            }

            // Assume the token will be 1 char - fixed up below.
            tokenBeg = cursor - 1;
            tokenEnd = cursor;

            // identifier/keyword/instanceof?
            // watch out for starting with a <backslash>
            final boolean identifierStart;
            boolean isUnicodeEscapeStart = false;
            if (c == '\\') {
                c = getChar();
                if (c == 'u') {
                    identifierStart = true;
                    isUnicodeEscapeStart = true;
                    stringBufferTop = 0;
                } else {
                    identifierStart = false;
                    ungetChar(c);
                    c = '\\';
                }
            } else {
                identifierStart = Character.isJavaIdentifierStart((char) c);
                if (identifierStart) {
                    stringBufferTop = 0;
                    addToString(c);
                }
            }

            if (identifierStart) {
                boolean containsEscape = isUnicodeEscapeStart;
                for (;;) {
                    if (isUnicodeEscapeStart) {
                        // strictly speaking we should probably push-back
                        // all the bad characters if the <backslash>uXXXX
                        // sequence is malformed. But since there isn't a
                        // correct context(is there?) for a bad Unicode
                        // escape sequence in an identifier, we can report
                        // an error here.
                        int escapeVal = 0;
                        for (int i = 0; i != 4; ++i) {
                            c = getChar();
                            escapeVal = Kit.xDigitToInt(c, escapeVal);
                            // Next check takes care about c < 0 and bad escape
                            if (escapeVal < 0) {
                                break;
                            }
                        }
                        if (escapeVal < 0) {
                            throw new ParsingException("invalid escape");
                        }
                        addToString(escapeVal);
                        isUnicodeEscapeStart = false;
                    } else {
                        c = getChar();
                        if (c == '\\') {
                            c = getChar();
                            if (c == 'u') {
                                isUnicodeEscapeStart = true;
                                containsEscape = true;
                            } else {
                                throw new ParsingException("illegal character: " + c);
                            }
                        } else {
                            if (c == EOF_CHAR
                                    || c == BYTE_ORDER_MARK
                                    || !Character.isJavaIdentifierPart((char) c)) {
                                break;
                            }
                            addToString(c);
                        }
                    }
                }
                ungetChar(c);

                String str = getStringFromBuffer();
                if (!containsEscape) {
                    // OPT we shouldn't have to make a string (object!) to
                    // check if it's a keyword.

                    // Return the corresponding token if it's a keyword
                    int result =
                            stringToKeyword(
                                    str, LANGUAGE_VERSION, STRICT_MODE);
                    if (result != Token.EOF) {
                        if ((result == Token.LET || result == Token.YIELD)
                                && LANGUAGE_VERSION < Context.VERSION_1_7) {
                            // LET and YIELD are tokens only in 1.7 and later
                            string = result == Token.LET ? "let" : "yield";
                            result = Token.NAME;
                        }
                        // Save the string in case we need to use in
                        // object literal definitions.
                        this.string = (String) allStrings.intern(str);
                        if (result != Token.RESERVED) {
                            return result;
                        } else if (LANGUAGE_VERSION >= Context.VERSION_ES6) {
                            return result;
                        } else if (!IS_RESERVED_KEYWORD_AS_IDENTIFIER) {
                            return result;
                        }
                    }
                } else if (isKeyword(
                        str,
                        LANGUAGE_VERSION,
                        STRICT_MODE)) {
                    // If a string contains unicodes, and converted to a keyword,
                    // we convert the last character back to unicode
                    str = convertLastCharToHex(str);
                }
                this.string = (String) allStrings.intern(str);
                return Token.NAME;
            }

            // is it a number?
            if (isDigit(c) || (c == '.' && isDigit(peekChar()))) {
                stringBufferTop = 0;
                int base = 10;
                final boolean es6 = LANGUAGE_VERSION >= Context.VERSION_ES6;
                boolean isOldOctal = false;

                if (c == '0') {
                    c = getChar();
                    if (c == 'x' || c == 'X') {
                        base = 16;
                        c = getChar();
                    } else if (es6 && (c == 'o' || c == 'O')) {
                        base = 8;
                        c = getChar();
                    } else if (es6 && (c == 'b' || c == 'B')) {
                        base = 2;
                        c = getChar();
                    } else if (isDigit(c)) {
                        base = 8;
                        isOldOctal = true;
                    } else {
                        addToString('0');
                    }
                }

                final int emptyDetector = stringBufferTop;
                if (base == 10 || base == 16 || (base == 8 && !isOldOctal) || base == 2) {
                    c = readDigits(base, c);
                    if (c == REPORT_NUMBER_FORMAT_ERROR) {
                        throw new ParsingException("msg.caught.nfe");
                    }
                } else {
                    while (isDigit(c)) {
                        // finally the oldOctal case
                        if (c >= '8') {
                            /*
                             * We permit 08 and 09 as decimal numbers, which
                             * makes our behavior a superset of the ECMA
                             * numeric grammar.  We might not always be so
                             * permissive, so we warn about it.
                             */
                            base = 10;

                            c = readDigits(base, c);
                            if (c == REPORT_NUMBER_FORMAT_ERROR) {
                                throw new ParsingException("msg.caught.nfe");
                            }
                            break;
                        }
                        addToString(c);
                        c = getChar();
                    }
                }
                if (stringBufferTop == emptyDetector && base != 10) {
                    throw new ParsingException("msg.caught.nfe");
                }

                boolean isInteger = true;

                if (es6 && c == 'n') {
                    c = getChar();
                } else if (base == 10 && (c == '.' || c == 'e' || c == 'E')) {
                    isInteger = false;
                    if (c == '.') {
                        isInteger = false;
                        addToString(c);
                        c = getChar();
                        c = readDigits(base, c);
                        if (c == REPORT_NUMBER_FORMAT_ERROR) {
                            throw new ParsingException("msg.caught.nfe");
                        }
                    }
                    if (c == 'e' || c == 'E') {
                        isInteger = false;
                        addToString(c);
                        c = getChar();
                        if (c == '+' || c == '-') {
                            addToString(c);
                            c = getChar();
                        }
                        if (!isDigit(c)) {
                            throw new ParsingException("missing exponent");
                        }
                        c = readDigits(base, c);
                        if (c == REPORT_NUMBER_FORMAT_ERROR) {
                            throw new ParsingException("msg.caught.nfe");
                        }
                    }
                }
                ungetChar(c);
                this.string = getStringFromBuffer();
                return Token.NUMBER;
            }

            // is it a string?
            if (c == '"' || c == '\'') {
                // We attempt to accumulate a string the fast way, by
                // building it directly out of the reader.  But if there
                // are any escaped characters in the string, we revert to
                // building it out of a StringBuffer.

                // delimiter for last string literal scanned
                final int quoteChar = c;
                stringBufferTop = 0;

                c = getCharIgnoreLineEnd(false);
                strLoop:
                while (c != quoteChar) {
                    boolean unterminated = false;
                    if (c == EOF_CHAR) {
                        unterminated = true;
                    } else if (c == '\n') {
                        switch (lineEndChar) {
                            case '\n':
                            case '\r':
                                unterminated = true;
                                break;
                            case 0x2028: // <LS>
                            case 0x2029: // <PS>
                                // Line/Paragraph separators need to be included as is
                                c = lineEndChar;
                                break;
                            default:
                                break;
                        }
                    }

                    if (unterminated) {
                        throw new ParsingException("msg.unterminated.string.lit");
                    }

                    if (c == '\\') {
                        // We've hit an escaped character
                        int escapeVal;

                        c = getChar();
                        switch (c) {
                            case 'b':
                                c = '\b';
                                break;
                            case 'f':
                                c = '\f';
                                break;
                            case 'n':
                                c = '\n';
                                break;
                            case 'r':
                                c = '\r';
                                break;
                            case 't':
                                c = '\t';
                                break;

                            // \v a late addition to the ECMA spec,
                            // it is not in Java, so use 0xb
                            case 'v':
                                c = 0xb;
                                break;

                            case 'u':
                                // Get 4 hex digits; if the u escape is not
                                // followed by 4 hex digits, use 'u' + the
                                // literal character sequence that follows.
                                final int escapeStart = stringBufferTop;
                                addToString('u');
                                escapeVal = 0;
                                for (int i = 0; i != 4; ++i) {
                                    c = getChar();
                                    escapeVal = Kit.xDigitToInt(c, escapeVal);
                                    if (escapeVal < 0) {
                                        continue strLoop;
                                    }
                                    addToString(c);
                                }
                                // prepare for replace of stored 'u' sequence
                                // by escape value
                                stringBufferTop = escapeStart;
                                c = escapeVal;
                                break;
                            case 'x':
                                // Get 2 hex digits, defaulting to 'x'+literal
                                // sequence, as above.
                                c = getChar();
                                escapeVal = Kit.xDigitToInt(c, 0);
                                if (escapeVal < 0) {
                                    addToString('x');
                                    continue strLoop;
                                }
                                final int c1 = c;
                                c = getChar();
                                escapeVal = Kit.xDigitToInt(c, escapeVal);
                                if (escapeVal < 0) {
                                    addToString('x');
                                    addToString(c1);
                                    continue strLoop;
                                }
                                // got 2 hex digits
                                c = escapeVal;
                                break;

                            case '\n':
                                // Remove line terminator after escape to follow
                                // SpiderMonkey and C/C++
                                c = getChar();
                                continue strLoop;

                            default:
                                if ('0' <= c && c < '8') {
                                    int val = c - '0';
                                    c = getChar();
                                    if ('0' <= c && c < '8') {
                                        val = 8 * val + c - '0';
                                        c = getChar();
                                        if ('0' <= c && c < '8' && val <= 037) {
                                            // c is 3rd char of octal sequence only
                                            // if the resulting val <= 0377
                                            val = 8 * val + c - '0';
                                            c = getChar();
                                        }
                                    }
                                    ungetChar(c);
                                    c = val;
                                }
                        }
                    }
                    addToString(c);
                    c = getChar(false);
                }

                final String str = getStringFromBuffer();
                this.string = (String) allStrings.intern(str);
                return Token.STRING;
            }

            switch (c) {
                case ';':
                    return Token.SEMI;
                case '[':
                    return Token.LB;
                case ']':
                    return Token.RB;
                case '{':
                    return Token.LC;
                case '}':
                    return Token.RC;
                case '(':
                    return Token.LP;
                case ')':
                    return Token.RP;
                case ',':
                    return Token.COMMA;
                case '?':
                    return Token.HOOK;
                case ':':
                    return Token.COLON;
                case '.':
                    return Token.DOT;

                case '|':
                    if (matchChar('|')) {
                        return Token.OR;
                    } else if (matchChar('=')) {
                        return Token.ASSIGN_BITOR;
                    } else {
                        return Token.BITOR;
                    }

                case '^':
                    if (matchChar('=')) {
                        return Token.ASSIGN_BITXOR;
                    }
                    return Token.BITXOR;

                case '&':
                    if (matchChar('&')) {
                        return Token.AND;
                    } else if (matchChar('=')) {
                        return Token.ASSIGN_BITAND;
                    } else {
                        return Token.BITAND;
                    }

                case '=':
                    if (matchChar('=')) {
                        if (matchChar('=')) {
                            return Token.SHEQ;
                        }
                        return Token.EQ;
                    } else if (matchChar('>')) {
                        return Token.ARROW;
                    } else {
                        return Token.ASSIGN;
                    }

                case '!':
                    if (matchChar('=')) {
                        if (matchChar('=')) {
                            return Token.SHNE;
                        }
                        return Token.NE;
                    }
                    return Token.NOT;

                case '<':
                    /* NB:treat HTML begin-comment as comment-till-eol */
                    if (matchChar('!')) {
                        if (matchChar('-')) {
                            if (matchChar('-')) {
                                tokenBeg = cursor - 4;
                                skipLine();
                                return Token.COMMENT;
                            }
                            ungetCharIgnoreLineEnd('-');
                        }
                        ungetCharIgnoreLineEnd('!');
                    }
                    if (matchChar('<')) {
                        if (matchChar('=')) {
                            return Token.ASSIGN_LSH;
                        }
                        return Token.LSH;
                    }
                    if (matchChar('=')) {
                        return Token.LE;
                    }
                    return Token.LT;

                case '>':
                    if (matchChar('>')) {
                        if (matchChar('>')) {
                            if (matchChar('=')) {
                                return Token.ASSIGN_URSH;
                            }
                            return Token.URSH;
                        }
                        if (matchChar('=')) {
                            return Token.ASSIGN_RSH;
                        }
                        return Token.RSH;
                    }
                    if (matchChar('=')) {
                        return Token.GE;
                    }
                    return Token.GT;

                case '*':
                    if (LANGUAGE_VERSION >= Context.VERSION_ES6) {
                        if (matchChar('*')) {
                            if (matchChar('=')) {
                                return Token.ASSIGN_EXP;
                            }
                            return Token.EXP;
                        }
                    }
                    if (matchChar('=')) {
                        return Token.ASSIGN_MUL;
                    }
                    return Token.MUL;

                case '/':
                    // is it a // comment?
                    if (matchChar('/')) {
                        tokenBeg = cursor - 2;
                        skipLine();
                        return Token.COMMENT;
                    }
                    // is it a /* or /** comment?
                    if (matchChar('*')) {
                        boolean lookForSlash = false;
                        tokenBeg = cursor - 2;
                        if (matchChar('*')) {
                            lookForSlash = true;
                        }
                        for (;;) {
                            c = getChar();
                            if (c == EOF_CHAR) {
                                tokenEnd = cursor - 1;
                                throw new ParsingException("msg.unterminated.comment");
                            } else if (c == '*') {
                                lookForSlash = true;
                            } else if (c == '/') {
                                if (lookForSlash) {
                                    tokenEnd = cursor;
                                    return Token.COMMENT;
                                }
                            } else {
                                lookForSlash = false;
                                tokenEnd = cursor;
                            }
                        }
                    }

                    if (matchChar('=')) {
                        return Token.ASSIGN_DIV;
                    }
                    return Token.DIV;

                case '%':
                    if (matchChar('=')) {
                        return Token.ASSIGN_MOD;
                    }
                    return Token.MOD;

                case '~':
                    return Token.BITNOT;

                case '+':
                    if (matchChar('=')) {
                        return Token.ASSIGN_ADD;
                    } else if (matchChar('+')) {
                        return Token.INC;
                    } else {
                        return Token.ADD;
                    }

                case '-':
                    if (matchChar('=')) {
                        c = Token.ASSIGN_SUB;
                    } else if (matchChar('-')) {
                        if (!dirtyLine) {
                            // treat HTML end-comment after possible whitespace
                            // after line start as comment-until-eol
                            if (matchChar('>')) {
                                skipLine();
                                return Token.COMMENT;
                            }
                        }
                        c = Token.DEC;
                    } else {
                        c = Token.SUB;
                    }
                    dirtyLine = true;
                    return c;

                case '`':
                    return Token.TEMPLATE_LITERAL;

                default:
                    throw new ParsingException("illegal character" + c);
            }
        }
    }

    /*
     * Helper to read the next digits according to the base
     * and ignore the number separator if there is one.
     */
    private int readDigits(final int base, final int firstC) throws IOException {
        if (isDigit(base, firstC)) {
            addToString(firstC);

            int c = getChar();
            if (c == EOF_CHAR) {
                return EOF_CHAR;
            }

            while (true) {
                if (c == NUMERIC_SEPARATOR) {
                    // we do no peek here, we are optimistic for performance
                    // reasons and because peekChar() only does an getChar/ungetChar.
                    c = getChar();
                    // if the line ends after the separator we have
                    // to report this as an error
                    if (c == '\n' || c == EOF_CHAR) {
                        return REPORT_NUMBER_FORMAT_ERROR;
                    }

                    if (!isDigit(base, c)) {
                        // bad luck we have to roll back
                        ungetChar(c);
                        return NUMERIC_SEPARATOR;
                    }
                    addToString(NUMERIC_SEPARATOR);
                } else if (isDigit(base, c)) {
                    addToString(c);
                    c = getChar();
                    if (c == EOF_CHAR) {
                        return EOF_CHAR;
                    }
                } else {
                    return c;
                }
            }
        }
        return firstC;
    }

    private static boolean isAlpha(final int c) {
        // Use 'Z' < 'a'
        if (c <= 'Z') {
            return 'A' <= c;
        }
        return 'a' <= c && c <= 'z';
    }

    private static boolean isDigit(final int base, final int c) {
        return (base == 10 && isDigit(c))
                || (base == 16 && isHexDigit(c))
                || (base == 8 && isOctalDigit(c))
                || (base == 2 && isDualDigit(c));
    }

    private static boolean isDualDigit(final int c) {
        return '0' == c || c == '1';
    }

    private static boolean isOctalDigit(final int c) {
        return '0' <= c && c <= '7';
    }

    private static boolean isDigit(final int c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isHexDigit(final int c) {
        return ('0' <= c && c <= '9') || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F');
    }

    /* As defined in ECMA.  jsscan.c uses C isspace() (which allows
     * \v, I think.)  note that code in getChar() implicitly accepts
     * '\r' == \u000D as well.
     */
    private static boolean isJSSpace(final int c) {
        if (c <= 127) {
            return c == 0x20 || c == 0x9 || c == 0xC || c == 0xB;
        }
        return c == 0xA0
                || c == BYTE_ORDER_MARK
                || Character.getType((char) c) == Character.SPACE_SEPARATOR;
    }

    private static boolean isJSFormatChar(final int c) {
        return c > 127 && Character.getType((char) c) == Character.FORMAT;
    }

    /** Parser calls the method when it gets / or /= in literal context. */
    void readRegExp(final int startToken) throws IOException, ParsingException {
        final int start = tokenBeg;
        stringBufferTop = 0;
        if (startToken == Token.ASSIGN_DIV) {
            // Miss-scanned /=
            addToString('=');
        } else {
            if (startToken != Token.DIV) {
                Kit.codeBug();
            }
            if (peekChar() == '*') {
                tokenEnd = cursor - 1;
                this.string = new String(stringBuffer, 0, stringBufferTop);
                throw new ParsingException("msg.unterminated.re.lit");
            }
        }

        boolean inCharSet = false; // true if inside a '['..']' pair
        int c;
        while ((c = getChar()) != '/' || inCharSet) {
            if (c == '\n' || c == EOF_CHAR) {
                throw new ParsingException("msg.unterminated.re.lit");
            }
            if (c == '\\') {
                addToString(c);
                c = getChar();
                if (c == '\n' || c == EOF_CHAR) {
                    throw new ParsingException("msg.unterminated.re.lit");
                }
            } else if (c == '[') {
                inCharSet = true;
            } else if (c == ']') {
                inCharSet = false;
            }
            addToString(c);
        }
        final int reEnd = stringBufferTop;

        while (true) {
            if (matchChar('g')) {
                addToString('g');
            } else if (matchChar('i')) {
                addToString('i');
            } else if (matchChar('m')) {
                addToString('m');
            } else if (matchChar('y')) {
                // FireFox 3
                addToString('y');
            } else {
                break;
            }
        }
        tokenEnd = start + stringBufferTop + 2; // include slashes

        if (isAlpha(peekChar())) {
            throw new ParsingException("msg.invalid.re.flag");
        }

        this.string = new String(stringBuffer, 0, reEnd);
    }

    private String getStringFromBuffer() {
        tokenEnd = cursor;
        return new String(stringBuffer, 0, stringBufferTop);
    }

    private void addToString(final int c) {
        final int n = stringBufferTop;
        if (n == stringBuffer.length) {
            final char[] tmp = new char[stringBuffer.length * 2];
            System.arraycopy(stringBuffer, 0, tmp, 0, n);
            stringBuffer = tmp;
        }
        stringBuffer[n] = (char) c;
        stringBufferTop = n + 1;
    }

    private void ungetChar(final int c) {
        // can not unread past across line boundary
        if (ungetCursor != 0 && ungetBuffer[ungetCursor - 1] == '\n') {
            Kit.codeBug();
        }
        ungetBuffer[ungetCursor++] = c;
        cursor--;
    }

    private boolean matchChar(final int test) throws IOException {
        final int c = getCharIgnoreLineEnd();
        if (c == test) {
            tokenEnd = cursor;
            return true;
        }
        ungetCharIgnoreLineEnd(c);
        return false;
    }

    private int peekChar() throws IOException {
        final int c = getChar();
        ungetChar(c);
        return c;
    }

    private int getChar() throws IOException {
        return getChar(true, false);
    }

    private int getChar(final boolean skipFormattingChars) throws IOException {
        return getChar(skipFormattingChars, false);
    }

    private int getChar(final boolean skipFormattingChars, final boolean ignoreLineEnd)
            throws IOException {
        if (ungetCursor != 0) {
            cursor++;
            return ungetBuffer[--ungetCursor];
        }

        for (;;) {
            int c;
            if (sourceString != null) {
                if (sourceCursor == sourceEnd) {
                    hitEOF = true;
                    return EOF_CHAR;
                }
                cursor++;
                c = sourceString.charAt(sourceCursor++);
            } else {
                if (sourceCursor == sourceEnd) {
                    if (!fillSourceBuffer()) {
                        hitEOF = true;
                        return EOF_CHAR;
                    }
                }
                cursor++;
                c = sourceBuffer[sourceCursor++];
            }

            if (!ignoreLineEnd && lineEndChar >= 0) {
                if (lineEndChar == '\r' && c == '\n') {
                    lineEndChar = '\n';
                    continue;
                }
                lineEndChar = -1;
                lineStart = sourceCursor - 1;
                lineno++;
            }

            if (c <= 127) {
                if (c == '\n' || c == '\r') {
                    lineEndChar = c;
                    c = '\n';
                }
            } else {
                if (c == BYTE_ORDER_MARK) {
                    return c; // BOM is considered whitespace
                }
                if (skipFormattingChars && isJSFormatChar(c)) {
                    continue;
                }
                if (ScriptRuntime.isJSLineTerminator(c)) {
                    lineEndChar = c;
                    c = '\n';
                }
            }
            return c;
        }
    }

    private int getCharIgnoreLineEnd() throws IOException {
        return getChar(true, true);
    }

    private int getCharIgnoreLineEnd(final boolean skipFormattingChars) throws IOException {
        return getChar(skipFormattingChars, true);
    }

    private void ungetCharIgnoreLineEnd(final int c) {
        ungetBuffer[ungetCursor++] = c;
        cursor--;
    }

    @SuppressWarnings("checkstyle:emptyblock")
    private void skipLine() throws IOException {
        // skip to end of line
        int c;
        while ((c = getChar()) != EOF_CHAR && c != '\n') { }
        ungetChar(c);
        tokenEnd = cursor;
    }

    private boolean fillSourceBuffer() throws IOException {
        if (sourceString != null) {
            Kit.codeBug();
        }
        if (sourceEnd == sourceBuffer.length) {
            if (lineStart != 0) {
                System.arraycopy(sourceBuffer, lineStart, sourceBuffer, 0, sourceEnd - lineStart);
                sourceEnd -= lineStart;
                sourceCursor -= lineStart;
                lineStart = 0;
            } else {
                final char[] tmp = new char[sourceBuffer.length * 2];
                System.arraycopy(sourceBuffer, 0, tmp, 0, sourceEnd);
                sourceBuffer = tmp;
            }
        }
        final int n = sourceReader.read(sourceBuffer, sourceEnd, sourceBuffer.length - sourceEnd);
        if (n < 0) {
            return false;
        }
        sourceEnd += n;
        return true;
    }

    /** Return the current position of the scanner cursor. */
    public int getCursor() {
        return cursor;
    }

    /** Return the absolute source offset of the last scanned token. */
    public int getTokenBeg() {
        return tokenBeg;
    }

    /** Return the absolute source end-offset of the last scanned token. */
    public int getTokenEnd() {
        return tokenEnd;
    }

    /** Return tokenEnd - tokenBeg */
    public int getTokenLength() {
        return tokenEnd - tokenBeg;
    }

    public String getTokenRaw() {
        return sourceString.substring(tokenBeg, tokenEnd);
    }

    private static String convertLastCharToHex(final String str) {
        final int lastIndex = str.length() - 1;
        final StringBuilder buf = new StringBuilder(str.substring(0, lastIndex));
        buf.append("\\u");
        final String hexCode = Integer.toHexString(str.charAt(lastIndex));
        for (int i = 0; i < 4 - hexCode.length(); ++i) {
            buf.append('0');
        }
        buf.append(hexCode);
        return buf.toString();
    }

    public int nextToken() throws ParsingException, IOException {
        int tt = getToken();
        while (tt == Token.EOL || tt == Token.COMMENT) {
            tt = getToken();
        }
        return tt;
    }

    // stuff other than whitespace since start of line
    private boolean dirtyLine;
    private String string = "";

    private char[] stringBuffer = new char[128];
    private int stringBufferTop;
    private final ObjToIntMap allStrings = new ObjToIntMap(50);

    // Room to backtrace from to < on failed match of the last - in <!--
    private final int[] ungetBuffer = new int[3];
    private int ungetCursor;

    private boolean hitEOF = false;

    private int lineStart = 0;
    private int lineEndChar = -1;
    int lineno;

    private String sourceString;
    private Reader sourceReader;
    private char[] sourceBuffer;
    private int sourceEnd;

    // sourceCursor is an index into a small buffer that keeps a
    // sliding window of the source stream.
    int sourceCursor;

    // cursor is a monotonically increasing index into the original
    // source stream, tracking exactly how far scanning has progressed.
    // Its value is the index of the next character to be scanned.
    int cursor;

    // Record start and end positions of last scanned token.
    int tokenBeg;
    int tokenEnd;

    private static final int LANGUAGE_VERSION = 0;
    private static final boolean IS_RESERVED_KEYWORD_AS_IDENTIFIER = true;
    private static final boolean STRICT_MODE = false;
}
