/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.testing.junit5

import org.junit.jupiter.api.Test
import org.openrewrite.Issue
import org.openrewrite.Recipe
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest

@Issue("https://github.com/openrewrite/rewrite-testing-frameworks/issues/170")
class UseWiremockExtensionTest : JavaRecipeTest {
    override val parser: JavaParser
        get() = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("junit", "wiremock-jre8")
            .build()

    override val recipe: Recipe
        get() = UseWiremockExtension()

    @Test
    fun optionsArg() = assertChanged(
        before = """
            import com.github.tomakehurst.wiremock.junit.WireMockRule;
            import org.junit.Rule;
            
            import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
            
            class Test {
                @Rule
                public WireMockRule wm = new WireMockRule(options().dynamicHttpsPort());
            }
        """,
        after = """
            import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
            import org.junit.jupiter.api.extension.RegisterExtension;
            
            import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
            
            class Test {
                @RegisterExtension
                public WireMockExtension wm = WireMockExtension.newInstance().options(options().dynamicHttpsPort()).build();
            }
        """
    )

    @Test
    fun failOnUnmatchedRequests() = assertChanged(
        before = """
            import com.github.tomakehurst.wiremock.junit.WireMockRule;
            import org.junit.Rule;
            
            import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
            
            class Test {
                @Rule
                public WireMockRule wm = new WireMockRule(options().dynamicHttpsPort(), false);
            }
        """,
        after = """
            import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
            import org.junit.jupiter.api.extension.RegisterExtension;
            
            import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
            
            class Test {
                @RegisterExtension
                public WireMockExtension wm = WireMockExtension.newInstance().options(options().dynamicHttpsPort()).failOnUnmatchedRequests(false).build();
            }
        """
    )

    @Test
    fun port() = assertChanged(
        before = """
            import com.github.tomakehurst.wiremock.junit.WireMockRule;
            import org.junit.Rule;
            
            class Test {
                @Rule
                public WireMockRule wm = new WireMockRule(7001);
            }
        """,
        after = """
            import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
            import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
            import org.junit.jupiter.api.extension.RegisterExtension;
            
            class Test {
                @RegisterExtension
                public WireMockExtension wm = WireMockExtension.newInstance().options(WireMockConfiguration.options().port(7001)).build();
            }
        """
    )

    @Test
    fun portAndHttpsPort() = assertChanged(
        before = """
            import com.github.tomakehurst.wiremock.junit.WireMockRule;
            import org.junit.Rule;
            
            class Test {
                @Rule
                public WireMockRule wm = new WireMockRule(7001, 7002);
            }
        """,
        after = """
            import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
            import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
            import org.junit.jupiter.api.extension.RegisterExtension;
            
            class Test {
                @RegisterExtension
                public WireMockExtension wm = WireMockExtension.newInstance().options(WireMockConfiguration.options().port(7001).httpsPort(7002)).build();
            }
        """
    )
}