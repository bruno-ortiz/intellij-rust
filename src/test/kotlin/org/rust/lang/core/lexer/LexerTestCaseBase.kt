/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.lexer

import com.intellij.lexer.Lexer
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.testFramework.LexerTestCase
import com.intellij.testFramework.UsefulTestCase
import org.jetbrains.annotations.NonNls
import org.junit.internal.runners.JUnit38ClassRunner
import org.junit.runner.RunWith
import org.rust.TestCase
import org.rust.pathToGoldTestFile
import org.rust.pathToSourceTestFile
import java.io.IOException

@RunWith(JUnit38ClassRunner::class) // TODO: drop the annotation when issue with Gradle test scanning go away
abstract class LexerTestCaseBase : LexerTestCase(), TestCase {
    override fun getDirPath(): String = throw UnsupportedOperationException()

    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        val camelCase = super.getTestName(lowercaseFirstLetter)
        return TestCase.camelOrWordsToSnake(camelCase)
    }

    // NOTE(matkad): this is basically a copy-paste of doFileTest.
    // The only difference is that encoding is set to utf-8
    protected fun doTest(lexer: Lexer = createLexer()) {
        val filePath = pathToSourceTestFile()
        var text = ""
        try {
            val fileText = FileUtil.loadFile(filePath.toFile(), CharsetToolkit.UTF8)
            text = StringUtil.convertLineSeparators(if (shouldTrim()) fileText.trim() else fileText)
        } catch (e: IOException) {
            fail("can't load file " + filePath + ": " + e.message)
        }
        doTest(text, null, lexer)
    }

    override fun doTest(@NonNls text: String, expected: String?, lexer: Lexer) {
        val result = printTokens(text, 0, lexer)
        if (expected != null) {
            UsefulTestCase.assertSameLines(expected, result)
        } else {
            UsefulTestCase.assertSameLinesWithFile(pathToGoldTestFile().toFile().canonicalPath, result)
        }
    }
}
