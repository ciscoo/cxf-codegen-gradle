/*
 * Copyright 2020-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mateo.cxf.codegen.workers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Wsdl2JsTests {

    private Wsdl2Js wsdl2Js;

    @BeforeEach
    void setUp() {
        this.wsdl2Js = ProjectBuilder.builder()
                .build()
                .getTasks()
                .register("test", Wsdl2Js.class)
                .get();
    }

    @Test
    void emptyActionsByDefault() {
        assertThat(this.wsdl2Js.getForkOptionsActions()).isEmpty();
    }

    @Test
    void additiveForkOptions() {
        this.wsdl2Js.forkOptions(options -> options.setEnvironment(Map.of()));
        this.wsdl2Js.forkOptions(options -> options.setEnvironment(Map.of()));

        var forkOptionsActions = this.wsdl2Js.getForkOptionsActions();
        assertThat(forkOptionsActions).hasSize(2);
    }
}
