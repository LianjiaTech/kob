
### How to build docker image

```batch
docker build --build-arg JAR_FILE=./target/schedule-server-processor-1.0.0-SNAPSHOT.jar -t lianjia-tech/kob-processor:latest .
```

### Quick start use the default application.yml config

```batch
docker run --rm -it --name kob-console -p 8669:8669 lianjia-tech/kob-processor:latest
```

### Custom  application.yml config

```batch
docker run --rm -it --name kob-processor -v /home/kob/processor:/usr/lib/kob/processor -p 8668:8668 -e 'CONFIG_LOCATION=/usr/lib/kob/processor/application.yml' lianjia-tech/kob-processor:latest
```

>**Note**:  If you are using PowerShell on Windows to run these commands use double quotes instead of single quotes.

```batch
docker run --rm -it --name kob-processor -v D:\docker\kob\processor:/usr/lib/kob/processor -p 8668:8668 -e "CONFIG_LOCATION=/usr/lib/kob/processor/application.yml" lianjia-tech/kob-processor:latest
```