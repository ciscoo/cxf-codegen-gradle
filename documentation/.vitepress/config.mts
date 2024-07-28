import { defineConfig } from 'vitepress'
import { readGradleMetadata } from './gradle.mts'

const { gradleVersion, version} = readGradleMetadata();

export default defineConfig({
    vite: {
        define: {
            __GRADLE_VERSION__: JSON.stringify(gradleVersion),
            __PLUGIN_VERSION__: JSON.stringify(version)
        }
    },
    outDir: "build/docs-dist",
    lang: "en-US",
    title: "CXF Codegen Gradle",
    description: "Gradle plugin implementation of CXF Codegen",
    lastUpdated: true,
    cleanUrls: true,
    themeConfig: {
        nav: [
            {text: 'API', link: '/api'},
            {text: 'Examples', link: '/examples'}
        ],
        sidebar: [
            {
                text: 'Introduction',
                items: [
                    {text: 'Overview', link: '/overview'},
                    {text: 'Getting Started', link: '/overview#getting-started'},
                    {text: 'Gradle Support', link: '/overview#supported-gradle-versions'},
                ]
            }
        ],

        socialLinks: [
            {icon: 'github', link: 'https://github.com/ciscoo/cxf-codegen-gradle'}
        ]
    }
})
