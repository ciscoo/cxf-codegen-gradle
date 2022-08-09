#!/usr/bin/env bash

readonly checksum_dir='documentation/build/checksum'
readonly current_checksum="${checksum_dir}/current-checksum.txt"
readonly published_checksum="${checksum_dir}/published-checksum.txt"
readonly github_pages_url='https://raw.githubusercontent.com/ciscoo/cxf-codegen-gradle/gh-pages/docs/snapshot/published-checksum.txt'

echo "Creating checksum directory ${checksum_dir}..."
mkdir -p "${checksum_dir}"
md5sum documentation/build.gradle.kts > "${current_checksum}"
md5sum $(find documentation/src -type f) >> "${current_checksum}"
md5sum $(find cxf-codegen-gradle -wholename '**/src/main/*.java') >> "${current_checksum}"

sort --output "${current_checksum}" "${current_checksum}"
echo
md5sum "${current_checksum}"

curl --silent --output "${published_checksum}" "${github_pages_url}"
md5sum "${published_checksum}"

if cmp --silent ${current_checksum} ${published_checksum}; then
  echo
  echo "Snapshot documentation already published with same source checksum."
  echo
else
  echo
  echo "Creating and publishing snapshot documentation..."
  echo
  cp -f "${current_checksum}" "${published_checksum}"
  ./gradlew gitPublishPush -Dorg.gradle.internal.launcher.welcomeMessageEnabled=false
fi
