<p align="center"><a href="https://generalbytes.com" target="_blank"><img src="https://www.generalbytes.com/cdn-4s78g/images/common/logo/GENERAL_BYTES.svg" width="200" alt="gbLogo"></a></p>

<p align="center">`
<a href="https://github.com/GENERALBYTESCOM/batm_public"><img src="https://github.com/laravel/framework/workflows/tests/badge.svg" alt="Build Status" ></a>
`</p>

## Operators sample website
<img src="./public/assets/img/homePage.png?raw=true" width="500" alt="homePage">

## Overview

**The Operators Sample Website (OSW) is a web application written in PHP and based on the Laravel 8 framework. The application helps your clients pre-arrange the crypto selling process on your General Bytes BATMThree or BATMFour terminals.**
<p align="center"><a href="https://www.generalbytes.com/en/products/batmfour" target="_blank"><img src="https://www.generalbytes.com/res/crc-30765327/configurator/20210602191011/355x1029/f-Sinc/q90/batm4_dispenser_and_without_cc_reader_and_with_nfc.png" width="200" alt="BATMFour"></a></p>


Advantages of the OSW:
* On the website, your clients can check the list of available terminals ready to sell their selected cryptocurrency for the required fiat amount.
* After selecting the preferred terminal, cryptocurrency and fiat amounts, our OSW immediately generates a QR code (account number) for sending crypto. So...you don't have to spend time in front of a terminal waiting for payment acceptance.
* OSW shows you the transaction status.


## Installation on UBUNTU 20.04 server:

** Install PHP & Nginx :**
```bash
$ sudo apt install -y php-mbstring php-xml php-fpm php-zip php-common php-cli unzip curl nginx
```

** Install composer: **
```bash
$ sudo curl -s https://getcomposer.org/installer | php
```
* Move the composer file to the /usr/local/bin path
```bash
$ sudo mv composer.phar /usr/local/bin/composer 
```
* Assign execute permission
```bash
$ sudo chmod +x   /usr/local/bin/composer
```
* Check composer installation
```bash
$ composer --version
```

** Install OperatorsSampleWebsite package **
```bash
$ git clone https://github.com/GENERALBYTESCOM/batm_public.git
```
* Navigate to installed folder website /operators_sample_website/website and update composer dependecies
```bash
$ composer update
```
* Update laravel setup
```bash
$ php artisan config:clear
$ php artisan key:generate
```
* Check your installation on devel server and click on url http://127.0.0.1:8000
```bash
$ php artisan serve
```

* Setup constants for CAS url, API_KEY, Banknote denomination and min fiat amount in an interface file IGbCasRestServices.php
* **path:** Website/App/GBlib/Repositories/IGbCasRestServices.php

<img src="./public/assets/img/IGbCasRestServices.png?raw=true" width="500" alt="interface"/>

* Check api key in your CAS

<img src="./public/assets/img/apiKey.png?raw=true" width="500" alt="apiKey">


** Configure Nginx **
```bash
  $ sudo chmod -R 755 /var/www/html/example
  $ sudo chown -R www-data:www-data /var/www/html/example
```
* Create an Nginx configuration file
```bash
$ sudo nano /etc/nginx/sites-available/example
```
* configuration
```bash
     server {
        listen 80;
        server_name yourservername.com;
        root /var/www/html/gbapp/public;

        add_header X-Frame-Options "SAMEORIGIN";
      add_header X-Content-Type-Options "nosniff";

        index index.php;

        charset utf-8;

        location / {
                try_files $uri $uri/ /index.php?$query_string;
        }

       location = /favicon.ico { access_log off; log_not_found off; }
       location = /robots.txt  { access_log off; log_not_found off; }
       error_page 404 /index.php;

       location ~ \.php$ {
        fastcgi_pass unix:/var/run/php/php8.0-fpm.sock;
        include snippets/fastcgi-php.conf;
        include fastcgi_params;
 
       location ~ /\.(?!well-known).* {
       deny all;
       }
    }
```
* Enable configuration
```bash
$ sudo ln -s /etc/nginx/sites-available/example /etc/nginx/sites-enabled/
```
* Restart Nginx
  $ sudo ln -s /etc/nginx/sites-available/example /etc/nginx/sites-enabled/
```bash
  $ sudo systemctl restart nginx
```
* Now your website should work  - yourservername.com

## User manual
* Visit your new Website and select sell button

<img src="./public/assets/img/homePage.png?raw=true" width="500" alt="homePage">

* Fill out amount and select currencies

<img src="./public/assets/img/sellAmountFiatCrypto.png?raw=true" width="500" alt="sellAmountFiatCrypto">

* Select terminal with available cash

<img src="./public/assets/img/terminalsWithAvailableCash.png?raw=true" width="500" alt="terminalsWithAvailableCash">

* Take a photo of the QR code and make a transaction from your crypto wallet

<img src="./public/assets/img/createSellTransaction.png?raw=true" width="500" alt="createSellTransaction">

* Visit the selected terminal to withdraw your fiat currency

<p align="center"><a href="https://www.generalbytes.com/en/products/batmthree" target="_blank"><img src="https://www.generalbytes.com/res/crc-2631124927/products/images/1920x1080/q90/f-Sinc/602845-cg-114-na-image-182.webp" width="400" alt="BATMThree"></a></p>










