# coding=utf-8
import zipfile
import shutil
import os
import sys

#源apk
source_release_apk = 'app-google-release.apk'
#app名称
app_name = 'app'

# 空文件 便于写入此空文件到apk包中作为channel文件
src_empty_file = 'channel/czt.txt'
# 创建一个空文件（不存在则创建）
f = open(src_empty_file, 'w') 
f.close()

# python3 : os.listdir()即可，这里使用兼容Python2的os.listdir('.')
for file in os.listdir('.'):
    if os.path.isfile(file):
        extension = os.path.splitext(file)[1][1:]
        if extension in 'apk':
            os.remove(file)
# 创建生成目录,与文件名相关
output_dir = 'outputs_apk/'
if os.path.exists(output_dir):
    shutil.rmtree(output_dir)

if not os.path.exists("../build/outputs/apk") or not os.path.isfile("../build/outputs/apk/"+source_release_apk):
    print 'Please build the release apk at first. \n \tTips: gradle clean assembleRelease'
    sys.exit(1)

shutil.copyfile("../build/outputs/apk/"+source_release_apk, source_release_apk)

# 获取当前目录中所有的apk源包
src_apks = []
# python3 : os.listdir()即可，这里使用兼容Python2的os.listdir('.')
for file in os.listdir('.'):
    if os.path.isfile(file):
        extension = os.path.splitext(file)[1][1:]
        if extension in 'apk':
            src_apks.append(file)
# 获取渠道列表
channel_file = 'channel/channel.txt'
f = open(channel_file)
lines = f.readlines()
f.close()

line_count = 0
for channel_name in lines:
    line_count += 1
    print "channel: "+channel_name,
print ""
print "channel list size: "+str(line_count)
print "---------build multi channel task-----------"
for src_apk in src_apks:
    # file name (with extension)
    src_apk_file_name = os.path.basename(src_apk)
    # 分割文件名与后缀
    temp_list = os.path.splitext(src_apk_file_name)
    # name without extension
    src_apk_name = (temp_list[0].split('-'))[0]
    # 后缀名，包含.   例如: ".apk "
    src_apk_extension = temp_list[1]
    
    # 目录不存在则创建
    if not os.path.exists(output_dir):
        os.mkdir(output_dir)
        
    # 遍历渠道号并创建对应渠道号的apk文件
    for line in lines:
        # 获取当前渠道号，因为从渠道文件中获得带有\n,所有strip一下
        target_channel = line.strip()
        # 拼接对应渠道号的apk
        target_apk = output_dir + src_apk_name + "-" + target_channel + "-release"+src_apk_extension  
        # 拷贝建立新apk
        shutil.copy(src_apk,  target_apk)
        # zip获取新建立的apk文件
        zipped = zipfile.ZipFile(target_apk, 'a', zipfile.ZIP_DEFLATED)
        # 初始化渠道信息
        empty_channel_file = "META-INF/cztchannel_{channel}".format(channel = target_channel)
        # 写入渠道信息
        zipped.write(src_empty_file, empty_channel_file)
        print "build successful: "+app_name+"/python/"+target_apk
        # 关闭zip流
        zipped.close()
    print "work done."
    print "you can run the install.sh script(\"./install.sh\") to install apk"
