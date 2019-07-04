
### How to build docker image

```batch
docker build --build-arg JAR_FILE=./target/schedule-server-console-1.0.0-SNAPSHOT.jar -t lianjia-tech/kob-console:latest .
```

### Quick start use the default application.yml config

```batch
docker run --rm -it --name kob-console -p 8669:8669 lianjia-tech/kob-console:latest
```

### Custom  application.yml config

```batch
docker run --rm -it --name kob-console -v /home/kob:/usr/lib/kob/console -p 8669:8669 -e 'CONFIG_LOCATION=/usr/lib/kob/console/application.yml' lianjia-tech/kob-console:latest
```

>**Note**:  If you are using PowerShell on Windows to run these commands use double quotes instead of single quotes.

```batch
docker run --rm -it --name kob-console -v D:\docker\kob\console:/usr/lib/kob/console -p 8669:8669 -e "CONFIG_LOCATION=/usr/lib/kob/console/application.yml" lianjia-tech/kob-console:latest
```