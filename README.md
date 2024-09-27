![Build project](https://github.com/shallowx/venus/blob/master/doc/badge.svg)

## Venus

URL Shortener System

- Supports large-scale clusters
- System support multi-level cache ensure venus performance by itself
- The function of the 'admin(package)' is to provide web management UI support
- The function of the 'openapi(package)' is to provide third-party queries, redirect and data analysis reports

## Performance

### Theme

- Core redirect function implementation

### Stress testing report
#### testing environment

- Mac Pro (12C 16G)
- VM: 12G(heap) +UseZGC
- Loop Count: Infinite
- specify thread lifetime duration(s): 300
- number of thread(users): 1000

#### performance report

```java
summary +557742   in 00:00:16 =35466.2/s  Avg:14 Min:0 Max:142 Err:0(0.00%) Active:1000 Started:1000 Finished:0
summary +1131290  in 00:00:30 =37723.5/s  Avg:18 Min:0 Max:195 Err:0(0.00%) Active:1000 Started:1000 Finished:0
summary =1689032  in 00:00:46 =36930.8/s  Avg:17 Min:0 Max:195 Err:0(0.00%)
summary +1083571  in 00:00:30 =36127.5/s  Avg:20 Min:0 Max:185 Err:0(0.00%) Active:1000 Started:1000 Finished:0
summary =2772603  in 00:01:16 =36607.8/s  Avg:18 Min:0 Max:195 Err:0(0.00%)
summary +1061514  in 00:00:30 =35398.0/s  Avg:22 Min:0 Max:255 Err:0(0.00%) Active:1000 Started:1000 Finished:0
summary =3834117  in 00:01:46 =36264.7/s  Avg:19 Min:0 Max:255 Err:0(0.00%)
summary +1090152  in 00:00:30 =36338.4/s  Avg:21 Min:0 Max:228 Err:0(0.00%) Active:1000 Started:1000 Finished:0
summary =4924269  in 00:02:16 =36274.5/s  Avg:19 Min:0 Max:255 Err:0(0.00%)
summary +1105998  in 00:00:30 =36896.1/s  Avg:20 Min:0 Max:253 Err:0(0.00%) Active:1000 Started:1000 Finished:0
summary =6030267  in 00:02:46 =36386.8/s  Avg:20 Min:0 Max:255 Err:0(0.00%)
summary +1095169  in 00:00:30 =36506.9/s  Avg:20 Min:0 Max:237 Err:0(0.00%) Active:1000 Started:1000 Finished:0
summary =7125436  in 00:03:16 =36405.2/s  Avg:20 Min:0 Max:255 Err:0(0.00%)
summary +1095980  in 00:00:30 =36532.7/s  Avg:19 Min:0 Max:188 Err:0(0.00%) Active:1000 Started:1000 Finished:0
summary =8221416  in 00:03:46 =36422.1/s  Avg:20 Min:0 Max:255 Err:0(0.00%)
summary +1101839  in 00:00:30 =36728.0/s  Avg:20 Min:0 Max:267 Err:0(0.00%) Active:1000 Started:1000 Finished:0
summary =9323255  in 00:04:16 =36457.8/s  Avg:20 Min:0 Max:267 Err:0(0.00%)
summary +1072470  in 00:00:30 =35636.2/s  Avg:22 Min:0 Max:263 Err:0(0.00%) Active:1000 Started:1000 Finished:0
summary =10395725 in 00:04:46 =36371.2/s  Avg:20 Min:0 Max:267 Err:0(0.00%)
summary +530512   in 00:00:15 =36549.2/s  Avg:20 Min:0 Max:192 Err:0(0.00%) Active:0    Started:1000 Finished:1000
summary =10926237 in 00:05:00 =36379.7/s  Avg:20 Min:0 Max:267 Err:0(0.00%)
```
## Quickly Start
| name  | version |
|-------|---------|
| Java  | 21      |
| MySQL | 8.x     |
| Redis | Latest  |

- Start MYSQL
- Start Redis
- Check the system setting(application.properties)

### Application
- Start the main class
  ```java 
  org.venus.VenusApplication#main()
  ```
- Add the URL mapping, please see the  
  ```java 
  org.venus.admin.controller.LinksRestController#add()
  ```

### Docker
- Add the URL mapping, please see 
  ```java 
  org.venus.admin.controller.LinksRestController#add()
  ```
- Ensure the JVM stack startup parameters in the script (run.sh) can be start service. Since the default is 12G, the container may have insufficient memory, causing the startup to fail
```shell
  docker build -t venus .
  docker run --name venus -p 8029:8029 venus
  ```

## Metrics

```shell
 http://127.0.0.1:18029/venus/actuator
```

## Links

* [Web Site]()
* [Downloads]()
* [Documentation]()
* [Official Discord server]()

## Security

- Complete SSL/TLS and StartTLS support

## How to build

For the detailed information about building and developing Venus, please visit the [developer guide](). This page only
gives very basic information.

You require the following to build Venus:

* Latest stable [OpenJDK 21](https://adoptium.net/)
* Latest stable [Gradle](https://docs.gradle.org/)

Note that this is build-time requirement. JDK-21 is enough to run your Venus-based application.

## Branches to look

Development of all versions takes place in each branch whose name is identical to <majorVersion>.<minorVersion>. For
example, the development of master resides in the branch 'master' respectively.
  

