#!/bin/bash

echo "Step 1: use the release apk already exists or gradle clean assembleRelease"
if [ -e "app/build/outputs/apk/app-google-release.apk" ]; then
    echo "tips: The release apk is already exists, do you want to rebuild project ?"
    read -p "please input(yes/no): "  value
    if [ "$value" = "yes" ]; then
        echo "rebuild project..."
        ./gradlew clean assembleRelease
    elif [ "$value" = "no" ]; then
        echo "Skip step 1..."
    else
        echo "invalid value, please input again."
        read -p "please input(yes/no): " aValue
        if [ "$aValue" = "yes" ]; then
            echo "rebuild project..."
            ./gradlew clean assembleRelease
        elif [ "$aValue" = "no" ]; then
            echo "Skip step 1..."
        else
            echo "invalid value, exit."
            exit 0
        fi
    fi
else
    echo "tips: The release apk is not exists, rebuild it..."
    ./gradlew clean assembleRelease
fi

echo "Step 2: build multi channel apk with build.py"
cd app/python && python build.py && cd ..
