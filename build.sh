#!/bin/bash
# ZDocs APK Build Script - installs Android SDK if needed, then builds release APK

set -e

# Set up Android SDK
export ANDROID_HOME="${HOME}/Android/Sdk"
export PATH="${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${PATH}"

echo "=== Step 1: Install Android SDK ==="
if [ ! -d "${ANDROID_HOME}" ]; then
    echo "Installing Android SDK..."
    mkdir -p "${ANDROID_HOME}/cmdline-tools"
    
    # Download command-line tools
    CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
    wget -q "${CMDLINE_TOOLS_URL}" -O /tmp/cmdline-tools.zip
    unzip -q /tmp/cmdline-tools.zip -d "${ANDROID_HOME}/cmdline-tools"
    mv "${ANDROID_HOME}/cmdline-tools/cmdline-tools" "${ANDROID_HOME}/cmdline-tools/latest" 2>/dev/null || true
    rm /tmp/cmdline-tools.zip
fi

echo "=== Step 2: Install SDK components ==="
yes | sdkmanager --licenses 2>/dev/null || true
sdkmanager --install "platforms;android-34" "build-tools;34.0.0" "platform-tools" 2>/dev/null

echo "=== Step 3: Create local.properties ==="
echo "sdk.dir=${ANDROID_HOME}" >> local.properties

echo "=== Step 4: Build Release APK ==="
export GRADLE_OPTS="-Xmx1536m -Dorg.gradle.daemon=false -Dorg.gradle.parallel=false -Dorg.gradle.caching=false"
./gradlew assembleRelease --no-daemon --stacktrace

echo "=== Step 5: Verify APK ==="
APK_PATH="app/build/outputs/apk/release/app-release.apk"
if [ -f "${APK_PATH}" ]; then
    APK_SIZE=$(du -h "${APK_PATH}" | cut -f1)
    echo "✅ APK built successfully! Size: ${APK_SIZE}"
    cp "${APK_PATH}" /home/z/my-project/download/ZDocs-v1.0.0-release.apk
    echo "✅ APK copied to /home/z/my-project/download/ZDocs-v1.0.0-release.apk"
else
    echo "❌ APK not found. Checking build outputs..."
    find app/build -name "*.apk" 2>/dev/null || true
    exit 1
fi
