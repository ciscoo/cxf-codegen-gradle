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
package io.mateo.cxf.codegen.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OptionTests {

    private Option option;

    @BeforeEach
    void beforeEach() {
        this.option = ProjectBuilder.builder().build().getObjects().newInstance(Option.class, "test");
    }

    @Test
    void getName() {
        assertThat(this.option.getName()).isEqualTo("test");
    }

    @Test
    void getWsdl() {
        assertThat(this.option.getWsdl().isPresent()).isFalse();
    }

    @Test
    void getOutputDirectory() {
        assertThat(this.option.getOutputDirectory().isPresent()).isFalse();
    }
}
