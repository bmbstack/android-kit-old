#!/bin/bash
   
#Modify this with your IP range
MY_IP_RANGE="172\.1\.7" #ezhensuo.inc

#You usually wouldn't have to modify this
PORT_BASE=5555

#Find USB devices only (no emulators, genymotion or connected devices
declare -a deviceArray=(`adb devices -l | grep -v emulator | grep -v vbox | grep -v "${MY_IP_RANGE}" | grep " device " | awk '{print $1}'`)  

echo "Found ${#deviceArray[@]} usb device(s)"
echo

for index in ${!deviceArray[*]}
	do
		echo "Finding IP address for device ${deviceArray[index]}"
		IP_ADDRESS=$(adb -s ${deviceArray[index]} shell ifconfig wlan0 | awk '{print $3}')

		echo "IP address found : $IP_ADDRESS "

		echo "Connecting..."
		adb -s ${deviceArray[index]} tcpip $(($PORT_BASE + $index))
		adb -s ${deviceArray[index]} connect "$IP_ADDRESS:$(($PORT_BASE + $index))"

		echo
		echo
	done

adb devices 

declare -a allDeviceArray=(`adb devices |grep ":${PORT_BASE}"|awk '{print $1}'`)  
for deviceIndex in ${!allDeviceArray[*]}
	do
		echo "================Install release apk to device ${allDeviceArray[deviceIndex]}=================="
		echo "uninstall apk..."
		adb -s ${allDeviceArray[deviceIndex]} uninstall com.androidkit.sample
		echo "uploading apk..."
		adb -s ${allDeviceArray[deviceIndex]} install app/python/outputs_apk/app-google-release.apk
		echo "startup apk..."
		adb -s ${allDeviceArray[deviceIndex]} shell am start -n com.androidkit.sample/com.androidkit.sample.MainActivity
	done

#exit