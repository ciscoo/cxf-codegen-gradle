import { defineConfig } from "vitepress";
import readGradleMetadata from "./gradle";

const gradleMetadata = readGradleMetadata();

export default defineConfig({
  vite: {
    server: {
      open: true,
    },
    define: {
      __VERSION__: gradleMetadata.version,
      __GRADLE_BUILD_VERSION__: gradleMetadata.gradleVersion,
      __CXF_VERSION__: gradleMetadata.cxfVersion,
    },
  },
  srcExclude: ["build/**"],
  outDir: "build/dist",
  lang: "en-US",
  title: "CXF Codegen Gradle",
  description: "Gradle plugin implementation of CXF Codegen",
  lastUpdated: true,
  cleanUrls: false,
  base: "/docs/current/user-guide",
  themeConfig: {
    sidebar: [
      {
        text: "Introduction",
        items: [
          { text: "Overview", link: "/overview" },
          { text: "Getting Started", link: "/getting-started" },
        ],
      },
      {
        text: "Plugin Configuration",
        items: [
          {
            text: "Dependency Management",
            link: "/configuration/dependency-management",
          },
        ],
      },
      {
        text: "Java Generation",
        items: [
          { text: "Overview", link: "/java-generation/overview" },
          { text: "Worker API", link: "/java-generation/worker-api" },
          { text: "Task API", link: "/java-generation/task-api" },
        ],
      },
      {
        text: "JavaScript Generation",
        items: [
          { text: "Overview", link: "/javascript-generation/overview" },
          { text: "Worker API", link: "/javascript-generation/worker-api" },
          { text: "Task API", link: "/javascript-generation/task-api" },
        ],
      },
      {
        text: "Examples",
        items: [
          { text: "Overview", link: "/examples/overview" },
          { text: "JAX-WS Binding File", link: "/examples/jax-ws-binding" },
          { text: "Specify Data Binding", link: "/examples/data-binding" },
          {
            text: "Specify Service to Generate Artifacts For",
            link: "/examples/service",
          },
          {
            text: "Loading A WSDL From A Maven Repository",
            link: "/examples/remote-wsdl",
          },
          { text: "Using XJC Extensions", link: "/examples/xjc-extensions" },
        ],
      },
    ],
    socialLinks: [
      { icon: "github", link: "https://github.com/ciscoo/cxf-codegen-gradle" },
    ],
  },
});
