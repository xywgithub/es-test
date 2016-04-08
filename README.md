
用来向百度查询某个地址的经纬度，并且将查询结果写入到ES中的工程。


构建方式
------------
标准的maven工程构建方式




代码说明
-----------
com.bank.location.select.BaiduLBSAPI 中的main函数为启动函数。  
## **主要流程**  
1. 	通过scc_address，scc_fullName，scc_lastfullname字段作为检索条件，从ES库中拿出所有含有这些字段的信息  
2. 将这些信息通过http向百度地图http://api.map.baidu.com/geocoder/v2/ 发送请求，从响应信息进行判断，筛选出有精确经纬坐标的地址信息。然后进行组合写回ES库中。
-------------  
## **主要类功能介绍**
1. BaiduLBSAPI此类是启动类，其中addr2loc()方法是对检索条件的筛选方法，根据精确度是否为1或0来判断是否有精确经纬度。resolveAddress()方法是将符合信息写入指  
定文件中。  
2. ESclient 此类是对连接关闭ES库的操作进行了封装。  
3. ETLBank 此类是将数据写入ES库中。  
4. GetAllBankAddar 此类是拿取相关字段作为检索条件，从ES库中拿取信息。  
5. HttpRequest 封装http向指定URL发送get,post请求。  
6. JsonTransform 格式化http响应回来的信息。  
7. ObtainLocation 将坐标写回ES库的线程实现类，因为数据信息量大，所以  
  需要启用线程池来启动多个线程执行。  


