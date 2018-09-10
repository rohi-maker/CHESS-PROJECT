## Build for production mode

```sh
sbt xitrumPackage
```

The project will be built and packaged to directory:
target/xitrum

## Renew free HTTPS certificate

See:
https://github.com/certbot/certbot

```sh
./letsencrypt-auto certonly -d play.synergychess.net
```
