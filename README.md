![Build project](https://github.com/shallowx/venus/blob/master/doc/badge.svg)

## Venus

URL Shortener System: supports large-scale clusters

- System support multi-level cache ensure venus performance by itself
- The function of the 'admin(package)' is to provide web management UI support
- The function of the 'openapi(package)' is to provide third-party queries, redirect and data analysis reports

## Architecture

![architecture](https://github.com/shallowx/venus/blob/master/doc/architecture.png)

## Performance

### Theme

- Core redirect function implementation

### Stress testing environment

- Mac Pro(12C16G)
- VM: 12G(heap)、+UseZGC
- Loop Couunt: Infinite
- specify thread lifetime duration(s): 300
- number of thread(users): 1000

performance-report

```java
summary +557742in 00:00:16=35466.2/
s Avg:14Min:0Max:142Err:0(0.00%)Active:1000Started:1000Finished:0
summary +1131290in 00:00:30=37723.5/
s Avg:18Min:0Max:195Err:0(0.00%)Active:1000Started:1000Finished:0
summary =1689032in 00:00:46=36930.8/
s Avg:17Min:0Max:195Err:0(0.00%)
summary +1083571in 00:00:30=36127.5/
s Avg:20Min:0Max:185Err:0(0.00%)Active:1000Started:1000Finished:0
summary =2772603in 00:01:16=36607.8/
s Avg:18Min:0Max:195Err:0(0.00%)
summary +1061514in 00:00:30=35398.0/
s Avg:22Min:0Max:255Err:0(0.00%)Active:1000Started:1000Finished:0
summary =3834117in 00:01:46=36264.7/
s Avg:19Min:0Max:255Err:0(0.00%)
summary +1090152in 00:00:30=36338.4/
s Avg:21Min:0Max:228Err:0(0.00%)Active:1000Started:1000Finished:0
summary =4924269in 00:02:16=36274.5/
s Avg:19Min:0Max:255Err:0(0.00%)
summary +1105998in 00:00:30=36896.1/
s Avg:20Min:0Max:253Err:0(0.00%)Active:1000Started:1000Finished:0
summary =6030267in 00:02:46=36386.8/
s Avg:20Min:0Max:255Err:0(0.00%)
summary +1095169in 00:00:30=36506.9/
s Avg:20Min:0Max:237Err:0(0.00%)Active:1000Started:1000Finished:0
summary =7125436in 00:03:16=36405.2/
s Avg:20Min:0Max:255Err:0(0.00%)
summary +1095980in 00:00:30=36532.7/
s Avg:19Min:0Max:188Err:0(0.00%)Active:1000Started:1000Finished:0
summary =8221416in 00:03:46=36422.1/
s Avg:20Min:0Max:255Err:0(0.00%)
summary +1101839in 00:00:30=36728.0/
s Avg:20Min:0Max:267Err:0(0.00%)Active:1000Started:1000Finished:0
summary =9323255in 00:04:16=36457.8/
s Avg:20Min:0Max:267Err:0(0.00%)
summary +1072470in 00:00:30=35636.2/
s Avg:22Min:0Max:263Err:0(0.00%)Active:1000Started:1000Finished:0
summary =10395725in 00:04:46=36371.2/
s Avg:20Min:0Max:267Err:0(0.00%)
summary +530512in 00:00:15=36549.2/
s Avg:20Min:0Max:192Err:0(0.00%)Active:0Started:1000Finished:1000
summary =10926237in 00:05:00=36379.7/
s Avg:20Min:0Max:267Err:0(0.00%)
```

## Support Protocol

- HTTP
- TCP/IP

HTTP protocol uses RESTful API and TCP/IP protocol uses Server/Client mode

## Metrics

- see this package 'org.venus.metrics'

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
  

