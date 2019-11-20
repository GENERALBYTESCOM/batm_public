#!/usr/bin/env bash

URL="./build/distributions/batm_ssh_tunnel-1.0.0.tar"
PORT=22222
DIR="/opt/batm_ssh_tunnel"
USER="batmsshtunnel"
DEPS="java sudo curl tar useradd systemctl"

SERVICE="batm-ssh-tunnel"
SERVICE_DIR="/etc/systemd/system/"

# check dependencies
for C in $DEPS; do
  command -v "$C" >/dev/null 2>&1 || { echo >&2 "$C is required but it's not installed. Aborting."; exit 1; }
done


### UN-INSTALL ###

if [[ "$1" == "--uninstall" ]]; then
  echo "=== UNINSTALL ==="
  echo "This will stop and remove the service, delete all the installed files and configuration and remove the system user. Would you like to continue? [enter|Ctrl+C]"
  read

  sudo systemctl stop "$SERVICE"
  sudo systemctl disable "$SERVICE"
  sudo systemctl daemon-reload
  sudo systemctl reset-failed
  sudo rm -v "$SERVICE_DIR$SERVICE.service" && echo "Service $SERVICE removed"

  sudo rm -rv "$DIR"

  sudo userdel --remove "$USER" && echo "User $USER removed"

  echo Uninstalled
  exit 0

fi



### INSTALL ###

# delete the installation directory if it already exists
if [[ -d "$DIR" ]]; then
  echo "$DIR already exists. This script will delete it and generate new password. Would you like to continue? [enter|Ctrl+C]"
  read
  sudo rm -rf "$DIR"
fi


echo "Adding user $USER"
sudo useradd --system --create-home "$USER"

# create the installation directory and cd into it
sudo mkdir -p "$DIR" || { echo >&2 "Cannot mkdir "$DIR".  Aborting."; exit 1; }
sudo chown "$USER" "$DIR"
cd "$DIR" || { echo >&2 "Cannod cd to "$DIR".  Aborting."; exit 1; }
cd -

echo "Downloading and unpacking to $DIR"
cat "$URL" | sudo -u "$USER" tar -xf -

cd "$DIR"
echo "Generating password"
pwd=`sudo -iu "$USER" "$DIR/bin/batm_ssh_tunnel" init`
if [[ -z "$PWD" ]]; then echo >&2 "Cannot generate password"; exit 1; fi


echo "Installing the service"
sudo cp "$SERVICE.service" "$SERVICE_DIR"
sudo chmod 644 "$SERVICE_DIR$SERVICE.service"
sudo systemctl daemon-reload
sudo systemctl enable "$SERVICE"

echo "Starting the service"
sudo systemctl stop "$SERVICE"
sudo systemctl start "$SERVICE"
sleep 1
systemctl is-active "$SERVICE" --quiet || { echo >&2 "Failed to start the service"; exit 1; }


echo
echo "Please make the port $PORT is accessible from BATM Master Server."
echo "No other ports need to be open."
echo "Use this Tunnel Password in CAS Crypto Settings."
echo "Make sure to keep the password SECRET!"
echo
echo "********************************"
echo "$pwd"
echo "********************************"
echo
