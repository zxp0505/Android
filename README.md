      
### Binder与Ashm
为什么将Binder与Ashm匿名共享内存放在开头，因为Binder与Ashm是Android在Linux内核上新加的机制，都是IPC机制。Binder是整个Android系统的核心机制，理解Binder对理解整个系统至关重要。对于Binder机制的介绍可以看universus写的《Android Binder设计与实现》。另外还两个老外介绍Binder的PPT也不错。      
     
[Android Binder设计与实现 - 设计篇](http://blog.csdn.net/universus/article/details/6211589)     
[inter-process method invocation in Android ](https://github.com/clarkehe/Binder/blob/master/doc/androidipm20110409-110409000514-phpapp01.pdf) （相对入门一点，可以先看这个）    
[Deep Dive into Android IPC:Binder Framework](https://github.com/clarkehe/Binder/blob/master/doc/Deep%20Dive%20into%20Android%20IPC:Binder%20Framework.pdf)      
[android与linux的关系](https://events.linuxfoundation.org/images/stories/slides/jls09/jls09_torres.pdf)  （看前几页PPT就好了）

Android的匿名共享内存机制是在内核实现的，也是基于Linux内核本身的共享内存机制，作了一层封装。Android提供了匿名共享内存的C运行库访问接口（但NDK不能使用），Java层也有访问接口，就是MemoryFile.java，通过研究MemoryFile可以了解匿名内存的机制。

[ An Introduction to Android Shared Memory](http://notjustburritos.tumblr.com/post/21442138796/an-introduction-to-android-shared-memory)       
[Android Binder 分析——匿名共享内存（Ashmem）](http://light3moon.com/2015/01/28/Android%20Binder%20%E5%88%86%E6%9E%90%E2%80%94%E2%80%94%E5%8C%BF%E5%90%8D%E5%85%B1%E4%BA%AB%E5%86%85%E5%AD%98[Ashmem]/)         
[Android系统匿名共享内存Ashmem（Anonymous Shared Memory）简要介绍和学习计划](http://blog.csdn.net/luoshengyang/article/details/6651971)     

Linux下的IPC机制，有System V and POSIX两个标准，提供的接口会不一样，其中就有共享内存的访问接口（shm_open/shmget），但在NDK下这两套接口都不能用。           

[Linux System V and POSIX IPC Examples](http://www.hildstrom.com/projects/ipc_sysv_posix/index.html)

### 绘制系统
绘制系统，关键就是了解SurfaceFlinger服务进程工作原理，知道有硬件绘制与软件绘制。在应用层，需要知道View、Surface、SurfaceHolder 、SurfaceTexture、SurfaceView、GLSurfaceView、TextureView的用法及区别。结合实际经验及源码，对Surface及SurfaceTexure进行了初步的分析。

[Surface](https://github.com/clarkehe/work/wiki/Android%E7%BB%98%E5%88%B6%E7%B3%BB%E7%BB%9F(1):-Surface)                    
[SurfaceTexture](https://github.com/clarkehe/work/wiki/Android%E7%BB%98%E5%88%B6%E7%B3%BB%E7%BB%9F(3):-SurfaceTexture)       
[Android 5.0(Lollipop)中的SurfaceTexture，TextureView, SurfaceView和GLSurfaceView](http://blog.csdn.net/jinzhuojun/article/details/44062175)     
[Android Graphics](https://source.android.com/devices/graphics/index.html)   

### 网络
移动端除了WIFI还有移动网络（弱网络）。移动网络是一个优势，可以实时在线并访问网络。移动网络的主要问题有：低速、高延迟、流量有成本、连接不稳定等，这些问题是在实际开发中要面对的。

[两个概念，延迟与丢包率](https://github.com/clarkehe/work/wiki/%E7%BD%91%E7%BB%9C(1):-%E4%B8%A4%E4%B8%AA%E6%A6%82%E5%BF%B5%EF%BC%8C%E5%BB%B6%E8%BF%9F%E4%B8%8E%E4%B8%A2%E5%8C%85%E7%8E%87)      
[域名劫持](https://github.com/clarkehe/work/wiki/%E7%BD%91%E7%BB%9C(2):-%E5%9F%9F%E5%90%8D%E5%8A%AB%E6%8C%81)

### 内存
手机内存更大的了，多至4G、6G，但内存依然是稀缺的。Android没有虚拟内存，再加上更多app驻留后台，内存还是不够用。作为有良心的app，没有必要不要驻留后台，更应减少内存的占用。android开发要面临的内存问题有：内存泄露、内存溢出、内存占用过多(导致驻留后台被系统回收)，GC导致卡顿等问题。要解决这些问题，要了解android内存管理机制及常见的内存分析工具。

[App内存浅析](https://github.com/clarkehe/work/wiki/Android%E5%86%85%E5%AD%98(1):-app%E5%86%85%E5%AD%98%E6%B5%85%E6%9E%90)                 

### 省电
相比内存，电池更是智能机（不仅仅是android机）的痛点，一天一充已是极限。 电池的消耗，除系统及显示屏外，就是app了。“又想马儿跑，又想马儿不吃草”，难！要省电，先了解android的电源管理机制，再学会用工具定量对的耗电进行测试。

[手机休眠引发的“血案”](https://github.com/clarkehe/work/wiki/%E6%89%8B%E6%9C%BA%E4%BC%91%E7%9C%A0%E5%BC%95%E5%8F%91%E7%9A%84%E2%80%9C%E8%A1%80%E6%A1%88%E2%80%9D)                   
[Android休眠机制](https://github.com/clarkehe/work/wiki/Android%E4%BC%91%E7%9C%A0%E6%9C%BA%E5%88%B6)                 

### 性能优化
性能优化就是减少卡顿，提高帧率（FPS）。帧率的降低，是因为没能在16ms内完成一帧的绘制。在熟悉android的绘制系统后，通过测试app实际的帧率了解流畅度，再通过系统提供的方法与工具来定位性能的关键点。

[使用SysTrace定位及优化性能问题实战](https://github.com/clarkehe/work/wiki/Android%E6%80%A7%E8%83%BD(1):-%E4%BD%BF%E7%94%A8SysTrace%E5%AE%9A%E4%BD%8D%E5%8F%8A%E4%BC%98%E5%8C%96%E6%80%A7%E8%83%BD%E9%97%AE%E9%A2%98%E5%AE%9E%E6%88%98)        
[Android Performance Case Study](http://www.curious-creature.com/docs/android-performance-case-study-1.html)   (老外写的很经典的性能优化的实践)

### 热补丁与插件化
2016年，热补丁技术很火，TX就出了三个方案，阿里出了两个方案，以WX出的Tinker方案最有代表性，Tinker已经开源。
理解插件机制，要理解Android下Dex文件及Class的加载机制，虚拟机不同实现对补丁方案也有影响。

插件化技术的比较早一点，开源的方案也比较多。插件用的技术与热补丁有一些相似之处，不同的要处理四大组件的生命周期。

[ClassLoader与MultiDex分包](https://github.com/clarkehe/work/wiki/Android(4):-ClassLoader%E4%B8%8EMultiDex%E5%88%86%E5%8C%85)      （插件与补丁的基础知识）        
[补丁方案及原理](https://github.com/clarkehe/work/wiki/%E7%83%AD%E8%A1%A5%E4%B8%81(1):-%E8%A1%A5%E4%B8%81%E6%96%B9%E6%A1%88%E5%8F%8A%E5%8E%9F%E7%90%86)           

### 包大小
有一定大小的安装包，是Native应用一个痛点，下载要时间，安装也要时间，还会占用户空间（要注意：安装包10MB大小，安装完成后实际占用是10MB的几倍空间）。用户的空间也是有限的，16G的手机不少，基本不够用，经常要清理手机空间。除包大小尽量优化外，App在使用过程中也不要产生不必要的数据占用用户空间，如果占用过多，自动清理或有手动清理的渠道。

[Shrink Your Code and Resources](https://developer.android.com/studio/build/shrink-code.html)   （先按照官方指南在打包时，清掉无有的代码与资源）          
[安装包立减1M--微信Android资源混淆打包工具](http://mp.weixin.qq.com/s?__biz=MzAwNDY1ODY2OQ==&mid=208135658&idx=1&sn=ac9bd6b4927e9e82f9fa14e396183a8f#rd)      

### H5
H5与Native之争，没有高下，现在都是Hybrid App（混合型应用）。H5有自己的优势，但也有三个突出的问题：加载慢、耗流量、性能（体验）相对差、有兼容性问题。对于H5要扬长避短，发挥其优势，解决其不足。对于性能问题，可参考之前写的《H5缓存机制浅析》。

[H5缓存机制浅析-移动端Web加载性能优化](http://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=402077566&idx=1&sn=def3337205c3aec5e0fde2476ee03397&scene=0&key=ac89cba618d2d976159e30761eefe9953dc2030a7d72c1872c445a8caaa0f1d3cc4eb416a1c7cfb82651db48d11f3f90&ascene=0&uin=MjAyNzY1NTU%3D&devicetype=iMac+MacBookPro12%2C1+OSX+OSX+10.11.1+build(15B42)&version=11020201&pass_ticket=Ot%2FkhKXqAqrGFzCH568zK5zy%2FSd6Yamb01L2dKV6dtY%3D)

###架构
       
###音视频与直播技术
2016年直播应用大爆发，总结直播相关的技术问题。建议先看下《移动直播技术秒开优化经验（含PPT）》，对视频直播的整个技术栈及相关问题有比较清晰的描述。

[移动直播技术秒开优化经验（含PPT）](http://weibo.com/ttarticle/p/show?id=2309403969318634263193)              
[H264编码模式](https://github.com/clarkehe/work/wiki/%E8%A7%86%E9%A2%91(1):-H264%E7%BC%96%E7%A0%81%E6%A8%A1%E5%BC%8F)              
[视频直播协议](https://github.com/clarkehe/work/wiki/%E8%A7%86%E9%A2%91%E7%9B%B4%E6%92%AD%E5%8D%8F%E8%AE%AE)

### 测试及app质量
测试能保证版本没有明显的Bug，但不能通过测试来保证App的质量。没有Bug不一定是有质量的，有质量的App需要开发、产品和用户一起打造。总结一些从技术上能提升与验证质量的一些方法。

[Crash及ANR捕捉上报](https://github.com/clarkehe/work/wiki/Android(7):-Crash%E5%8F%8AANR%E6%8D%95%E6%8D%89%E4%B8%8A%E6%8A%A5)         
[代码质量检查](https://github.com/clarkehe/work/wiki/Android(8):-%E4%BB%A3%E7%A0%81%E8%B4%A8%E9%87%8F%E6%A3%80%E6%9F%A5)         
[独家分享【Android ANR的成因分析及分析定位技术】](http://developer.baidu.com/forum/topic/show?topicId=3938)       
[ANR机制以及问题分析](http://duanqz.github.io/2015-10-12-ANR-Analysis#service)      

### 编程技术
与语言无关的一些编程技术与思想。

[侵入式接口、反向控制、依赖注入](https://github.com/clarkehe/work/wiki/Coding(1):-%E4%BE%B5%E5%85%A5%E5%BC%8F%E6%8E%A5%E5%8F%A3%E3%80%81%E5%8F%8D%E5%90%91%E6%8E%A7%E5%88%B6%E3%80%81%E4%BE%9D%E8%B5%96%E6%B3%A8%E5%85%A5)              
[RxJava](https://github.com/clarkehe/work/wiki/Coding(4):-RxJava)               

### Android实践
[Activity生命周期中的onSaveInstanceState](https://github.com/clarkehe/work/wiki/Android(1):-Activity%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F%E4%B8%AD%E7%9A%84onSaveInstanceState)          
[事件分发机制](https://github.com/clarkehe/work/wiki/Android(2):-%E4%BA%8B%E4%BB%B6%E5%88%86%E5%8F%91%E6%9C%BA%E5%88%B6)        
[Service Intent must be explicit 异常解决](https://github.com/clarkehe/work/wiki/Android(3):-Service-Intent-must-be-explicit-%E5%BC%82%E5%B8%B8%E8%A7%A3%E5%86%B3)           
[Set service foreground with notification](https://github.com/clarkehe/work/wiki/Android(5):-Set-service-foreground-with-notification)             
[Get running app for Android 5.1](https://github.com/clarkehe/work/wiki/Android(6):-Get-running-app-for-Android-5.1)        
[国产机的那些坑](https://github.com/clarkehe/work/wiki/Android(9):-%E5%9B%BD%E4%BA%A7%E6%9C%BA%E7%9A%84%E9%82%A3%E4%BA%9B%E5%9D%91)           
[Dialog、AlertDialog、PopupWindow、DialogFragment](https://github.com/clarkehe/work/wiki/Android(14):-Dialog%E3%80%81AlertDialog%E3%80%81PopupWindow%E3%80%81DialogFragment)                 
[View三大属性参数：attrs、defStyleAttr、defStyleRes](https://github.com/clarkehe/work/wiki/android(17):-View%E4%B8%89%E5%A4%A7%E5%B1%9E%E6%80%A7%E5%8F%82%E6%95%B0%EF%BC%9Aattrs%E3%80%81defStyleAttr%E3%80%81defStyleRes)              
[Fragment常见的坑](https://github.com/clarkehe/work/wiki/android(18):-Fragment%E5%B8%B8%E8%A7%81%E7%9A%84%E5%9D%91)      
[Glide使用及注意的地方](https://github.com/clarkehe/work/wiki/Coding(7):-Glide%E4%BD%BF%E7%94%A8%E5%8F%8A%E6%B3%A8%E6%84%8F%E7%9A%84%E5%9C%B0%E6%96%B9)               
[Activity启动模式图文详解](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0520/2897.html)   

### 心得体会
[从PC角度看移动端开发技术](https://github.com/clarkehe/work/wiki/Android(13):-%E4%BB%8EPC%E7%AB%AF%E5%BC%80%E5%8F%91%E8%A7%92%E5%BA%A6%E7%9C%8B%E7%A7%BB%E5%8A%A8%E7%AB%AF%E5%BC%80%E5%8F%91)     
[技术方案的选择也要考虑用户的价值与利益](https://github.com/clarkehe/work/wiki/%E6%9D%82%E8%B0%88(1):-%E6%8A%80%E6%9C%AF%E6%96%B9%E6%A1%88%E7%9A%84%E9%80%89%E6%8B%A9%E4%B9%9F%E8%A6%81%E8%80%83%E8%99%91%E7%94%A8%E6%88%B7%E7%9A%84%E4%BB%B7%E5%80%BC%E4%B8%8E%E5%88%A9%E7%9B%8A)               
[沟通与团队合作](https://github.com/clarkehe/work/wiki/%E6%9D%82%E8%B0%88(2):-%E6%B2%9F%E9%80%9A%E4%B8%8E%E5%9B%A2%E9%98%9F%E5%90%88%E4%BD%9C)        
[自省沟通](https://github.com/clarkehe/work/wiki/%E6%9D%82%E8%B0%88(3):-%E8%87%AA%E7%9C%81%E6%B2%9F%E9%80%9A)          
[这个Bug改不改？](https://github.com/clarkehe/work/wiki/%E6%9D%82%E8%B0%88(4):-%E8%BF%99%E4%B8%AABug%E6%94%B9%E4%B8%8D%E6%94%B9%EF%BC%9F)             
[工作沟通要带有目的](https://github.com/clarkehe/work/wiki/%E6%9D%82%E8%B0%88(5):-%E5%B7%A5%E4%BD%9C%E6%B2%9F%E9%80%9A%E8%A6%81%E5%B8%A6%E6%9C%89%E7%9B%AE%E7%9A%84)        
[目标沟通，SMART原则](https://github.com/clarkehe/work/wiki/%E6%9D%82%E8%B0%88(6):-%E7%9B%AE%E6%A0%87%E6%B2%9F%E9%80%9A%EF%BC%8CSMART%E5%8E%9F%E5%88%99)           
[《情商》读后感之一](https://github.com/clarkehe/work/wiki/%E6%9D%82%E8%B0%88(7):-%E3%80%8A%E6%83%85%E5%95%86%E3%80%8B%E8%AF%BB%E5%90%8E%E6%84%9F%E4%B9%8B%E4%B8%80)                     
[修心，让自己安静下来](https://github.com/clarkehe/work/wiki/%E6%9D%82%E8%B0%88(8):-%E4%BF%AE%E5%BF%83%EF%BC%8C%E8%AE%A9%E8%87%AA%E5%B7%B1%E5%AE%89%E9%9D%99%E4%B8%8B%E6%9D%A5)

### 项目团队
[我在Facebook的十点经验分享](https://github.com/clarkehe/work/wiki/%E8%BD%AC%E8%BD%BD1:-%E6%88%91%E5%9C%A8Facebook%E7%9A%84%E5%8D%81%E7%82%B9%E7%BB%8F%E9%AA%8C%E5%88%86%E4%BA%AB)   
[知乎上的面试经验（汤涛）](https://github.com/clarkehe/work/wiki/%E9%9D%A2%E8%AF%95(1):-%E7%9F%A5%E4%B9%8E%E4%B8%8A%E7%9A%84%E7%BB%8F%E9%AA%8C%EF%BC%88%E6%B1%A4%E6%B6%9B%EF%BC%89)          
[最近面试总结](https://github.com/clarkehe/work/wiki/%E9%9D%A2%E8%AF%95(2):-%E6%9C%80%E8%BF%91%E9%9D%A2%E8%AF%95%E6%80%BB%E7%BB%93)   
[手游宝一年的总结](https://github.com/clarkehe/work/wiki/%E9%A1%B9%E7%9B%AE%E7%BB%8F%E9%AA%8C(1):-%E6%89%8B%E6%B8%B8%E5%AE%9D%E4%B8%80%E5%B9%B4%E7%9A%84%E6%80%BB%E7%BB%93)           
[评价与考核](https://github.com/clarkehe/work/wiki/%E9%A1%B9%E7%9B%AE%E7%BB%8F%E9%AA%8C(2):-%E8%AF%84%E4%BB%B7%E4%B8%8E%E8%80%83%E6%A0%B8)            
[技术Leader的职责](https://github.com/clarkehe/work/wiki/%E6%8A%80%E6%9C%AFLeader%E7%9A%84%E8%81%8C%E8%B4%A3)

### 产品
[开发看产品](https://github.com/clarkehe/work/wiki/%E6%88%91%E5%AF%B9%E4%BA%A7%E5%93%81%E7%9A%84%E7%9C%8B%E6%B3%95)       
[Pony对QQMail的邮件摘录](http://wenku.baidu.com/view/d7f60a8471fe910ef12df8a0.html)            
[马化腾培训教材：让产品自己召人](http://wenku.baidu.com/view/5c53dd27ccbff121dd3683fd.html)          
[产品设计与用户体验--马化腾](http://wenku.baidu.com/view/a226ad6483d049649a66581d.html)          
