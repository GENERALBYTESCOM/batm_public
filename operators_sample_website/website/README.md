<p align="center"><a href="https://generalbytes.com" target="_blank"><img src="https://www.generalbytes.com/cdn-4s78g/images/common/logo/GENERAL_BYTES.svg" width="200" alt="gbLogo"></a></p>

<p align="center">`
<a href="https://github.com/GENERALBYTESCOM/batm_public"><img src="https://github.com/laravel/framework/workflows/tests/badge.svg" alt="Build Status" ></a>
`</p>

## Operators sample website
<img src="./public/assets/img/homePage.png?raw=true" width="500" alt="homePage">

## Overview

**The Operators Sample Website (OSW) is a sample web application that demonstrates how operators can enable their customers initiate sell transactions online via operator's website and later visit two-way BATMThree or BATMFour ATM when cash is ready for withdrawal.**
<p align="center"><a href="https://www.generalbytes.com/en/products/batmfour" target="_blank"><img src="https://www.generalbytes.com/res/crc-30765327/configurator/20210602191011/355x1029/f-Sinc/q90/batm4_dispenser_and_without_cc_reader_and_with_nfc.png" width="200" alt="BATMFour"></a></p>


Advantages of the OSW:
* On the website, your clients can check the list of available terminals ready to sell their selected cryptocurrency for the required fiat amount.
* After selecting the preferred terminal, cryptocurrency and fiat amounts, our OSW immediately generates a QR code (account number) for sending crypto. So...you don't have to spend time in front of a terminal waiting for payment acceptance.
* OSW shows you the transaction status.


## Installation on UBUNTU 20.04 server:

** Install PHP & Nginx :**
```bash
$ sudo add-apt-repository ppa:ondrej/php
$ sudo apt-get update
$ sudo apt-get install php8.0
$ sudo apt install -y php8.0-mbstring php8.0-fpm php8.0-xml php8.0-zip php8.0-mysql php8.0-common php8.0-cli unzip curl nginx
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

** Install git: **
```bash
$ sudo apt install git-all
```
** Install OperatorsSampleWebsite package **
```bash
$ git clone https://github.com/GENERALBYTESCOM/batm_public.git
```

* Navigate to installed folder website /operators_sample_website/website and update composer dependencies
```bash
$ composer update
```

* Check your installation on devel server and open in browser link http://127.0.0.1:8000
```bash
$ php artisan serve
```

* Setup constants for CAS url, API_KEY, Banknote denomination and min fiat amount in an interface file IGbCasRestServices.php
* **path:** Website/App/GBlib/Repositories/IGbCasRestServices.php

<img src="./public/assets/img/IGbCasRestServices.png?raw=true" width="500" alt="interface"/>

* Check api key in your CAS

<img src="./public/assets/img/apiKey.png?raw=true" width="500" alt="apiKey">


** Configure Nginx **
* move batm_public project to /var/www/html/ and change ownership
```bash
  $ sudo mv /home/userFolder/batm_public /var/www/html/
  $ sudo chown -R www-data:www-data /var/www/html/batm_public
```
* Create a Nginx configuration file
```bash
$ sudo nano /etc/nginx/sites-available/osw.conf
```
* configuration
```bash
     server {
        listen 80;
        server_name yourservername.com;
        root /var/www/html/batm_public/operators_sample_website/website/public;

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
    }
```
* Enable configuration
```bash
$ sudo ln -s /etc/nginx/sites-available/osw.conf /etc/nginx/sites-enabled/
```

* Set yourservername.com on Ubuntu server
```bash
$ sudo nano /etc/hosts
```

* Restart Nginx
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










