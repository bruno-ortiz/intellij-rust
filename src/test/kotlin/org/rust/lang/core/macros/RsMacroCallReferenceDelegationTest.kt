/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.macros

import org.rust.CheckTestmarkHit
import org.rust.ExpandMacros
import org.rust.lang.core.resolve.RsResolveTestBase
import org.rust.lang.core.resolve.ref.RsMacroBodyReferenceDelegateImpl.Testmarks

@ExpandMacros
class RsMacroCallReferenceDelegationTest : RsResolveTestBase() {
    @CheckTestmarkHit(Testmarks.Touched::class)
    fun `test item context`() = checkByCode("""
        struct X;
             //X
        macro_rules! foo { ($($ i:item)*) => { $( $ i )* }; }
        foo! {
            type T = X;
        }          //^
    """)

    @CheckTestmarkHit(Testmarks.Touched::class)
    fun `test statement context`() = checkByCode("""
        struct X;
             //X
        macro_rules! foo { ($($ i:item)*) => { $( $ i )* }; }
        fn main () {
            foo! {
                type T = X;
            };         //^
        }
    """)

    @CheckTestmarkHit(Testmarks.Touched::class)
    fun `test expression context`() = checkByCode("""
        struct X;
             //X
        macro_rules! foo { ($($ i:tt)*) => { $( $ i )* }; }
        fn main () {
            let a = foo!(X);
        }              //^
    """)

    @CheckTestmarkHit(Testmarks.Touched::class)
    fun `test type context`() = checkByCode("""
        struct X;
             //X
        macro_rules! foo { ($($ i:tt)*) => { $( $ i )* }; }
        type T = foo!(X);
                    //^
    """)

    // TODO implement `getContext()` in all RsPat PSI elements
    @CheckTestmarkHit(Testmarks.Touched::class)
    fun `test pattern context`() = expect<IllegalStateException> {
        checkByCode("""
        const X: i32 = 0;
            //X
        macro_rules! foo { ($($ i:tt)*) => { $( $ i )* }; }
        fn main() {
            match 0 {
                foo!(X) => {}
                   //^
                _ => {}
            }
        }
    """)
    }

    @CheckTestmarkHit(Testmarks.Touched::class)
    fun `test lifetime`() = checkByCode("""
        macro_rules! foo {
            ($ i:item) => { $ i };
        }
        struct S<'a>(&'a u8);
        impl<'a> S<'a> {
            //X
            foo! {
                fn foo(&self) -> &'a u8 {}
            }                    //^
        }
    """)

    @CheckTestmarkHit(Testmarks.Touched::class)
    fun `test 2-segment path 1`() = checkByCode("""
        mod foo {
          //X
            pub struct X;
        }
        macro_rules! foo { ($($ i:item)*) => { $( $ i )* }; }
        foo! {
            type T = foo::X;
        }          //^
    """)

    @CheckTestmarkHit(Testmarks.Touched::class)
    fun `test 2-segment path 2`() = checkByCode("""
        mod foo {
            pub struct X;
        }            //X
        macro_rules! foo { ($($ i:item)*) => { $( $ i )* }; }
        foo! {
            type T = foo::X;
        }               //^
    """)
}
