## Telegue
[![Telegue](http://img.youtube.com/vi/pe9sFdJ25Fc/0.jpg)](http://www.youtube.com/watch?v=pe9sFdJ25Fc)

### About

![MainScreen](http://1.bp.blogspot.com/-WQkB6ihRSGo/U1I2YjR9yNI/AAAAAAAAJao/cHhyaRniMjg/s1600/Screenshot_2014-04-19-11-56-52.png)

This is android example apps for virt2real.
For more info, go to: http://wiki.virt2real.ru/wiki/%D0%9A%D0%B0%D0%BA_%D1%81%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C_%D1%83%D0%BF%D1%80%D0%B0%D0%B2%D0%BB%D1%8F%D0%B5%D0%BC%D1%83%D1%8E_%D1%82%D0%B5%D0%BB%D0%B5%D0%B6%D0%BA%D1%83

![http://wiki.virt2real.ru/wiki/%D0%9A%D0%B0%D0%BA_%D1%81%D0%B4%D0%B5%D0%BB%D0%B0%D1%82%D1%8C_%D1%83%D0%BF%D1%80%D0%B0%D0%B2%D0%BB%D1%8F%D0%B5%D0%BC%D1%83%D1%8E_%D1%82%D0%B5%D0%BB%D0%B5%D0%B6%D0%BA%D1%83](http://wiki.virt2real.ru/images/4/40/IMG_4213.jpg)

### Fealture
1. Connect/disconnect to virt2real.
2. Send commands to motors.
3. Get status from virt2real: battery vol, wi-fi status.
4. Change setup settings: ip-address, port and timeout. 
5. Receive H.264 video, base at [gstreamer1.0](http://docs.gstreamer.com/x/WQAp)

### Usage
* Scheme:
![scheme](http://wiki.virt2real.ru/images/6/69/Telegue.png)
* Image for Virt2real 13_03_2014 or later, with telegue module: http://files.virt2real.ru/firmware/virt2real-board/1.1
* Device with Android OS. Min version: Android 4.0 «Ice Cream Sandwich». Api level >=11.
* Default settings: 192.168.1.128:1083 and 6000 ms timeout.
![http://max-imsblog.blogspot.ru/2014/04/telegue-android-app-for-virt2real.html](http://3.bp.blogspot.com/-KhhuHZzmPjw/U0rARzMnXQI/AAAAAAAAJQs/xuu_hQpsslo/s1600/Screenshot_2014-04-13-20-47-33.png)
* On board virt2real console(for example, by putty) command to start video-streaming. IP - your virt2real address:
```
gst-launch -v v4l2src always-copy=false chain-ipipe=true ! \
   video/x-raw-yuv,format='(fourcc)'NV12, width=640, height=480, framerate='(fraction)'30/1 ! \
   dmaiaccel ! \
   dmaienc_h264 ddrbuf=true encodingpreset=2 ratecontrol=4 targetbitrate=600000 ! \
   rtph264pay !  queue ! \
   udpsink port=8888 host=IP sync=false
```
* Copy your caps from log, and set it in '/jni/gst.c' app_function method.
[Modify caps](http://stackoverflow.com/questions/18511765/gstreamer-in-android-udp-stream):
without type conversion (***)

## Build

### requires
1. Eclipse Java EE IDE with Android Development tools, SDK and NDK.
2. [gstreamer-1.0-android-arm-1.2.3-release](http://gstreamer.freedesktop.org/data/pkg/android/1.2.3/)

### settings
C/C++ Build
* Build Variables:
```
| Name           | Type      | Value                                                 |
| -------------- | --------- | ----------------------------------------------------- |
| GSTREAMER_ROOT | Directory | PATH TO YOUR gstreamer-1.0-android-arm-1.2.3-release  |
| NDK            | String    | PATH TO YOUR android-ndk-r9d                          |
```
* Enviroment:
```
| Name                        | Value              | Origin      |
| --------------------------- | ------------------ | ----------- |
| GSTREAMER_NDK_BUILD_PATH    | ${NDK}             | USER:CONFIG |
| GSTREAMER_SDK_ROOT_ANDROID  | ${GSTREAMER_ROOT}  | USER:CONFIG |
```

### Project base
1. autobahn-0.5.0.jar - The WebSocket Protocol support https://github.com/tavendo/AutobahnAndroid and jackson-core-asl-1.9.7.jar and jackson-mapper-asl-1.9.7.jar
2. TrackJoystickView - to motor control. https://github.com/mksmbrtsh/TrackJoystickView
3. [gstreamer1.0](http://gstreamer.com/)

### Tamiya base chassis project
https://grabcad.com/library/tamiya-chassis-1
