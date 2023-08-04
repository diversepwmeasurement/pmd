/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

class Java21PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java21p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("21-preview")
                    .withResourceContext(Java21PreviewTreeDumpTest.class, "jdkversiontests/java21p/");
    private final JavaParsingHelper java21 = java21p.withDefaultVersion("21");

    Java21PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java21p;
    }

    @Test
    void templateProcessors() {
        doTest("Jep430_StringTemplates");
    }

    @Test
    void templateProcessorsBeforeJava21Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java21.parseResource("Jep430_StringTemplates.java"));
        assertTrue(thrown.getMessage().contains("String templates is a preview feature of JDK 21, you should select your language version accordingly"));
    }

    @Test
    void templateExpressionType() {
        ASTCompilationUnit unit = java21p.parse("class Foo {{ int i = 1; String s = STR.\"i = \\{i}\"; }}");
        ASTTemplateExpression templateExpression = unit.descendants(ASTTemplateExpression.class).first();
        JTypeMirror typeMirror = templateExpression.getTypeMirror();
        assertEquals("java.lang.String", ((JClassSymbol) typeMirror.getSymbol()).getCanonicalName());
    }
}
