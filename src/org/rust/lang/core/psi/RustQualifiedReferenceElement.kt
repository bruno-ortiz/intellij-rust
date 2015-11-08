package org.rust.lang.core.psi

import com.intellij.psi.PsiElement
import org.rust.lang.core.resolve.ref.RustReference

public interface RustQualifiedReferenceElement : RustNamedElement {

    val isFullyQualified: Boolean

    fun getSeparator(): PsiElement?

    fun getNameElement(): PsiElement?

    fun getQualifier(): RustQualifiedReferenceElement?

    override fun getReference(): RustReference?
}
